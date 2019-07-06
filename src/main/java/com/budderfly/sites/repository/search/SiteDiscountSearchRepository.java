package com.budderfly.sites.repository.search;

import com.budderfly.sites.domain.SiteDiscount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the SiteDiscount entity.
 */
public interface SiteDiscountSearchRepository extends ElasticsearchRepository<SiteDiscount, Long> {
}
