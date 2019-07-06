package com.budderfly.sites.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;

/**
 * Criteria class for the SiteDiscount entity. This class is used in SiteDiscountResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /site-discounts?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SiteDiscountCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter percentage;

    private BooleanFilter autoUpdate;

    private BooleanFilter accrued;

    private BooleanFilter override;

    private LongFilter siteId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimalFilter percentage) {
        this.percentage = percentage;
    }

    public BooleanFilter getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(BooleanFilter autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public BooleanFilter getAccrued() {
        return accrued;
    }

    public void setAccrued(BooleanFilter accrued) {
        this.accrued = accrued;
    }

    public BooleanFilter getOverride() {
        return override;
    }

    public void setOverride(BooleanFilter override) {
        this.override = override;
    }

    public LongFilter getSiteId() {
        return siteId;
    }

    public void setSiteId(LongFilter siteId) {
        this.siteId = siteId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SiteDiscountCriteria that = (SiteDiscountCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(percentage, that.percentage) &&
            Objects.equals(autoUpdate, that.autoUpdate) &&
            Objects.equals(accrued, that.accrued) &&
            Objects.equals(override, that.override) &&
            Objects.equals(siteId, that.siteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        percentage,
        autoUpdate,
        accrued,
        override,
        siteId
        );
    }

    @Override
    public String toString() {
        return "SiteDiscountCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (percentage != null ? "percentage=" + percentage + ", " : "") +
                (autoUpdate != null ? "autoUpdate=" + autoUpdate + ", " : "") +
                (accrued != null ? "accrued=" + accrued + ", " : "") +
                (override != null ? "override=" + override + ", " : "") +
                (siteId != null ? "siteId=" + siteId + ", " : "") +
            "}";
    }

}
