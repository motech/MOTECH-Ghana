package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
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
@RequestMapping("/module/motechmodule/demo-patient")
@SessionAttributes("patient")
public class DemoPatientController {

	private static Log log = LogFactory.getLog(MotherController.class);

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

	@ModelAttribute("regions")
	public List<String> getRegions() {
		return contextService.getMotechService().getAllRegions();
	}

	@ModelAttribute("districts")
	public List<String> getDistricts() {
		return contextService.getMotechService().getAllDistricts();
	}

	@ModelAttribute("communities")
	public List<Community> getCommunities() {
		return contextService.getMotechService().getAllCommunities();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@ModelAttribute("patient")
	public WebPatient getWebPatient(@RequestParam(required = false) Integer id) {
		WebPatient result = new WebPatient();
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				webModelConverter.patientToWeb(patient, result);
			}
		} else {
			// Pre-populate so demo entry isn't a long process
			result.setFirstName("Jane");
			result.setLastName("Doe");
			result.setSex(Gender.FEMALE);
			Calendar dobCal = new GregorianCalendar();
			dobCal.set(1984, 0, 4);
			result.setBirthDate(dobCal.getTime());
			result.setBirthDateEst(Boolean.FALSE);
			result.setRegisteredGHS(Boolean.TRUE);
			result.setInsured(Boolean.FALSE);
			result.setRegion(getRegions().get(0));
			result.setDistrict(getDistricts().get(0));
			result.setCommunity(getCommunities().get(0).getName());
			result.setAddress("Somewhere important");
			result.setRegisterPregProgram(Boolean.TRUE);
			result.setPhoneType(ContactNumberType.PERSONAL);
			result.setMediaType(MediaType.TEXT);
			result.setLanguage("en");
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitForm(@ModelAttribute("patient") WebPatient patient,
			Errors errors, ModelMap model, SessionStatus status,
			HttpSession session) {

		log.debug("Register Demo Patient");

		if (patient.getMotechId() != null
				&& openmrsBean.getPatientByMotechId(patient.getMotechId()
						.toString()) != null) {
			errors.rejectValue("motechId", "motechmodule.motechId.nonunique");
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

		if (Boolean.TRUE.equals(patient.getRegisteredGHS())) {
			ValidationUtils.rejectIfEmpty(errors, "regNumberGHS",
					"motechmodule.regNumberGHS.required");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "insured",
				"motechmodule.insured.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "region",
				"motechmodule.region.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "district",
				"motechmodule.district.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "community",
				"motechmodule.community.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address",
				"motechmodule.address.required");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors,
				"registerPregProgram",
				"motechmodule.registerPregProgram.required");

		if (Boolean.TRUE.equals(patient.getRegisterPregProgram())) {
			if (!Boolean.TRUE.equals(patient.getTermsConsent())) {
				errors.rejectValue("termsConsent",
						"motechmodule.termsConsent.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber",
					"motechmodule.phoneNumber.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneType",
					"motechmodule.phoneType.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mediaType",
					"motechmodule.mediaType.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language",
					"motechmodule.language.required");
		}

		if (!errors.hasErrors()) {
			registrarBean.demoRegisterPatient(patient.getMotechId(), patient
					.getFirstName(), patient.getMiddleName(), patient
					.getLastName(), patient.getPrefName(), patient
					.getBirthDate(), patient.getBirthDateEst(), patient
					.getSex(), patient.getRegisteredGHS(), patient
					.getRegNumberGHS(), patient.getInsured(),
					patient.getNhis(), patient.getNhisExpDate(), patient
							.getRegion(), patient.getDistrict(), patient
							.getCommunity(), patient.getAddress(), patient
							.getClinic(), patient.getRegisterPregProgram(),
					patient.getPhoneNumber(), patient.getPhoneType(), patient
							.getMediaType(), patient.getLanguage(), patient
							.getReligion(), patient.getOccupation());

			model.addAttribute("successMsg",
					"motechmodule.Demo.Patient.register.success");

			status.setComplete();

			// Save the registration number for next step of demo
			session.setAttribute("demoLastGHSRN", patient.getRegNumberGHS());

			return "redirect:/module/motechmodule/demo-success.htm";
		}

		return "/module/motechmodule/demo-patient";
	}
}
