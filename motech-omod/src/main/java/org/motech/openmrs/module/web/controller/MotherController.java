package org.motech.openmrs.module.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.web.model.WebModelConverter;
import org.motech.openmrs.module.web.model.WebPatient;
import org.motech.svc.RegistrarBean;
import org.openmrs.Location;
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
@RequestMapping("/module/motechmodule/mother")
@SessionAttributes("mother")
public class MotherController {

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
	public List<Location> getRegions() {
		return contextService.getMotechService().getAllRegions();
	}

	@ModelAttribute("districts")
	public List<Location> getDistricts() {
		return contextService.getMotechService().getAllDistricts();
	}

	@ModelAttribute("communities")
	public List<Location> getCommunities() {
		return contextService.getMotechService().getAllCommunities();
	}

	@ModelAttribute("clinics")
	public List<Location> getClinics() {
		return contextService.getMotechService().getAllClinics();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@ModelAttribute("mother")
	public WebPatient getWebMother(@RequestParam(required = false) Integer id) {
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
	public String submitForm(@ModelAttribute("mother") WebPatient mother,
			Errors errors, ModelMap model, SessionStatus status) {

		log.debug("Register Pregnant Mother");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
				"motechmodule.firstName.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
				"motechmodule.lastName.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthDate",
				"motechmodule.birthDate.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthDateEst",
				"motechmodule.birthDateEst.required");
		if (!Boolean.TRUE.equals(mother.getRegisteredGHS())) {
			errors.rejectValue("registeredGHS",
					"motechmodule.registeredGHS.required");
		} else {
			ValidationUtils.rejectIfEmpty(errors, "regNumberGHS",
					"motechmodule.regNumberGHS.required");
			if (mother.getRegNumberGHS() != null
					&& registrarBean.getPatientBySerial(mother
							.getRegNumberGHS()) != null) {
				errors.rejectValue("regNumberGHS",
						"motechmodule.regNumberGHS.nonunique");
			}
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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "clinic",
				"motechmodule.clinic.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dueDate",
				"motechmodule.dueDate.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dueDateConfirmed",
				"motechmodule.dueDateConfirmed.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gravida",
				"motechmodule.gravida.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parity",
				"motechmodule.parity.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "hivStatus",
				"motechmodule.hivStatus.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors,
				"registerPregProgram",
				"motechmodule.registerPregProgram.required");

		if (Boolean.TRUE.equals(mother.getRegisterPregProgram())) {
			if (!Boolean.TRUE.equals(mother.getTermsConsent())) {
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
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "whoRegistered",
					"motechmodule.whoRegistered.required");
		}

		if (!errors.hasErrors()) {
			registrarBean.registerPregnantMother(mother.getFirstName(), mother
					.getLastName(), mother.getPrefName(),
					mother.getBirthDate(), mother.getBirthDateEst(), mother
							.getRegisteredGHS(), mother.getRegNumberGHS(),
					mother.getInsured(), mother.getNhis(), mother
							.getNhisExpDate(), mother.getRegion(), mother
							.getDistrict(), mother.getCommunity(), mother
							.getAddress(), mother.getClinic(), mother
							.getDueDate(), mother.getDueDateConfirmed(), mother
							.getGravida(), mother.getGravida(), mother
							.getHivStatus(), mother.getRegisterPregProgram(),
					mother.getPrimaryPhone(), mother.getPrimaryPhoneType(),
					mother.getSecondaryPhone(), mother.getSecondaryPhoneType(),
					mother.getMediaTypeInfo(), mother.getMediaTypeReminder(),
					mother.getLanguageVoice(), mother.getLanguageText(), mother
							.getWhoRegistered(), mother.getReligion(), mother
							.getOccupation());

			model.addAttribute("successMsg",
					"motechmodule.Mother.register.success");

			status.setComplete();

			return "redirect:/module/motechmodule/viewdata.form";
		}

		return "/module/motechmodule/mother";
	}
}
