package org.motech.openmrs.module.impl;

import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerService;

/**
 * The implementation of the ContextService interface. It simply delegates to
 * the normal OpenMRS Context.getXService(...) invocations normally found
 * sprinkled throughout OpenMRS code. When testing, it is likely that this
 * implementation will not be used. More likely, it will be stubbed as a mock
 * collaborator to test units in isolation.
 */
public class ContextServiceImpl implements ContextService {

	public void authenticate(String username, String password) {
		Context.authenticate(username, password);
	}

	public void openSession() {
		Context.openSession();
	}

	public void closeSession() {
		Context.closeSession();
	}

	public void addProxyPrivilege(String privilege) {
		Context.addProxyPrivilege(privilege);
	}

	public void removeProxyPrivilege(String privilege) {
		Context.removeProxyPrivilege(privilege);
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

	public ObsService getObsService() {
		return Context.getObsService();
	}

	public ConceptService getConceptService() {
		return Context.getConceptService();
	}

	public SchedulerService getSchedulerService() {
		return Context.getSchedulerService();
	}

	public AdministrationService getAdministrationService() {
		return Context.getAdministrationService();
	}

	public MotechService getMotechService() {
		return Context.getService(MotechService.class);
	}

	public RegistrarBean getRegistrarBean() {
		return this.getMotechService().getRegistrarBean();
	}

}
