package com.budderfly.sites.client;

import feign.FeignException;

class BillingClientException extends FeignException {
    private final int status;

    BillingClientException(Integer status, String message) {
        super("(" + status.toString() + "): " + message);
        this.status = status;
    }
}
