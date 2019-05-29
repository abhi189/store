#!/bin/bash

# Some credit to https://github.com/maxtsepkov/bash_colors/blob/master/bash_colors.sh
#
# Constants and functions for terminal colors. Not using tput
# Author: Steve Wyckoff

COLOR_SCRIPT=color-logger.bash
COLOR_VERSION=0.9.0

COLOR_ESC="\x1B["

COLOR_RESET=0             # reset all attributes to their defaults
COLOR_RESET_UNDERLINE=24  # underline off
COLOR_RESET_REVERSE=27    # reverse off
COLOR_DEFAULT=39          # set underscore off, set default foreground color
COLOR_DEFAULTB=49         # set default background color

COLOR_BOLD=1              # set bold
COLOR_BRIGHT=2            # set half-bright (simulated with color on a color display)
COLOR_UNDERSCORE=4        # set underscore (simulated with color on a color display)
COLOR_REVERSE=7           # set reverse video

COLOR_BLACK=30            # set black foreground
COLOR_RED=31              # set red foreground
COLOR_GREEN=32            # set green foreground
COLOR_BROWN=33            # set brown foreground
COLOR_BLUE=34             # set blue foreground
COLOR_MAGENTA=35          # set magenta foreground
COLOR_CYAN=36             # set cyan foreground
COLOR_WHITE=37            # set white foreground

COLOR_BLACK_BG=40           # set black background
COLOR_RED_BG=41             # set red background
COLOR_GREEN_BG=42           # set green background
COLOR_BROWN_BG=43           # set brown background
COLOR_BLUE_BG=44            # set blue background
COLOR_MAGENTA_BG=45         # set magenta background
COLOR_CYAN_BG=46            # set cyan background
COLOR_WHITE_BG=47           # set white background

COLOR_DEBUG=$COLOR_BLUE
COLOR_INFO=$COLOR_MAGENTA
COLOR_HIGHLIGHT=$COLOR_CYAN
COLOR_WARN=$COLOR_BROWN
COLOR_ERROR=$COLOR_RED

COLOR_SUCCESS=$COLOR_GREEN

logger_wrap_escape() {
  local paint="$1"
  local message="$2"

  if ! [ $paint -ge 0 -a $paint -le 47 ] 2>/dev/null; then
    echo "escape: argument for \"$paint\" is out of range" >&2 && return 1
  fi

  if [ -z "$message" ]; then
    echo "No message passed in"

    exit 1
  fi

  message="${COLOR_ESC}${paint}m${message}${COLOR_ESC}${COLOR_RESET}m"

  echo -ne "\n$message\n"
}

logger_color(){
  local paint="$1"
  shift

  for message in "$@";
  do
    logger_wrap_escape "$paint" "$message"
  done

  echo
}

# PUBLIC API
debug(){
  logger_color "$COLOR_DEBUG" "$@"
}

info(){
  logger_color "$COLOR_INFO" "$@"
}

warn(){
  logger_color "$COLOR_WARN" "$@"
}

error(){
  logger_color "$COLOR_ERROR" "$@"
}

highlight(){
  logger_wrap_escape $COLOR_HIGHLIGHT "$1"
}

success(){
  logger_color "$COLOR_SUCCESS" "$@"
}

die () {
    error "$@"
    exit 1
}

[ "$#" -eq 1 ] || die "1 argument is required, $# provided, please provide an env. $(highlight 'Usage: ./deploy.sh qa2ms')"

ENV_QA="qa2ms"
ENV_PROD="prod1ms"
MYSQL_HOST_QA="qa.caolf1xhi8cw.us-west-2.rds.amazonaws.com"
MYSQL_HOST_PROD="prod.caolf1xhi8cw.us-west-2.rds.amazonaws.com"
MYSQL_DB_NAME=$(grep -E "url.*mysql" src/main/resources/config/application-prod.yml | sed 's/.*\/\(.*[^?]\)?.*/\1/g')
MYSQLDUMP_IGNORE_FLAGS="--ignore-table=$MYSQL_DB_NAME.jhi_entity_audit_event --ignore-table=$MYSQL_DB_NAME.jhi_persistent_audit_event --ignore-table=$MYSQL_DB_NAME.jhi_persistent_audit_evt_data"
MYSQLDUMP_OUT_DIR=/tmp
MYSQLDUMP_CONFIG_QA="$HOME/.my_qa.cnf"
MYSQLDUMP_CONFIG_PROD="$HOME/.my_prod.cnf"
S3_BUCKET_NAME="budderfly-backups"
S3_BUCKET_PREFIX="db"

