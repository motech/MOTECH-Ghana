package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebRCTPatient;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RCTPatientsController {

    @Autowired
    ContextService contextService;



    @RequestMapping(value = "/module/motechmodule/viewrctpatients", method = RequestMethod.GET)
    public String list(ModelMap modelMap){
        return "/module/motechmodule/viewrctpatients";
    }

    @ModelAttribute("rctpatients")
    public List<WebRCTPatient> getRCTPatients(){
        List<RCTPatient> allRCTPatients = contextService.getMotechService().getRctService().getAllRCTPatients();
        OpenmrsBean openmrsBean = contextService.getMotechService().getOpenmrsBean();
        ArrayList<WebRCTPatient> webRCTPatients = new ArrayList<WebRCTPatient>();
        for(RCTPatient rctPatient : allRCTPatients){
            Patient patientByMotechId = openmrsBean.getPatientByMotechId(rctPatient.getStudyId());
            WebRCTPatient webRCTPatient = new WebRCTPatient(patientByMotechId.getGivenName(), patientByMotechId.getFamilyName(),
                    rctPatient.getStudyId(), rctPatient.getEnrolledBy().getSystemId(), rctPatient.getEnrolledBy().getGivenName(),
                    rctPatient.getEnrolledBy().getFamilyName());
            webRCTPatients.add(webRCTPatient);
        }
        return webRCTPatients;
    }


}
