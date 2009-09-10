package org.motech.openmrs.module;

import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

public interface ContextService {

	public void authenticate(String username, String password);

	public LocationService getLocationService();

	public PersonService getPersonService();

	public UserService getUserService();

	public PatientService getPatientService();

	public EncounterService getEncounterService();

	public ConceptService getConceptService();

	public MotechService getMotechService();
}
