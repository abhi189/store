package com.budderfly.sites.client;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BillingClientException extends FeignException {
    private final int status;

    private final Logger log = LoggerFactory.getLogger(BillingClientException.class);

    BillingClientException(Integer status, String message) {
        super("(" + status.toString() + "): " + message);
        this.status = status;
        log.info("STATUS: "+status);
    }
}
