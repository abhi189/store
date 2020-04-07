package com.budderfly.sites.aop.filter;

import com.budderfly.sites.security.SecurityUtils;
import com.budderfly.sites.service.SiteService;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Aspect for filtering site data based on ownership
 */
@Aspect
@Component
public class SiteOwnershipFilterAspect {
    private final Logger log = LoggerFactory.getLogger(SiteOwnershipFilterAspect.class);
    private final SiteService siteService;
    @PersistenceContext
    public EntityManager entityManager;

    public SiteOwnershipFilterAspect(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * Pointcut that matches all Spring beans in the application's service packages.
     */
    @Pointcut("within(com.budderfly.sites.service..*)")
    public void allServicesPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all methods with @SiteOwnershipFilter annotation
     */
    @Pointcut("@annotation(com.budderfly.sites.repository.SiteOwnershipFilter)")
    public void siteOwnershipFilterAnnotation() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     */
    @Before("allServicesPointcut() && siteOwnershipFilterAnnotation()")
    public void applySitesOwnershipFilter(JoinPoint joinPoint) {
        log.debug("Enter applySitesOwnershipFilter on jointPoint {}", joinPoint.toLongString());
            final Optional<String> login = SecurityUtils.getCurrentUserLogin();
            log.debug("Applying site ownership filter for user {}", login);
            if (login.isPresent()) {
                List<String> budderflyIDs = new ArrayList<>();

                try {
                    budderflyIDs = siteService.getShopsOwnedByUser(login.get());
                } catch (HystrixRuntimeException ex) {
                    log.debug("Failed to get shops in SITE OWNERSHIP FILTER " + ex.getCause().getMessage());
                } catch (Exception exc) {
                    log.debug("Failed to get shops in SITE OWNERSHIP FILTER " + exc.getMessage());
                }

                if (budderflyIDs.isEmpty()) {
                    budderflyIDs.add("''"); //Ugly but needed as an empty IN () command is not supported
                }
                Filter filter = entityManager.unwrap(Session.class).enableFilter("SITE_OWNERSHIP_FILTER");
                filter.setParameterList("siteIds", budderflyIDs);

                try {
                    filter.validate();
                } catch (HibernateException e) {
                    log.debug("SITE OWNERSHIP FILTER validation failed " + e.getCause().getMessage());
                }

            } else {
                log.warn("No user is signed in for SITE OWNERSHIP FILTER"); // should never happen
            }
    }

}
