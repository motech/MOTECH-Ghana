package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechIdVerhoeffValidator;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
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
@RequestMapping("/module/motechmodule/patient")
@SessionAttributes("patient")
public class PatientController {

	private static Log log = LogFactory.getLog(PatientController.class);

	private WebModelConverter webModelConverter;

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	@Autowired
	@Qualifier("openmrsBean")
	private OpenmrsBean openmrsBean;

	private ContextService contextService;

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
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

		String timePattern = MotechConstants.TIME_FORMAT_DELIVERY_TIME;
		SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, "timeOfDay",
				new CustomDateEditor(timeFormat, true, timePattern.length()));

		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@ModelAttribute("regions")
	public List<String> getRegions() {
		return contextService.getMotechService().getRegions(
				MotechConstants.LOCATION_GHANA);
	}

	@ModelAttribute("districts")
	public List<String> getDistricts() {
		return contextService.getMotechService().getDistricts(
				MotechConstants.LOCATION_GHANA,
				MotechConstants.LOCATION_UPPER_EAST);
	}

	@ModelAttribute("communities")
	public List<Community> getCommunities() {
		return contextService.getMotechService().getCommunities(
				MotechConstants.LOCATION_GHANA,
				MotechConstants.LOCATION_UPPER_EAST,
				MotechConstants.LOCATION_KASSENA_NANKANA_WEST);
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@ModelAttribute("patient")
	public WebPatient getWebPatient(@RequestParam(required = false) Integer id) {
		WebPatient webPatient = new WebPatient();
		// Set default region for new patients
		webPatient.setRegion(MotechConstants.LOCATION_UPPER_EAST);
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				webModelConverter.patientToWeb(patient, webPatient);
			}
		}
		return webPatient;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitForm(@ModelAttribute("patient") WebPatient patient,
			Errors errors, ModelMap model, SessionStatus status) {

		log.debug("Register Patient");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registrationMode",
				"motechmodule.registrationMode.required");
		if (patient.getRegistrationMode() == RegistrationMode.USE_PREPRINTED_ID) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "motechId",
					"motechmodule.motechId.required");
		}
		if (patient.getMotechId() != null) {
			String motechIdString = patient.getMotechId().toString();
			boolean validId = false;
			MotechIdVerhoeffValidator validator = new MotechIdVerhoeffValidator();
			try {
				validId = validator.isValid(motechIdString);
			} catch (Exception e) {
			}
			if (!validId) {
				errors.rejectValue("motechId", "motechmodule.motechId.invalid");
			} else if (openmrsBean.getPatientByMotechId(motechIdString) != null) {
				errors.rejectValue("motechId",
						"motechmodule.motechId.nonunique");
			}
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registrantType",
				"motechmodule.registrantType.required");

		if (patient.getRegistrantType() == RegistrantType.PREGNANT_MOTHER) {
			if (patient.getSex() != null && patient.getSex() != Gender.FEMALE) {
				errors.rejectValue("sex", "motechmodule.sex.female.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dueDate",
					"motechmodule.dueDate.required");
			if (patient.getDueDate() != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, 9);
				if (patient.getDueDate().after(calendar.getTime())) {
					errors.rejectValue("dueDate",
							"motechmodule.dueDate.overninemonths");
				}
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors,
					"dueDateConfirmed",
					"motechmodule.dueDateConfirmed.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gravida",
					"motechmodule.gravida.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parity",
					"motechmodule.parity.required");
			if (patient.getParity() != null && patient.getGravida() != null
					&& patient.getParity() > patient.getGravida()) {
				errors.rejectValue("parity", "motechmodule.parity.range");
			}
		} else if (patient.getRegistrantType() == RegistrantType.CHILD_UNDER_FIVE) {
			if (patient.getBirthDate() != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, -5);
				if (patient.getBirthDate().before(calendar.getTime())) {
					errors.rejectValue("birthDate",
							"motechmodule.birthDate.notunderfive");
				}
			}
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
				"motechmodule.firstName.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
				"motechmodule.lastName.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthDate",
				"motechmodule.birthDate.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthDateEst",
				"motechmodule.birthDateEst.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sex",
				"motechmodule.sex.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "insured",
				"motechmodule.insured.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "region",
				"motechmodule.region.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "district",
				"motechmodule.district.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "communityId",
				"motechmodule.communityId.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address",
				"motechmodule.address.required");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enroll",
				"motechmodule.enroll.required");

		if (Boolean.TRUE.equals(patient.getEnroll())) {
			if (!Boolean.TRUE.equals(patient.getConsent())) {
				errors.rejectValue("consent", "motechmodule.consent.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneType",
					"motechmodule.phoneType.required");
			if (patient.getPhoneType() == ContactNumberType.PERSONAL
					|| patient.getPhoneType() == ContactNumberType.HOUSEHOLD) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors,
						"phoneNumber", "motechmodule.phoneNumber.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mediaType",
					"motechmodule.mediaType.required");
			if (patient.getPhoneType() == ContactNumberType.PUBLIC
					&& patient.getMediaType() != null
					&& patient.getMediaType() != MediaType.VOICE) {
				errors.rejectValue("mediaType", "motechmodule.mediaType.voice");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language",
					"motechmodule.language.required");
			if (patient.getMediaType() == MediaType.TEXT
					&& patient.getLanguage() != null
					&& !patient.getLanguage().equals("en")) {
				errors.rejectValue("language", "motechmodule.language.english");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interestReason",
					"motechmodule.interestReason.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "howLearned",
					"motechmodule.howLearned.required");
			if (patient.getRegistrantType() == RegistrantType.OTHER) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors,
						"messagesStartWeek",
						"motechmodule.messagesStartWeek.required");
			}
		}

		Community community = null;
		if (patient.getCommunityId() != null) {
			community = registrarBean
					.getCommunityById(patient.getCommunityId());
			if (community == null) {
				errors.rejectValue("communityId",
						"motechmodule.communityId.notexist");
			}
		}

		Patient mother = null;
		if (patient.getMotherMotechId() != null) {
			mother = openmrsBean.getPatientByMotechId(patient
					.getMotherMotechId().toString());
			if (mother == null) {
				errors.rejectValue("motherMotechId",
						"motechmodule.motechId.notexist");
			}
		}

		if (patient.getPhoneNumber() != null
				&& !patient.getPhoneNumber().matches(
						MotechConstants.PHONE_REGEX_PATTERN)) {
			errors.rejectValue("phoneNumber",
					"motechmodule.phoneNumber.invalid");
		}

		validateTextLength(errors, "firstName", patient.getFirstName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "middleName", patient.getMiddleName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "lastName", patient.getLastName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "prefName", patient.getPrefName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "nhis", patient.getNhis(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "address", patient.getAddress(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "phoneNumber", patient.getPhoneNumber(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);

		if (!errors.hasErrors()) {
			registrarBean.registerPatient(patient.getRegistrationMode(),
					patient.getMotechId(), patient.getRegistrantType(), patient
							.getFirstName(), patient.getMiddleName(), patient
							.getLastName(), patient.getPrefName(), patient
							.getBirthDate(), patient.getBirthDateEst(), patient
							.getSex(), patient.getInsured(), patient.getNhis(),
					patient.getNhisExpDate(), mother, community, patient
							.getAddress(), patient.getPhoneNumber(), patient
							.getDueDate(), patient.getDueDateConfirmed(),
					patient.getGravida(), patient.getParity(), patient
							.getEnroll(), patient.getConsent(), patient
							.getPhoneType(), patient.getMediaType(), patient
							.getLanguage(), patient.getDayOfWeek(), patient
							.getTimeOfDay(), patient.getInterestReason(),
					patient.getHowLearned(), patient.getMessagesStartWeek());

			model.addAttribute("successMsg",
					"motechmodule.Patient.register.success");

			status.setComplete();

			return "redirect:/module/motechmodule/viewdata.form";
		}

		return "/module/motechmodule/patient";
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
