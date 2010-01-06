package org.motech.openmrs.module.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.web.model.WebPatient;
import org.motech.svc.RegistrarBean;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);

		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}

	@ModelAttribute("locations")
	public List<Location> getLocations() {
		return registrarBean.getAllClinics();
	}

	@ModelAttribute("patient")
	public WebPatient getWebPatient(@RequestParam(required = false) Integer id) {
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				return new WebPatient(patient);
			}
		}
		return new WebPatient();
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
