package com.budderfly.sites.service.mapper;

import com.budderfly.sites.domain.*;
import com.budderfly.sites.service.dto.SiteDiscountDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity SiteDiscount and its DTO SiteDiscountDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SiteDiscountMapper extends EntityMapper<SiteDiscountDTO, SiteDiscount> {


    @Mapping(target = "site", ignore = true)
    SiteDiscount toEntity(SiteDiscountDTO siteDiscountDTO);

    default SiteDiscount fromId(Long id) {
        if (id == null) {
            return null;
        }
        SiteDiscount siteDiscount = new SiteDiscount();
        siteDiscount.setId(id);
        return siteDiscount;
    }
}
