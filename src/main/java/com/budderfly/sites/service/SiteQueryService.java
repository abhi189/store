package com.budderfly.sites.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import com.budderfly.sites.repository.SiteFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.budderfly.sites.domain.Site;
import com.budderfly.sites.domain.*; // for static metamodels
import com.budderfly.sites.repository.SiteRepository;
import com.budderfly.sites.repository.search.SiteSearchRepository;
import com.budderfly.sites.service.dto.SiteCriteria;
import com.budderfly.sites.service.dto.SiteDTO;
import com.budderfly.sites.service.mapper.SiteMapper;

/**
 * Service for executing complex queries for Site entities in the database.
 * The main input is a {@link SiteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SiteDTO} or a {@link Page} of {@link SiteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SiteQueryService extends QueryService<Site> {

    private final Logger log = LoggerFactory.getLogger(SiteQueryService.class);

    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;

    private final SiteSearchRepository siteSearchRepository;

    public SiteQueryService(SiteRepository siteRepository, SiteMapper siteMapper, SiteSearchRepository siteSearchRepository) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.siteSearchRepository = siteSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SiteDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SiteDTO> findByCriteria(SiteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Site> specification = createSpecification(criteria);
        return siteMapper.toDto(siteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SiteDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @SiteFilter
    @Transactional(readOnly = true)
    public Page<SiteDTO> findByCriteria(SiteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Site> specification = createSpecification(criteria);
        return siteRepository.findAll(specification, page)
            .map(siteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SiteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Site> specification = createSpecification(criteria);
        return siteRepository.count(specification);
    }

    /**
     * Function to convert SiteCriteria to a {@link Specification}
     */
    private Specification<Site> createSpecification(SiteCriteria criteria) {
        Specification<Site> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Site_.id));
            }
            if (criteria.getBudderflyId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBudderflyId(), Site_.budderflyId));
            }
            if (criteria.getCustomerName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCustomerName(), Site_.customerName));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Site_.status));
            }
            if (criteria.getCompanyType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCompanyType(), Site_.companyType));
            }
            if (criteria.getStoreNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStoreNumber(), Site_.storeNumber));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Site_.address));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), Site_.city));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), Site_.state));
            }
            if (criteria.getZip() != null) {
                specification = specification.and(buildStringSpecification(criteria.getZip(), Site_.zip));
            }
            if (criteria.getBillingType() != null) {
                specification = specification.and(buildSpecification(criteria.getBillingType(), Site_.billingType));
            }
            if (criteria.getPaymentType() != null) {
                specification = specification.and(buildSpecification(criteria.getPaymentType(), Site_.paymentType));
            }
            if (criteria.getSiteType() != null) {
                specification = specification.and(buildSpecification(criteria.getSiteType(), Site_.siteType));
            }
            if (criteria.getOwnerName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOwnerName(), Site_.ownerName));
            }
            if (criteria.getOwnerEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOwnerEmail(), Site_.ownerEmail));
            }
            if (criteria.getOwnerPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOwnerPhone(), Site_.ownerPhone));
            }
            if (criteria.getAddress1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress1(), Site_.address1));
            }
            if (criteria.getAddress2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress2(), Site_.address2));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLatitude(), Site_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLongitude(), Site_.longitude));
            }
            if (criteria.getTaxExempt() != null) {
                specification = specification.and(buildSpecification(criteria.getTaxExempt(), Site_.taxExempt));
            }
            if (criteria.getRollBilling() != null) {
                specification = specification.and(buildSpecification(criteria.getRollBilling(), Site_.rollBilling));
            }
            if (criteria.getEmoVersion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmoVersion(), Site_.emoVersion));
            }
            if (criteria.getBillingContact() != null) {
                specification = specification.and(buildSpecification(criteria.getBillingContact(), Site_.billingContact));
            }
            if (criteria.getFranchiseContact() != null) {
                specification = specification.and(buildSpecification(criteria.getFranchiseContact(), Site_.franchiseContact));
            }
            if (criteria.getParentSiteId() != null) {
                specification = specification.and(buildSpecification(criteria.getParentSiteId(),
                    root -> root.join(Site_.parentSite, JoinType.LEFT).get(Site_.id)));
            }
            if (criteria.getSiteContact() != null) {
                specification = specification.and(buildSpecification(criteria.getSiteContact(), Site_.siteContact));
            }
            if (criteria.getEnableTicketDispatch() != null) {
                specification = specification.and(buildSpecification(criteria.getEnableTicketDispatch(), Site_.enableTicketDispatch));
            }
            if (criteria.getTimeZoneId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTimeZoneId(), Site_.timeZoneId));
            }
        }
        return specification;
    }
}
