package com.budderfly.sites.client;

import com.budderfly.sites.service.dto.SiteAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "billing", url = "", fallbackFactory = BillingClientFallbackFactory.class, configuration = BillingClientConfiguration.class)
public interface BillingClient {

    @RequestMapping(value = "/api/site-accounts/budderflySiteAccounts", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<SiteAccountDTO>> getSitesAccounts();

}
