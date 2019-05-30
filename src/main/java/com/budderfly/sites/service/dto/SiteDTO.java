package com.budderfly.sites.service.dto;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.budderfly.sites.domain.enumeration.SiteStatus;
import com.budderfly.sites.domain.enumeration.BillingType;
import com.budderfly.sites.domain.enumeration.PaymentType;
import com.budderfly.sites.domain.enumeration.SiteType;

/**
 * A DTO for the Site entity.
 */
public class SiteDTO extends AbstractAuditingDTO implements Serializable {

    @Id
    private Long id;

    @NotNull
    private String budderflyId;

    private String customerName;

    @NotNull
    private SiteStatus status;

    private String companyType;

    private String storeNumber;

    private String address;

    private String city;

    private String state;

    private String zip;

    @NotNull
    private BillingType billingType;

    @NotNull
    private PaymentType paymentType;

    private SiteType siteType;

    @NotNull
    private String ownerName;

    @NotNull
    private String ownerEmail;

    @NotNull
    private String ownerPhone;

    private String address1;

    private String address2;

    private String latitude;

    private String longitude;

    private Boolean taxExempt;

    private Boolean rollBilling;

    private String emoVersion;

    private String billingContact;

    private String siteContact;

    private String franchiseContact;

    private Long parentSiteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBudderflyId() {
        return budderflyId;
    }

    public void setBudderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public SiteStatus getStatus() {
        return status;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public SiteType getSiteType() {
        return siteType;
    }

    public void setSiteType(SiteType siteType) {
        this.siteType = siteType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean isTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(Boolean taxExempt) {
        this.taxExempt = taxExempt;
    }

    public Boolean isRollBilling() {
        return rollBilling;
    }

    public void setRollBilling(Boolean rollBilling) {
        this.rollBilling = rollBilling;
    }

    public String getEmoVersion() {
        return emoVersion;
    }

    public void setEmoVersion(String emoVersion) {
        this.emoVersion = emoVersion;
    }

    public String getBillingContact() {
        return billingContact;
    }

    public void setBillingContact(String billingContact) {
        this.billingContact = billingContact;
    }

    public String getSiteContact() {
        return siteContact;
    }

    public void setSiteContact(String siteContact) {
        this.siteContact = siteContact;
    }

    public String getFranchiseContact() {
        return franchiseContact;
    }

    public void setFranchiseContact(String franchiseContact) {
        this.franchiseContact = franchiseContact;
    }

    public Long getParentSiteId() {
        return parentSiteId;
    }

    public void setParentSiteId(Long siteId) {
        this.parentSiteId = siteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SiteDTO siteDTO = (SiteDTO) o;
        if(siteDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), siteDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SiteDTO{" +
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
            ", parentSite=" + getParentSiteId() +
            "}";
    }
}
