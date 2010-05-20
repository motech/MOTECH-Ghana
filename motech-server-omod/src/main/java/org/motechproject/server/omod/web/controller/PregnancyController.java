package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.Gender;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
@RequestMapping("/module/motechmodule/pregnancy")
@SessionAttributes("pregnancy")
public class PregnancyController {

	private static Log log = LogFactory.getLog(PregnancyController.class);

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
		String datePattern = "dd/MM/yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);

		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true, datePattern.length()));
		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@ModelAttribute("pregnancy")
	public WebPatient getWebPregnancy(@RequestParam(required = false) Integer id) {
		WebPatient result = new WebPatient();
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				webModelConverter.patientToWeb(patient, result);
			}
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitForm(@ModelAttribute("pregnancy") WebPatient pregnancy,
			Errors errors, ModelMap model, SessionStatus status) {

		log.debug("Register New Pregnancy on Existing Patient");

		if (!Gender.FEMALE.equals(pregnancy.getSex())) {
			errors.reject("motechmodule.sex.female.required");
		}
		if (registrarBean.getActivePregnancy(pregnancy.getId()) != null) {
			errors.reject("motechmodule.Pregnancy.active");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dueDate",
				"motechmodule.dueDate.required");
		if (pregnancy.getDueDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, 9);
			if (pregnancy.getDueDate().after(calendar.getTime())) {
				errors.rejectValue("dueDate",
						"motechmodule.dueDate.overninemonths");
			}
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dueDateConfirmed",
				"motechmodule.dueDateConfirmed.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors,
				"registerPregProgram",
				"motechmodule.registerPregProgram.required");

		if (Boolean.TRUE.equals(pregnancy.getRegisterPregProgram())) {
			if (!Boolean.TRUE.equals(pregnancy.getTermsConsent())) {
				errors.rejectValue("termsConsent",
						"motechmodule.termsConsent.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "primaryPhone",
					"motechmodule.primaryPhone.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,
					"primaryPhoneType",
					"motechmodule.primaryPhoneType.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mediaTypeInfo",
					"motechmodule.mediaTypeInfo.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,
					"mediaTypeReminder",
					"motechmodule.mediaTypeReminder.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageVoice",
					"motechmodule.languageVoice.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageText",
					"motechmodule.languageText.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "howLearned",
					"motechmodule.howLearned.required");
		}

		if (!errors.hasErrors()) {
			registrarBean.registerPregnancy(pregnancy.getId(), pregnancy
					.getDueDate(), pregnancy.getDueDateConfirmed(), pregnancy
					.getRegisterPregProgram(), pregnancy.getPrimaryPhone(),
					pregnancy.getPrimaryPhoneType(), pregnancy
							.getSecondaryPhone(), pregnancy
							.getSecondaryPhoneType(), pregnancy
							.getMediaTypeInfo(), pregnancy
							.getMediaTypeReminder(), pregnancy
							.getLanguageVoice(), pregnancy.getLanguageText(),
					pregnancy.getHowLearned());
			;
			model.addAttribute("successMsg",
					"motechmodule.Pregnancy.register.success");

			status.setComplete();

			return "redirect:/module/motechmodule/viewdata.form";
		}

		return "/module/motechmodule/pregnancy";
	}
}
