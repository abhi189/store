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
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the Contact entity. This class is used in ContactResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /contacts?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ContactCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter contactType;

    private StringFilter name;

    private StringFilter phoneNumber;

    private StringFilter webPage;

    private StringFilter notes;

    private StringFilter city;

    private StringFilter street;

    private StringFilter zipCode;

    private StringFilter country;

    private StringFilter contactEmail;

    private InstantFilter createdDate;

    private InstantFilter lastModifyDate;

    private StringFilter modifiedBy;

    private StringFilter createdBy;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getContactType() {
        return contactType;
    }

    public void setContactType(StringFilter contactType) {
        this.contactType = contactType;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getWebPage() {
        return webPage;
    }

    public void setWebPage(StringFilter webPage) {
        this.webPage = webPage;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public StringFilter getCity() {
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getStreet() {
        return street;
    }

    public void setStreet(StringFilter street) {
        this.street = street;
    }

    public StringFilter getZipCode() {
        return zipCode;
    }

    public void setZipCode(StringFilter zipCode) {
        this.zipCode = zipCode;
    }

    public StringFilter getCountry() {
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public StringFilter getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(StringFilter contactEmail) {
        this.contactEmail = contactEmail;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(InstantFilter lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public StringFilter getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(StringFilter modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ContactCriteria that = (ContactCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(contactType, that.contactType) &&
            Objects.equals(name, that.name) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(webPage, that.webPage) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(city, that.city) &&
            Objects.equals(street, that.street) &&
            Objects.equals(zipCode, that.zipCode) &&
            Objects.equals(country, that.country) &&
            Objects.equals(contactEmail, that.contactEmail) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifyDate, that.lastModifyDate) &&
            Objects.equals(modifiedBy, that.modifiedBy) &&
            Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        contactType,
        name,
        phoneNumber,
        webPage,
        notes,
        city,
        street,
        zipCode,
        country,
        contactEmail,
        createdDate,
        lastModifyDate,
        modifiedBy,
        createdBy
        );
    }

    @Override
    public String toString() {
        return "ContactCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (contactType != null ? "contactType=" + contactType + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
                (webPage != null ? "webPage=" + webPage + ", " : "") +
                (notes != null ? "notes=" + notes + ", " : "") +
                (city != null ? "city=" + city + ", " : "") +
                (street != null ? "street=" + street + ", " : "") +
                (zipCode != null ? "zipCode=" + zipCode + ", " : "") +
                (country != null ? "country=" + country + ", " : "") +
                (contactEmail != null ? "contactEmail=" + contactEmail + ", " : "") +
                (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
                (lastModifyDate != null ? "lastModifyDate=" + lastModifyDate + ", " : "") +
                (modifiedBy != null ? "modifiedBy=" + modifiedBy + ", " : "") +
                (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            "}";
    }

}
