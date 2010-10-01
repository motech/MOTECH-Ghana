package org.motechproject.server.omod.web.controller;

import java.util.List;

import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebFacility;
import org.openmrs.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

@Controller
public class FacilityController {

	@Autowired
	private ContextService contextService;

	@ModelAttribute("facilities")
	public List<Facility> getFacilities() {
		return contextService.getMotechService().getAllFacilities();
	}

	@RequestMapping(value = "/module/motechmodule/facility.form", method = RequestMethod.GET)
	public String viewFacilities() {
		return "/module/motechmodule/facility";
	}

    @RequestMapping(value = "/module/motechmodule/addfacility.form", method = RequestMethod.GET)
    public String viewAddFacilityForm(ModelMap modelMap){
        modelMap.addAttribute("facility", new WebFacility());
        return "/module/motechmodule/addfacility";
    }

    @RequestMapping(value = "/module/motechmodule/addfacility.form", method = RequestMethod.POST)
    public String submitAddFacility(@ModelAttribute("facility") WebFacility facility, Errors errors,ModelMap modelMap, SessionStatus status){
        if(contextService.getMotechService().getFacilityByLocationUuid(facility.getUuid()) != null){
            errors.rejectValue("uuid","motechmodule.Facility.invalid.location");
        }
        if(errors.hasErrors()){
            return "/module/motechmodule/addfacility";
        }
        Facility newFacility = new Facility();
        newFacility.setLocation(contextService.getLocationService().getLocationByUuid(facility.getUuid()));
        newFacility.setPhoneNumber(facility.getPhoneNumber());
        contextService.getRegistrarBean().saveFacility(newFacility);
        return "redirect:/module/motechmodule/facility.form";
    }

    @ModelAttribute("locations")
    public List<Location> getLocations(){
        return contextService.getLocationService().getAllLocations(false);
    }
}
