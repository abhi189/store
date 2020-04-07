package com.budderfly.sites.web.rest;

import com.budderfly.sites.SitesApp;

import com.budderfly.sites.config.SecurityBeanOverrideConfiguration;

import com.budderfly.sites.domain.Site;
import com.budderfly.sites.repository.SiteRepository;
import com.budderfly.sites.repository.search.SiteSearchRepository;
import com.budderfly.sites.service.SiteService;
import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.mapper.SiteMapper;
import com.budderfly.sites.web.rest.errors.ExceptionTranslator;
import com.budderfly.sites.service.SiteQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.budderfly.sites.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.budderfly.sites.domain.enumeration.SiteStatus;
import com.budderfly.sites.domain.enumeration.BillingType;
import com.budderfly.sites.domain.enumeration.PaymentType;
import com.budderfly.sites.domain.enumeration.SiteType;
/**
 * Test class for the SiteResource REST controller.
 *
 * @see SiteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, SitesApp.class})
public class SiteResourceIntTest {

    private static final String DEFAULT_BUDDERFLY_ID = "AAAAAAAAAA";
    private static final String UPDATED_BUDDERFLY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_NAME = "BBBBBBBBBB";

    private static final SiteStatus DEFAULT_STATUS = SiteStatus.ACTIVE;
    private static final SiteStatus UPDATED_STATUS = SiteStatus.INACTIVE;

    private static final String DEFAULT_COMPANY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_STORE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_STORE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_ZIP = "AAAAAAAAAA";
    private static final String UPDATED_ZIP = "BBBBBBBBBB";

    private static final BillingType DEFAULT_BILLING_TYPE = BillingType.AMU_Forward;
    private static final BillingType UPDATED_BILLING_TYPE = BillingType.AMU_Arrears;

    private static final PaymentType DEFAULT_PAYMENT_TYPE = PaymentType.ETF;
    private static final PaymentType UPDATED_PAYMENT_TYPE = PaymentType.Check;

    private static final SiteType DEFAULT_SITE_TYPE = SiteType.Physical;
    private static final SiteType UPDATED_SITE_TYPE = SiteType.Virtual;

    private static final String DEFAULT_OWNER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_2 = "BBBBBBBBBB";

    private static final String DEFAULT_LATITUDE = "AAAAAAAAAA";
    private static final String UPDATED_LATITUDE = "BBBBBBBBBB";

    private static final String DEFAULT_LONGITUDE = "AAAAAAAAAA";
    private static final String UPDATED_LONGITUDE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_TAX_EXEMPT = false;
    private static final Boolean UPDATED_TAX_EXEMPT = true;

    private static final Boolean DEFAULT_ROLL_BILLING = false;
    private static final Boolean UPDATED_ROLL_BILLING = true;

    private static final String DEFAULT_EMO_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_EMO_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_DESK_ID = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_DESK_ID = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLE_TICKET_DISPATCH = false;
    private static final Boolean UPDATED_ENABLE_TICKET_DISPATCH = true;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private SiteService siteService;

    /**
     * This repository is mocked in the com.budderfly.sites.repository.search test package.
     *
     * @see com.budderfly.sites.repository.search.SiteSearchRepositoryMockConfiguration
     */
    @Autowired
    private SiteSearchRepository mockSiteSearchRepository;

    @Autowired
    private SiteQueryService siteQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restSiteMockMvc;

    private Site site;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SiteResource siteResource = new SiteResource(siteService, siteQueryService);
        this.restSiteMockMvc = MockMvcBuilders.standaloneSetup(siteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Site createEntity(EntityManager em) {
        Site site = new Site()
            .budderflyId(DEFAULT_BUDDERFLY_ID)
            .customerName(DEFAULT_CUSTOMER_NAME)
            .status(DEFAULT_STATUS)
            .companyType(DEFAULT_COMPANY_TYPE)
            .storeNumber(DEFAULT_STORE_NUMBER)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .zip(DEFAULT_ZIP)
            .billingType(DEFAULT_BILLING_TYPE)
            .paymentType(DEFAULT_PAYMENT_TYPE)
            .siteType(DEFAULT_SITE_TYPE)
            .ownerName(DEFAULT_OWNER_NAME)
            .ownerEmail(DEFAULT_OWNER_EMAIL)
            .ownerPhone(DEFAULT_OWNER_PHONE)
            .address1(DEFAULT_ADDRESS_1)
            .address2(DEFAULT_ADDRESS_2)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .taxExempt(DEFAULT_TAX_EXEMPT)
            .rollBilling(DEFAULT_ROLL_BILLING)
            .emoVersion(DEFAULT_EMO_VERSION)
            .contactDeskId(DEFAULT_CONTACT_DESK_ID)
            .enableTicketDispatch(DEFAULT_ENABLE_TICKET_DISPATCH);
        return site;
    }

    @Before
    public void initTest() {
        site = createEntity(em);
    }

    @Test
    @Transactional
    public void createSite() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate + 1);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getBudderflyId()).isEqualTo(DEFAULT_BUDDERFLY_ID);
        assertThat(testSite.getCustomerName()).isEqualTo(DEFAULT_CUSTOMER_NAME);
        assertThat(testSite.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSite.getCompanyType()).isEqualTo(DEFAULT_COMPANY_TYPE);
        assertThat(testSite.getStoreNumber()).isEqualTo(DEFAULT_STORE_NUMBER);
        assertThat(testSite.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testSite.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testSite.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testSite.getZip()).isEqualTo(DEFAULT_ZIP);
        assertThat(testSite.getBillingType()).isEqualTo(DEFAULT_BILLING_TYPE);
        assertThat(testSite.getPaymentType()).isEqualTo(DEFAULT_PAYMENT_TYPE);
        assertThat(testSite.getSiteType()).isEqualTo(DEFAULT_SITE_TYPE);
        assertThat(testSite.getOwnerName()).isEqualTo(DEFAULT_OWNER_NAME);
        assertThat(testSite.getOwnerEmail()).isEqualTo(DEFAULT_OWNER_EMAIL);
        assertThat(testSite.getOwnerPhone()).isEqualTo(DEFAULT_OWNER_PHONE);
        assertThat(testSite.getAddress1()).isEqualTo(DEFAULT_ADDRESS_1);
        assertThat(testSite.getAddress2()).isEqualTo(DEFAULT_ADDRESS_2);
        assertThat(testSite.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testSite.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testSite.isTaxExempt()).isEqualTo(DEFAULT_TAX_EXEMPT);
        assertThat(testSite.isRollBilling()).isEqualTo(DEFAULT_ROLL_BILLING);
        assertThat(testSite.getEmoVersion()).isEqualTo(DEFAULT_EMO_VERSION);
        assertThat(testSite.getContactDeskId()).isEqualTo(DEFAULT_CONTACT_DESK_ID);
        assertThat(testSite.getEnableTicketDispatch()).isEqualTo(DEFAULT_ENABLE_TICKET_DISPATCH);

        // Validate the Site in Elasticsearch
        verify(mockSiteSearchRepository, times(1)).save(testSite);
    }

    @Test
    @Transactional
    public void createSiteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site with an existing ID
        site.setId(1L);
        SiteDTO siteDTO = siteMapper.toDto(site);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);

        // Validate the Site in Elasticsearch
        verify(mockSiteSearchRepository, times(0)).save(site);
    }

    @Test
    @Transactional
    public void checkBudderflyIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setBudderflyId(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setStatus(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBillingTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setBillingType(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPaymentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setPaymentType(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setOwnerName(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setOwnerEmail(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setOwnerPhone(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSites() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].budderflyId").value(hasItem(DEFAULT_BUDDERFLY_ID.toString())))
            .andExpect(jsonPath("$.[*].customerName").value(hasItem(DEFAULT_CUSTOMER_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].companyType").value(hasItem(DEFAULT_COMPANY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].storeNumber").value(hasItem(DEFAULT_STORE_NUMBER)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].zip").value(hasItem(DEFAULT_ZIP.toString())))
            .andExpect(jsonPath("$.[*].billingType").value(hasItem(DEFAULT_BILLING_TYPE.toString())))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].siteType").value(hasItem(DEFAULT_SITE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerEmail").value(hasItem(DEFAULT_OWNER_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].ownerPhone").value(hasItem(DEFAULT_OWNER_PHONE.toString())))
            .andExpect(jsonPath("$.[*].address1").value(hasItem(DEFAULT_ADDRESS_1.toString())))
            .andExpect(jsonPath("$.[*].address2").value(hasItem(DEFAULT_ADDRESS_2.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.toString())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.toString())))
            .andExpect(jsonPath("$.[*].taxExempt").value(hasItem(DEFAULT_TAX_EXEMPT.booleanValue())))
            .andExpect(jsonPath("$.[*].rollBilling").value(hasItem(DEFAULT_ROLL_BILLING.booleanValue())))
            .andExpect(jsonPath("$.[*].emoVersion").value(hasItem(DEFAULT_EMO_VERSION.toString())))
            .andExpect(jsonPath("$.[*].contactDeskId").value(hasItem(DEFAULT_CONTACT_DESK_ID.toString())))
            .andExpect(jsonPath("$.[*].enableTicketDispatch").value(hasItem(DEFAULT_ENABLE_TICKET_DISPATCH.booleanValue())));
    }

    @Test
    @Transactional
    public void getSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", site.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(site.getId().intValue()))
            .andExpect(jsonPath("$.budderflyId").value(DEFAULT_BUDDERFLY_ID.toString()))
            .andExpect(jsonPath("$.customerName").value(DEFAULT_CUSTOMER_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.companyType").value(DEFAULT_COMPANY_TYPE.toString()))
            .andExpect(jsonPath("$.storeNumber").value(DEFAULT_STORE_NUMBER))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.zip").value(DEFAULT_ZIP.toString()))
            .andExpect(jsonPath("$.billingType").value(DEFAULT_BILLING_TYPE.toString()))
            .andExpect(jsonPath("$.paymentType").value(DEFAULT_PAYMENT_TYPE.toString()))
            .andExpect(jsonPath("$.siteType").value(DEFAULT_SITE_TYPE.toString()))
            .andExpect(jsonPath("$.ownerName").value(DEFAULT_OWNER_NAME.toString()))
            .andExpect(jsonPath("$.ownerEmail").value(DEFAULT_OWNER_EMAIL.toString()))
            .andExpect(jsonPath("$.ownerPhone").value(DEFAULT_OWNER_PHONE.toString()))
            .andExpect(jsonPath("$.address1").value(DEFAULT_ADDRESS_1.toString()))
            .andExpect(jsonPath("$.address2").value(DEFAULT_ADDRESS_2.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.toString()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.toString()))
            .andExpect(jsonPath("$.taxExempt").value(DEFAULT_TAX_EXEMPT.booleanValue()))
            .andExpect(jsonPath("$.rollBilling").value(DEFAULT_ROLL_BILLING.booleanValue()))
            .andExpect(jsonPath("$.emoVersion").value(DEFAULT_EMO_VERSION.toString()))
            .andExpect(jsonPath("$.contactDeskId").value(DEFAULT_CONTACT_DESK_ID.toString()))
            .andExpect(jsonPath("$.enableTicketDispatch").value(DEFAULT_ENABLE_TICKET_DISPATCH.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllSitesByBudderflyIdIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where budderflyId equals to DEFAULT_BUDDERFLY_ID
        defaultSiteShouldBeFound("budderflyId.equals=" + DEFAULT_BUDDERFLY_ID);

        // Get all the siteList where budderflyId equals to UPDATED_BUDDERFLY_ID
        defaultSiteShouldNotBeFound("budderflyId.equals=" + UPDATED_BUDDERFLY_ID);
    }

    @Test
    @Transactional
    public void getAllSitesByBudderflyIdIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where budderflyId in DEFAULT_BUDDERFLY_ID or UPDATED_BUDDERFLY_ID
        defaultSiteShouldBeFound("budderflyId.in=" + DEFAULT_BUDDERFLY_ID + "," + UPDATED_BUDDERFLY_ID);

        // Get all the siteList where budderflyId equals to UPDATED_BUDDERFLY_ID
        defaultSiteShouldNotBeFound("budderflyId.in=" + UPDATED_BUDDERFLY_ID);
    }

    @Test
    @Transactional
    public void getAllSitesByBudderflyIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where budderflyId is not null
        defaultSiteShouldBeFound("budderflyId.specified=true");

        // Get all the siteList where budderflyId is null
        defaultSiteShouldNotBeFound("budderflyId.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByCustomerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where customerName equals to DEFAULT_CUSTOMER_NAME
        defaultSiteShouldBeFound("customerName.equals=" + DEFAULT_CUSTOMER_NAME);

        // Get all the siteList where customerName equals to UPDATED_CUSTOMER_NAME
        defaultSiteShouldNotBeFound("customerName.equals=" + UPDATED_CUSTOMER_NAME);
    }

    @Test
    @Transactional
    public void getAllSitesByCustomerNameIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where customerName in DEFAULT_CUSTOMER_NAME or UPDATED_CUSTOMER_NAME
        defaultSiteShouldBeFound("customerName.in=" + DEFAULT_CUSTOMER_NAME + "," + UPDATED_CUSTOMER_NAME);

        // Get all the siteList where customerName equals to UPDATED_CUSTOMER_NAME
        defaultSiteShouldNotBeFound("customerName.in=" + UPDATED_CUSTOMER_NAME);
    }

    @Test
    @Transactional
    public void getAllSitesByCustomerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where customerName is not null
        defaultSiteShouldBeFound("customerName.specified=true");

        // Get all the siteList where customerName is null
        defaultSiteShouldNotBeFound("customerName.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where status equals to DEFAULT_STATUS
        defaultSiteShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the siteList where status equals to UPDATED_STATUS
        defaultSiteShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllSitesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSiteShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the siteList where status equals to UPDATED_STATUS
        defaultSiteShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllSitesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where status is not null
        defaultSiteShouldBeFound("status.specified=true");

        // Get all the siteList where status is null
        defaultSiteShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByCompanyTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where companyType equals to DEFAULT_COMPANY_TYPE
        defaultSiteShouldBeFound("companyType.equals=" + DEFAULT_COMPANY_TYPE);

        // Get all the siteList where companyType equals to UPDATED_COMPANY_TYPE
        defaultSiteShouldNotBeFound("companyType.equals=" + UPDATED_COMPANY_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesByCompanyTypeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where companyType in DEFAULT_COMPANY_TYPE or UPDATED_COMPANY_TYPE
        defaultSiteShouldBeFound("companyType.in=" + DEFAULT_COMPANY_TYPE + "," + UPDATED_COMPANY_TYPE);

        // Get all the siteList where companyType equals to UPDATED_COMPANY_TYPE
        defaultSiteShouldNotBeFound("companyType.in=" + UPDATED_COMPANY_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesByCompanyTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where companyType is not null
        defaultSiteShouldBeFound("companyType.specified=true");

        // Get all the siteList where companyType is null
        defaultSiteShouldNotBeFound("companyType.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByStoreNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where storeNumber equals to DEFAULT_STORE_NUMBER
        defaultSiteShouldBeFound("storeNumber.equals=" + DEFAULT_STORE_NUMBER);

        // Get all the siteList where storeNumber equals to UPDATED_STORE_NUMBER
        defaultSiteShouldNotBeFound("storeNumber.equals=" + UPDATED_STORE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllSitesByStoreNumberIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where storeNumber in DEFAULT_STORE_NUMBER or UPDATED_STORE_NUMBER
        defaultSiteShouldBeFound("storeNumber.in=" + DEFAULT_STORE_NUMBER + "," + UPDATED_STORE_NUMBER);

        // Get all the siteList where storeNumber equals to UPDATED_STORE_NUMBER
        defaultSiteShouldNotBeFound("storeNumber.in=" + UPDATED_STORE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllSitesByStoreNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where storeNumber is not null
        defaultSiteShouldBeFound("storeNumber.specified=true");

        // Get all the siteList where storeNumber is null
        defaultSiteShouldNotBeFound("storeNumber.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByStoreNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where storeNumber greater than or equals to DEFAULT_STORE_NUMBER
        defaultSiteShouldBeFound("storeNumber.greaterOrEqualThan=" + DEFAULT_STORE_NUMBER);

        // Get all the siteList where storeNumber greater than or equals to UPDATED_STORE_NUMBER
        defaultSiteShouldNotBeFound("storeNumber.greaterOrEqualThan=" + UPDATED_STORE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllSitesByStoreNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where storeNumber less than or equals to DEFAULT_STORE_NUMBER
        defaultSiteShouldNotBeFound("storeNumber.lessThan=" + DEFAULT_STORE_NUMBER);

        // Get all the siteList where storeNumber less than or equals to UPDATED_STORE_NUMBER
        defaultSiteShouldBeFound("storeNumber.lessThan=" + UPDATED_STORE_NUMBER);
    }


    @Test
    @Transactional
    public void getAllSitesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address equals to DEFAULT_ADDRESS
        defaultSiteShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the siteList where address equals to UPDATED_ADDRESS
        defaultSiteShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllSitesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultSiteShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the siteList where address equals to UPDATED_ADDRESS
        defaultSiteShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllSitesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address is not null
        defaultSiteShouldBeFound("address.specified=true");

        // Get all the siteList where address is null
        defaultSiteShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where city equals to DEFAULT_CITY
        defaultSiteShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the siteList where city equals to UPDATED_CITY
        defaultSiteShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    public void getAllSitesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where city in DEFAULT_CITY or UPDATED_CITY
        defaultSiteShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the siteList where city equals to UPDATED_CITY
        defaultSiteShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    public void getAllSitesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where city is not null
        defaultSiteShouldBeFound("city.specified=true");

        // Get all the siteList where city is null
        defaultSiteShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where state equals to DEFAULT_STATE
        defaultSiteShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the siteList where state equals to UPDATED_STATE
        defaultSiteShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllSitesByStateIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where state in DEFAULT_STATE or UPDATED_STATE
        defaultSiteShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the siteList where state equals to UPDATED_STATE
        defaultSiteShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllSitesByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where state is not null
        defaultSiteShouldBeFound("state.specified=true");

        // Get all the siteList where state is null
        defaultSiteShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByZipIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where zip equals to DEFAULT_ZIP
        defaultSiteShouldBeFound("zip.equals=" + DEFAULT_ZIP);

        // Get all the siteList where zip equals to UPDATED_ZIP
        defaultSiteShouldNotBeFound("zip.equals=" + UPDATED_ZIP);
    }

    @Test
    @Transactional
    public void getAllSitesByZipIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where zip in DEFAULT_ZIP or UPDATED_ZIP
        defaultSiteShouldBeFound("zip.in=" + DEFAULT_ZIP + "," + UPDATED_ZIP);

        // Get all the siteList where zip equals to UPDATED_ZIP
        defaultSiteShouldNotBeFound("zip.in=" + UPDATED_ZIP);
    }

    @Test
    @Transactional
    public void getAllSitesByZipIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where zip is not null
        defaultSiteShouldBeFound("zip.specified=true");

        // Get all the siteList where zip is null
        defaultSiteShouldNotBeFound("zip.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByBillingTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where billingType equals to DEFAULT_BILLING_TYPE
        defaultSiteShouldBeFound("billingType.equals=" + DEFAULT_BILLING_TYPE);

        // Get all the siteList where billingType equals to UPDATED_BILLING_TYPE
        defaultSiteShouldNotBeFound("billingType.equals=" + UPDATED_BILLING_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesByBillingTypeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where billingType in DEFAULT_BILLING_TYPE or UPDATED_BILLING_TYPE
        defaultSiteShouldBeFound("billingType.in=" + DEFAULT_BILLING_TYPE + "," + UPDATED_BILLING_TYPE);

        // Get all the siteList where billingType equals to UPDATED_BILLING_TYPE
        defaultSiteShouldNotBeFound("billingType.in=" + UPDATED_BILLING_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesByBillingTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where billingType is not null
        defaultSiteShouldBeFound("billingType.specified=true");

        // Get all the siteList where billingType is null
        defaultSiteShouldNotBeFound("billingType.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByPaymentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where paymentType equals to DEFAULT_PAYMENT_TYPE
        defaultSiteShouldBeFound("paymentType.equals=" + DEFAULT_PAYMENT_TYPE);

        // Get all the siteList where paymentType equals to UPDATED_PAYMENT_TYPE
        defaultSiteShouldNotBeFound("paymentType.equals=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesByPaymentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where paymentType in DEFAULT_PAYMENT_TYPE or UPDATED_PAYMENT_TYPE
        defaultSiteShouldBeFound("paymentType.in=" + DEFAULT_PAYMENT_TYPE + "," + UPDATED_PAYMENT_TYPE);

        // Get all the siteList where paymentType equals to UPDATED_PAYMENT_TYPE
        defaultSiteShouldNotBeFound("paymentType.in=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesByPaymentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where paymentType is not null
        defaultSiteShouldBeFound("paymentType.specified=true");

        // Get all the siteList where paymentType is null
        defaultSiteShouldNotBeFound("paymentType.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesBySiteTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where siteType equals to DEFAULT_SITE_TYPE
        defaultSiteShouldBeFound("siteType.equals=" + DEFAULT_SITE_TYPE);

        // Get all the siteList where siteType equals to UPDATED_SITE_TYPE
        defaultSiteShouldNotBeFound("siteType.equals=" + UPDATED_SITE_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesBySiteTypeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where siteType in DEFAULT_SITE_TYPE or UPDATED_SITE_TYPE
        defaultSiteShouldBeFound("siteType.in=" + DEFAULT_SITE_TYPE + "," + UPDATED_SITE_TYPE);

        // Get all the siteList where siteType equals to UPDATED_SITE_TYPE
        defaultSiteShouldNotBeFound("siteType.in=" + UPDATED_SITE_TYPE);
    }

    @Test
    @Transactional
    public void getAllSitesBySiteTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where siteType is not null
        defaultSiteShouldBeFound("siteType.specified=true");

        // Get all the siteList where siteType is null
        defaultSiteShouldNotBeFound("siteType.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerName equals to DEFAULT_OWNER_NAME
        defaultSiteShouldBeFound("ownerName.equals=" + DEFAULT_OWNER_NAME);

        // Get all the siteList where ownerName equals to UPDATED_OWNER_NAME
        defaultSiteShouldNotBeFound("ownerName.equals=" + UPDATED_OWNER_NAME);
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerNameIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerName in DEFAULT_OWNER_NAME or UPDATED_OWNER_NAME
        defaultSiteShouldBeFound("ownerName.in=" + DEFAULT_OWNER_NAME + "," + UPDATED_OWNER_NAME);

        // Get all the siteList where ownerName equals to UPDATED_OWNER_NAME
        defaultSiteShouldNotBeFound("ownerName.in=" + UPDATED_OWNER_NAME);
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerName is not null
        defaultSiteShouldBeFound("ownerName.specified=true");

        // Get all the siteList where ownerName is null
        defaultSiteShouldNotBeFound("ownerName.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerEmail equals to DEFAULT_OWNER_EMAIL
        defaultSiteShouldBeFound("ownerEmail.equals=" + DEFAULT_OWNER_EMAIL);

        // Get all the siteList where ownerEmail equals to UPDATED_OWNER_EMAIL
        defaultSiteShouldNotBeFound("ownerEmail.equals=" + UPDATED_OWNER_EMAIL);
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerEmailIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerEmail in DEFAULT_OWNER_EMAIL or UPDATED_OWNER_EMAIL
        defaultSiteShouldBeFound("ownerEmail.in=" + DEFAULT_OWNER_EMAIL + "," + UPDATED_OWNER_EMAIL);

        // Get all the siteList where ownerEmail equals to UPDATED_OWNER_EMAIL
        defaultSiteShouldNotBeFound("ownerEmail.in=" + UPDATED_OWNER_EMAIL);
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerEmail is not null
        defaultSiteShouldBeFound("ownerEmail.specified=true");

        // Get all the siteList where ownerEmail is null
        defaultSiteShouldNotBeFound("ownerEmail.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerPhone equals to DEFAULT_OWNER_PHONE
        defaultSiteShouldBeFound("ownerPhone.equals=" + DEFAULT_OWNER_PHONE);

        // Get all the siteList where ownerPhone equals to UPDATED_OWNER_PHONE
        defaultSiteShouldNotBeFound("ownerPhone.equals=" + UPDATED_OWNER_PHONE);
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerPhone in DEFAULT_OWNER_PHONE or UPDATED_OWNER_PHONE
        defaultSiteShouldBeFound("ownerPhone.in=" + DEFAULT_OWNER_PHONE + "," + UPDATED_OWNER_PHONE);

        // Get all the siteList where ownerPhone equals to UPDATED_OWNER_PHONE
        defaultSiteShouldNotBeFound("ownerPhone.in=" + UPDATED_OWNER_PHONE);
    }

    @Test
    @Transactional
    public void getAllSitesByOwnerPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where ownerPhone is not null
        defaultSiteShouldBeFound("ownerPhone.specified=true");

        // Get all the siteList where ownerPhone is null
        defaultSiteShouldNotBeFound("ownerPhone.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByAddress1IsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address1 equals to DEFAULT_ADDRESS_1
        defaultSiteShouldBeFound("address1.equals=" + DEFAULT_ADDRESS_1);

        // Get all the siteList where address1 equals to UPDATED_ADDRESS_1
        defaultSiteShouldNotBeFound("address1.equals=" + UPDATED_ADDRESS_1);
    }

    @Test
    @Transactional
    public void getAllSitesByAddress1IsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address1 in DEFAULT_ADDRESS_1 or UPDATED_ADDRESS_1
        defaultSiteShouldBeFound("address1.in=" + DEFAULT_ADDRESS_1 + "," + UPDATED_ADDRESS_1);

        // Get all the siteList where address1 equals to UPDATED_ADDRESS_1
        defaultSiteShouldNotBeFound("address1.in=" + UPDATED_ADDRESS_1);
    }

    @Test
    @Transactional
    public void getAllSitesByAddress1IsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address1 is not null
        defaultSiteShouldBeFound("address1.specified=true");

        // Get all the siteList where address1 is null
        defaultSiteShouldNotBeFound("address1.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByAddress2IsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address2 equals to DEFAULT_ADDRESS_2
        defaultSiteShouldBeFound("address2.equals=" + DEFAULT_ADDRESS_2);

        // Get all the siteList where address2 equals to UPDATED_ADDRESS_2
        defaultSiteShouldNotBeFound("address2.equals=" + UPDATED_ADDRESS_2);
    }

    @Test
    @Transactional
    public void getAllSitesByAddress2IsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address2 in DEFAULT_ADDRESS_2 or UPDATED_ADDRESS_2
        defaultSiteShouldBeFound("address2.in=" + DEFAULT_ADDRESS_2 + "," + UPDATED_ADDRESS_2);

        // Get all the siteList where address2 equals to UPDATED_ADDRESS_2
        defaultSiteShouldNotBeFound("address2.in=" + UPDATED_ADDRESS_2);
    }

    @Test
    @Transactional
    public void getAllSitesByAddress2IsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where address2 is not null
        defaultSiteShouldBeFound("address2.specified=true");

        // Get all the siteList where address2 is null
        defaultSiteShouldNotBeFound("address2.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where latitude equals to DEFAULT_LATITUDE
        defaultSiteShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the siteList where latitude equals to UPDATED_LATITUDE
        defaultSiteShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllSitesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultSiteShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the siteList where latitude equals to UPDATED_LATITUDE
        defaultSiteShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllSitesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where latitude is not null
        defaultSiteShouldBeFound("latitude.specified=true");

        // Get all the siteList where latitude is null
        defaultSiteShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where longitude equals to DEFAULT_LONGITUDE
        defaultSiteShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the siteList where longitude equals to UPDATED_LONGITUDE
        defaultSiteShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllSitesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultSiteShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the siteList where longitude equals to UPDATED_LONGITUDE
        defaultSiteShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllSitesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where longitude is not null
        defaultSiteShouldBeFound("longitude.specified=true");

        // Get all the siteList where longitude is null
        defaultSiteShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByTaxExemptIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where taxExempt equals to DEFAULT_TAX_EXEMPT
        defaultSiteShouldBeFound("taxExempt.equals=" + DEFAULT_TAX_EXEMPT);

        // Get all the siteList where taxExempt equals to UPDATED_TAX_EXEMPT
        defaultSiteShouldNotBeFound("taxExempt.equals=" + UPDATED_TAX_EXEMPT);
    }

    @Test
    @Transactional
    public void getAllSitesByTaxExemptIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where taxExempt in DEFAULT_TAX_EXEMPT or UPDATED_TAX_EXEMPT
        defaultSiteShouldBeFound("taxExempt.in=" + DEFAULT_TAX_EXEMPT + "," + UPDATED_TAX_EXEMPT);

        // Get all the siteList where taxExempt equals to UPDATED_TAX_EXEMPT
        defaultSiteShouldNotBeFound("taxExempt.in=" + UPDATED_TAX_EXEMPT);
    }

    @Test
    @Transactional
    public void getAllSitesByTaxExemptIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where taxExempt is not null
        defaultSiteShouldBeFound("taxExempt.specified=true");

        // Get all the siteList where taxExempt is null
        defaultSiteShouldNotBeFound("taxExempt.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByRollBillingIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where rollBilling equals to DEFAULT_ROLL_BILLING
        defaultSiteShouldBeFound("rollBilling.equals=" + DEFAULT_ROLL_BILLING);

        // Get all the siteList where rollBilling equals to UPDATED_ROLL_BILLING
        defaultSiteShouldNotBeFound("rollBilling.equals=" + UPDATED_ROLL_BILLING);
    }

    @Test
    @Transactional
    public void getAllSitesByRollBillingIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where rollBilling in DEFAULT_ROLL_BILLING or UPDATED_ROLL_BILLING
        defaultSiteShouldBeFound("rollBilling.in=" + DEFAULT_ROLL_BILLING + "," + UPDATED_ROLL_BILLING);

        // Get all the siteList where rollBilling equals to UPDATED_ROLL_BILLING
        defaultSiteShouldNotBeFound("rollBilling.in=" + UPDATED_ROLL_BILLING);
    }

    @Test
    @Transactional
    public void getAllSitesByRollBillingIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where rollBilling is not null
        defaultSiteShouldBeFound("rollBilling.specified=true");

        // Get all the siteList where rollBilling is null
        defaultSiteShouldNotBeFound("rollBilling.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByEmoVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where emoVersion equals to DEFAULT_EMO_VERSION
        defaultSiteShouldBeFound("emoVersion.equals=" + DEFAULT_EMO_VERSION);

        // Get all the siteList where emoVersion equals to UPDATED_EMO_VERSION
        defaultSiteShouldNotBeFound("emoVersion.equals=" + UPDATED_EMO_VERSION);
    }

    @Test
    @Transactional
    public void getAllSitesByEmoVersionIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where emoVersion in DEFAULT_EMO_VERSION or UPDATED_EMO_VERSION
        defaultSiteShouldBeFound("emoVersion.in=" + DEFAULT_EMO_VERSION + "," + UPDATED_EMO_VERSION);

        // Get all the siteList where emoVersion equals to UPDATED_EMO_VERSION
        defaultSiteShouldNotBeFound("emoVersion.in=" + UPDATED_EMO_VERSION);
    }

    @Test
    @Transactional
    public void getAllSitesByEmoVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where emoVersion is not null
        defaultSiteShouldBeFound("emoVersion.specified=true");

        // Get all the siteList where emoVersion is null
        defaultSiteShouldNotBeFound("emoVersion.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByEnableTicketDispatchIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where enableTicketDispatch equals to DEFAULT_ENABLE_TICKET_DISPATCH
        defaultSiteShouldBeFound("enableTicketDispatch.equals=" + DEFAULT_ENABLE_TICKET_DISPATCH);

        // Get all the siteList where enableTicketDispatch equals to UPDATED_ENABLE_TICKET_DISPATCH
        defaultSiteShouldNotBeFound("enableTicketDispatch.equals=" + UPDATED_ENABLE_TICKET_DISPATCH);
    }

    @Test
    @Transactional
    public void getAllSitesByEnableTicketDispatchIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where enableTicketDispatch in DEFAULT_ENABLE_TICKET_DISPATCH or UPDATED_ENABLE_TICKET_DISPATCH
        defaultSiteShouldBeFound("enableTicketDispatch.in=" + DEFAULT_ENABLE_TICKET_DISPATCH + "," + UPDATED_ENABLE_TICKET_DISPATCH);

        // Get all the siteList where enableTicketDispatch equals to UPDATED_ENABLE_TICKET_DISPATCH
        defaultSiteShouldNotBeFound("enableTicketDispatch.in=" + UPDATED_ENABLE_TICKET_DISPATCH);
    }

    @Test
    @Transactional
    public void getAllSitesByEnableTicketDispatchIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where enableTicketDispatch is not null
        defaultSiteShouldBeFound("enableTicketDispatch.specified=true");

        // Get all the siteList where enableTicketDispatch is null
        defaultSiteShouldNotBeFound("enableTicketDispatch.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByParentSiteIsEqualToSomething() throws Exception {
        // Initialize the database
        Site parentSite = SiteResourceIntTest.createEntity(em);
        em.persist(parentSite);
        em.flush();
        site.setParentSite(parentSite);
        siteRepository.saveAndFlush(site);
        Long parentSiteId = parentSite.getId();

        // Get all the siteList where parentSite equals to parentSiteId
        defaultSiteShouldBeFound("parentSiteId.equals=" + parentSiteId);

        // Get all the siteList where parentSite equals to parentSiteId + 1
        defaultSiteShouldNotBeFound("parentSiteId.equals=" + (parentSiteId + 1));
    }

    @Test
    @Transactional
    public void getAllSitesByContactDeskIdIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where contactDeskId equals to DEFAULT_CONTACT_DESK_ID
        defaultSiteShouldBeFound("contactDeskId.equals=" + DEFAULT_CONTACT_DESK_ID);

        // Get all the siteList where contactDeskId equals to UPDATED_CONTACT_DESK_ID
        defaultSiteShouldNotBeFound("contactDeskId.equals=" + UPDATED_CONTACT_DESK_ID);
    }

    @Test
    @Transactional
    public void getAllSitesByContactDeskIdIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where contactDeskId in DEFAULT_CONTACT_DESK_ID or UPDATED_CONTACT_DESK_ID
        defaultSiteShouldBeFound("contactDeskId.in=" + DEFAULT_CONTACT_DESK_ID + "," + UPDATED_CONTACT_DESK_ID);

        // Get all the siteList where contactDeskId equals to UPDATED_CONTACT_DESK_ID
        defaultSiteShouldNotBeFound("contactDeskId.in=" + UPDATED_CONTACT_DESK_ID);
    }

    @Test
    @Transactional
    public void getAllSitesByContactDeskIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where contactDeskId is not null
        defaultSiteShouldBeFound("contactDeskId.specified=true");

        // Get all the siteList where contactDeskId is null
        defaultSiteShouldNotBeFound("contactDeskId.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSiteShouldBeFound(String filter) throws Exception {
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].budderflyId").value(hasItem(DEFAULT_BUDDERFLY_ID.toString())))
            .andExpect(jsonPath("$.[*].customerName").value(hasItem(DEFAULT_CUSTOMER_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].companyType").value(hasItem(DEFAULT_COMPANY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].storeNumber").value(hasItem(DEFAULT_STORE_NUMBER)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].zip").value(hasItem(DEFAULT_ZIP.toString())))
            .andExpect(jsonPath("$.[*].billingType").value(hasItem(DEFAULT_BILLING_TYPE.toString())))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].siteType").value(hasItem(DEFAULT_SITE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerEmail").value(hasItem(DEFAULT_OWNER_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].ownerPhone").value(hasItem(DEFAULT_OWNER_PHONE.toString())))
            .andExpect(jsonPath("$.[*].address1").value(hasItem(DEFAULT_ADDRESS_1.toString())))
            .andExpect(jsonPath("$.[*].address2").value(hasItem(DEFAULT_ADDRESS_2.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.toString())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.toString())))
            .andExpect(jsonPath("$.[*].taxExempt").value(hasItem(DEFAULT_TAX_EXEMPT.booleanValue())))
            .andExpect(jsonPath("$.[*].rollBilling").value(hasItem(DEFAULT_ROLL_BILLING.booleanValue())))
            .andExpect(jsonPath("$.[*].emoVersion").value(hasItem(DEFAULT_EMO_VERSION.toString())))
            .andExpect(jsonPath("$.[*].contactDeskId").value(hasItem(DEFAULT_CONTACT_DESK_ID.toString())));

        // Check, that the count call also returns 1
        restSiteMockMvc.perform(get("/api/sites/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSiteShouldNotBeFound(String filter) throws Exception {
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSiteMockMvc.perform(get("/api/sites/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSite() throws Exception {
        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site
        Site updatedSite = siteRepository.findById(site.getId()).get();
        // Disconnect from session so that the updates on updatedSite are not directly saved in db
        em.detach(updatedSite);
        updatedSite
            .budderflyId(UPDATED_BUDDERFLY_ID)
            .customerName(UPDATED_CUSTOMER_NAME)
            .status(UPDATED_STATUS)
            .companyType(UPDATED_COMPANY_TYPE)
            .storeNumber(UPDATED_STORE_NUMBER)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .zip(UPDATED_ZIP)
            .billingType(UPDATED_BILLING_TYPE)
            .paymentType(UPDATED_PAYMENT_TYPE)
            .siteType(UPDATED_SITE_TYPE)
            .ownerName(UPDATED_OWNER_NAME)
            .ownerEmail(UPDATED_OWNER_EMAIL)
            .ownerPhone(UPDATED_OWNER_PHONE)
            .address1(UPDATED_ADDRESS_1)
            .address2(UPDATED_ADDRESS_2)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .taxExempt(UPDATED_TAX_EXEMPT)
            .rollBilling(UPDATED_ROLL_BILLING)
            .emoVersion(UPDATED_EMO_VERSION)
            .contactDeskId(UPDATED_CONTACT_DESK_ID)
            .enableTicketDispatch(UPDATED_ENABLE_TICKET_DISPATCH);
        SiteDTO siteDTO = siteMapper.toDto(updatedSite);

        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getBudderflyId()).isEqualTo(UPDATED_BUDDERFLY_ID);
        assertThat(testSite.getCustomerName()).isEqualTo(UPDATED_CUSTOMER_NAME);
        assertThat(testSite.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSite.getCompanyType()).isEqualTo(UPDATED_COMPANY_TYPE);
        assertThat(testSite.getStoreNumber()).isEqualTo(UPDATED_STORE_NUMBER);
        assertThat(testSite.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testSite.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testSite.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testSite.getZip()).isEqualTo(UPDATED_ZIP);
        assertThat(testSite.getBillingType()).isEqualTo(UPDATED_BILLING_TYPE);
        assertThat(testSite.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);
        assertThat(testSite.getSiteType()).isEqualTo(UPDATED_SITE_TYPE);
        assertThat(testSite.getOwnerName()).isEqualTo(UPDATED_OWNER_NAME);
        assertThat(testSite.getOwnerEmail()).isEqualTo(UPDATED_OWNER_EMAIL);
        assertThat(testSite.getOwnerPhone()).isEqualTo(UPDATED_OWNER_PHONE);
        assertThat(testSite.getAddress1()).isEqualTo(UPDATED_ADDRESS_1);
        assertThat(testSite.getAddress2()).isEqualTo(UPDATED_ADDRESS_2);
        assertThat(testSite.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testSite.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testSite.isTaxExempt()).isEqualTo(UPDATED_TAX_EXEMPT);
        assertThat(testSite.isRollBilling()).isEqualTo(UPDATED_ROLL_BILLING);
        assertThat(testSite.getEmoVersion()).isEqualTo(UPDATED_EMO_VERSION);
        assertThat(testSite.getContactDeskId()).isEqualTo(UPDATED_CONTACT_DESK_ID);
        assertThat(testSite.getEnableTicketDispatch()).isEqualTo(UPDATED_ENABLE_TICKET_DISPATCH);

        // Validate the Site in Elasticsearch
        verify(mockSiteSearchRepository, times(1)).save(testSite);
    }

    @Test
    @Transactional
    public void updateNonExistingSite() throws Exception {
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Site in Elasticsearch
        verify(mockSiteSearchRepository, times(0)).save(site);
    }

    @Test
    @Transactional
    public void deleteSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        int databaseSizeBeforeDelete = siteRepository.findAll().size();

        // Get the site
        restSiteMockMvc.perform(delete("/api/sites/{id}", site.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Site in Elasticsearch
        verify(mockSiteSearchRepository, times(1)).deleteById(site.getId());
    }

    @Test
    @Transactional
    public void searchSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        when(mockSiteSearchRepository.search(queryStringQuery("id:" + site.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(site), PageRequest.of(0, 1), 1));
        // Search the site
        restSiteMockMvc.perform(get("/api/_search/sites?query=id:" + site.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].budderflyId").value(hasItem(DEFAULT_BUDDERFLY_ID)))
            .andExpect(jsonPath("$.[*].customerName").value(hasItem(DEFAULT_CUSTOMER_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].companyType").value(hasItem(DEFAULT_COMPANY_TYPE)))
            .andExpect(jsonPath("$.[*].storeNumber").value(hasItem(DEFAULT_STORE_NUMBER)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].zip").value(hasItem(DEFAULT_ZIP)))
            .andExpect(jsonPath("$.[*].billingType").value(hasItem(DEFAULT_BILLING_TYPE.toString())))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].siteType").value(hasItem(DEFAULT_SITE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME)))
            .andExpect(jsonPath("$.[*].ownerEmail").value(hasItem(DEFAULT_OWNER_EMAIL)))
            .andExpect(jsonPath("$.[*].ownerPhone").value(hasItem(DEFAULT_OWNER_PHONE)))
            .andExpect(jsonPath("$.[*].address1").value(hasItem(DEFAULT_ADDRESS_1)))
            .andExpect(jsonPath("$.[*].address2").value(hasItem(DEFAULT_ADDRESS_2)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].taxExempt").value(hasItem(DEFAULT_TAX_EXEMPT.booleanValue())))
            .andExpect(jsonPath("$.[*].rollBilling").value(hasItem(DEFAULT_ROLL_BILLING.booleanValue())))
            .andExpect(jsonPath("$.[*].emoVersion").value(hasItem(DEFAULT_EMO_VERSION.toString())))
            .andExpect(jsonPath("$.[*].contactDeskId").value(hasItem(DEFAULT_CONTACT_DESK_ID)))
            .andExpect(jsonPath("$.[*].enableTicketDispatch").value(hasItem(DEFAULT_ENABLE_TICKET_DISPATCH.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);
        Site site1 = new Site();
        site1.setId(1L);
        Site site2 = new Site();
        site2.setId(site1.getId());
        assertThat(site1).isEqualTo(site2);
        site2.setId(2L);
        assertThat(site1).isNotEqualTo(site2);
        site1.setId(null);
        assertThat(site1).isNotEqualTo(site2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SiteDTO.class);
        SiteDTO siteDTO1 = new SiteDTO();
        siteDTO1.setId(1L);
        SiteDTO siteDTO2 = new SiteDTO();
        assertThat(siteDTO1).isNotEqualTo(siteDTO2);
        siteDTO2.setId(siteDTO1.getId());
        assertThat(siteDTO1).isEqualTo(siteDTO2);
        siteDTO2.setId(2L);
        assertThat(siteDTO1).isNotEqualTo(siteDTO2);
        siteDTO1.setId(null);
        assertThat(siteDTO1).isNotEqualTo(siteDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(siteMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(siteMapper.fromId(null)).isNull();
    }
}
