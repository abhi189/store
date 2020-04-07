package com.budderfly.sites.service.impl;

import com.budderfly.sites.client.AuthenticateClient;
import com.budderfly.sites.client.BillingClient;
import com.budderfly.sites.domain.Contact;
import com.budderfly.sites.domain.Site;
import com.budderfly.sites.domain.enumeration.*;
import com.budderfly.sites.repository.ContactRepository;
import com.budderfly.sites.repository.SiteFilter;
import com.budderfly.sites.repository.SiteOwnershipFilter;
import com.budderfly.sites.repository.SiteRepository;
import com.budderfly.sites.repository.search.SiteSearchRepository;
import com.budderfly.sites.service.KafkaProducer;
import com.budderfly.sites.service.SiteService;
import com.budderfly.sites.service.TimezoneService;
import com.budderfly.sites.service.dto.SiteAccountDTO;
import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.dto.SiteSyncDTO;
import com.budderfly.sites.service.dto.SiteWorkOrdersDTO;
import com.budderfly.sites.service.mapper.SiteMapper;
import org.hibernate.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;
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
    private final TimezoneService timezoneService;
    private static final Lock reindexLock = new ReentrantLock();
    private final Pattern VALID_BUDDERFLY_ID = Pattern.compile("^([A-Z]{2,}-[0-9]{1,})+$");

    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper, SiteSearchRepository siteSearchRepository, ContactRepository contactRepository,
                           BillingClient billingClient, ElasticsearchOperations elasticsearchTemplate, AuthenticateClient authenticateClient,
                           KafkaProducer kafkaProducer, TimezoneService timezoneService) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.siteSearchRepository = siteSearchRepository;
        this.billingClient = billingClient;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.authenticateClient = authenticateClient;
        this.contactRepository = contactRepository;
        this.kafkaProducer = kafkaProducer;
        this.timezoneService = timezoneService;
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
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    @SiteFilter
    public List<SiteDTO> findAll() throws TransactionException{
        List<SiteDTO> list = new ArrayList<>();
        try{
            log.debug("Request to get all Sites");
            List<SiteDTO> sites = siteMapper.toDto(siteRepository.findAll());
            return sites;
        }catch (TransactionException e){
            log.warn("Transactional Error: "+e.getMessage(),e);
            return list;
        }
    }

    /**
     * Get all the sites filtered by the shops the current logged in user owns.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    @SiteOwnershipFilter
    public List<SiteDTO> findAllFiltered() throws TransactionException{
        List<SiteDTO> list = new ArrayList<>();
        try{
            log.debug("Request to get all Sites Filted by site ownership");
            List<SiteDTO> sites = siteMapper.toDto(siteRepository.findAll());
            return sites;
        }catch (TransactionException e){
            log.warn("Transactional Error: "+e.getMessage(),e);
            return list;
        }
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

    @Override
    public SiteDTO createSite(SiteAccountDTO siteAccount){
        SiteDTO newSite = new SiteDTO();
        try{
            if(siteAccount.getBudderflyId() != null) {
                log.info("VERIFYING BUDDERFLYID: {}",siteAccount.getBudderflyId());

                Site site = siteRepository.findByBudderflyId(siteAccount.getBudderflyId().toUpperCase());

                if (site == null) {
                    try {
                        if (VALID_BUDDERFLY_ID.matcher(siteAccount.getBudderflyId().toUpperCase()).find()) {
                            log.info("{} IS a VALID BUDDERFLYID and is going to be ADDED as a NEW SITE",siteAccount.getBudderflyId());

                            newSite.setBudderflyId(siteAccount.getBudderflyId().toUpperCase());
                            newSite.setCustomerName(siteAccount.getCustomerName());
                            newSite.setCompanyType(siteAccount.getSiteCode());
                            newSite.setStoreNumber(siteAccount.getCustomerCode());
                            newSite.setStatus(SiteStatus.ACTIVE);
                            newSite.setAddress(siteAccount.getSiteAddress1());
                            newSite.setPaymentType(PaymentType.Check);
                            if (siteAccount.getArrears() != null && siteAccount.getArrears() == true) {
                                newSite.setBillingType(BillingType.AMU_Arrears);
                            } else {
                                newSite.setBillingType(BillingType.AMU_Forward);
                            }
                            newSite.setOwnerName("");
                            newSite.setOwnerEmail("");
                            newSite.setOwnerPhone("");

                            newSite = this.save(newSite);


                        } else {
                            log.warn("BUDDERFLYID: {} IS NOT a VALID NAME",siteAccount.getBudderflyId());
                        }
                    } catch (DataIntegrityViolationException e) {
                        log.warn("Site {} Already Exist",siteAccount.getBudderflyId());
                    }
                } else {
                    newSite = siteMapper.toDto(site);
                    log.warn("BUDDERFLYID: {} already exist in database",siteAccount.getBudderflyId());
                }
            }
        }finally {
            return newSite;
        }
    }

    /**
     * sync Check current Sites Accounts and create new Sites if they don't exists
     *
     * @void create new Sites
     */
    @Async
    @Override
    public void syncSites(){
        log.info("Getting Current SitesAccounts");
        try {

            List<Site> currentSites = siteRepository.findAll();
            List<String> budderflyIds = new ArrayList<>();

            Iterator<Site> iter = currentSites.iterator();

            while (iter.hasNext() ){

                budderflyIds.add(iter.next().getBudderflyId());

            }
            log.info("Requesting NEW SITES to BillingMS");
            billingClient.findNewSites(budderflyIds);

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
                String contactEmail = siteSyncDTO.getContactEmail();
                String billingEmail = siteSyncDTO.getBillingEmail();
                String franchiseEmail = siteSyncDTO.getFranchiseEmail();

                siteDTO.setEmoVersion(siteSyncDTO.getEmoVersion());
                siteDTO.setSiteContact(getIdFromEmail(contactEmail));
                siteDTO.setBillingContact(getIdFromEmail(billingEmail));
                siteDTO.setFranchiseContact(getIdFromEmail(franchiseEmail));
                siteDTO.setContactDeskId(siteSyncDTO.getContactDeskId());
                if ((siteDTO.getLatitude() == null || siteDTO.getLatitude().isEmpty()) && siteSyncDTO.getLatitude() != null){
                    siteDTO.setLatitude(siteSyncDTO.getLatitude());
                }
                if ((siteDTO.getLongitude() == null || siteDTO.getLongitude().isEmpty()) && siteSyncDTO.getLongitude() != null){
                    siteDTO.setLongitude(siteSyncDTO.getLongitude());
                }
                siteDTO.setTimeZoneId(this.timezoneService.getTimeZoneId(siteSyncDTO.getLatitude(), siteSyncDTO.getLongitude()));
                siteDTO.setAddress(siteSyncDTO.getAddress());
                siteDTO.setCity(siteSyncDTO.getCity());
                siteDTO.setState(siteSyncDTO.getState());
                siteDTO.setZip(siteSyncDTO.getZipCode());
                log.debug("EMO Version {} set to Site {}.", siteDTO.getEmoVersion(), siteDTO.getBudderflyId());
                this.save(siteDTO);
                sitesSynced[0]++;

                List<String> emails = new ArrayList<>();
                if (contactEmail != null && !contactEmail.equals("")) {
                    emails.add(contactEmail);
                }
                if (billingEmail != null && !billingEmail.equals("")) {
                    emails.add(billingEmail);
                }
                if (franchiseEmail != null && !franchiseEmail.equals("")) {
                    emails.add(franchiseEmail);
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
    public Page<SiteDTO> getSitesByAuthenticateLogin(String login, Pageable pageable) {
        Page<SiteDTO> sites = siteRepository.findByBudderflyIdIn(getShopsOwnedByUser(login), pageable).map(siteMapper::toDto);
        return sites;
    }

    @Override
    public Page<SiteDTO> getSitesWithoutAuthenticateLogin(String login, Pageable pageable) {
        List<String> shops = getShopsOwnedByUser(login);
        if (shops == null || shops.isEmpty()) {
            return siteRepository.findAll(pageable).map(siteMapper::toDto);
        }

        return siteRepository.findByBudderflyIdNotIn(shops, pageable).map(siteMapper::toDto);
    }

    public List<SiteDTO> getSitesFromList(List<String> budderflyIds) {
        return siteRepository.findByBudderflyIdIn(budderflyIds, Pageable.unpaged()).map(siteMapper::toDto).getContent();
    }

    @Override
    public List<SiteDTO> getSiteBasedOnSiteOwnership(String email) {
        List<Site> sites = siteRepository.getSiteBasedOnSiteOwnership(email);
        return siteMapper.toDto(sites);
    }

    /*
     * GET SiteWorkOrdersDTO with mock data to display the work orders with site location for the Installer app
     *
     * @return list of SiteWorkOrdersDTO
     */
    @Override
    public List<SiteWorkOrdersDTO> getSitesAndWorkOrders() {

        List<SiteWorkOrdersDTO> siteWorkOrdersDTOS = new ArrayList<>();

        List<String[]> workOrders = new ArrayList<>();

        workOrders.add(new String[] {"6726","Scheduled","2022-03-11 08:54:25","SUBW-7104","805-b College St. Se","Lacey","WA","98503"});
        workOrders.add(new String[] {"6903","Scheduled","2020-02-09 09:18:03","SUBW-45774","7951 Villa Rica Highway","Dallas","GA","30157"});
        workOrders.add(new String[] {"6672","Scheduled","2019-03-26 18:30:38","KFC-324001","1793 S Creek One A","Powhatan","VA","23139"});
        workOrders.add(new String[] {"6596","Scheduled","2018-12-20 13:26:08","CAW-101","2321 Whitney Avenue","Hamden","CT","06518"});
        workOrders.add(new String[] {"6275","Scheduled","2018-05-31 06:00:00","OLYM-187","7479 US Highway 11","Potsdam","NY","13676"});

        for (int i=0; i<workOrders.size(); i++) {
            SiteWorkOrdersDTO siteWorkOrdersDTO = new SiteWorkOrdersDTO();

            siteWorkOrdersDTO.setWorkOrderNumber(workOrders.get(i)[0]);
            siteWorkOrdersDTO.setStatus(workOrders.get(i)[1]);
            siteWorkOrdersDTO.setScheduledDateAndTime(workOrders.get(i)[2]);
            siteWorkOrdersDTO.setBudderflyId(workOrders.get(i)[3]);
            siteWorkOrdersDTO.setAddress(workOrders.get(i)[4]);
            siteWorkOrdersDTO.setCity(workOrders.get(i)[5]);
            siteWorkOrdersDTO.setState(workOrders.get(i)[6]);
            siteWorkOrdersDTO.setZip(workOrders.get(i)[7]);

            siteWorkOrdersDTOS.add(siteWorkOrdersDTO);
        }

        return siteWorkOrdersDTOS;
    }
}
