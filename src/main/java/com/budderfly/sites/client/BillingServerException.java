package com.budderfly.sites.client;

import feign.FeignException;

class BillingServerException extends FeignException {
    private final int status;

    BillingServerException(Integer status, String message) {
        super("(" + status.toString() + "): " + message);
        this.status = status;
    }
}
