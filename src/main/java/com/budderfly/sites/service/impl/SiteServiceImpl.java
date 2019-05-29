package com.budderfly.sites.service.impl;

import com.budderfly.sites.domain.enumeration.BillingType;
import com.budderfly.sites.domain.enumeration.PaymentType;
import com.budderfly.sites.domain.enumeration.SiteStatus;
import com.budderfly.sites.service.SiteService;
import com.budderfly.sites.domain.Site;
import com.budderfly.sites.repository.SiteRepository;
import com.budderfly.sites.repository.search.SiteSearchRepository;
import com.budderfly.sites.service.dto.SiteAccountDTO;
import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.dto.SiteSyncDTO;
import com.budderfly.sites.service.mapper.SiteMapper;
import com.budderfly.sites.client.BillingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Site.
 */
@Service
@Transactional
public class SiteServiceImpl implements SiteService {

    private final Logger log = LoggerFactory.getLogger(SiteServiceImpl.class);

    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;

    private final SiteSearchRepository siteSearchRepository;

    private final BillingClient billingClient;

    private final ElasticsearchOperations elasticsearchTemplate;

    private static final Lock reindexLock = new ReentrantLock();

    private final Pattern VALID_BUDDERFLY_ID = Pattern.compile("^([A-Z]{2,}-[0-9]{1,})+$");

    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper, SiteSearchRepository siteSearchRepository, BillingClient billingClient, ElasticsearchOperations elasticsearchTemplate) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.siteSearchRepository = siteSearchRepository;
        this.billingClient = billingClient;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    /**
     * Save a site.
     *
     * @param siteDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SiteDTO save(SiteDTO siteDTO) {
        log.debug("Request to save Site : {}", siteDTO);
        Site site = siteMapper.toEntity(siteDTO);
        site = siteRepository.save(site);
        SiteDTO result = siteMapper.toDto(site);
        siteSearchRepository.save(site);
        return result;
    }

    /**
     * Get all the sites.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sites");
        return siteRepository.findAll(pageable)
            .map(siteMapper::toDto);
    }

    /**
     * Get all the sites.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> findAll() {
        log.debug("Request to get all Sites");
        List<SiteDTO> sites = siteMapper.toDto(siteRepository.findAll());
        return sites;
    }

    /**
     * Get one site by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SiteDTO> findOne(Long id) {
        log.debug("Request to get Site : {}", id);
        return siteRepository.findById(id)
            .map(siteMapper::toDto);
    }

    /**
     * Delete the site by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Site : {}", id);
        siteRepository.deleteById(id);
        siteSearchRepository.deleteById(id);
    }

    /**
     * Search for the site corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SiteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sites for query {}", query);
        return siteSearchRepository.search(queryStringQuery(query), pageable)
            .map(siteMapper::toDto);
    }

    public List<SiteDTO> findSitesBySiteEmail(String email) {
        log.debug("Request to get sites based on email " + email);
        return siteMapper.toDto(siteRepository.findBySiteContact(email));
    }

    /**
     * sync Check current Sites Accounts and create new Sites if they don't exists
     *
     * @void create new Sites
     */
    @Override
    public void syncSites(){
        log.info("Getting Current SitesAccounts");
        try {
            ResponseEntity<List<SiteAccountDTO>> sitesAccounts = billingClient.getSitesAccounts();

            if (sitesAccounts != null && sitesAccounts.getStatusCode().equals(HttpStatus.OK)) {

                List<Site> currentSites = siteRepository.findAll();

                for (SiteAccountDTO siteAccount : sitesAccounts.getBody()) {
                    Site result = currentSites.stream()
                        .filter(item -> item.getBudderflyId().equals(siteAccount.getBudderflyId()))
                        .findFirst()
                        .orElse(null);

                    if (result == null && VALID_BUDDERFLY_ID.matcher(siteAccount.getBudderflyId()).find()) {
                        log.info("" + siteAccount.getBudderflyId() + " IS a VALID BUDDERFLYID and is going to be ADDED as a NEW SITE");

                        SiteDTO site = new SiteDTO();
                        Site siteEntity = siteMapper.toEntity(site);

                        site.setBudderflyId(siteAccount.getBudderflyId());
                        site.setCustomerName(siteAccount.getCustomerName());
                        site.setCompanyType(siteAccount.getSiteCode());
                        site.setStoreNumber(siteAccount.getCustomerCode());
                        site.setStatus(SiteStatus.ACTIVE);
                        site.setAddress(siteAccount.getSiteAddress1());
                        site.setPaymentType(PaymentType.Check);
                        if (siteAccount.getArrears() != null && siteAccount.getArrears() == true) {
                            site.setBillingType(BillingType.AMU_Arrears);
                        } else {
                            site.setBillingType(BillingType.AMU_Forward);
                        }
                        site.setOwnerName("");
                        site.setOwnerEmail("");
                        site.setOwnerPhone("");

                        this.save(site);
                        siteSearchRepository.save(siteEntity);

                    } else {
                        if (!VALID_BUDDERFLY_ID.matcher(siteAccount.getBudderflyId()).find()){
                            log.warn("BUDDERFLYID: " + siteAccount.getBudderflyId() + " IS NOT a VALID NAME");
                        }
                    }
                }
            } else {
                log.error("Error trying to get SitesAccounts from Billing Microservice");
            }
        }catch(Exception e){
            log.error("Error trying to Sync Sites: "+e.getMessage());
        }
    }

    @Override
    public Long findSiteIdByBudderflyId(String budderflyId) {
        log.info("getting site ID by budderfly ID");
        Long result = siteRepository.findSiteIdByBudderflyId(budderflyId);
        return result == null ? 0 : result;
    }

    @Override
    public SiteDTO findByBudderflyId(String budderflyId){
        Site site = this.siteRepository.findByBudderflyId(budderflyId);
        return siteMapper.toDto(site);
    }

    @Async
    @Override
    public void syncInjobsData(List<SiteSyncDTO> listSitesSync) {
        log.info("Starting sync process with injobs data.");
        int[] sitesSynced = {0};
        listSitesSync.forEach(siteSyncDTO -> {
            SiteDTO siteDTO = this.findByBudderflyId(siteSyncDTO.getBudderflyId());
            if (siteDTO != null){
                siteDTO.setEmoVersion(siteSyncDTO.getEmoVersion());
                log.debug("EMO Version {} set to Site {}.", siteDTO.getEmoVersion(), siteDTO.getBudderflyId());
                this.save(siteDTO);
                sitesSynced[0]++;
            }
        });
        log.info("Sync process with injobs data finished. Synced {} sites.", sitesSynced[0]);
    }
}
