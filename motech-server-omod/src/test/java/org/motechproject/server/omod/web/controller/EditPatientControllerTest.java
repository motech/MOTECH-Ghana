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

package org.motechproject.server.omod.web.controller;

import junit.framework.TestCase;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.MessageLanguage;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.Date;

import static org.easymock.EasyMock.*;

public class EditPatientControllerTest extends TestCase {

    RegistrarBean registrarBean;
    EditPatientController controller;
    ContextService contextService;
    MotechService motechService;
    WebModelConverter webModelConverter;
    SessionStatus status;
    PatientService patientService;
    OpenmrsBean openmrsBean;

    @Override
    protected void setUp() {
        registrarBean = createMock(RegistrarBean.class);
        openmrsBean = createMock(OpenmrsBean.class);
        contextService = createMock(ContextService.class);
        webModelConverter = createMock(WebModelConverter.class);
        controller = new EditPatientController();
        controller.setRegistrarBean(registrarBean);
        controller.setOpenmrsBean(openmrsBean);
        controller.setContextService(contextService);
        controller.setWebModelConverter(webModelConverter);
        motechService = createMock(MotechService.class);

        patientService = createMock(PatientService.class);
        status = createMock(SessionStatus.class);
    }

    @Override
    protected void tearDown() {
        controller = null;
        registrarBean = null;
        openmrsBean = null;
        patientService = null;
        contextService = null;
        motechService = null;
        status = null;
    }

    public void testGetWebPatientMissingId() {
        Integer patientId = null;

        replay(registrarBean, patientService);

        WebPatient webPatient = controller.getWebPatient(patientId);

        verify(registrarBean, patientService);

        assertNull("Patient is not new for null id", webPatient.getId());
    }

    public void testGetWebPatientInvalidId() {
        Integer patientId = 1;
        Patient patient = null;

        expect(contextService.getPatientService()).andReturn(patientService);
        expect(patientService.getPatient(patientId)).andReturn(patient);

        replay(registrarBean, contextService, patientService);

        WebPatient webPatient = controller.getWebPatient(patientId);

        verify(registrarBean, contextService, patientService);

        assertNull("Patient is not new for invalid id", webPatient.getId());
    }

    public void testGetWebPatientValidId() {
        Integer patientId = 1;
        Patient patient = new Patient(patientId);

        expect(contextService.getPatientService()).andReturn(patientService);
        expect(patientService.getPatient(patientId)).andReturn(patient);
        webModelConverter.patientToWeb(eq(patient), (WebPatient) anyObject());

        replay(registrarBean, contextService, patientService, webModelConverter);

        WebPatient webPatient = controller.getWebPatient(patientId);

        verify(registrarBean, contextService, patientService, webModelConverter);

        assertNotNull(webPatient);
    }

    public void testEditPatient() {
        Integer patientId = 1, communityId = 11112, motherId = 2;
        String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
        String region = "Region", district = "District", address = "Address", nhis = "1234DEF";
        String phoneNumber = "0123456789";
        Boolean birthDateEst = true, enroll = true, consent = true, insured = true;
        Date date = new Date();
        Gender sex = Gender.FEMALE;
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        MediaType mediaType = MediaType.TEXT;
        String language = "en";
        DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;

        WebPatient patient = new WebPatient();
        patient.setId(patientId);
        patient.setFirstName(firstName);
        patient.setMiddleName(middleName);
        patient.setLastName(lastName);
        patient.setPrefName(prefName);
        patient.setBirthDate(date);
        patient.setBirthDateEst(birthDateEst);
        patient.setSex(sex);
        patient.setInsured(insured);
        patient.setNhis(nhis);
        patient.setNhisExpDate(date);
        patient.setMotherMotechId(motherId);
        patient.setRegion(region);
        patient.setDistrict(district);
        patient.setCommunityId(communityId);
        patient.setAddress(address);
        patient.setPhoneNumber(phoneNumber);
        patient.setPhoneType(phoneType);
        patient.setMediaType(mediaType);
        patient.setLanguage(language);
        patient.setDueDate(date);
        patient.setEnroll(enroll);
        patient.setConsent(consent);
        patient.setDayOfWeek(dayOfWeek);
        patient.setTimeOfDay(date);
        patient.setFacility(11117);

        Errors errors = new BeanPropertyBindingResult(patient, "patient");
        ModelMap model = new ModelMap();

        Patient openmrsPatient = new Patient(1);
        Patient motherPatient = new Patient(2);
        Community community = new Community();

        expect(registrarBean.getPatientById(patientId)).andReturn(
                openmrsPatient);
        expect(openmrsBean.getPatientByMotechId(motherId.toString()))
                .andReturn(motherPatient);
        expect(registrarBean.getCommunityById(communityId))
                .andReturn(community);
        Facility facility = new Facility();
        facility.setFacilityId(11117);
        expect(registrarBean.getFacilityById(facility.getFacilityId())).andReturn(facility);

        registrarBean.editPatient(openmrsPatient, firstName, middleName,
                lastName, prefName, date, birthDateEst, sex, insured, nhis,
                date, motherPatient, community, address, phoneNumber, date,
                enroll, consent, phoneType, mediaType, language, dayOfWeek,
                date, facility);

        status.setComplete();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllFacilities()).andReturn(new ArrayList<Facility>());
        expect(motechService.getAllLanguages()).andReturn(new ArrayList<MessageLanguage>());

        replay(registrarBean, status, contextService, motechService, openmrsBean);

        controller.submitForm(patient, errors, model, status);

        verify(registrarBean, status, contextService, motechService,openmrsBean);

        assertTrue("Missing success message in model", model.containsAttribute("successMsg"));
    }
}
