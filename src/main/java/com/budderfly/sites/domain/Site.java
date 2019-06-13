package com.budderfly.sites.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;

import org.hibernate.annotations.Cache;
import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.budderfly.sites.domain.enumeration.SiteStatus;

import com.budderfly.sites.domain.enumeration.BillingType;

import com.budderfly.sites.domain.enumeration.PaymentType;

import com.budderfly.sites.domain.enumeration.SiteType;

/**
 * A Site.
 */
@Entity
@Table(name = "site")
@FilterDef(name = "SITE_FILTER", parameters = {@ParamDef(name = "siteIds", type = "string")})
@Filter(name = "SITE_FILTER", condition = "budderfly_id IN (:siteIds)")
@Document(indexName = "site")
public class Site extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "budderfly_id", nullable = false)
    private String budderflyId;

    @Column(name = "customer_name")
    private String customerName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SiteStatus status;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false)
    private BillingType billingType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "site_type")
    private SiteType siteType;

    @NotNull
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @NotNull
    @Column(name = "owner_email", nullable = false)
    private String ownerEmail;

    @NotNull
    @Column(name = "owner_phone", nullable = false)
    private String ownerPhone;

    @Column(name = "address_1")
    private String address1;

    @Column(name = "address_2")
    private String address2;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "tax_exempt")
    private Boolean taxExempt;

    @Column(name = "roll_billing")
    private Boolean rollBilling;

    @Column(name = "emo_version")
    private String emoVersion;

    @Column(name = "billing_contact")
    private Long billingContact;

    @Column(name = "site_contact")
    private Long siteContact;

    @Column(name = "franchise_contact")
    private Long franchiseContact;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Site parentSite;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBudderflyId() {
        return budderflyId;
    }

    public Site budderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
        return this;
    }

    public void setBudderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Site customerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public SiteStatus getStatus() {
        return status;
    }

    public Site status(SiteStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    public String getCompanyType() {
        return companyType;
    }

    public Site companyType(String companyType) {
        this.companyType = companyType;
        return this;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public Site storeNumber(String storeNumber) {
        this.storeNumber = storeNumber;
        return this;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getAddress() {
        return address;
    }

    public Site address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public Site city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public Site state(String state) {
        this.state = state;
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public Site zip(String zip) {
        this.zip = zip;
        return this;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BillingType getBillingType() {
        return billingType;
    }

    public Site billingType(BillingType billingType) {
        this.billingType = billingType;
        return this;
    }

    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public Site paymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public SiteType getSiteType() {
        return siteType;
    }

    public Site siteType(SiteType siteType) {
        this.siteType = siteType;
        return this;
    }

    public void setSiteType(SiteType siteType) {
        this.siteType = siteType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Site ownerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public Site ownerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public Site ownerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
        return this;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getAddress1() {
        return address1;
    }

    public Site address1(String address1) {
        this.address1 = address1;
        return this;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public Site address2(String address2) {
        this.address2 = address2;
        return this;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getLatitude() {
        return latitude;
    }

    public Site latitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Site longitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean isTaxExempt() {
        return taxExempt;
    }

    public Site taxExempt(Boolean taxExempt) {
        this.taxExempt = taxExempt;
        return this;
    }

    public void setTaxExempt(Boolean taxExempt) {
        this.taxExempt = taxExempt;
    }

    public Boolean isRollBilling() {
        return rollBilling;
    }

    public Site rollBilling(Boolean rollBilling) {
        this.rollBilling = rollBilling;
        return this;
    }

    public void setRollBilling(Boolean rollBilling) {
        this.rollBilling = rollBilling;
    }

    public String getEmoVersion() {
        return emoVersion;
    }

    public Site emoVersion(String emoVersion) {
        this.emoVersion = emoVersion;
        return this;
    }

    public void setEmoVersion(String emoVersion) {
        this.emoVersion = emoVersion;
    }

    public Long getBillingContact() {
        return billingContact;
    }

    public Site billingContact(Long billingContact) {
        this.billingContact = billingContact;
        return this;
    }

    public void setBillingContact(Long billingContact) {
        this.billingContact = billingContact;
    }

    public Long getSiteContact() {
        return siteContact;
    }

    public Site siteContact(Long siteContact) {
        this.siteContact = siteContact;
        return this;
    }

    public void setSiteContact(Long siteContact) {
        this.siteContact = siteContact;
    }

    public Long getFranchiseContact() {
        return franchiseContact;
    }

    public Site franchiseContact(Long franchiseContact) {
        this.franchiseContact = franchiseContact;
        return this;
    }

    public void setFranchiseContact(Long franchiseContact) {
        this.franchiseContact = franchiseContact;
    }

    public Site getParentSite() {
        return parentSite;
    }

    public Site parentSite(Site site) {
        this.parentSite = site;
        return this;
    }

    public void setParentSite(Site site) {
        this.parentSite = site;
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
        Site site = (Site) o;
        if (site.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), site.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Site{" +
            "id=" + getId() +
            ", budderflyId='" + getBudderflyId() + "'" +
            ", customerName='" + getCustomerName() + "'" +
            ", status='" + getStatus() + "'" +
            ", companyType='" + getCompanyType() + "'" +
            ", storeNumber=" + getStoreNumber() +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", state='" + getState() + "'" +
            ", zip='" + getZip() + "'" +
            ", billingType='" + getBillingType() + "'" +
            ", paymentType='" + getPaymentType() + "'" +
            ", siteType='" + getSiteType() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", ownerEmail='" + getOwnerEmail() + "'" +
            ", ownerPhone='" + getOwnerPhone() + "'" +
            ", address1='" + getAddress1() + "'" +
            ", address2='" + getAddress2() + "'" +
            ", latitude='" + getLatitude() + "'" +
            ", longitude='" + getLongitude() + "'" +
            ", taxExempt='" + isTaxExempt() + "'" +
            ", rollBilling='" + isRollBilling() + "'" +
            ", emoVersion='" + getEmoVersion() + "'" +
            ", billingContact='" + getBillingContact() + "'" +
            ", siteContact='" + getSiteContact() + "'" +
            ", franchiseContact='" + getFranchiseContact() + "'" +
            "}";
    }
}
