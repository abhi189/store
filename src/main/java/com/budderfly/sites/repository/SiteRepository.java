package com.budderfly.sites.repository;

import com.budderfly.sites.domain.Site;
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

    Site findByBudderflyId(String budderflyId);

    List<Site> findBySiteContact(String email);
}
