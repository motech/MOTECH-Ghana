package org.motechproject.server.openmrs.module.web.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.openmrs.module.ContextService;
import org.motechproject.server.openmrs.module.web.model.WebModelConverter;
import org.motechproject.server.openmrs.module.web.model.WebPatient;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/module/motechmodule/demo-enrollpatient")
@SessionAttributes("enrollpatient")
public class DemoEnrollController {

	private static Log log = LogFactory.getLog(MotherController.class);

	private WebModelConverter webModelConverter;

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	private ContextService contextService;

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@Autowired
	public void setWebModelConverter(WebModelConverter webModelConverter) {
		this.webModelConverter = webModelConverter;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@ModelAttribute("enrollpatient")
	public WebPatient getWebPatient(@RequestParam(required = false) Integer id,
			HttpSession session) {
		WebPatient result = new WebPatient();
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				webModelConverter.patientToWeb(patient, result);
			}
		}

		String regNum = (String) session.getAttribute("demoLastGHSRN");
		if (regNum != null)
			result.setRegNumberGHS(regNum);

		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitForm(
			@ModelAttribute("enrollpatient") WebPatient patient, Errors errors,
			ModelMap model, SessionStatus status, HttpSession session) {

		log.debug("Register Demo Patient");

		ValidationUtils.rejectIfEmpty(errors, "regNumberGHS",
				"motechmodule.regNumberGHS.required");

		if (patient.getRegNumberGHS() != null
				&& registrarBean
						.getPatientByMotechId(patient.getRegNumberGHS()) == null) {
			errors.rejectValue("regNumberGHS",
					"motechmodule.regNumberGHS.notexist");
		}

		if (!Boolean.TRUE.equals(patient.getTermsConsent())) {
			errors.rejectValue("termsConsent",
					"motechmodule.termsConsent.required");
		}

		if (!errors.hasErrors()) {
			registrarBean.demoEnrollPatient(patient.getRegNumberGHS());

			model.addAttribute("successMsg",
					"motechmodule.Demo.Patient.enroll.success");

			status.setComplete();

			session.removeAttribute("demoLastGHSRN");

			return "redirect:/module/motechmodule/demo-success.htm";
		}

		return "/module/motechmodule/demo-enrollpatient";
	}
}