if [[ -z "$MYSQL_DB_NAME" ]]; then
  die "Unable to determine database name. Abort!"
fi

if [[ "$1" != "$ENV_QA" && "$1" != "$ENV_PROD" ]]; then
  die "Expected $(highlight $ENV_QA) or $(highlight $ENV_PROD) as parameter values."
fi

DB_ENV=""
MYSQL_HOST=""
MYSQL_CREDENTIALS=""
MYSQLDUMP_OUT_FN=""
if [[ "$1" = "$ENV_QA" ]]; then
  DB_ENV="qa"
  MYSQL_HOST=$MYSQL_HOST_QA
  if [[ -f "$MYSQLDUMP_CONFIG_QA" ]]; then
    MYSQL_CREDENTIALS="--defaults-file=$MYSQLDUMP_CONFIG_QA"
  fi
fi
if [ "$1" = "$ENV_PROD" ]; then
  DB_ENV="prod"
  MYSQL_HOST=$MYSQL_HOST_PROD
  if [[ -f "$MYSQLDUMP_CONFIG_PROD" ]]; then
    MYSQL_CREDENTIALS="--defaults-file=$MYSQLDUMP_CONFIG_PROD"
  fi
fi

# backup
MYSQLDUMP_OUT_FN="${MYSQL_DB_NAME}_${DB_ENV}_$(date +%Y%m%d).sql"
debug "Making backup of $1.$MYSQL_DB_NAME..."
mysqldump ${MYSQL_CREDENTIALS:--u fernando.acuna -p} -h $MYSQL_HOST $MYSQL_DB_NAME $MYSQLDUMP_IGNORE_FLAGS > $MYSQLDUMP_OUT_DIR/$MYSQLDUMP_OUT_FN || die "mysqldump failed. Abort!"
debug "Compressing backup of $1 ("$MYSQLDUMP_OUT_DIR/$MYSQLDUMP_OUT_FN")..."
gzip -9 "$MYSQLDUMP_OUT_DIR/$MYSQLDUMP_OUT_FN" || die "gzip failed. Abort!"
MYSQLDUMP_OUT_FN="$MYSQLDUMP_OUT_FN".gz

# AWS login
debug "Login to AWS"
login=$(aws ecr get-login --no-include-email --region us-west-2)
eval $login

if [[ $? != 0 ]]; then
  die "Login to AWS failed. Abort!"
fi

# copy backup to AWS S3
debug "Pushing backup "$MYSQLDUMP_OUT_FN" to $(echo $DB_ENV | tr 'a-z' 'A-Z') AWS S3://$S3_BUCKET_NAME/$S3_BUCKET_PREFIX/$DB_ENV/..."
aws s3 cp "$MYSQLDUMP_OUT_DIR/$MYSQLDUMP_OUT_FN" s3://$S3_BUCKET_NAME/$S3_BUCKET_PREFIX/$DB_ENV/

if [[ $? != 0 ]]; then
  die "Upload failed. Abort!"
fi

success "Backup finished."

##
## cleanup
##
# get list of files to delete
LAST_MONTH=$(date --date='-1 month' +%Y-%m-%d)
FILES_TO_DELETE_JSON=$(aws s3api list-objects --bucket $S3_BUCKET_NAME --prefix "$S3_BUCKET_PREFIX/$DB_ENV/$MYSQL_DB_NAME" --query "Contents[?LastModified<'$LAST_MONTH'].{Key: Key}" | sed ':a;N;$!ba;s/\n\ *//g')
if [[ ! -z "$FILES_TO_DELETE_JSON" ]] && [[ "$FILES_TO_DELETE_JSON" != "null" ]] && [[ "$FILES_TO_DELETE_JSON" != "[]" ]]; then
  FILES_TO_DELETE_JSON='{"Objects":'${FILES_TO_DELETE_JSON}'}'
else
  debug "No files to delete."
  exit 0
fi

# delete files
aws s3api delete-objects --bucket $S3_BUCKET_NAME --delete ''"$FILES_TO_DELETE_JSON"''
if [[ $? != 0 ]]; then
  debug "$FILES_TO_DELETE_JSON"
  die "Delete failed for the files above. Abort!"
fi

success "Cleanup finished."
