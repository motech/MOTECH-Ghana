/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.omod.impl;

import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.service.ScheduleMaintService;
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
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
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

	public void becomeUser(String systemId) {
		Context.becomeUser(systemId);
	}

	public User getAuthenticatedUser() {
		return Context.getAuthenticatedUser();
	}

	public void logout() {
		Context.logout();
	}

	public void openSession() {
		Context.openSession();
	}

	public boolean isSessionOpen() {
		return Context.isSessionOpen();
	}

	public boolean isAuthenticated() {
		return Context.isAuthenticated();
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

	public boolean hasPrivilege(String privilege) {
		return Context.hasPrivilege(privilege);
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

	public ScheduleMaintService getScheduleMaintService() {
		return this.getMotechService().getScheduleMaintService();
	}

	public IdentifierSourceService getIdentifierSourceService() {
		return Context.getService(IdentifierSourceService.class);
	}
}
