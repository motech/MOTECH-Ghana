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

import flexjson.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.omod.MotechIdVerhoeffValidator;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.RegistrantType;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@SessionAttributes("patient")
public class PatientController extends BasePatientController {

    private static Log log = LogFactory.getLog(PatientController.class);

    private WebModelConverter webModelConverter;

    @Autowired
    @Qualifier("registrarBean")
    private RegistrarBean registrarBean;

    @Autowired
    @Qualifier("openmrsBean")
    private OpenmrsBean openmrsBean;
    private static final String EMPTY = "";

    @Autowired
    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }

    public void setOpenmrsBean(OpenmrsBean openmrsBean) {
        this.openmrsBean = openmrsBean;
    }

    @Autowired
    public void setWebModelConverter(WebModelConverter webModelConverter) {
        this.webModelConverter = webModelConverter;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        String datePattern = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, datePattern.length()));

        String timePattern = MotechConstants.TIME_FORMAT_DELIVERY_TIME;
        SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, "timeOfDay", new CustomDateEditor(timeFormat, true, timePattern.length()));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.GET)
    public void viewForm(@RequestParam(required = false) Integer id, ModelMap model) {
        populateJavascriptMaps(model, new WebPatient());
    }

    @ModelAttribute("patient")
    public WebPatient getWebPatient(@RequestParam(required = false) Integer id) {
        WebPatient webPatient = new WebPatient();
        // Set default region for new patients
        if (id != null) {
            Patient patient = contextService.getPatientService().getPatient(id);
            if (patient != null) {
                webModelConverter.patientToWeb(patient, webPatient);
            }
        }
        return webPatient;
    }

    @RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.POST)
    public String submitForm(@ModelAttribute("patient") WebPatient patient,
                             Errors errors, ModelMap model, SessionStatus status) {

        log.debug("Register Patient");

        if (patient.getMotechId() != null) {
            String motechIdString = patient.getMotechId().toString();
            boolean validId = false;
            MotechIdVerhoeffValidator validator = new MotechIdVerhoeffValidator();
            try {
                validId = validator.isValid(motechIdString);
            } catch (Exception e) {
            }
            if (!validId) {
                errors.rejectValue("motechId", "motechmodule.motechId.invalid");
            } else if (openmrsBean.getPatientByMotechId(motechIdString) != null) {
                errors.rejectValue("motechId", "motechmodule.motechId.nonunique");
            }
        }

        if (patient.getRegistrantType() == RegistrantType.PREGNANT_MOTHER) {

            if (patient.getDueDate() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 9);
                if (patient.getDueDate().after(calendar.getTime())) {
                    errors.rejectValue("dueDate", "motechmodule.dueDate.overninemonths");
                }
            }
        } else if (patient.getRegistrantType() == RegistrantType.CHILD_UNDER_FIVE) {
            if (patient.getBirthDate() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -5);
                if (patient.getBirthDate().before(calendar.getTime())) {
                    errors.rejectValue("birthDate",
                            "motechmodule.birthDate.notunderfive");
                }
            }
        }
        
        if (patient.getBirthDate() != null) {
            if (patient.getBirthDate().after(Calendar.getInstance().getTime())) {
                errors.rejectValue("birthDate", "motechmodule.birthdate.future");
            }
        }
        
        Community community = null;
        if (patient.getCommunityId() != null) {
            community = registrarBean
                    .getCommunityById(patient.getCommunityId());
            if (community == null) {
                errors.rejectValue("communityId", "motechmodule.communityId.notexist");
            }
        }

        Facility facility = null;
        if (patient.getFacility() != null) {
            facility = registrarBean.getFacilityById(patient.getFacility());
            if (facility == null) {
                errors.rejectValue("facility", "motechmodule.facility.does.not.exist");
            }
        }

        Patient mother = null;
        if (patient.getMotherMotechId() != null) {
            mother = openmrsBean.getPatientByMotechId(patient
                    .getMotherMotechId().toString());
            if (mother == null) {
                errors.rejectValue("motherMotechId", "motechmodule.motechId.notexist");
            }
        }

        if (patient.getPhoneNumber() != null
                && !patient.getPhoneNumber().matches(
                MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("phoneNumber", "motechmodule.phoneNumber.invalid");
        }

        if (!errors.hasErrors()) {
            registrarBean.registerPatient(patient.getRegistrationMode(),
                    patient.getMotechId(), patient.getRegistrantType(), patient
                    .getFirstName(), patient.getMiddleName(), patient
                    .getLastName(), patient.getPrefName(), patient
                    .getBirthDate(), patient.getBirthDateEst(), patient
                    .getSex(), patient.getInsured(), patient.getNhis(),
                    patient.getNhisExpDate(), mother, community, facility, patient
                    .getAddress(), patient.getPhoneNumber(), patient
                    .getDueDate(), patient.getDueDateConfirmed(),
                    patient.getEnroll(), patient.getConsent(), patient
                    .getPhoneType(), patient.getMediaType(), patient
                    .getLanguage(), patient.getDayOfWeek(), patient
                    .getTimeOfDay(), patient.getInterestReason(),
                    patient.getHowLearned(), patient.getMessagesStartWeek());

            model.addAttribute("successMsg", "motechmodule.Patient.register.success");

            status.setComplete();

            return "redirect:/module/motechmodule/viewdata.form";
        }
        populateJavascriptMaps(model, patient);

        return "/module/motechmodule/patient";
    }

    @RequestMapping(value = "/module/motechmodule/patient/getMotherInfo.form", method = RequestMethod.GET)
    public String getMotherInformation(@RequestParam(required = true) String motechId,
                                        ModelMap model) {
        Patient patient = openmrsBean.getPatientByMotechId(motechId);
        WebPatient webPatient = new WebPatient();
        if ((patient != null) && (!StringUtils.isBlank(motechId))) {
            webModelConverter.patientToWeb(patient, webPatient);
        }
        String jsonData = new JSONSerializer().serialize(webPatient);
        model.addAttribute("patient", jsonData);
        return "/module/motechmodule/mother_data";
    }

    void validateTextLength(Errors errors, String fieldname, String fieldValue,
                            int lengthLimit) {

        if (fieldValue != null && fieldValue.length() > lengthLimit) {
            errors.rejectValue(fieldname, "motechmodule.string.maxlength",
                    new Integer[]{lengthLimit},
                    "Specified text is longer than max characters.");
        }
    }
}
