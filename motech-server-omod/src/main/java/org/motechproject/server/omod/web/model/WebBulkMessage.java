package org.motechproject.server.omod.web.model;

import org.motechproject.server.model.PreferredLocation;
import org.motechproject.server.omod.web.encoder.SpaceEncoder;

public class WebBulkMessage {

    private String content;
    private String recipients;
    private String region;
    private String district;
    private String subDistrict;
    private Integer facility;

    public String getContent() {
        return content;
    }

    public Integer getFacility() {
        return facility;
    }

    public void setFacility(Integer facility) {
        this.facility = facility;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String content() {
        return content;
    }

    public String recipients() {
        return recipients;
    }

    public String content(SpaceEncoder spaceEncoder) {
        return spaceEncoder.encode(content);
    }

    public PreferredLocation getPreferredLocation() {
        return new PreferredLocation(region, district, subDistrict, facility, null);
    }
}
