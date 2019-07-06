package com.budderfly.sites.web.rest;

import com.budderfly.sites.SitesApp;

import com.budderfly.sites.config.SecurityBeanOverrideConfiguration;

import com.budderfly.sites.domain.SiteDiscount;
import com.budderfly.sites.domain.Site;
import com.budderfly.sites.repository.SiteDiscountRepository;
import com.budderfly.sites.repository.search.SiteDiscountSearchRepository;
import com.budderfly.sites.service.SiteDiscountService;
import com.budderfly.sites.service.dto.SiteDiscountDTO;
import com.budderfly.sites.service.mapper.SiteDiscountMapper;
import com.budderfly.sites.web.rest.errors.ExceptionTranslator;
import com.budderfly.sites.service.dto.SiteDiscountCriteria;
import com.budderfly.sites.service.SiteDiscountQueryService;

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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


import static com.budderfly.sites.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SiteDiscountResource REST controller.
 *
 * @see SiteDiscountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, SitesApp.class})
public class SiteDiscountResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PERCENTAGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PERCENTAGE = new BigDecimal(2);

    private static final Boolean DEFAULT_AUTO_UPDATE = false;
    private static final Boolean UPDATED_AUTO_UPDATE = true;

    private static final Boolean DEFAULT_ACCRUED = false;
    private static final Boolean UPDATED_ACCRUED = true;

    private static final Boolean DEFAULT_OVERRIDE = false;
    private static final Boolean UPDATED_OVERRIDE = true;

    @Autowired
    private SiteDiscountRepository siteDiscountRepository;

    @Autowired
    private SiteDiscountMapper siteDiscountMapper;

    @Autowired
    private SiteDiscountService siteDiscountService;

    /**
     * This repository is mocked in the com.budderfly.sites.repository.search test package.
     *
     * @see com.budderfly.sites.repository.search.SiteDiscountSearchRepositoryMockConfiguration
     */
    @Autowired
    private SiteDiscountSearchRepository mockSiteDiscountSearchRepository;

    @Autowired
    private SiteDiscountQueryService siteDiscountQueryService;

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

    private MockMvc restSiteDiscountMockMvc;

    private SiteDiscount siteDiscount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SiteDiscountResource siteDiscountResource = new SiteDiscountResource(siteDiscountService, siteDiscountQueryService);
        this.restSiteDiscountMockMvc = MockMvcBuilders.standaloneSetup(siteDiscountResource)
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
    public static SiteDiscount createEntity(EntityManager em) {
        SiteDiscount siteDiscount = new SiteDiscount()
            .name(DEFAULT_NAME)
            .percentage(DEFAULT_PERCENTAGE)
            .autoUpdate(DEFAULT_AUTO_UPDATE)
            .accrued(DEFAULT_ACCRUED)
            .override(DEFAULT_OVERRIDE);
        return siteDiscount;
    }

    @Before
    public void initTest() {
        siteDiscount = createEntity(em);
    }

    @Test
    @Transactional
    public void createSiteDiscount() throws Exception {
        int databaseSizeBeforeCreate = siteDiscountRepository.findAll().size();

        // Create the SiteDiscount
        SiteDiscountDTO siteDiscountDTO = siteDiscountMapper.toDto(siteDiscount);
        restSiteDiscountMockMvc.perform(post("/api/site-discounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDiscountDTO)))
            .andExpect(status().isCreated());

        // Validate the SiteDiscount in the database
        List<SiteDiscount> siteDiscountList = siteDiscountRepository.findAll();
        assertThat(siteDiscountList).hasSize(databaseSizeBeforeCreate + 1);
        SiteDiscount testSiteDiscount = siteDiscountList.get(siteDiscountList.size() - 1);
        assertThat(testSiteDiscount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSiteDiscount.getPercentage()).isEqualTo(DEFAULT_PERCENTAGE);
        assertThat(testSiteDiscount.isAutoUpdate()).isEqualTo(DEFAULT_AUTO_UPDATE);
        assertThat(testSiteDiscount.isAccrued()).isEqualTo(DEFAULT_ACCRUED);
        assertThat(testSiteDiscount.isOverride()).isEqualTo(DEFAULT_OVERRIDE);

        // Validate the SiteDiscount in Elasticsearch
        verify(mockSiteDiscountSearchRepository, times(1)).save(testSiteDiscount);
    }

    @Test
    @Transactional
    public void createSiteDiscountWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = siteDiscountRepository.findAll().size();

        // Create the SiteDiscount with an existing ID
        siteDiscount.setId(1L);
        SiteDiscountDTO siteDiscountDTO = siteDiscountMapper.toDto(siteDiscount);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteDiscountMockMvc.perform(post("/api/site-discounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDiscountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SiteDiscount in the database
        List<SiteDiscount> siteDiscountList = siteDiscountRepository.findAll();
        assertThat(siteDiscountList).hasSize(databaseSizeBeforeCreate);

        // Validate the SiteDiscount in Elasticsearch
        verify(mockSiteDiscountSearchRepository, times(0)).save(siteDiscount);
    }

    @Test
    @Transactional
    public void getAllSiteDiscounts() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList
        restSiteDiscountMockMvc.perform(get("/api/site-discounts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(siteDiscount.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].percentage").value(hasItem(DEFAULT_PERCENTAGE.intValue())))
            .andExpect(jsonPath("$.[*].autoUpdate").value(hasItem(DEFAULT_AUTO_UPDATE.booleanValue())))
            .andExpect(jsonPath("$.[*].accrued").value(hasItem(DEFAULT_ACCRUED.booleanValue())))
            .andExpect(jsonPath("$.[*].override").value(hasItem(DEFAULT_OVERRIDE.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getSiteDiscount() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get the siteDiscount
        restSiteDiscountMockMvc.perform(get("/api/site-discounts/{id}", siteDiscount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(siteDiscount.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.percentage").value(DEFAULT_PERCENTAGE.intValue()))
            .andExpect(jsonPath("$.autoUpdate").value(DEFAULT_AUTO_UPDATE.booleanValue()))
            .andExpect(jsonPath("$.accrued").value(DEFAULT_ACCRUED.booleanValue()))
            .andExpect(jsonPath("$.override").value(DEFAULT_OVERRIDE.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where name equals to DEFAULT_NAME
        defaultSiteDiscountShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the siteDiscountList where name equals to UPDATED_NAME
        defaultSiteDiscountShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSiteDiscountShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the siteDiscountList where name equals to UPDATED_NAME
        defaultSiteDiscountShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where name is not null
        defaultSiteDiscountShouldBeFound("name.specified=true");

        // Get all the siteDiscountList where name is null
        defaultSiteDiscountShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByPercentageIsEqualToSomething() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where percentage equals to DEFAULT_PERCENTAGE
        defaultSiteDiscountShouldBeFound("percentage.equals=" + DEFAULT_PERCENTAGE);

        // Get all the siteDiscountList where percentage equals to UPDATED_PERCENTAGE
        defaultSiteDiscountShouldNotBeFound("percentage.equals=" + UPDATED_PERCENTAGE);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByPercentageIsInShouldWork() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where percentage in DEFAULT_PERCENTAGE or UPDATED_PERCENTAGE
        defaultSiteDiscountShouldBeFound("percentage.in=" + DEFAULT_PERCENTAGE + "," + UPDATED_PERCENTAGE);

        // Get all the siteDiscountList where percentage equals to UPDATED_PERCENTAGE
        defaultSiteDiscountShouldNotBeFound("percentage.in=" + UPDATED_PERCENTAGE);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByPercentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where percentage is not null
        defaultSiteDiscountShouldBeFound("percentage.specified=true");

        // Get all the siteDiscountList where percentage is null
        defaultSiteDiscountShouldNotBeFound("percentage.specified=false");
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByAutoUpdateIsEqualToSomething() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where autoUpdate equals to DEFAULT_AUTO_UPDATE
        defaultSiteDiscountShouldBeFound("autoUpdate.equals=" + DEFAULT_AUTO_UPDATE);

        // Get all the siteDiscountList where autoUpdate equals to UPDATED_AUTO_UPDATE
        defaultSiteDiscountShouldNotBeFound("autoUpdate.equals=" + UPDATED_AUTO_UPDATE);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByAutoUpdateIsInShouldWork() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where autoUpdate in DEFAULT_AUTO_UPDATE or UPDATED_AUTO_UPDATE
        defaultSiteDiscountShouldBeFound("autoUpdate.in=" + DEFAULT_AUTO_UPDATE + "," + UPDATED_AUTO_UPDATE);

        // Get all the siteDiscountList where autoUpdate equals to UPDATED_AUTO_UPDATE
        defaultSiteDiscountShouldNotBeFound("autoUpdate.in=" + UPDATED_AUTO_UPDATE);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByAutoUpdateIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where autoUpdate is not null
        defaultSiteDiscountShouldBeFound("autoUpdate.specified=true");

        // Get all the siteDiscountList where autoUpdate is null
        defaultSiteDiscountShouldNotBeFound("autoUpdate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByAccruedIsEqualToSomething() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where accrued equals to DEFAULT_ACCRUED
        defaultSiteDiscountShouldBeFound("accrued.equals=" + DEFAULT_ACCRUED);

        // Get all the siteDiscountList where accrued equals to UPDATED_ACCRUED
        defaultSiteDiscountShouldNotBeFound("accrued.equals=" + UPDATED_ACCRUED);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByAccruedIsInShouldWork() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where accrued in DEFAULT_ACCRUED or UPDATED_ACCRUED
        defaultSiteDiscountShouldBeFound("accrued.in=" + DEFAULT_ACCRUED + "," + UPDATED_ACCRUED);

        // Get all the siteDiscountList where accrued equals to UPDATED_ACCRUED
        defaultSiteDiscountShouldNotBeFound("accrued.in=" + UPDATED_ACCRUED);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByAccruedIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where accrued is not null
        defaultSiteDiscountShouldBeFound("accrued.specified=true");

        // Get all the siteDiscountList where accrued is null
        defaultSiteDiscountShouldNotBeFound("accrued.specified=false");
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where override equals to DEFAULT_OVERRIDE
        defaultSiteDiscountShouldBeFound("override.equals=" + DEFAULT_OVERRIDE);

        // Get all the siteDiscountList where override equals to UPDATED_OVERRIDE
        defaultSiteDiscountShouldNotBeFound("override.equals=" + UPDATED_OVERRIDE);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where override in DEFAULT_OVERRIDE or UPDATED_OVERRIDE
        defaultSiteDiscountShouldBeFound("override.in=" + DEFAULT_OVERRIDE + "," + UPDATED_OVERRIDE);

        // Get all the siteDiscountList where override equals to UPDATED_OVERRIDE
        defaultSiteDiscountShouldNotBeFound("override.in=" + UPDATED_OVERRIDE);
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsByOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        // Get all the siteDiscountList where override is not null
        defaultSiteDiscountShouldBeFound("override.specified=true");

        // Get all the siteDiscountList where override is null
        defaultSiteDiscountShouldNotBeFound("override.specified=false");
    }

    @Test
    @Transactional
    public void getAllSiteDiscountsBySiteIsEqualToSomething() throws Exception {
        // Initialize the database
        Site site = SiteResourceIntTest.createEntity(em);
        em.persist(site);
        em.flush();
        siteDiscount.setSite(site);
        site.setSiteDiscount(siteDiscount);
        siteDiscountRepository.saveAndFlush(siteDiscount);
        Long siteId = site.getId();

        // Get all the siteDiscountList where site equals to siteId
        defaultSiteDiscountShouldBeFound("siteId.equals=" + siteId);

        // Get all the siteDiscountList where site equals to siteId + 1
        defaultSiteDiscountShouldNotBeFound("siteId.equals=" + (siteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSiteDiscountShouldBeFound(String filter) throws Exception {
        restSiteDiscountMockMvc.perform(get("/api/site-discounts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(siteDiscount.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].percentage").value(hasItem(DEFAULT_PERCENTAGE.intValue())))
            .andExpect(jsonPath("$.[*].autoUpdate").value(hasItem(DEFAULT_AUTO_UPDATE.booleanValue())))
            .andExpect(jsonPath("$.[*].accrued").value(hasItem(DEFAULT_ACCRUED.booleanValue())))
            .andExpect(jsonPath("$.[*].override").value(hasItem(DEFAULT_OVERRIDE.booleanValue())));

        // Check, that the count call also returns 1
        restSiteDiscountMockMvc.perform(get("/api/site-discounts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSiteDiscountShouldNotBeFound(String filter) throws Exception {
        restSiteDiscountMockMvc.perform(get("/api/site-discounts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSiteDiscountMockMvc.perform(get("/api/site-discounts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSiteDiscount() throws Exception {
        // Get the siteDiscount
        restSiteDiscountMockMvc.perform(get("/api/site-discounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSiteDiscount() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        int databaseSizeBeforeUpdate = siteDiscountRepository.findAll().size();

        // Update the siteDiscount
        SiteDiscount updatedSiteDiscount = siteDiscountRepository.findById(siteDiscount.getId()).get();
        // Disconnect from session so that the updates on updatedSiteDiscount are not directly saved in db
        em.detach(updatedSiteDiscount);
        updatedSiteDiscount
            .name(UPDATED_NAME)
            .percentage(UPDATED_PERCENTAGE)
            .autoUpdate(UPDATED_AUTO_UPDATE)
            .accrued(UPDATED_ACCRUED)
            .override(UPDATED_OVERRIDE);
        SiteDiscountDTO siteDiscountDTO = siteDiscountMapper.toDto(updatedSiteDiscount);

        restSiteDiscountMockMvc.perform(put("/api/site-discounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDiscountDTO)))
            .andExpect(status().isOk());

        // Validate the SiteDiscount in the database
        List<SiteDiscount> siteDiscountList = siteDiscountRepository.findAll();
        assertThat(siteDiscountList).hasSize(databaseSizeBeforeUpdate);
        SiteDiscount testSiteDiscount = siteDiscountList.get(siteDiscountList.size() - 1);
        assertThat(testSiteDiscount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSiteDiscount.getPercentage()).isEqualTo(UPDATED_PERCENTAGE);
        assertThat(testSiteDiscount.isAutoUpdate()).isEqualTo(UPDATED_AUTO_UPDATE);
        assertThat(testSiteDiscount.isAccrued()).isEqualTo(UPDATED_ACCRUED);
        assertThat(testSiteDiscount.isOverride()).isEqualTo(UPDATED_OVERRIDE);

        // Validate the SiteDiscount in Elasticsearch
        verify(mockSiteDiscountSearchRepository, times(1)).save(testSiteDiscount);
    }

    @Test
    @Transactional
    public void updateNonExistingSiteDiscount() throws Exception {
        int databaseSizeBeforeUpdate = siteDiscountRepository.findAll().size();

        // Create the SiteDiscount
        SiteDiscountDTO siteDiscountDTO = siteDiscountMapper.toDto(siteDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSiteDiscountMockMvc.perform(put("/api/site-discounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDiscountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SiteDiscount in the database
        List<SiteDiscount> siteDiscountList = siteDiscountRepository.findAll();
        assertThat(siteDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SiteDiscount in Elasticsearch
        verify(mockSiteDiscountSearchRepository, times(0)).save(siteDiscount);
    }

    @Test
    @Transactional
    public void deleteSiteDiscount() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);

        int databaseSizeBeforeDelete = siteDiscountRepository.findAll().size();

        // Get the siteDiscount
        restSiteDiscountMockMvc.perform(delete("/api/site-discounts/{id}", siteDiscount.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SiteDiscount> siteDiscountList = siteDiscountRepository.findAll();
        assertThat(siteDiscountList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SiteDiscount in Elasticsearch
        verify(mockSiteDiscountSearchRepository, times(1)).deleteById(siteDiscount.getId());
    }

    @Test
    @Transactional
    public void searchSiteDiscount() throws Exception {
        // Initialize the database
        siteDiscountRepository.saveAndFlush(siteDiscount);
        when(mockSiteDiscountSearchRepository.search(queryStringQuery("id:" + siteDiscount.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(siteDiscount), PageRequest.of(0, 1), 1));
        // Search the siteDiscount
        restSiteDiscountMockMvc.perform(get("/api/_search/site-discounts?query=id:" + siteDiscount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(siteDiscount.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].percentage").value(hasItem(DEFAULT_PERCENTAGE.intValue())))
            .andExpect(jsonPath("$.[*].autoUpdate").value(hasItem(DEFAULT_AUTO_UPDATE.booleanValue())))
            .andExpect(jsonPath("$.[*].accrued").value(hasItem(DEFAULT_ACCRUED.booleanValue())))
            .andExpect(jsonPath("$.[*].override").value(hasItem(DEFAULT_OVERRIDE.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SiteDiscount.class);
        SiteDiscount siteDiscount1 = new SiteDiscount();
        siteDiscount1.setId(1L);
        SiteDiscount siteDiscount2 = new SiteDiscount();
        siteDiscount2.setId(siteDiscount1.getId());
        assertThat(siteDiscount1).isEqualTo(siteDiscount2);
        siteDiscount2.setId(2L);
        assertThat(siteDiscount1).isNotEqualTo(siteDiscount2);
        siteDiscount1.setId(null);
        assertThat(siteDiscount1).isNotEqualTo(siteDiscount2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SiteDiscountDTO.class);
        SiteDiscountDTO siteDiscountDTO1 = new SiteDiscountDTO();
        siteDiscountDTO1.setId(1L);
        SiteDiscountDTO siteDiscountDTO2 = new SiteDiscountDTO();
        assertThat(siteDiscountDTO1).isNotEqualTo(siteDiscountDTO2);
        siteDiscountDTO2.setId(siteDiscountDTO1.getId());
        assertThat(siteDiscountDTO1).isEqualTo(siteDiscountDTO2);
        siteDiscountDTO2.setId(2L);
        assertThat(siteDiscountDTO1).isNotEqualTo(siteDiscountDTO2);
        siteDiscountDTO1.setId(null);
        assertThat(siteDiscountDTO1).isNotEqualTo(siteDiscountDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(siteDiscountMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(siteDiscountMapper.fromId(null)).isNull();
    }
}
