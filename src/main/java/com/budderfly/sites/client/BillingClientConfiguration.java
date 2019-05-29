package com.budderfly.sites.client;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import io.github.jhipster.security.uaa.LoadBalancedResourceDetails;

import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;

import java.io.IOException;

@Configuration
public class BillingClientConfiguration {

    private final LoadBalancedResourceDetails loadBalancedResourceDetails;

    public BillingClientConfiguration(LoadBalancedResourceDetails loadBalancedResourceDetails) {
        this.loadBalancedResourceDetails = loadBalancedResourceDetails;
    }

    @Bean
    public LoadBalancedResourceDetails getLoadBalancedResourceDetails(){
        return this.loadBalancedResourceDetails;
    }

    @Bean
    public Retryer feignRetryer() { return new BillingClientRetryer(); }

    @Bean
    public ErrorDecoder feignErrorDecoder() { return new BillingErrorDecoder(); }

    // from AuthorizedFeignClient's default configuration class
    @Bean(name = "oauth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor() throws IOException {
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), loadBalancedResourceDetails);
    }
}
