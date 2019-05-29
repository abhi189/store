package com.budderfly.sites.web.rest;
import com.budderfly.sites.domain.Contact;
import com.budderfly.sites.service.ContactService;
import com.budderfly.sites.web.rest.errors.BadRequestAlertException;
import com.budderfly.sites.web.rest.util.HeaderUtil;
import com.budderfly.sites.web.rest.util.PaginationUtil;
import com.budderfly.sites.service.dto.ContactDTO;
import com.budderfly.sites.service.dto.ContactCriteria;
import com.budderfly.sites.service.ContactQueryService;
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
 * REST controller for managing Contact.
 */
@RestController
@RequestMapping("/api")
public class ContactResource {

    private final Logger log = LoggerFactory.getLogger(ContactResource.class);

    private static final String ENTITY_NAME = "sitesContact";

    private final ContactService contactService;

    private final ContactQueryService contactQueryService;

    public ContactResource(ContactService contactService, ContactQueryService contactQueryService) {
        this.contactService = contactService;
        this.contactQueryService = contactQueryService;
    }

    /**
     * POST  /contacts : Create a new contact.
     *
     * @param contactDTO the contactDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contactDTO, or with status 400 (Bad Request) if the contact has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contacts")
    public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO contactDTO) throws URISyntaxException {
        log.debug("REST request to save Contact : {}", contactDTO);
        if (contactDTO.getId() != null) {
            throw new BadRequestAlertException("A new contact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ContactDTO result = contactService.save(contactDTO);
        return ResponseEntity.created(new URI("/api/contacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contacts : Updates an existing contact.
     *
     * @param contactDTO the contactDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contactDTO,
     * or with status 400 (Bad Request) if the contactDTO is not valid,
     * or with status 500 (Internal Server Error) if the contactDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contacts")
    public ResponseEntity<ContactDTO> updateContact(@RequestBody ContactDTO contactDTO) throws URISyntaxException {
        log.debug("REST request to update Contact : {}", contactDTO);
        if (contactDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ContactDTO result = contactService.save(contactDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, contactDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contacts : get all the contacts.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of contacts in body
     */
    @GetMapping("/contacts")
    public ResponseEntity<List<ContactDTO>> getAllContacts(ContactCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Contacts by criteria: {}", criteria);
        Page<ContactDTO> page = contactQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contacts");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/contacts/by-email/{email}")
    public ResponseEntity<List<ContactDTO>> getContactsByEmail(@PathVariable String email) {
        log.debug("REST request to get Contacts by email: " + email);
        List<ContactDTO> contactDtos = contactService.findByEmail(email);
        return ResponseEntity.ok().body(contactDtos);
    }

    /**
    * GET  /contacts/count : count all the contacts.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/contacts/count")
    public ResponseEntity<Long> countContacts(ContactCriteria criteria) {
        log.debug("REST request to count Contacts by criteria: {}", criteria);
        return ResponseEntity.ok().body(contactQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /contacts/:id : get the "id" contact.
     *
     * @param id the id of the contactDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contactDTO, or with status 404 (Not Found)
     */
    @GetMapping("/contacts/{id}")
    public ResponseEntity<ContactDTO> getContact(@PathVariable Long id) {
        log.debug("REST request to get Contact : {}", id);
        Optional<ContactDTO> contactDTO = contactService.findOne(id);
        return ResponseUtil.wrapOrNotFound(contactDTO);
    }

    /**
     * DELETE  /contacts/:id : delete the "id" contact.
     *
     * @param id the id of the contactDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        log.debug("REST request to delete Contact : {}", id);
        contactService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/contacts?query=:query : search for the contact corresponding
     * to the query.
     *
     * @param query the query of the contact search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/contacts")
    public ResponseEntity<List<ContactDTO>> searchContacts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Contacts for query {}", query);
        Page<ContactDTO> page = contactService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contacts");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
