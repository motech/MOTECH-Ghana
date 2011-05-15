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

package org.motechproject.server.model;

import org.openmrs.Location;
import org.openmrs.Patient;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Facility implements Serializable {

    private static final long serialVersionUID = -791545747208269078L;

    private Long id;
    private Integer facilityId;
    private Location location;
    private String phoneNumber;
    private String additionalPhoneNumber1;
    private String additionalPhoneNumber2;
    private String additionalPhoneNumber3;
    private String additionalPhoneNumber4;
    private Set<Community> communities;
    private Set<Patient> patients = new HashSet<Patient>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(Set<Community> communities) {
        this.communities = communities;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    public boolean addPatient(Patient patient) {
        return getPatients().add(patient);
    }

    public boolean hasPatients(Integer numberOfPatients) {
        return patients.size() == numberOfPatients;
    }

    public String name() {
        return location.getName();
    }

    public boolean hasPatients(Patient patient) {
        return patients.contains(patient);
    }

    public boolean remove(Patient patient) {
        return patients.remove(patient);
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

    public void setAdditionalPhoneNumber2(String additionalPhoneNumber2) {
        this.additionalPhoneNumber2 = additionalPhoneNumber2;
    }

    public String getAdditionalPhoneNumber3() {
        return additionalPhoneNumber3;
    }

    public void setAdditionalPhoneNumber3(String additionalPhoneNumber3) {
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
    }

    public String getAdditionalPhoneNumber4() {
        return additionalPhoneNumber4;
    }

    public void setAdditionalPhoneNumber4(String additionalPhoneNumber4) {
        this.additionalPhoneNumber4 = additionalPhoneNumber4;
    }
}
