package com.budderfly.sites.service;

import java.time.ZoneId;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.iakovlev.timeshape.TimeZoneEngine;

/**
 * TimezoneService to retrieve Timezone ID using Timeshape library.
 */
@Service
@Transactional
public class TimezoneService {

    /**
     * Initialization boundaries to save memory
     * These cover the North America region
     */ 
    private static double MIN_LAT = 20;
    private static double MIN_LON = -180;
    private static double MAX_LAT = 90;
    private static double MAX_LON = -50;
    private static double MIN_VALID_LAT = -90;
    private static double MIN_VALID_LON = -180;
    private static double MAX_VALID_LAT = 90;
    private static double MAX_VALID_LON = 180;

    private TimeZoneEngine engine;

    private final Logger log = LoggerFactory.getLogger(TimezoneService.class);

    public TimezoneService() {
        log.debug("Initializing TimeZoneEngine with coordinates: MIN_LAT {} MIN_LON {} MAX_LAT {} MAX_LONG {}", MIN_LAT, MIN_LON, MAX_LAT, MAX_LON);
        engine = TimeZoneEngine.initialize(MIN_LAT, MIN_LON, MAX_LAT, MAX_LON, false);
    }

    /**
     * 
     * @param latitude
     * @param longitude
     * @return an Optional object containing the ZoneId which can be used to get the Time Offset for specific coordinates
     */
    private Optional<ZoneId> getZoneId(double latitude, double longitude) {
        log.debug("Request to getZoneId with latitude {} and longitude {}", latitude, longitude);
        return engine.query(latitude, longitude);
    }

    public String getTimeZoneId(String latitude, String longitude) {
        if (coordinatesAreValid(latitude, longitude)){
            Optional<ZoneId> optionalZoneId = getZoneId(Double.parseDouble(latitude), Double.parseDouble(longitude));
            if (optionalZoneId.isPresent()){
                String timeZoneId = optionalZoneId.get().getId();
                return timeZoneId;
            }
        }
        return null;
    }

    private boolean coordinatesAreValid(String latitude, String longitude) {
        if (latitude == null || latitude.isEmpty() || longitude == null || longitude.isEmpty()){
            return false;
        }
        try {
            double latitudeDouble = Double.parseDouble(latitude);
            double longitudeDouble = Double.parseDouble(longitude);

            if (latitudeDouble < MIN_VALID_LAT || latitudeDouble > MAX_VALID_LAT || longitudeDouble < MIN_VALID_LON || longitudeDouble > MAX_VALID_LON){
                log.warn("Coordinates are not valid. Cannot get the TimeZoneId.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            log.warn("Coordinates are not valid. Cannot get the TimeZoneId.");
            return false;
        }
    }

}