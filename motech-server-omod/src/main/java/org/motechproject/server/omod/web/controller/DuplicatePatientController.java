/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebDuplicatePatients;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class DuplicatePatientController {

    @Autowired
    private ContextService contextService;

    @RequestMapping(value = "/module/motechmodule/duplicatepatients", method = RequestMethod.GET)
    public String viewForm(ModelMap modelMap){
        modelMap.addAttribute(new WebDuplicatePatients());

        List<Patient> list = contextService.getMotechService().getAllDuplicatePatients();
        modelMap.addAttribute("duplicate_patients", list);
        return "/module/motechmodule/duplicatepatients";
    }

    @RequestMapping(value = "/module/motechmodule/duplicatepatients", method = RequestMethod.POST)
    public String submitForm(@ModelAttribute("webDuplicatePatients") WebDuplicatePatients duplicatePatients, ModelMap modelMap){
        for(String uuid : duplicatePatients.getUuid()){
            Patient patient = contextService.getPatientService().getPatientByUuid(uuid);
            contextService.getMotechService().deletePatientIdentifier(patient.getPatientId());
            contextService.getPatientService().purgePatient(patient);
        }
        return "redirect:/module/motechmodule/duplicatepatients.form";
    }

}
