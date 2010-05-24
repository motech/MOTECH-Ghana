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
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
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

		String timePattern = MotechConstants.TIME_FORMAT_PERSON_ATTRIBUTE_DELIVERY_TIME;
		SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, "timeOfDay",
				new CustomDateEditor(timeFormat, true, timePattern.length()));

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

		Patient patient = null;
		if (pregnancy.getId() != null) {
			patient = registrarBean.getPatientById(pregnancy.getId());
			if (patient == null) {
				errors.reject("motechmodule.id.notexist");
			}
		} else {
			errors.reject("motechmodule.id.required");
		}

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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gravida",
				"motechmodule.gravida.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parity",
				"motechmodule.parity.required");
		if (pregnancy.getParity() != null && pregnancy.getGravida() != null
				&& pregnancy.getParity() > pregnancy.getGravida()) {
			errors.rejectValue("parity", "motechmodule.parity.range");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enroll",
				"motechmodule.enroll.required");

		if (Boolean.TRUE.equals(pregnancy.getEnroll())) {
			if (!Boolean.TRUE.equals(pregnancy.getConsent())) {
				errors.rejectValue("consent", "motechmodule.consent.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneType",
					"motechmodule.phoneType.required");
			if (pregnancy.getPhoneType() == ContactNumberType.PERSONAL
					|| pregnancy.getPhoneType() == ContactNumberType.HOUSEHOLD) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors,
						"phoneNumber", "motechmodule.phoneNumber.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mediaType",
					"motechmodule.mediaType.required");
			if (pregnancy.getPhoneType() == ContactNumberType.PUBLIC
					&& pregnancy.getMediaType() != null
					&& pregnancy.getMediaType() != MediaType.VOICE) {
				errors.rejectValue("mediaType", "motechmodule.mediaType.voice");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language",
					"motechmodule.language.required");
			if (pregnancy.getMediaType() == MediaType.TEXT
					&& pregnancy.getLanguage() != null
					&& !pregnancy.getLanguage().equals("en")) {
				errors.rejectValue("language", "motechmodule.language.english");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interestReason",
					"motechmodule.interestReason.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "howLearned",
					"motechmodule.howLearned.required");
		}
		if (pregnancy.getPhoneNumber() != null
				&& !pregnancy.getPhoneNumber().matches(
						MotechConstants.PHONE_REGEX_PATTERN)) {
			errors.rejectValue("phoneNumber",
					"motechmodule.phoneNumber.invalid");
		}

		validateTextLength(errors, "phoneNumber", pregnancy.getPhoneNumber(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);

		if (!errors.hasErrors()) {
			registrarBean.registerPregnancy(patient, pregnancy.getDueDate(),
					pregnancy.getDueDateConfirmed(), pregnancy.getGravida(),
					pregnancy.getParity(), pregnancy.getEnroll(), pregnancy
							.getConsent(), pregnancy.getPhoneNumber(),
					pregnancy.getPhoneType(), pregnancy.getMediaType(),
					pregnancy.getLanguage(), pregnancy.getDayOfWeek(),
					pregnancy.getTimeOfDay(), pregnancy.getInterestReason(),
					pregnancy.getHowLearned());
			;
			model.addAttribute("successMsg",
					"motechmodule.Pregnancy.register.success");

			status.setComplete();

			return "redirect:/module/motechmodule/viewdata.form";
		}

		return "/module/motechmodule/pregnancy";
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
