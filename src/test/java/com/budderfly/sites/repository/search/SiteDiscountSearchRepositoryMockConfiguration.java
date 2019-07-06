package com.budderfly.sites.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of SiteDiscountSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SiteDiscountSearchRepositoryMockConfiguration {

    @MockBean
    private SiteDiscountSearchRepository mockSiteDiscountSearchRepository;

}
