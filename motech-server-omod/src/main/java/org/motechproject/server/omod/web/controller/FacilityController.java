package org.motechproject.server.omod.web.controller;

import java.util.List;

import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FacilityController {

	@Autowired
	private ContextService contextService;

	@ModelAttribute("facilities")
	public List<Facility> getFacilities() {
		return contextService.getMotechService().getAllFacilities();
	}

	@RequestMapping(value = "/module/motechmodule/facility", method = RequestMethod.GET)
	public String viewFacilities() {
		return "/module/motechmodule/facility";
	}
}
