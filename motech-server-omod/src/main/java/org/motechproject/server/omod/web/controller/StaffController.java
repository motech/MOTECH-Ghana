package org.motechproject.server.omod.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.web.model.WebStaff;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/motechmodule/staff")
public class StaffController {

	protected final Log log = LogFactory.getLog(StaffController.class);

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@ModelAttribute("staff")
	public WebStaff getWebStaff() {
		WebStaff staff = new WebStaff();
		return staff;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String viewStaffForm(ModelMap model) {

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

		if (!errors.hasErrors()) {
			User user = registrarBean.registerStaff(staff.getFirstName(), staff
					.getLastName(), staff.getPhone(), staff.getType());

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
					new Integer[] { lengthLimit },
					"Specified text is longer than max characters.");
		}
	}

}
