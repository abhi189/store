package com.budderfly.sites.client;
import com.budderfly.sites.service.dto.SiteAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

import java.util.List;

@Component
public class BillingClientFallbackFactory implements BillingClient, FallbackFactory<BillingClient> {

    private final Logger log = LoggerFactory.getLogger(BillingClientFallbackFactory.class);

    private final Throwable cause;

    public BillingClientFallbackFactory() {
        this(null);
    }

    BillingClientFallbackFactory(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public BillingClient create(Throwable cause) {
        // log the reason for the fallback
        if (cause != null && cause.getMessage() != null) {
            log.error(cause.getMessage());
        }

        return new BillingClientFallbackFactory(cause);
    }

    @Override
    public ResponseEntity<RestResponsePage<SiteAccountDTO>> getSitesAccounts(Long page, Long size) {
        log.warn("getSitesAccounts: problems detected while interacting with the Billing service.");
        return null;
    }
}
