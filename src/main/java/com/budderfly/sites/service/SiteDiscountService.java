package com.budderfly.sites.service;

import com.budderfly.sites.domain.SiteDiscount;
import com.budderfly.sites.repository.SiteDiscountRepository;
import com.budderfly.sites.repository.search.SiteDiscountSearchRepository;
import com.budderfly.sites.service.dto.SiteDiscountDTO;
import com.budderfly.sites.service.mapper.SiteDiscountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing SiteDiscount.
 */
@Service
@Transactional
public class SiteDiscountService {

    private final Logger log = LoggerFactory.getLogger(SiteDiscountService.class);

    private final SiteDiscountRepository siteDiscountRepository;

    private final SiteDiscountMapper siteDiscountMapper;

    private final SiteDiscountSearchRepository siteDiscountSearchRepository;

    public SiteDiscountService(SiteDiscountRepository siteDiscountRepository, SiteDiscountMapper siteDiscountMapper, SiteDiscountSearchRepository siteDiscountSearchRepository) {
        this.siteDiscountRepository = siteDiscountRepository;
        this.siteDiscountMapper = siteDiscountMapper;
        this.siteDiscountSearchRepository = siteDiscountSearchRepository;
    }

    /**
     * Save a siteDiscount.
     *
     * @param siteDiscountDTO the entity to save
     * @return the persisted entity
     */
    public SiteDiscountDTO save(SiteDiscountDTO siteDiscountDTO) {
        log.debug("Request to save SiteDiscount : {}", siteDiscountDTO);

        SiteDiscount siteDiscount = siteDiscountMapper.toEntity(siteDiscountDTO);
        siteDiscount = siteDiscountRepository.save(siteDiscount);
        SiteDiscountDTO result = siteDiscountMapper.toDto(siteDiscount);
        siteDiscountSearchRepository.save(siteDiscount);
        return result;
    }

    /**
     * Get all the siteDiscounts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SiteDiscountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SiteDiscounts");
        return siteDiscountRepository.findAll(pageable)
            .map(siteDiscountMapper::toDto);
    }



    /**
     *  get all the siteDiscounts where Site is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<SiteDiscountDTO> findAllWhereSiteIsNull() {
        log.debug("Request to get all siteDiscounts where Site is null");
        return StreamSupport
            .stream(siteDiscountRepository.findAll().spliterator(), false)
            .filter(siteDiscount -> siteDiscount.getSite() == null)
            .map(siteDiscountMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one siteDiscount by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<SiteDiscountDTO> findOne(Long id) {
        log.debug("Request to get SiteDiscount : {}", id);
        return siteDiscountRepository.findById(id)
            .map(siteDiscountMapper::toDto);
    }

    /**
     * Delete the siteDiscount by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete SiteDiscount : {}", id);
        siteDiscountRepository.deleteById(id);
        siteDiscountSearchRepository.deleteById(id);
    }

    /**
     * Search for the siteDiscount corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SiteDiscountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SiteDiscounts for query {}", query);
        return siteDiscountSearchRepository.search(queryStringQuery(query), pageable)
            .map(siteDiscountMapper::toDto);
    }
}
