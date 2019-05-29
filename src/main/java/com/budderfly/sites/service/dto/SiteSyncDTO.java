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

    public SiteSyncDTO() {
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
            '}';
    }
}
