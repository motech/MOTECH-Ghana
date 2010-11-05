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

import java.util.HashSet;
import java.util.Set;

import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageType;
import org.springframework.transaction.annotation.Transactional;

public class CompositeGatewayManager implements GatewayManager {
	
	private GatewayManager voiceGatewayManager;
	private GatewayManager textGatewayManager;
	
	private CompositeGatewayMessageHandler compositeMessageHandler;
	
	public String getMessageStatus(GatewayResponse response) {

		if ( response
				.getGatewayRequest()
				.getMessageRequest()
				.getMessageType() == MessageType.VOICE ) {
			return voiceGatewayManager.getMessageStatus(response);
		}
		
		if ( response
				.getGatewayRequest()
				.getMessageRequest()
				.getMessageType() == MessageType.TEXT ) {
			return textGatewayManager.getMessageStatus(response);
		}
		
		return null;
		
	}

	public MStatus mapMessageStatus(GatewayResponse response) {
		
		if ( response
				.getGatewayRequest()
				.getMessageRequest()
				.getMessageType() == MessageType.VOICE ) {
			return voiceGatewayManager.mapMessageStatus(response);
		}
		
		if ( response
				.getGatewayRequest()
				.getMessageRequest()
				.getMessageType() == MessageType.TEXT ) {
			return textGatewayManager.mapMessageStatus(response);
		}
		
		return null;
	}

        @Transactional
	@SuppressWarnings("unchecked")
	public Set<GatewayResponse> sendMessage(GatewayRequest messageDetails)
        {
		
		if ( messageDetails
				.getMessageRequest()
				.getMessageType() == MessageType.VOICE ) {
			return voiceGatewayManager.sendMessage(messageDetails);
		}
		
		if ( messageDetails
				.getMessageRequest()
				.getMessageType() == MessageType.TEXT ) {
			return textGatewayManager.sendMessage(messageDetails);
		}
		
		return new HashSet<GatewayResponse>();
	}

	public GatewayMessageHandler getMessageHandler() {
		return compositeMessageHandler;
	}
	
	public void setMessageHandler(GatewayMessageHandler messageHandler) {
		if ( messageHandler instanceof CompositeGatewayMessageHandler )
			this.compositeMessageHandler = (CompositeGatewayMessageHandler)messageHandler;
		else 
			this.compositeMessageHandler = null;
	}

	public CompositeGatewayMessageHandler getCompositeMessageHandler() {
		return compositeMessageHandler;
	}

	public void setCompositeMessageHandler(
			CompositeGatewayMessageHandler compositeMessageHandler) {
		this.compositeMessageHandler = compositeMessageHandler;
	}

	public GatewayManager getVoiceGatewayManager() {
		return voiceGatewayManager;
	}

	public void setVoiceGatewayManager(GatewayManager voiceGatewayManager) {
		this.voiceGatewayManager = voiceGatewayManager;
	}

	public GatewayManager getTextGatewayManager() {
		return textGatewayManager;
	}

	public void setTextGatewayManager(GatewayManager textGatewayManager) {
		this.textGatewayManager = textGatewayManager;
	}

}
