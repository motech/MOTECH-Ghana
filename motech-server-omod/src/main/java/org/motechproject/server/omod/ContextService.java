/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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
import org.openmrs.module.idgen.service.IdentifierSourceService;
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

	public IdentifierSourceService getIdentifierSourceService();
}
