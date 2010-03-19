package org.motechproject.server.omod;

import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.scheduler.SchedulerService;

/**
 * An interface providing many operations necessary for interfacing with
 * OpenMRS. It was created so that we could inject the services rather than
 * doing registry lookups via the static Context.getService(...) methds.
 */
public interface ContextService {

	public void authenticate(String username, String password);

	public void becomeUser(String systemId);

	public User getAuthenticatedUser();

	public void logout();

	public void openSession();

	public boolean isSessionOpen();

	public boolean isAuthenticated();

	public void closeSession();

	public void addProxyPrivilege(String privilege);

	public void removeProxyPrivilege(String privilege);

	public boolean hasPrivilege(String privilege);

	public LocationService getLocationService();

	public PersonService getPersonService();

	public UserService getUserService();

	public PatientService getPatientService();

	public EncounterService getEncounterService();

	public ObsService getObsService();

	public ConceptService getConceptService();

	public SchedulerService getSchedulerService();

	public AdministrationService getAdministrationService();

	public MotechService getMotechService();

	public RegistrarBean getRegistrarBean();

	public ScheduleMaintService getScheduleMaintService();
}
