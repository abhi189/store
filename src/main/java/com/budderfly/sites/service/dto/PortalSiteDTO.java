package com.budderfly.sites.service.dto;

import com.budderfly.sites.domain.enumeration.PaymentType;
import java.io.Serializable;

/*
    This class contains the properties of a SiteDTO that a portal user can modify
*/
public class PortalSiteDTO implements Serializable {

    public PaymentType paymentType;
    public Long id;

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
