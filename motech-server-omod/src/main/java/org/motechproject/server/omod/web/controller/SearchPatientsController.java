package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.RegistrarBean;
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
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping(value = "/module/motechmodule/search")
public class SearchPatientsController {

	protected final Log log = LogFactory.getLog(EditPatientController.class);

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	private ContextService contextService;
	private WebModelConverter webModelConverter;

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
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

	@ModelAttribute("communities")
	public List<Location> getCommunities() {
		return contextService.getMotechService().getAllCommunities();
	}

	@ModelAttribute("patient")
	public WebPatient getWebPatient(ModelMap model) {
		return new WebPatient();
	}

	@ModelAttribute("matchingPatients")
	public List<WebPatient> getMatchingPatients(ModelMap model) {
		return new ArrayList<WebPatient>();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submitForm(@ModelAttribute("patient") WebPatient webPatient,
			Errors errors, ModelMap model, SessionStatus status) {

		if (log.isDebugEnabled()) {
			log.debug("Search Matching Patients: " + webPatient.getFirstName()
					+ ", " + webPatient.getLastName() + ", "
					+ webPatient.getPrefName() + ", "
					+ webPatient.getBirthDate() + ", "
					+ webPatient.getCommunity() + ", "
					+ webPatient.getPrimaryPhone() + ", "
					+ webPatient.getNhis() + ", " + webPatient.getMotechId());
		}

		Integer parsedPhoneNumber = null;
		try {
			parsedPhoneNumber = Integer.parseInt(webPatient.getPrimaryPhone());
		} catch (NumberFormatException e) {
		}

		if (!errors.hasErrors()) {
			List<WebPatient> matchingWebPatientsList = new ArrayList<WebPatient>();
			List<Patient> matchingPatientsList = registrarBean.getPatients(
					webPatient.getFirstName(), webPatient.getLastName(),
					webPatient.getPrefName(), webPatient.getBirthDate(),
					webPatient.getCommunity(), parsedPhoneNumber, webPatient
							.getNhis(), webPatient.getMotechId());

			for (Patient patient : matchingPatientsList) {
				WebPatient newWebPatient = new WebPatient();
				webModelConverter.patientToWeb(patient, newWebPatient);
				matchingWebPatientsList.add(newWebPatient);
			}
			model.addAttribute("matchingPatients", matchingWebPatientsList);
		}
	}

}
