/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.omod.web.model;

import org.motechproject.server.model.ghana.Facility;
import org.openmrs.Location;

import java.util.Date;

public class WebFacility {
    public static final String BLANK_NAME = "motechmodule.name.blank";
    public static final String BLANK_COUNTRY = "motechmodule.country.blank";
    public static final String BLANK_REGION = "motechmodule.region.blank";
    public static final String BLANK_DISTRICT = "motechmodule.district.blank";
    public static final String BLANK_PROVINCE = "motechmodule.province.blank";

    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String REGION = "region";
    public static final String DISTRICT = "countyDistrict";
    public static final String PROVINCE = "stateProvince";
    public static final String INVALID_PHONE_NUMBER = "motechmodule.phoneNumber.invalid";
    public static final String PHONE_NUMBER = "phoneNumber";

    private String phoneNumber;
    private String additionalPhoneNumber1;
    private String additionalPhoneNumber2;
    private String additionalPhoneNumber3;
    private String country;
    private String region;
    private String countyDistrict;
    private String stateProvince;
    private String neighborhoodCell;
    private String name;
    private Location location;
    private Facility facility;

    public WebFacility() {
        this.location = new Location();
        this.facility = new Facility();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountyDistrict() {
        return countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public String getNeighborhoodCell() {
        return neighborhoodCell;
    }

    public void setNeighborhoodCell(String neighborhoodCell) {
        this.neighborhoodCell = neighborhoodCell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getAdditionalPhoneNumber1() {
        return additionalPhoneNumber1;
    }

    public void setAdditionalPhoneNumber1(String additionalPhoneNumber1) {
        this.additionalPhoneNumber1 = additionalPhoneNumber1;
    }

    public String getAdditionalPhoneNumber2() {
        return additionalPhoneNumber2;
    }

    public void setAdditionalPhoneNumber2(String additionalphoneNumber2) {
        this.additionalPhoneNumber2 = additionalphoneNumber2;
    }

    public String getAdditionalPhoneNumber3() {
        return additionalPhoneNumber3;
    }

    public void setAdditionalPhoneNumber3(String additionalPhoneNumber3) {
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
    }

    public Facility getFacility() {
        location.setCountry(this.country);
        location.setRegion(this.region);
        location.setStateProvince(this.stateProvince);
        location.setCountyDistrict(this.countyDistrict);
        location.setNeighborhoodCell(this.neighborhoodCell);
        location.setName(this.name);
        location.setDateCreated(new Date());

        facility.setPhoneNumber(this.phoneNumber);
        facility.setLocation(location);
        facility.setAdditionalPhoneNumber1(this.additionalPhoneNumber1);
        facility.setAdditionalPhoneNumber2(this.additionalPhoneNumber2);
        facility.setAdditionalPhoneNumber3(this.additionalPhoneNumber3);
        return facility;
    }
}
