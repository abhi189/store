package com.budderfly.sites.repository;

import com.budderfly.sites.domain.Site;
import com.budderfly.sites.domain.enumeration.SiteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Site entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {

    @Query("SELECT s.id FROM Site s WHERE s.budderflyId = ?1")
    Long findSiteIdByBudderflyId(String budderflyId);

    @Query("SELECT s FROM Site s, Contact c WHERE c.contactEmail = ?1 AND ( s.siteContact = c.id OR s.billingContact = c.id OR s.franchiseContact = c.id )")
    Page<Site> getSiteBasedOnSiteOwnership(String email, Pageable pageable);

    Site findByBudderflyId(String budderflyId);

    List<Site> findByStatus(SiteStatus siteStatus);

    List<Site> findBySiteContact(String email);
}
