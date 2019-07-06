package com.budderfly.sites.repository;

import com.budderfly.sites.domain.SiteDiscount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the SiteDiscount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteDiscountRepository extends JpaRepository<SiteDiscount, Long>, JpaSpecificationExecutor<SiteDiscount> {

}
