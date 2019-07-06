package com.budderfly.sites.repository;

import com.budderfly.sites.domain.Contact;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Contact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {

    List<Contact> findByContactEmail(String email);

    List<Contact> findByContactEmailContainingIgnoreCase(String email);

}
