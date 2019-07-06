package com.budderfly.sites.client;

import com.budderfly.sites.service.dto.SiteAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "billing", url = "", fallbackFactory = BillingClientFallbackFactory.class, configuration = BillingClientConfiguration.class)
public interface BillingClient {

    @RequestMapping(value = "/api/site-accounts/budderflySiteAccountsPageable?page={pageNumber}&size={size}", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<RestResponsePage<SiteAccountDTO>> getSitesAccounts(@PathVariable("pageNumber") Long page, @PathVariable("size")Long size);
}
