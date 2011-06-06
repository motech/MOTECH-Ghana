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
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.server.omod.web.model.WebStaff;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/module/motechmodule/staff")
public class StaffController {

    protected final Log log = LogFactory.getLog(StaffController.class);

    @Autowired
    @Qualifier("registrarBean")
    private RegistrarBean registrarBean;

    @Autowired
    @Qualifier("openmrsBean")
    private OpenmrsBean openmrsBean;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder
                .registerCustomEditor(String.class, new StringTrimmerEditor(
                        true));
    }

    @ModelAttribute("staff")
    public WebStaff getWebStaff() {
        return new WebStaff();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewStaffForm(@RequestParam(value = "staffId", required = false) String staffId, ModelMap model) {
        if (staffId != null) {
            User staff = openmrsBean.getStaffBySystemId(staffId);
            PersonAttribute phoneNumberAttr = staff.getAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName());
            PersonAttribute staffTypeAttr = staff.getAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName());

            WebStaff webStaff = new WebStaff(staff.getPersonName().getGivenName(), staff.getPersonName().getFamilyName(), phoneNumberAttr.getValue(), staffTypeAttr.getValue(), staffId);
            model.addAttribute("staff", webStaff);
        }
        model.addAttribute("staffTypes", registrarBean.getStaffTypes());
        return "/module/motechmodule/staff";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerStaff(@ModelAttribute("staff") WebStaff staff,
                                Errors errors, ModelMap model) {

        log.debug("Register Staff");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
                "motechmodule.firstName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
                "motechmodule.lastName.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type",
                "motechmodule.staffType.required");

        validateTextLength(errors, "firstName", staff.getFirstName(),
                MotechConstants.MAX_STRING_LENGTH_OPENMRS);
        validateTextLength(errors, "lastName", staff.getLastName(),
                MotechConstants.MAX_STRING_LENGTH_OPENMRS);
        validateTextLength(errors, "phone", staff.getPhone(),
                MotechConstants.MAX_STRING_LENGTH_OPENMRS);

        if (staff.getPhone() != null
                && !staff.getPhone().matches(
                MotechConstants.PHONE_REGEX_PATTERN)) {
            errors.rejectValue("phone", "motechmodule.phoneNumber.invalid");
        }

        if (!errors.hasErrors()) {
            User user = registrarBean.registerStaff(staff.getFirstName(), staff
                    .getLastName(), staff.getPhone(), staff.getType(), staff.getStaffId());

            model.addAttribute("successMsg", "Added user: Name = "
                    + user.getPersonName() + ", Staff ID = "
                    + user.getSystemId());
        }
        model.addAttribute("staffTypes", registrarBean.getStaffTypes());
        return "/module/motechmodule/staff";
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
