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

		if (!errors.hasErrors()) {
			registrarBean.editPatient(webPatient.getId(), webPatient
					.getFirstName(), webPatient.getLastName(), webPatient
					.getPrefName(), webPatient.getBirthDate(), webPatient
					.getBirthDateEst(), webPatient.getSex(), webPatient
					.getRegisteredGHS(), webPatient.getRegNumberGHS(),
					webPatient.getInsured(), webPatient.getNhis(), webPatient
							.getNhisExpDate(), webPatient.getRegion(),
					webPatient.getDistrict(), webPatient.getCommunity(),
					webPatient.getAddress(), webPatient.getClinic(), webPatient
							.getPrimaryPhone(), webPatient
							.getPrimaryPhoneType(), webPatient
							.getSecondaryPhone(), webPatient
							.getSecondaryPhoneType(), webPatient
							.getMediaTypeInfo(), webPatient
							.getMediaTypeReminder(), webPatient
							.getLanguageVoice(), webPatient.getLanguageText(),
					webPatient.getWhoRegistered());

			model.addAttribute("successMsg",
					"motechmodule.Patient.edit.success");
			status.setComplete();
		}
	}

}
