package com.budderfly.sites.client;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BillingErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final Logger log = LoggerFactory.getLogger(BillingErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {

        /*
         * The current implementation of feign client doesn't resolve the response body as an accessible String object.
         * For this reason, we need to manipulate the response.body() manually in order to obtain a string representation.
         */
        String responseBody = "";
        try {
            responseBody = new String(Util.toByteArray(response.body().asInputStream()));
        } catch (IOException e) {
            // ignore
        }

        if (response.status() >= 400 && response.status() <= 499) {
            log.error("STATUS: "+response.status());
            return new BillingClientException(response.status(), response.reason() + responseBody);
        }
        if (response.status() >= 500 && response.status() <= 599) {
            log.error("STATUS: "+response.status());
            return new BillingServerException(response.status(), response.reason() + responseBody);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
