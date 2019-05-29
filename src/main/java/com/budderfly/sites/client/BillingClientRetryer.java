package com.budderfly.sites.client;

import feign.RetryableException;
import feign.Retryer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillingClientRetryer implements Retryer {

    private final Logger log = LoggerFactory.getLogger(BillingClientRetryer.class);

    private final int maxAttempts;
    private final long backoff;
    private int attempt;

    BillingClientRetryer() {
        this(2000, 3);
    }

    BillingClientRetryer(long backoff, int maxAttempts) {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

    public void continueOrPropagate(RetryableException e) {
        if (this.attempt++ >= this.maxAttempts) {
            log.error("{}", e);
            throw e;
        }

        log.warn(e.toString());
        log.warn("retrying({}/{}) ...", this.attempt, this.maxAttempts);

        try {
            Thread.sleep(this.backoff);
        } catch (InterruptedException ignored) {
            log.error("RETRYER ERROR: "+ignored.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Retryer clone() {
        return new BillingClientRetryer(this.backoff, this.maxAttempts);
    }
}
