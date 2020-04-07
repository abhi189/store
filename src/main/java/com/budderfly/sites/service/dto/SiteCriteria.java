package com.budderfly.sites.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import com.budderfly.sites.domain.enumeration.SiteStatus;
import com.budderfly.sites.domain.enumeration.BillingType;
import com.budderfly.sites.domain.enumeration.PaymentType;
import com.budderfly.sites.domain.enumeration.SiteType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the Site entity. This class is used in SiteResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /sites?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SiteCriteria implements Serializable {
    /**
     * Class for filtering SiteStatus
     */
    public static class SiteStatusFilter extends Filter<SiteStatus> {
    }
    /**
     * Class for filtering BillingType
     */
    public static class BillingTypeFilter extends Filter<BillingType> {
    }
    /**
     * Class for filtering PaymentType
     */
    public static class PaymentTypeFilter extends Filter<PaymentType> {
    }
    /**
     * Class for filtering SiteType
     */
    public static class SiteTypeFilter extends Filter<SiteType> {
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter budderflyId;

    private StringFilter customerName;

    private SiteStatusFilter status;

    private StringFilter companyType;

    private StringFilter storeNumber;

    private StringFilter address;

    private StringFilter city;

    private StringFilter state;

    private StringFilter zip;

    private BillingTypeFilter billingType;

    private PaymentTypeFilter paymentType;

    private SiteTypeFilter siteType;

    private StringFilter ownerName;

    private StringFilter ownerEmail;

    private StringFilter ownerPhone;

    private StringFilter address1;

    private StringFilter address2;

    private StringFilter latitude;

    private StringFilter longitude;

    private BooleanFilter taxExempt;

    private BooleanFilter rollBilling;

    private StringFilter emoVersion;

    private LongFilter billingContact;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private LongFilter siteContact;

    private LongFilter franchiseContact;

    private LongFilter parentSiteId;

    private LongFilter contactId;
    
    private StringFilter contactDeskId;

    private BooleanFilter enableTicketDispatch;
    
    private StringFilter timeZoneId;
    
    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getBudderflyId() {
        return budderflyId;
    }

    public void setBudderflyId(StringFilter budderflyId) {
        this.budderflyId = budderflyId;
    }

    public StringFilter getCustomerName() {
        return customerName;
    }

    public void setCustomerName(StringFilter customerName) {
        this.customerName = customerName;
    }

    public SiteStatusFilter getStatus() {
        return status;
    }

    public void setStatus(SiteStatusFilter status) {
        this.status = status;
    }

    public StringFilter getCompanyType() {
        return companyType;
    }

    public void setCompanyType(StringFilter companyType) {
        this.companyType = companyType;
    }

    public StringFilter getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(StringFilter storeNumber) {
        this.storeNumber = storeNumber;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getCity() {
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getState() {
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public StringFilter getZip() {
        return zip;
    }

    public void setZip(StringFilter zip) {
        this.zip = zip;
    }

    public BillingTypeFilter getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingTypeFilter billingType) {
        this.billingType = billingType;
    }

    public PaymentTypeFilter getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeFilter paymentType) {
        this.paymentType = paymentType;
    }

    public SiteTypeFilter getSiteType() {
        return siteType;
    }

    public void setSiteType(SiteTypeFilter siteType) {
        this.siteType = siteType;
    }

    public StringFilter getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(StringFilter ownerName) {
        this.ownerName = ownerName;
    }

    public StringFilter getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(StringFilter ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public StringFilter getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(StringFilter ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public StringFilter getAddress1() {
        return address1;
    }

    public void setAddress1(StringFilter address1) {
        this.address1 = address1;
    }

    public StringFilter getAddress2() {
        return address2;
    }

    public void setAddress2(StringFilter address2) {
        this.address2 = address2;
    }

    public StringFilter getLatitude() {
        return latitude;
    }

    public void setLatitude(StringFilter latitude) {
        this.latitude = latitude;
    }

    public StringFilter getLongitude() {
        return longitude;
    }

    public void setLongitude(StringFilter longitude) {
        this.longitude = longitude;
    }

    public BooleanFilter getTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(BooleanFilter taxExempt) {
        this.taxExempt = taxExempt;
    }

    public BooleanFilter getRollBilling() {
        return rollBilling;
    }

    public void setRollBilling(BooleanFilter rollBilling) {
        this.rollBilling = rollBilling;
    }

    public StringFilter getEmoVersion() {
        return emoVersion;
    }

    public void setEmoVersion(StringFilter emoVersion) {
        this.emoVersion = emoVersion;
    }

    public LongFilter getBillingContact() {
        return billingContact;
    }

    public void setBillingContact(LongFilter billingContact) {
        this.billingContact = billingContact;
    }

    public LongFilter getSiteContact() {
        return siteContact;
    }

    public void setSiteContact(LongFilter siteContact) {
        this.siteContact = siteContact;
    }

    public LongFilter getFranchiseContact() {
        return franchiseContact;
    }

    public void setFranchiseContact(LongFilter franchiseContact) {
        this.franchiseContact = franchiseContact;
    }

    public LongFilter getParentSiteId() {
        return parentSiteId;
    }

    public void setParentSiteId(LongFilter parentSiteId) {
        this.parentSiteId = parentSiteId;
    }

    public LongFilter getContactId() {
        return contactId;
    }

    public void setContactId(LongFilter contactId) {
        this.contactId = contactId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public StringFilter getContactDeskId() {
        return contactDeskId;
    }

    public void setContactDeskId(StringFilter contactDeskId) {
        this.contactDeskId = contactDeskId;
    }

    public BooleanFilter getEnableTicketDispatch() {
        return enableTicketDispatch;
    }

    public void setEnableTicketDispatch(BooleanFilter enableTicketDispatch) {
        this.enableTicketDispatch = enableTicketDispatch;
    }

    public StringFilter getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(StringFilter timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SiteCriteria that = (SiteCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(budderflyId, that.budderflyId) &&
            Objects.equals(customerName, that.customerName) &&
            Objects.equals(status, that.status) &&
            Objects.equals(companyType, that.companyType) &&
            Objects.equals(storeNumber, that.storeNumber) &&
            Objects.equals(address, that.address) &&
            Objects.equals(city, that.city) &&
            Objects.equals(state, that.state) &&
            Objects.equals(zip, that.zip) &&
            Objects.equals(billingType, that.billingType) &&
            Objects.equals(paymentType, that.paymentType) &&
            Objects.equals(siteType, that.siteType) &&
            Objects.equals(ownerName, that.ownerName) &&
            Objects.equals(ownerEmail, that.ownerEmail) &&
            Objects.equals(ownerPhone, that.ownerPhone) &&
            Objects.equals(address1, that.address1) &&
            Objects.equals(address2, that.address2) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(taxExempt, that.taxExempt) &&
            Objects.equals(rollBilling, that.rollBilling) &&
            Objects.equals(emoVersion, that.emoVersion) &&
            Objects.equals(billingContact, that.billingContact) &&
            Objects.equals(siteContact, that.siteContact) &&
            Objects.equals(franchiseContact, that.franchiseContact) &&
            Objects.equals(parentSiteId, that.parentSiteId) &&
            Objects.equals(contactDeskId, that.contactDeskId) &&
            Objects.equals(enableTicketDispatch, that.enableTicketDispatch) &&
            Objects.equals(timeZoneId, that.timeZoneId) &&
            Objects.equals(contactId, that.contactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        budderflyId,
        customerName,
        status,
        companyType,
        storeNumber,
        address,
        city,
        state,
        zip,
        billingType,
        paymentType,
        siteType,
        ownerName,
        ownerEmail,
        ownerPhone,
        address1,
        address2,
        latitude,
        longitude,
        taxExempt,
        rollBilling,
        emoVersion,
        billingContact,
        siteContact,
        franchiseContact,
        parentSiteId,
        contactId,
        contactDeskId,
        enableTicketDispatch,
        timeZoneId
        );
    }

    @Override
    public String toString() {
        return "SiteCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (budderflyId != null ? "budderflyId=" + budderflyId + ", " : "") +
                (customerName != null ? "customerName=" + customerName + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (companyType != null ? "companyType=" + companyType + ", " : "") +
                (storeNumber != null ? "storeNumber=" + storeNumber + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (city != null ? "city=" + city + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (zip != null ? "zip=" + zip + ", " : "") +
                (billingType != null ? "billingType=" + billingType + ", " : "") +
                (paymentType != null ? "paymentType=" + paymentType + ", " : "") +
                (siteType != null ? "siteType=" + siteType + ", " : "") +
                (ownerName != null ? "ownerName=" + ownerName + ", " : "") +
                (ownerEmail != null ? "ownerEmail=" + ownerEmail + ", " : "") +
                (ownerPhone != null ? "ownerPhone=" + ownerPhone + ", " : "") +
                (address1 != null ? "address1=" + address1 + ", " : "") +
                (address2 != null ? "address2=" + address2 + ", " : "") +
                (latitude != null ? "latitude=" + latitude + ", " : "") +
                (longitude != null ? "longitude=" + longitude + ", " : "") +
                (taxExempt != null ? "taxExempt=" + taxExempt + ", " : "") +
                (rollBilling != null ? "rollBilling=" + rollBilling + ", " : "") +
                (emoVersion != null ? "emoVersion=" + emoVersion + ", " : "") +
                (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
                (billingContact != null ? "billingContact=" + billingContact + ", " : "") +
                (siteContact != null ? "siteContact=" + siteContact + ", " : "") +
                (franchiseContact != null ? "franchiseContact=" + franchiseContact + ", " : "") +
                (parentSiteId != null ? "parentSiteId=" + parentSiteId + ", " : "") +
                (contactId != null ? "contactId=" + contactId + ", " : "") +
                (contactDeskId != null ? "contactDeskId=" + contactDeskId + ", " : "") +
                (enableTicketDispatch != null ? "enableTicketDispatch=" + enableTicketDispatch + ", " : "") +
                (timeZoneId != null ? "timeZoneId=" + timeZoneId + ", " : "") +
            "}";
    }

}
