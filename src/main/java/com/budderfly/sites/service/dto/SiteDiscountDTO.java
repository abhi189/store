package com.budderfly.sites.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the SiteDiscount entity.
 */
public class SiteDiscountDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    private String name;

    private BigDecimal percentage;

    private Boolean autoUpdate;

    private Boolean accrued;

    private Boolean override;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public Boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public Boolean isAccrued() {
        return accrued;
    }

    public void setAccrued(Boolean accrued) {
        this.accrued = accrued;
    }

    public Boolean isOverride() {
        return override;
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SiteDiscountDTO siteDiscountDTO = (SiteDiscountDTO) o;
        if (siteDiscountDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), siteDiscountDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SiteDiscountDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", percentage=" + getPercentage() +
            ", autoUpdate='" + isAutoUpdate() + "'" +
            ", accrued='" + isAccrued() + "'" +
            ", override='" + isOverride() + "'" +
            "}";
    }
}
