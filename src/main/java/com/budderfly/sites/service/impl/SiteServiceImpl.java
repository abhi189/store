package com.budderfly.sites.service.impl;

import com.budderfly.sites.client.AuthenticateClient;
import com.budderfly.sites.client.BillingClient;
import com.budderfly.sites.client.RestResponsePage;
import com.budderfly.sites.domain.Contact;
import com.budderfly.sites.domain.Site;
import com.budderfly.sites.domain.enumeration.*;
import com.budderfly.sites.repository.ContactRepository;
import com.budderfly.sites.repository.SiteFilter;
import com.budderfly.sites.repository.SiteRepository;
import com.budderfly.sites.repository.search.SiteSearchRepository;
import com.budderfly.sites.service.KafkaProducer;
import com.budderfly.sites.service.SiteService;
import com.budderfly.sites.service.dto.SiteAccountDTO;
import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.dto.SiteSyncDTO;
import com.budderfly.sites.service.mapper.SiteMapper;
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

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

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
    private final ContactRepository contactRepository;
    private final BillingClient billingClient;
    private final AuthenticateClient authenticateClient;
    private final KafkaProducer kafkaProducer;
    private final ElasticsearchOperations elasticsearchTemplate;
    private static final Lock reindexLock = new ReentrantLock();
    private final Pattern VALID_BUDDERFLY_ID = Pattern.compile("^([A-Z]{2,}-[0-9]{1,})+$");

    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper, SiteSearchRepository siteSearchRepository, ContactRepository contactRepository,
                           BillingClient billingClient, ElasticsearchOperations elasticsearchTemplate, AuthenticateClient authenticateClient, KafkaProducer kafkaProducer) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.siteSearchRepository = siteSearchRepository;
        this.billingClient = billingClient;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.authenticateClient = authenticateClient;
        this.contactRepository = contactRepository;
        this.kafkaProducer = kafkaProducer;
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
    @SiteFilter
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

            Long page = new Long(1);
            Long size = new Long(100);
            ResponseEntity<RestResponsePage<SiteAccountDTO>> sitesAccounts = billingClient.getSitesAccounts(page, size);

            for (int i = 1; i < sitesAccounts.getBody().getTotalPages(); i++) {
                if (page > 1) sitesAccounts = billingClient.getSitesAccounts(page, size);

                log.debug("CHECKING SITEACCOUNTS CONTENT");
                if (sitesAccounts != null && sitesAccounts.getStatusCode().equals(HttpStatus.OK)) {
                    log.debug("READING SITEACCOUNT CONTENT");
                    log.debug("VERIFYING BODY: "+sitesAccounts.getBody().getContent());

                    List<Site> currentSites = siteRepository.findAll();

                    for (SiteAccountDTO siteAccount : sitesAccounts.getBody().getContent()) {
                        log.debug("VERIFYING BUDDERFLYID: "+ siteAccount.getBudderflyId());
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
                            if (!VALID_BUDDERFLY_ID.matcher(siteAccount.getBudderflyId()).find()) {
                                log.warn("BUDDERFLYID: " + siteAccount.getBudderflyId() + " IS NOT a VALID NAME");
                            }
                        }
                    }
                    page++;
               }else{
                log.error("Error trying to get SitesAccounts from Billing Microservice");
            }
        }
        }catch (Exception e) {
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

    @Override
    public List<SiteDTO> findSitesBySiteStatus(SiteStatus siteStatus) {
        List<Site> sites = this.siteRepository.findByStatus(siteStatus);
        return siteMapper.toDto(sites);
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
                siteDTO.setSiteContact(getIdFromEmail(siteSyncDTO.getContactEmail()));
                siteDTO.setBillingContact(getIdFromEmail(siteSyncDTO.getBillingEmail()));
                siteDTO.setFranchiseContact(getIdFromEmail(siteSyncDTO.getFranchiseEmail()));
                log.debug("EMO Version {} set to Site {}.", siteDTO.getEmoVersion(), siteDTO.getBudderflyId());
                this.save(siteDTO);
                sitesSynced[0]++;

                List<String> emails = new ArrayList<>();
                if (siteSyncDTO.getContactEmail() != null) {
                    emails.add(siteSyncDTO.getContactEmail());
                }
                if (siteSyncDTO.getBillingEmail() != null) {
                    emails.add(siteSyncDTO.getBillingEmail());
                }
                if (siteSyncDTO.getFranchiseEmail() != null) {
                    emails.add(siteSyncDTO.getFranchiseEmail());
                }
                if (emails != null && !emails.isEmpty()) {
                    Map<String, Object> dict = new HashMap<String, Object>();
                    dict.put(KafkaDataKeys.EMAIL.toString(), emails);
                    dict.put(KafkaDataKeys.BUDDERFLY_ID.toString(), siteSyncDTO.getBudderflyId());

                    kafkaProducer.sendMessage(KafkaTopics.OWNER_SYNC, dict); // update authenticate.user_site with shop
                }
            }
        });
        log.info("Sync process with injobs data finished. Synced {} sites.", sitesSynced[0]);
    }

    private Long getIdFromEmail(String contactEmail) {
        if (contactEmail == null || contactEmail.equals("")) {
            return null;
        }

        List<Contact> contact = contactRepository.findByContactEmail(contactEmail);
        if (contact == null || contact.isEmpty()) {
            log.debug(contactEmail + " exists on a site but not under contacts");
            return null;
        }

        return contact.get(0).getId();
    }

    @Override
    public List<String> getShopsOwnedByUser(String login) {
        List<String> ids = authenticateClient.getShopsOwnedByUser(login);

        return ids;
    }

    @Override
    public Page<SiteDTO> getSiteBasedOnSiteOwnership(String email, Pageable pageable) {
        Page<SiteDTO> sites = siteRepository.getSiteBasedOnSiteOwnership(email, pageable).map(siteMapper::toDto);
        return sites;
    }
}
