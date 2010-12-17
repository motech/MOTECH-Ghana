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

package org.motechproject.server.svc.impl;

import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.svc.IdBean;
import org.motechproject.server.svc.LocationBean;

/**
 * An implementation of the LocationBean interface.
 */
public class LocationBeanImpl implements LocationBean {

	private ContextService contextService;
	private IdBean idBean;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setIdBean(IdBean idBean) {
		this.idBean = idBean;
	}

	public Facility getFacilityById(Integer facilityId) {
		return contextService.getMotechService().getFacilityById(facilityId);
	}

	public Community getCommunityById(Integer communityId) {
		return contextService.getMotechService().getCommunityById(communityId);
	}

	public Community saveCommunity(Community community) {
		if (community.getCommunityId() == null) {
			community.setCommunityId(Integer.parseInt(idBean
					.generateCommunityId()));
		}

		return contextService.getMotechService().saveCommunity(community);
	}

	public Facility saveNewFacility(Facility facility) {
		facility.setFacilityId(Integer.parseInt(idBean.generateFacilityId()));
		return contextService.getMotechService().saveFacility(facility);
	}
}
