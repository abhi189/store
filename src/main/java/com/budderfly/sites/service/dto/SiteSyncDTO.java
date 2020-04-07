package com.budderfly.sites.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the syncing the Site entity with data received from Injobs.
 */
public class SiteSyncDTO implements Serializable {

    private Long id;

    private String budderflyId;

    private String emoVersion;

    private String contactEmail;

    private String billingEmail;

    private String franchiseEmail;

    private String contactDeskId;

    private String latitude;

    private String longitude;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    public SiteSyncDTO() {
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getFranchiseEmail() {
        return franchiseEmail;
    }

    public void setFranchiseEmail(String franchiseEmail) {
        this.franchiseEmail = franchiseEmail;
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

    public SiteSyncDTO(String budderflyId) {
        this.budderflyId = budderflyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmoVersion() {
        return emoVersion;
    }

    public void setEmoVersion(String emoVersion) {
        this.emoVersion = emoVersion;
    }

    public String getBudderflyId() {
        return budderflyId;
    }

    public void setBudderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
    }
    public String getContactDeskId() {
        return contactDeskId;
    }

    public void setContactDeskId(String contactDeskId) {
        this.contactDeskId = contactDeskId;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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
        return "SiteSyncDTO{" +
            "id=" + getId() +
            ", emoVersion='" + getEmoVersion() + '\'' +
            ", budderflyId='" + getBudderflyId() + '\'' +
            ", contactEmail='" + getContactEmail() + '\'' +
            ", franchiseEmail='" + getFranchiseEmail() + '\'' +
            ", billingEmail='" + getBillingEmail() + '\'' +
            ", contactDeskId='" + getContactDeskId() + '\'' +
            ", latitude='" + getLatitude() + '\'' +
            ", longitude='" + getLongitude() + '\'' +
            ", city='" + getCity() + '\'' +
            ", state='" + getState() + '\'' +
            ", zipCode='" + getZipCode() + '\'' +
            '}';
    }
}
