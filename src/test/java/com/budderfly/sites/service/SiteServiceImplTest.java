package com.budderfly.sites.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.budderfly.sites.domain.Site;
import com.budderfly.sites.repository.SiteRepository;
import com.budderfly.sites.repository.search.SiteSearchRepository;
import com.budderfly.sites.service.dto.SiteAccountDTO;
import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.impl.SiteServiceImpl;
import com.budderfly.sites.service.mapper.SiteMapper;

import org.hibernate.TransactionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by dmaure on 02/10/19.
 */
public class SiteServiceImplTest {

    @Mock
    private SiteRepository siteRepository;
    @Mock
    private SiteSearchRepository siteSearchRepository;
    @Mock
    public SiteMapper siteMapper;
    @Mock
    public TimezoneService timezoneService;

    @InjectMocks
    SiteServiceImpl siteService;

    String budderflyId = "SUBWTEST-2015";
    String companyType = "Supplier";
    String ownerName = "John Smith";
    String accountNumber = "654321";
    public SiteDTO siteDTO = new SiteDTO();
    public Site site = new Site();
    public SiteAccountDTO siteAccountDTO = new SiteAccountDTO();
    public List<SiteDTO> listaSites = new ArrayList();
    public List<Site> siteList = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        siteDTO.setId(1l);
        siteDTO.setBudderflyId(budderflyId);
        siteDTO.setCompanyType(companyType);
        siteDTO.setOwnerName(ownerName);
        listaSites.add(siteDTO);

        site.setId(1l);
        site.setBudderflyId(budderflyId);
        site.setCompanyType(companyType);
        site.setOwnerName(ownerName);
        siteList.add(site);

        siteAccountDTO.setId(1l);
        siteAccountDTO.setBudderflyId(budderflyId);
        siteAccountDTO.setAccountNumber(accountNumber);

    }

    @Test
    public void Test_createSite(){

        when(siteRepository.findByBudderflyId(anyString())).thenReturn(null);
        when(siteMapper.toDto(any(Site.class))).thenReturn(siteDTO);

        SiteDTO siteDTOTest = siteService.createSite(siteAccountDTO);

        assertThat(siteDTOTest.getBudderflyId()).isNotNull();
        assertThat(siteDTOTest.getBudderflyId()).isEqualTo(siteAccountDTO.getBudderflyId());

    }

    @Test
    public void Test_createDuplicatedSite(){

        when(siteRepository.findByBudderflyId(anyString())).thenReturn(site);

        SiteDTO siteDTOTest = siteService.createSite(siteAccountDTO);

        assertThat(siteDTOTest.getBudderflyId()).isNull();

    }

    @Test
    public void Test_BudderflyIdNull(){
        siteAccountDTO.setBudderflyId(null);
        SiteDTO siteDTOTest = siteService.createSite(siteAccountDTO);

        assertThat(siteDTOTest.getBudderflyId()).isNull();
    }

    @Test
    public void Test_invalidBudderflyId(){
        siteAccountDTO.setBudderflyId("asd213!-asd2");
        SiteDTO siteDTOTest = siteService.createSite(siteAccountDTO);

        assertThat(siteDTOTest.getBudderflyId()).isNull();
    }


    @Test
    public void Test_findAllSites(){

        when(siteRepository.findAll()).thenReturn(siteList);

        when(siteMapper.toDto(anyList())).thenReturn(listaSites);

        List<SiteDTO> result = siteService.findAll();

        Assert.assertEquals(listaSites, result);
        assertThat(result.get(0).getBudderflyId()).isEqualTo(budderflyId);
    }

    @Test
    public void Test_FailFindAllSites(){

        when(siteRepository.findAll()).thenThrow(new TransactionException("Error well catched"));

        List result = siteService.findAll();
        if(!result.isEmpty()) Assert.fail("Transaction Exception not catched");
    }


}
