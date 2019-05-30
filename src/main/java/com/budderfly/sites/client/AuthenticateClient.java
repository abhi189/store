package com.budderfly.sites.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@AuthorizedFeignClient(name = "authenticate")
public interface AuthenticateClient {

    @GetMapping("/api/user-sites-shops/{login}")
    List<String> getShopsOwnedByUser(@PathVariable("login") String login);

}
