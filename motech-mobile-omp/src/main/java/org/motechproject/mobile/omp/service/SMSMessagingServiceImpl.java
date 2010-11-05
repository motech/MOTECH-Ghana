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

package org.motechproject.mobile.omp.service;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.util.MotechException;
import org.motechproject.mobile.omp.manager.GatewayManager;
import org.motechproject.ws.ContactNumberType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.motechproject.mobile.omp.manager.GatewayMessageHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * An SMS specific implementation of the MessagingService interface
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Jul 15, 2009
 */
public class SMSMessagingServiceImpl implements MessagingService {

    private CacheService cache;
    private GatewayManager gatewayManager;
    private CoreManager coreManager;
    private SMSMessagingServiceWorker worker;
    private static Logger logger = Logger.getLogger(SMSMessagingServiceImpl.class);

    /**
     * 
     * @see MessageService.scheduleMessage
     */
    @Transactional
    public void scheduleMessage(GatewayRequest message) {
        cache.saveMessage(message.getGatewayRequestDetails());
    }

    /**
     * 
     * @see MessageService.scheduleMessage
     */
    @Transactional
    public void scheduleMessage(GatewayRequestDetails message) {
        cache.saveMessage(message);
    }

    @Transactional
    public void scheduleTransactionalMessage(GatewayRequest message) {
        cache.mergeMessage(message);
    }

    /**
     *
     * @see MessagingService.sendScheduledMessages
     */
    @Transactional(readOnly = true)
    public void sendScheduledMessages() {

        logger.info("Fetching cached GatewayRequests");

        List<GatewayRequest> scheduledMessages = cache.getMessagesByStatusAndSchedule(MStatus.SCHEDULED, new Date());

        if (scheduledMessages != null && scheduledMessages.size() > 0) {
            logger.info("Sending messages");
            for (GatewayRequest message : scheduledMessages) {
                try {
                    sendTransactionalMessage(message);
                } catch (Exception e) {
                    logger.error("SMS message sending error: ", e);
                }
            }
            logger.info("Sending completed successfully");
        } else {
            logger.info("No scheduled messages Found");
        }

    }

    /**
     *
     * @see MessagingService.sendMessage(MessageDetails messageDetails)
     */
    public Map<Boolean, Set<GatewayResponse>> sendTransactionalMessage(GatewayRequest messageDetails) {
        return getWorker().sendMessage(messageDetails);
    }

    /**
     *
     * @see MessagingService.sendMessage(MessageDetails messageDetails)
     */
    @Transactional
    public Map<Boolean, Set<GatewayResponse>> sendMessage(GatewayRequest messageDetails) {
        logger.debug("Sending message to gateway");
        Set<GatewayResponse> responseList = null;
        Map<Boolean, Set<GatewayResponse>> result = new HashMap<Boolean, Set<GatewayResponse>>();
        try {
            if ((messageDetails.getRecipientsNumber() == null || messageDetails.getRecipientsNumber().isEmpty())
                    && !ContactNumberType.PUBLIC.toString().equals(messageDetails.getMessageRequest().getPhoneNumberType())) {
                messageDetails.setMessageStatus(MStatus.INVALIDNUM);
            } else {
                responseList = this.getGatewayManager().sendMessage(messageDetails);
                result.put(true, responseList);
                logger.debug(responseList);
                logger.debug("Updating message status");
                messageDetails.setResponseDetails(responseList);
                messageDetails.setMessageStatus(MStatus.SENT);
            }
        } catch (MotechException me) {
            logger.error("Error sending message", me);
            messageDetails.setMessageStatus(MStatus.SCHEDULED);

            GatewayMessageHandler orHandler = getGatewayManager().getMessageHandler();
            responseList = orHandler.parseMessageResponse(messageDetails, "error: 901 - Cannot Connect to gateway | Details: " + me.getMessage());
            result.put(false, responseList);
        }
        this.getCache().saveMessage(messageDetails);

        return result;
    }

    /**
     *
     * @see MessagingService.sendMessage(MessageDetails messageDetails)
     */
    @Transactional
    public Long sendMessage(GatewayRequestDetails messageDetails) {
        logger.info("Sending message to gateway");
        GatewayRequest message = (GatewayRequest) messageDetails.getGatewayRequests().toArray()[0];

        if ((message.getRecipientsNumber() != null || message.getRecipientsNumber().isEmpty()) 
        		&& !ContactNumberType.PUBLIC.toString().equals(message.getMessageRequest().getPhoneNumberType())) {
            message.setMessageStatus(MStatus.INVALIDNUM);
        } else {
            try {
                Set<GatewayResponse> responseList = this.gatewayManager.sendMessage(message);
                logger.debug(responseList);
                logger.info("Updating message status");
                message.setResponseDetails(responseList);
                message.setMessageStatus(MStatus.SENT);
            } catch (MotechException me) {
                logger.error("Error sending message", me);
                message.setMessageStatus(MStatus.SCHEDULED);
            }
        }
        this.cache.saveMessage(messageDetails);

        return message.getId();
    }

    /**
     * 
     */
    @Transactional(readOnly = true)
    public void updateMessageStatuses() {
        logger.info("Updating GatewayResponse objects");

        GatewayResponse gwResp = coreManager.createGatewayResponse();
        gwResp.setMessageStatus(MStatus.PENDING);

        List<GatewayResponse> pendingMessages = cache.getResponses(gwResp);

        for (GatewayResponse response : pendingMessages) {
            try {
                updateMessageStatus(response);
            } catch (Exception e) {
                logger.error("SMS message update error");
            }
        }

    }

    public void updateMessageStatus(GatewayResponse response){
        getWorker().updateMessageStatus(response);
    }

    /**
     * 
     * @see MessageService.getMessageStatus
     */
    public String getMessageStatus(GatewayResponse response) {
        logger.info("Calling GatewayManager.getMessageStatus");
        return gatewayManager.getMessageStatus(response);
    }

    /**
     * @return the cache
     */
    public CacheService getCache() {
        return cache;
    }

    /**
     * @param cache the cache to set
     */
    public void setCache(CacheService cache) {
        logger.debug("Setting SMSMessagingServiceImpl.cache:");
        logger.debug(cache);
        this.cache = cache;
    }

    /**
     * @return the gatewayManager
     */
    public GatewayManager getGatewayManager() {
        return gatewayManager;
    }

    /**
     * @param gatewayManager the gatewayManager to set
     */
    public void setGatewayManager(GatewayManager gatewayManager) {
        logger.debug("Setting SMSMessagingServiceImpl.gatewayManager:");
        logger.debug(gatewayManager);
        this.gatewayManager = gatewayManager;
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public void setCoreManager(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    /**
     * @return the worker
     */
    public SMSMessagingServiceWorker getWorker() {
        return worker;
    }

    /**
     * @param worker the worker to set
     */
    public void setWorker(SMSMessagingServiceWorker worker) {
        this.worker = worker;
    }
}
