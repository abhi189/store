package com.budderfly.sites.client;

import com.budderfly.sites.service.dto.SiteAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@FeignClient(name = "billing", url = "", fallbackFactory = BillingClientFallbackFactory.class, configuration = BillingClientConfiguration.class)
public interface BillingClient {

    @RequestMapping(value = "/api/site-accounts/find-new-sites", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Void findNewSites(List<String> budderflyIds);
}
