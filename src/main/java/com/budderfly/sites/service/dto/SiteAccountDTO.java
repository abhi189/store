package com.budderfly.sites.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Created by dmaure on 30/11/18.
 */
public class SiteAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String utilityProvider;

    @NotNull
    private String accountNumber;

    private String customerName;

    private String customerCode;

    private String siteCode;

    private String customerType;

    private String siteAddress1;

    private String siteState;

    private Boolean arrears;

    private String budderflyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUtilityProvider() {
        return utilityProvider;
    }

    public void setUtilityProvider(String utilityProvider) {
        this.utilityProvider = utilityProvider;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getSiteAddress1() {
        return siteAddress1;
    }

    public void setSiteAddress1(String siteAddress1) {
        this.siteAddress1 = siteAddress1;
    }

    public String getSiteState() {
        return siteState;
    }

    public void setSiteState(String siteState) {
        this.siteState = siteState;
    }

    public Boolean getArrears() {
        return arrears;
    }

    public void setArrears(Boolean arrears) {
        this.arrears = arrears;
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

        SiteAccountDTO siteAccountDTO = (SiteAccountDTO) o;
        if(siteAccountDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), siteAccountDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SiteAccountDTO{" +
            "id=" + getId() +
            ", utilityProvider='" + getUtilityProvider() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", customerName='" + getCustomerName() + "'" +
            ", customerCode='" + getCustomerCode() + "'" +
            ", siteCode='" + getSiteCode() + "'" +
            ", customerType='" + getCustomerType() + "'" +
            ", siteState='" + getSiteState() + "'" +
            ", arrears=" + getArrears() +
            ", budderflyId='" + getBudderflyId() + "'" +
            "}";
    }
}
