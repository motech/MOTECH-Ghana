package org.openmrs.module.motechmodule.impl;

import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.motechmodule.ContextService;
import org.openmrs.module.motechmodule.MotechService;

public class ContextServiceImpl implements ContextService {

	public void authenticate(String username, String password) {
		Context.authenticate(username, password);
	}

	public LocationService getLocationService() {
		return Context.getLocationService();
	}

	public PersonService getPersonService() {
		return Context.getPersonService();
	}

	public UserService getUserService() {
		return Context.getUserService();
	}

	public PatientService getPatientService() {
		return Context.getPatientService();
	}

	public EncounterService getEncounterService() {
		return Context.getEncounterService();
	}

	public ConceptService getConceptService() {
		return Context.getConceptService();
	}

	public MotechService getMotechService() {
		return Context.getService(MotechService.class);
	}

}
