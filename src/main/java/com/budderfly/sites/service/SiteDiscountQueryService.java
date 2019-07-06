package com.budderfly.sites.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.budderfly.sites.domain.SiteDiscount;
import com.budderfly.sites.domain.*; // for static metamodels
import com.budderfly.sites.repository.SiteDiscountRepository;
import com.budderfly.sites.repository.search.SiteDiscountSearchRepository;
import com.budderfly.sites.service.dto.SiteDiscountCriteria;
import com.budderfly.sites.service.dto.SiteDiscountDTO;
import com.budderfly.sites.service.mapper.SiteDiscountMapper;

/**
 * Service for executing complex queries for SiteDiscount entities in the database.
 * The main input is a {@link SiteDiscountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SiteDiscountDTO} or a {@link Page} of {@link SiteDiscountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SiteDiscountQueryService extends QueryService<SiteDiscount> {

    private final Logger log = LoggerFactory.getLogger(SiteDiscountQueryService.class);

    private final SiteDiscountRepository siteDiscountRepository;

    private final SiteDiscountMapper siteDiscountMapper;

    private final SiteDiscountSearchRepository siteDiscountSearchRepository;

    public SiteDiscountQueryService(SiteDiscountRepository siteDiscountRepository, SiteDiscountMapper siteDiscountMapper, SiteDiscountSearchRepository siteDiscountSearchRepository) {
        this.siteDiscountRepository = siteDiscountRepository;
        this.siteDiscountMapper = siteDiscountMapper;
        this.siteDiscountSearchRepository = siteDiscountSearchRepository;
    }

    /**
     * Return a {@link List} of {@link SiteDiscountDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SiteDiscountDTO> findByCriteria(SiteDiscountCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SiteDiscount> specification = createSpecification(criteria);
        return siteDiscountMapper.toDto(siteDiscountRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SiteDiscountDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SiteDiscountDTO> findByCriteria(SiteDiscountCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SiteDiscount> specification = createSpecification(criteria);
        return siteDiscountRepository.findAll(specification, page)
            .map(siteDiscountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SiteDiscountCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SiteDiscount> specification = createSpecification(criteria);
        return siteDiscountRepository.count(specification);
    }

    /**
     * Function to convert SiteDiscountCriteria to a {@link Specification}
     */
    private Specification<SiteDiscount> createSpecification(SiteDiscountCriteria criteria) {
        Specification<SiteDiscount> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), SiteDiscount_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), SiteDiscount_.name));
            }
            if (criteria.getPercentage() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPercentage(), SiteDiscount_.percentage));
            }
            if (criteria.getAutoUpdate() != null) {
                specification = specification.and(buildSpecification(criteria.getAutoUpdate(), SiteDiscount_.autoUpdate));
            }
            if (criteria.getAccrued() != null) {
                specification = specification.and(buildSpecification(criteria.getAccrued(), SiteDiscount_.accrued));
            }
            if (criteria.getOverride() != null) {
                specification = specification.and(buildSpecification(criteria.getOverride(), SiteDiscount_.override));
            }
            if (criteria.getSiteId() != null) {
                specification = specification.and(buildSpecification(criteria.getSiteId(),
                    root -> root.join(SiteDiscount_.site, JoinType.LEFT).get(Site_.id)));
            }
        }
        return specification;
    }
}
