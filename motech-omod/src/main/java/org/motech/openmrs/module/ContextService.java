package org.motech.openmrs.module;

import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

/**
 * An interface providing many operations necessary for interfacing with
 * OpenMRS. It was created so that we could inject the services rather than
 * doing registry lookups via the static Context.getService(...) methds.
 */
public interface ContextService {

	public void authenticate(String username, String password);

	public LocationService getLocationService();

	public PersonService getPersonService();

	public UserService getUserService();

	public PatientService getPatientService();

	public EncounterService getEncounterService();

	public ObsService getObsService();

	public ConceptService getConceptService();

	public MotechService getMotechService();
}
