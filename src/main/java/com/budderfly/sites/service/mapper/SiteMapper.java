package com.budderfly.sites.service.mapper;

import com.budderfly.sites.domain.*;
import com.budderfly.sites.service.dto.SiteDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Site and its DTO SiteDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SiteMapper extends EntityMapper<SiteDTO, Site> {

    @Mapping(source = "parentSite.id", target = "parentSiteId")
    SiteDTO toDto(Site site);

    @Mapping(source = "parentSiteId", target = "parentSite")
    Site toEntity(SiteDTO siteDTO);

    default Site fromId(Long id) {
        if (id == null) {
            return null;
        }
        Site site = new Site();
        site.setId(id);
        return site;
    }
}
