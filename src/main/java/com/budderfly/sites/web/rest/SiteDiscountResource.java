package com.budderfly.sites.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.budderfly.sites.service.SiteDiscountService;
import com.budderfly.sites.web.rest.errors.BadRequestAlertException;
import com.budderfly.sites.web.rest.util.HeaderUtil;
import com.budderfly.sites.web.rest.util.PaginationUtil;
import com.budderfly.sites.service.dto.SiteDiscountDTO;
import com.budderfly.sites.service.dto.SiteDiscountCriteria;
import com.budderfly.sites.service.SiteDiscountQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing SiteDiscount.
 */
@RestController
@RequestMapping("/api")
public class SiteDiscountResource {

    private final Logger log = LoggerFactory.getLogger(SiteDiscountResource.class);

    private static final String ENTITY_NAME = "sitesSiteDiscount";

    private final SiteDiscountService siteDiscountService;

    private final SiteDiscountQueryService siteDiscountQueryService;

    public SiteDiscountResource(SiteDiscountService siteDiscountService, SiteDiscountQueryService siteDiscountQueryService) {
        this.siteDiscountService = siteDiscountService;
        this.siteDiscountQueryService = siteDiscountQueryService;
    }

    /**
     * POST  /site-discounts : Create a new siteDiscount.
     *
     * @param siteDiscountDTO the siteDiscountDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new siteDiscountDTO, or with status 400 (Bad Request) if the siteDiscount has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/site-discounts")
    @Timed
    public ResponseEntity<SiteDiscountDTO> createSiteDiscount(@RequestBody SiteDiscountDTO siteDiscountDTO) throws URISyntaxException {
        log.debug("REST request to save SiteDiscount : {}", siteDiscountDTO);
        if (siteDiscountDTO.getId() != null) {
            throw new BadRequestAlertException("A new siteDiscount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SiteDiscountDTO result = siteDiscountService.save(siteDiscountDTO);
        return ResponseEntity.created(new URI("/api/site-discounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /site-discounts : Updates an existing siteDiscount.
     *
     * @param siteDiscountDTO the siteDiscountDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated siteDiscountDTO,
     * or with status 400 (Bad Request) if the siteDiscountDTO is not valid,
     * or with status 500 (Internal Server Error) if the siteDiscountDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/site-discounts")
    @Timed
    public ResponseEntity<SiteDiscountDTO> updateSiteDiscount(@RequestBody SiteDiscountDTO siteDiscountDTO) throws URISyntaxException {
        log.debug("REST request to update SiteDiscount : {}", siteDiscountDTO);
        if (siteDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SiteDiscountDTO result = siteDiscountService.save(siteDiscountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, siteDiscountDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /site-discounts : get all the siteDiscounts.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of siteDiscounts in body
     */
    @GetMapping("/site-discounts")
    @Timed
    public ResponseEntity<List<SiteDiscountDTO>> getAllSiteDiscounts(SiteDiscountCriteria criteria, Pageable pageable) {
        log.debug("REST request to get SiteDiscounts by criteria: {}", criteria);
        Page<SiteDiscountDTO> page = siteDiscountQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/site-discounts");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /site-discounts/count : count all the siteDiscounts.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/site-discounts/count")
    @Timed
    public ResponseEntity<Long> countSiteDiscounts(SiteDiscountCriteria criteria) {
        log.debug("REST request to count SiteDiscounts by criteria: {}", criteria);
        return ResponseEntity.ok().body(siteDiscountQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /site-discounts/:id : get the "id" siteDiscount.
     *
     * @param id the id of the siteDiscountDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the siteDiscountDTO, or with status 404 (Not Found)
     */
    @GetMapping("/site-discounts/{id}")
    @Timed
    public ResponseEntity<SiteDiscountDTO> getSiteDiscount(@PathVariable Long id) {
        log.debug("REST request to get SiteDiscount : {}", id);
        Optional<SiteDiscountDTO> siteDiscountDTO = siteDiscountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(siteDiscountDTO);
    }

    /**
     * DELETE  /site-discounts/:id : delete the "id" siteDiscount.
     *
     * @param id the id of the siteDiscountDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/site-discounts/{id}")
    @Timed
    public ResponseEntity<Void> deleteSiteDiscount(@PathVariable Long id) {
        log.debug("REST request to delete SiteDiscount : {}", id);
        siteDiscountService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/site-discounts?query=:query : search for the siteDiscount corresponding
     * to the query.
     *
     * @param query the query of the siteDiscount search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/site-discounts")
    @Timed
    public ResponseEntity<List<SiteDiscountDTO>> searchSiteDiscounts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of SiteDiscounts for query {}", query);
        Page<SiteDiscountDTO> page = siteDiscountService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/site-discounts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
