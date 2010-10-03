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
