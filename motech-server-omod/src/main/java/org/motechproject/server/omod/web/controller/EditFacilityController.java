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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.util.MotechConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
@SessionAttributes("facility")
public class EditFacilityController {

    private Log log = LogFactory.getLog(EditFacilityController.class);

    private ContextService contextService;

    @Autowired
    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    @ModelAttribute("communities")
    public List<Community> getCommunities() {
        return contextService.getMotechService().getAllCommunities(true);
    }

    @ModelAttribute("facility")
    public Facility getFacility(
            @RequestParam(required = true) Integer facilityId) {
        Facility facility = new Facility();
        if (facilityId != null) {
            facility = contextService.getMotechService().getFacilityById(
                    facilityId);
        }
        return facility;
    }

    @RequestMapping(value = "/module/motechmodule/editfacility", method = RequestMethod.GET)
    public String viewFacilityForm(
            @RequestParam(required = true) Integer facilityId) {
        return "/module/motechmodule/editfacility";
    }

    @RequestMapping(value = "/module/motechmodule/editfacility", method = RequestMethod.POST)
    public String saveFacility(Facility facility, Errors errors,
                               ModelMap model, SessionStatus status) {

        log.debug("Saving Facility");

        if (facility.getPhoneNumber() != null
                && !facility.getPhoneNumber().matches(
                MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("phoneNumber",
                    "motechmodule.phoneNumber.invalid");
        }

        validateAdditionalPhoneNumbers(facility, errors);

        if (!errors.hasErrors()) {
            contextService.getMotechService().saveFacility(facility);
            status.setComplete();
            return "redirect:/module/motechmodule/facility.form";
        }
        return "/module/motechmodule/editfacility";
    }

    private void validateAdditionalPhoneNumbers(Facility facility, Errors errors) {

        if (!isBlank(facility.getAdditionalPhoneNumber1()) && !facility.getAdditionalPhoneNumber1().matches(MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("additionalPhoneNumber1",
                    "motechmodule.phoneNumber.invalid");
        }

        if (!isBlank(facility.getAdditionalPhoneNumber2()) && !facility.getAdditionalPhoneNumber2().matches(MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("additionalPhoneNumber2",
                    "motechmodule.phoneNumber.invalid");
        }

        if (!isBlank(facility.getAdditionalPhoneNumber3()) && !facility.getAdditionalPhoneNumber3().matches(MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("additionalPhoneNumber3",
                    "motechmodule.phoneNumber.invalid");
        }
        if (!isBlank(facility.getAdditionalPhoneNumber4()) && !facility.getAdditionalPhoneNumber4().matches(MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("additionalPhoneNumber4",
                    "motechmodule.phoneNumber.invalid");
        }
    }

    boolean isBlank(String input) {
        if (input == null)
            return true;
        if (input == "")
            return true;
        if (input.trim() == "")
            return true;
        return false;
    }


}
