package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
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
@RequestMapping(value = "/module/motechmodule/editpatient")
@SessionAttributes("patient")
public class EditPatientController {

	protected final Log log = LogFactory.getLog(EditPatientController.class);

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

	@ModelAttribute("patient")
	public WebPatient getWebPatient(@RequestParam(required = false) Integer id) {
		WebPatient result = new WebPatient();
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				webModelConverter.patientToWeb(patient, result);
			}
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submitForm(@ModelAttribute("patient") WebPatient webPatient,
			Errors errors, ModelMap model, SessionStatus status) {

		log.debug("Edit Patient");

		Patient patient = null;
		if (webPatient.getId() != null) {
			patient = registrarBean.getPatientById(webPatient.getId());
			if (patient == null) {
				errors.reject("motechmodule.id.notexist");
			}
		} else {
			errors.reject("motechmodule.id.required");
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

		if (Boolean.TRUE.equals(webPatient.getEnroll())) {
			if (!Boolean.TRUE.equals(webPatient.getConsent())) {
				errors.rejectValue("consent", "motechmodule.consent.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneType",
					"motechmodule.phoneType.required");
			if (webPatient.getPhoneType() == ContactNumberType.PERSONAL
					|| webPatient.getPhoneType() == ContactNumberType.HOUSEHOLD) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors,
						"phoneNumber", "motechmodule.phoneNumber.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mediaType",
					"motechmodule.mediaType.required");
			if (webPatient.getPhoneType() == ContactNumberType.PUBLIC
					&& webPatient.getMediaType() != null
					&& webPatient.getMediaType() != MediaType.VOICE) {
				errors.rejectValue("mediaType", "motechmodule.mediaType.voice");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language",
					"motechmodule.language.required");
			if (webPatient.getMediaType() == MediaType.TEXT
					&& webPatient.getLanguage() != null
					&& !webPatient.getLanguage().equals("en")) {
				errors.rejectValue("language", "motechmodule.language.english");
			}
		}

		Community community = null;
		if (webPatient.getCommunityId() != null) {
			community = registrarBean.getCommunityById(webPatient
					.getCommunityId());
			if (community == null) {
				errors.rejectValue("communityId",
						"motechmodule.communityId.notexist");
			}
		}

		if (webPatient.getPhoneNumber() != null
				&& !webPatient.getPhoneNumber().matches(
						MotechConstants.PHONE_REGEX_PATTERN)) {
			errors.rejectValue("phoneNumber",
					"motechmodule.phoneNumber.invalid");
		}

		validateTextLength(errors, "firstName", webPatient.getFirstName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "middleName", webPatient.getMiddleName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "lastName", webPatient.getLastName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "prefName", webPatient.getPrefName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "nhis", webPatient.getNhis(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "address", webPatient.getAddress(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "phoneNumber", webPatient.getPhoneNumber(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);

		if (!errors.hasErrors()) {
			registrarBean.editPatient(patient, webPatient.getFirstName(),
					webPatient.getMiddleName(), webPatient.getLastName(),
					webPatient.getPrefName(), webPatient.getBirthDate(),
					webPatient.getBirthDateEst(), webPatient.getSex(),
					webPatient.getInsured(), webPatient.getNhis(), webPatient
							.getNhisExpDate(), community, webPatient
							.getAddress(), webPatient.getPhoneNumber(),
					webPatient.getDueDate(), webPatient.getEnroll(), webPatient
							.getConsent(), webPatient.getPhoneType(),
					webPatient.getMediaType(), webPatient.getLanguage(),
					webPatient.getDayOfWeek(), webPatient.getTimeOfDay());

			model.addAttribute("successMsg",
					"motechmodule.Patient.edit.success");
			status.setComplete();
		}
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
