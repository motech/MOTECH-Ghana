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

package org.motechproject.mobile.omp.manager;

import java.util.Set;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageType;

public class CompositeGatewayMessageHandler implements GatewayMessageHandler {

	private GatewayMessageHandler voiceHandler;
	private GatewayMessageHandler textHandler;
	private CoreManager coreManager;
	
	/**
	 * must directly call either the text or voice handler
	 * @return null
	 */
	public MStatus lookupResponse(String code) {
		return null;
	}

	/**
	 * must directly call either the text or voice handler
	 * @return null
	 */
	public MStatus lookupStatus(String code) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public Set<GatewayResponse> parseMessageResponse(GatewayRequest message,
			String gatewayResponse) {
		
		if ( message
				.getMessageRequest()
				.getMessageType() == MessageType.VOICE)
			return voiceHandler.parseMessageResponse(message, gatewayResponse);
		
		if ( message
				.getMessageRequest()
				.getMessageType() == MessageType.TEXT)
			return textHandler.parseMessageResponse(message, gatewayResponse);
		
		
		return null;
	}

	/**
	 * must directly call either the text or voice handler
	 * @return null
	 */
	public MStatus parseMessageStatus(String messageStatus) {
		return null;
	}

	public CoreManager getCoreManager() {
		return coreManager;
	}
	
	public void setCoreManager(CoreManager coreManager) {
		this.coreManager = coreManager;
	}

	public GatewayMessageHandler getVoiceHandler() {
		return voiceHandler;
	}

	public void setVoiceHandler(GatewayMessageHandler voiceHandler) {
		this.voiceHandler = voiceHandler;
	}

	public GatewayMessageHandler getTextHandler() {
		return textHandler;
	}

	public void setTextHandler(GatewayMessageHandler textHandler) {
		this.textHandler = textHandler;
	}

}
