package com.budderfly.sites.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A SiteDiscount.
 */
@Entity
@Table(name = "site_discount")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sitediscount")
public class SiteDiscount extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "percentage", precision = 10, scale = 2)
    private BigDecimal percentage;

    @Column(name = "auto_update")
    private Boolean autoUpdate;

    @Column(name = "accrued")
    private Boolean accrued;

    @Column(name = "override")
    private Boolean override;

    @OneToOne(mappedBy = "siteDiscount")
    @JsonIgnore
    private Site site;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public SiteDiscount name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public SiteDiscount percentage(BigDecimal percentage) {
        this.percentage = percentage;
        return this;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public Boolean isAutoUpdate() {
        return autoUpdate;
    }

    public SiteDiscount autoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        return this;
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public Boolean isAccrued() {
        return accrued;
    }

    public SiteDiscount accrued(Boolean accrued) {
        this.accrued = accrued;
        return this;
    }

    public void setAccrued(Boolean accrued) {
        this.accrued = accrued;
    }

    public Boolean isOverride() {
        return override;
    }

    public SiteDiscount override(Boolean override) {
        this.override = override;
        return this;
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }

    public Site getSite() {
        return site;
    }

    public SiteDiscount site(Site site) {
        this.site = site;
        return this;
    }

    public void setSite(Site site) {
        this.site = site;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteDiscount siteDiscount = (SiteDiscount) o;
        if (siteDiscount.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), siteDiscount.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SiteDiscount{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", percentage=" + getPercentage() +
            ", autoUpdate='" + isAutoUpdate() + "'" +
            ", accrued='" + isAccrued() + "'" +
            ", override='" + isOverride() + "'" +
            "}";
    }
}
