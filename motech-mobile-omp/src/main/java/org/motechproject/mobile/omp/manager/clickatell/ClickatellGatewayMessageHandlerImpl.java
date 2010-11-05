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

package org.motechproject.mobile.omp.manager.clickatell;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.omp.manager.GatewayMessageHandler;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author henry
 */
public class ClickatellGatewayMessageHandlerImpl implements GatewayMessageHandler{
    
    private CoreManager coreManager;
    private static Logger logger = Logger.getLogger(ClickatellGatewayMessageHandlerImpl.class);
    private Map<MStatus, String> codeStatusMap;
    private Map<MStatus, String> codeResponseMap;

    /**
     *
     * @see GatewayMessageHandler.parseResponse
     */
    public Set<GatewayResponse> parseMessageResponse(GatewayRequest message, String gatewayResponse) {
        logger.debug("Parsing message gateway response");
        logger.debug(gatewayResponse);

        if(message == null)
            return null;

        if(gatewayResponse.trim().isEmpty())
            return null;

        Set<GatewayResponse> responses = new HashSet<GatewayResponse>();
        String[] responseLines = gatewayResponse.trim().split("\n");

        for(String line : responseLines){
            String[] responseParts = line.split(" ");

            if(responseParts[0].equalsIgnoreCase("ID:")){
                GatewayResponse response = getCoreManager().createGatewayResponse();
                
                response.setGatewayMessageId(responseParts[1]);
                response.setRequestId(message.getRequestId());
                response.setMessageStatus(MStatus.PENDING);
                response.setGatewayRequest(message);
                response.setResponseText(gatewayResponse);
                response.setDateCreated(new Date());

                if(responseParts.length == 4)
                    response.setRecipientNumber(responseParts[3]);
                else
                    response.setRecipientNumber(message.getRecipientsNumber());

                responses.add(response);
            }
            else{
                logger.error("Gateway returned error: " + gatewayResponse);
                
                String errorCode = responseParts[1];
                errorCode.replaceAll(",", "");
                errorCode.trim();
                
                MStatus status = lookupResponse(errorCode);
                
                GatewayResponse response = getCoreManager().createGatewayResponse();
                
                response.setRequestId(message.getRequestId());
                response.setMessageStatus(status);
                response.setGatewayRequest(message);
                response.setResponseText(gatewayResponse);
                response.setDateCreated(new Date());
                
                responses.add(response);
            }
        }
        logger.debug(responses);
        return responses;
    }

    /**
     *
     * @see GatewayMessageHandler.parseMessageStatus
     */
    public MStatus parseMessageStatus(String gatewayResponse) {
        logger.debug("Parsing message gateway status response");

        String status;
                
        String[] responseParts = gatewayResponse.split(" ");
        
        if(responseParts.length == 2){
            status = responseParts[1];
        }
        else if(responseParts.length == 4){
            status = responseParts[3];
        }
        else{
            status = "";
        }
        
        return lookupStatus(status);
    }
    
    /**
     * @see GatewayMessageHandler.lookupStatus
     */
    public MStatus lookupStatus(String code){
        if(code.isEmpty()){
            return MStatus.RETRY;
        }
        
        for(Entry<MStatus, String> entry: getCodeStatusMap().entrySet()){
            if(entry.getValue().contains(code)){
                return entry.getKey();
            }
        }
        return MStatus.RETRY;
    }
    
    /**
     * @see GatewayMessageHandler.lookupResponse
     */
    public MStatus lookupResponse(String code){
        if(code.isEmpty()){
            return MStatus.RETRY;
        }
        
        for(Entry<MStatus, String> entry: getCodeResponseMap().entrySet()){
            if(entry.getValue().contains(code)){
                return entry.getKey();
            }
        }
        return MStatus.RETRY;
    }

    /**
     * @return the coreManager
     */
    public CoreManager getCoreManager() {
        return coreManager;
    }

    /**
     * @param coreManager the coreManager to set
     */
    public void setCoreManager(CoreManager coreManager) {
        logger.debug("Setting ClickatellGatewayMessageHandlerImpl.coreManager");
        logger.debug(coreManager);
        this.coreManager = coreManager;
    }

    public Map<MStatus, String> getCodeStatusMap() {
        return codeStatusMap;
    }

    public void setCodeStatusMap(Map<MStatus, String> codeStatusMap) {
        this.codeStatusMap = codeStatusMap;
    }

    public Map<MStatus, String> getCodeResponseMap() {
        return codeResponseMap;
    }

    public void setCodeResponseMap(Map<MStatus, String> codeResponseMap) {
        this.codeResponseMap = codeResponseMap;
    }
}
