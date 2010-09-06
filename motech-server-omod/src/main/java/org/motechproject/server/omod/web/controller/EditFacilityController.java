package org.motechproject.server.omod.web.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

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

		if (!errors.hasErrors()) {
			contextService.getMotechService().saveFacility(facility);
			status.setComplete();
			return "redirect:/module/motechmodule/facility.form";
		}
		return "/module/motechmodule/editfacility";
	}
}
