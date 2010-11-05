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

package org.motechproject.mobile.omp.manager.intellivr;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.omp.manager.GatewayMessageHandler;

public class IntellIVRGatewayMessageHandler implements GatewayMessageHandler {

	MStatus defaultStatus = MStatus.PENDING;
	MStatus defaultResponse = MStatus.SCHEDULED;
	private Map<String, MStatus> statusMap;
	private Map<String, MStatus> responseMap;
	private CoreManager coreManager;
	
	public Set<GatewayResponse> parseMessageResponse(GatewayRequest gatewayRequest,
			String statusMessage) {

		Set<GatewayResponse> responses = new HashSet<GatewayResponse>();
		
		GatewayResponse gwResponse = coreManager.createGatewayResponse();
		
		gwResponse.setGatewayRequest(gatewayRequest);
		gwResponse.setGatewayMessageId(gatewayRequest.getMessageRequest().getId().toString());
		gwResponse.setRecipientNumber(gatewayRequest.getRecipientsNumber());
		gwResponse.setRequestId(gatewayRequest.getRequestId());
		gwResponse.setResponseText(statusMessage);
		gwResponse.setMessageStatus(lookupStatus(statusMessage));
		gwResponse.setDateCreated(new Date());
		
		responses.add(gwResponse);
		
		return responses;
	}

	public MStatus parseMessageStatus(String messageStatus) {
		return lookupStatus(messageStatus);
	}

	public MStatus lookupResponse(String code) {
		MStatus responseStatus = responseMap.get(code);
		return responseStatus != null ? responseStatus : defaultResponse;
	}

	public MStatus lookupStatus(String code) {
		MStatus status = responseMap.get(code);
		return status != null ? status : defaultStatus;
	}
	
	public CoreManager getCoreManager() {
		return coreManager;
	}

	public void setCoreManager(CoreManager coreManager) {
		this.coreManager = coreManager;
	}
	
	public Map<String, MStatus> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<String, MStatus> statusMap) {
		this.statusMap = statusMap;
	}

	public Map<String, MStatus> getResponseMap() {
		return responseMap;
	}

	public void setResponseMap(Map<String, MStatus> responseMap) {
		this.responseMap = responseMap;
	}

}
