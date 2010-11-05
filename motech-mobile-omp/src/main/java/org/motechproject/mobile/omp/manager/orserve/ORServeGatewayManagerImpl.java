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

package org.motechproject.mobile.omp.manager.orserve;

import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.util.MotechException;
import org.motechproject.mobile.omp.manager.GatewayManager;
import org.motechproject.mobile.omp.manager.GatewayMessageHandler;
import com.outreachcity.orserve.messaging.SMSMessenger;
import com.outreachcity.orserve.messaging.SMSMessengerSoap;
import java.net.URL;
import java.util.Set;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Handles all interactions with the OutReach Server message gateway
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * @date Jul 15, 2009
 */
public class ORServeGatewayManagerImpl implements GatewayManager {
    private String productCode;
    private String senderId;
    private GatewayMessageHandler messageHandler;
    private static Logger logger = Logger.getLogger(ORServeGatewayManagerImpl.class);

    /**
     *
     * @see GatewayManager.send
     */
    public Set<GatewayResponse> sendMessage(GatewayRequest messageDetails) {
        String gatewayResponse;

        if(messageDetails == null)
            return null;

        logger.debug("Building ORServe message gateway webservice proxy class");
        URL wsdlURL = null;
        try {
          wsdlURL = new URL("http://www.outreachcity.com/orserve/messaging/smsmessenger.asmx?WSDL");
        } catch ( MalformedURLException e ) {
          logger.error("Error creating web service client", e);
          throw new MotechException(e.getMessage());
        }
        
        logger.debug("Calling sendMessage method of ORServe message gateway");
        logger.debug(messageDetails);
        try{
            SMSMessenger messenger = new SMSMessenger(wsdlURL, new QName("http://www.outreachcity.com/ORServe/Messaging/", "SMSMessenger"));
            SMSMessengerSoap soap = messenger.getSMSMessengerSoap();
            gatewayResponse = soap.sendMessage(messageDetails.getMessage(), messageDetails.getRecipientsNumber(), getSenderId(), getProductCode(), String.valueOf(messageDetails.getGatewayRequestDetails().getNumberOfPages()));
        }
        catch(Exception ex){
            logger.error("Error sending message", ex);
            throw new MotechException(ex.getMessage());
        }
        messageDetails.setDateSent(new Date());
        
        logger.debug("Parsing gateway response");
        return messageHandler.parseMessageResponse(messageDetails, gatewayResponse);
    }

    /**
     *
     * @see GatewayManager.getMessageStatus
     */
    public String getMessageStatus(GatewayResponse response) {
        String gatewayResponse;

        logger.debug("Building ORServe message gateway webservice proxy class");
        URL wsdlURL = null;
        try {
          wsdlURL = new URL("http://www.outreachcity.com/orserve/messaging/smsmessenger.asmx?WSDL");
        } catch ( MalformedURLException e ) {
          logger.error("Error creating web service client", e);
          gatewayResponse = e.getMessage();
        }
        SMSMessenger messenger = new SMSMessenger(wsdlURL, new QName("http://www.outreachcity.com/ORServe/Messaging/", "SMSMessenger"));
        SMSMessengerSoap soap = messenger.getSMSMessengerSoap();

        logger.debug("Calling getMessageStatus method of ORServe message gateway");
        try{
            gatewayResponse = soap.getMessageStatus(response.getGatewayMessageId(), productCode);
        }
        catch(Exception ex){
            logger.error("Error querying message", ex);
            gatewayResponse = ex.getMessage();
        }
        return gatewayResponse;
    }

    public MStatus mapMessageStatus(GatewayResponse response) {
        return messageHandler.parseMessageStatus(response.getResponseText());
    }

    /**
     * @return the productCode
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode the productCode to set
     */
    public void setProductCode(String productCode) {
        logger.debug("Setting ORServeGatewayManagerImpl.productCode");
        logger.debug(productCode);
        this.productCode = productCode;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(String senderId) {
        logger.debug("Setting ORServeGatewayManagerImpl.senderId");
        logger.debug(senderId);
        this.senderId = senderId;
    }

    /**
     * @return the messageHandler
     */
    public GatewayMessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * @param messageHandler the messageHandler to set
     */
    public void setMessageHandler(GatewayMessageHandler messageHandler) {
        logger.debug("Setting SMSMessagingServiceImpl.handler:");
        logger.debug(messageHandler);
        this.messageHandler = messageHandler;
    }

}
