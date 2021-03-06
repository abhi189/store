package com.budderfly.sites.service;

import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.dto.SiteSyncDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import java.util.Optional;

/**
 * Service Interface for managing Site.
 */
public interface SiteService {

    /**
     * Save a site.
     *
     * @param siteDTO the entity to save
     * @return the persisted entity
     */
    SiteDTO save(SiteDTO siteDTO);

    /**
     * Get all the sites.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SiteDTO> findAll(Pageable pageable);

    /**
     * Get all the sites.
     *
     * @return the list of entities
     */
    List<SiteDTO> findAll();

    /**
     * Get the "id" site.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<SiteDTO> findOne(Long id);

    /**
     * Delete the "id" site.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the site corresponding to the query.
     *
     * @param query the query of the search
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<SiteDTO> search(String query, Pageable pageable);

    /**
     *  Check current siteAccounts and create New Sites
     *  @void
     */
    void syncSites();

    Long findSiteIdByBudderflyId(String budderflyId);

    SiteDTO findByBudderflyId(String budderflyId);

    void syncInjobsData(List<SiteSyncDTO> listSitesSync);

    List<SiteDTO> findSitesBySiteEmail(String email);

    List<String> getShopsOwnedByUser(String login);

}
