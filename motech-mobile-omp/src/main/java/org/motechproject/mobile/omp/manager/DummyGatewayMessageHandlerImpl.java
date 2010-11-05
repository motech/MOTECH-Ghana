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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.motechproject.mobile.omp.manager;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.motechproject.mobile.core.util.MotechIDGenerator;

/**
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Jul 31, 2009
 */
public class DummyGatewayMessageHandlerImpl implements GatewayMessageHandler {
    CoreManager coreManager;
    private Map<MStatus, String> codeStatusMap;
    private Map<MStatus, String> codeResponseMap;

    public Set<GatewayResponse> parseMessageResponse(GatewayRequest message, String gatewayResponse) {
        if(message == null)
            return null;
        
        if(gatewayResponse.isEmpty())
            return null;

        Set<GatewayResponse> responseList = new HashSet<GatewayResponse>();
        GatewayResponse response = coreManager.createGatewayResponse();
        response.setGatewayRequest(message);
        response.setMessageStatus(MStatus.DELIVERED);
        response.setRecipientNumber(message.getRecipientsNumber());
        response.setGatewayMessageId(MotechIDGenerator.generateID(10).toString());
        response.setRequestId(message.getRequestId());
        response.setResponseText(gatewayResponse);
        response.setDateCreated(new Date());

        responseList.add(response);
        return responseList;
    }

    public MStatus parseMessageStatus(String gatewayResponse) {
        String status;

        String[] responseParts = gatewayResponse.split(" ");

        if(responseParts.length == 4){
            status = responseParts[3];
        }
        else{
            status = "";
        }

        return lookupStatus(status);
    }

    public MStatus lookupStatus(String code) {
        if(code.isEmpty()){
            return MStatus.PENDING;
        }

        for(Entry<MStatus, String> entry: codeStatusMap.entrySet()){
            if(entry.getValue().contains(code)){
                return entry.getKey();
            }
        }
        return MStatus.PENDING;
    }

    public MStatus lookupResponse(String code) {
        if(code.isEmpty()){
            return MStatus.SCHEDULED;
        }

        for(Entry<MStatus, String> entry: codeResponseMap.entrySet()){
            if(entry.getValue().contains(code)){
                return entry.getKey();
            }
        }
        return MStatus.SCHEDULED;
    }

    public CoreManager getCoreManager() {
        return this.coreManager;
    }

    public void setCoreManager(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    /**
     * @param codeStatusMap the codeStatusMap to set
     */
    public void setCodeStatusMap(Map<MStatus, String> codeStatusMap) {
        this.codeStatusMap = codeStatusMap;
    }

    /**
     * @param codeResponseMap the codeResponseMap to set
     */
    public void setCodeResponseMap(Map<MStatus, String> codeResponseMap) {
        this.codeResponseMap = codeResponseMap;
    }

}
