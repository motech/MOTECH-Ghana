package org.motechproject.server.omod.web.model;

import org.motechproject.server.model.Community;

public class WebCommunity {

    public WebCommunity(){

    }

    public WebCommunity(Community community) {
        this. communityId = community.getCommunityId();
        this.name = community.getName();
        this.facilityId = community.getFacility().getFacilityId();
    }

    private Integer communityId;

    private String name;

    private Integer facilityId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }
}
