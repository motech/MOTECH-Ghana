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

package org.motechproject.mobile.modemgw;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.omp.manager.GatewayMessageHandler;

public class ModemGatewayMessageHandlerImpl implements GatewayMessageHandler {

	CoreManager coreManager;

	MStatus defaultStatus = MStatus.PENDING;
	MStatus defaultResponse = MStatus.SCHEDULED;
	Map<String, MStatus> responseMap;
	Map<String, MStatus> statusMap;

	public CoreManager getCoreManager() {
		return coreManager;
	}

	public void setCoreManager(CoreManager coreManager) {
		this.coreManager = coreManager;
	}

	public void setResponseMap(Map<String, MStatus> responseMap) {
		this.responseMap = responseMap;
	}

	public MStatus lookupResponse(String responseCode) {
		MStatus responseStatus = responseMap.get(responseCode);
		return responseStatus != null ? responseStatus : defaultResponse;
	}

	public void setStatusMap(Map<String, MStatus> statusMap) {
		this.statusMap = statusMap;
	}

	public MStatus lookupStatus(String statusCode) {
		MStatus status = responseMap.get(statusCode);
		return status != null ? status : defaultStatus;
	}

	@SuppressWarnings("unchecked")
	public Set<GatewayResponse> parseMessageResponse(GatewayRequest msg,
			String gatewayResponse) {

		Set<GatewayResponse> responses = new HashSet<GatewayResponse>();

		GatewayResponse response = coreManager.createGatewayResponse();

		// Use the gateway request id as gateway message id
		response.setGatewayMessageId(msg.getRequestId());
		response.setGatewayRequest(msg);
		response.setRecipientNumber(msg.getRecipientsNumber());
		response.setRequestId(msg.getRequestId());
		response.setResponseText(gatewayResponse);
		response.setMessageStatus(lookupStatus(gatewayResponse));
		response.setDateCreated(new Date());

		responses.add(response);

		return responses;
	}

	public MStatus parseMessageStatus(String gatewayResponse) {
		return lookupStatus(gatewayResponse);
	}

}
