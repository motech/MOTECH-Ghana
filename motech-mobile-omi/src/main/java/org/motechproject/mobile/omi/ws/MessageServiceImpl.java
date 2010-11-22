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

package org.motechproject.mobile.omi.ws;

import org.motechproject.mobile.omi.manager.OMIManager;
import org.motechproject.mobile.omi.service.OMIService;

import java.util.Date;
import org.apache.log4j.Logger;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.Patient;
import org.motechproject.ws.PatientMessage;
import org.motechproject.ws.mobile.MessageService;

/**
 * An implementation of the MessageService interface.
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created 30-07-09
 */
public class MessageServiceImpl implements MessageService {

    private OMIManager omiManager;
    private static Logger logger = Logger.getLogger(MessageServiceImpl.class);

    /**
    *
    * @see MessageService.sendPatientMessages
    */
    public void sendPatientMessages(PatientMessage[] messages) {
        logger.debug("Called MessageService.sendPatientMessages with number of messages: " + 
            (messages != null ? messages.length : "null"));
       
        logger.info("Processing request...");
        if( messages != null) {
            OMIService omiService = omiManager.createOMIService();
            
            for( PatientMessage message:messages) {
                omiService.savePatientMessageRequest(message.getMessageId(), message.getPersonalInfo(), 
                        message.getPatientNumber(), message.getPatientNumberType(), message.getLangCode(), 
                        message.getMediaType(), message.getNotificationType(), message.getStartDate(), 
                        message.getEndDate(), message.getRecipientId());
            }
        }
    }
    
    /**
     *
     * @see MessageService.sendPatientMessage
     */
    public MessageStatus sendPatientMessage(String messageId, 
                                            NameValuePair[] personalInfo, 
                                            String patientNumber, 
                                            ContactNumberType patientNumberType, 
                                            String langCode, 
                                            MediaType messageType, 
                                            Long notificationType, 
                                            Date startDate, 
                                            Date endDate, 
                                            String recipientId) {
        logger.debug("Called MessageService.sendPatientMessage with parameters:\n\rmessageId - " + messageId + 
        			 "\n\rclinic - " + patientNumber + 
        			 "\n\rpatientNumbrType - " + patientNumberType + 
        			 "\n\rmessageType - " + messageType + 
        			 "\n\rstartDate - " + startDate + 
        			 "\n\rendDate - " + endDate +
        			 "\n\rrecipientId - " + (recipientId != null ? recipientId : "null"));
        logger.info("Processing request...");
        return omiManager.createOMIService().savePatientMessageRequest(messageId, personalInfo, patientNumber, patientNumberType, langCode, messageType, notificationType, startDate, endDate, recipientId);
    }

    /**
     *
     * @see MessageService.sendCHPSMessage
     */
    public MessageStatus sendCHPSMessage(String messageId,
                                         NameValuePair[] personalInfo,
                                         String workerNumber,
                                         Patient[] patientList,
                                         String langCode,
                                         MediaType messageType,
                                         Long notificationType,
                                         Date startDate,
                                         Date endDate) {
        logger.debug("Called MessageService.sendCHPSMessage with parameters:\n\rmessageId - " + messageId + "\n\rworkerNumber - " + workerNumber + "\n\rstartDate - " + startDate + "\n\rendDate - " + endDate);
        logger.info("Processing request...");
        return this.omiManager.createOMIService().saveCHPSMessageRequest(messageId, personalInfo, workerNumber, patientList, langCode, messageType, notificationType, startDate, endDate);
    }

    /**
     *
     * @see MessageService.sendDefaulterMessage
     */
    public MessageStatus sendDefaulterMessage(String messageId,
                                              String workerNumber,
                                              Care[] cares,
                                              MediaType mediaType,
                                              Date startDate,
                                              Date endDate) {
        logger.info("Processing request...");
        return this.omiManager.createOMIService().sendDefaulterMessage(messageId, workerNumber, cares, mediaType, startDate, endDate);
    }

    /**
     *
     * @see MessageService.sendDeliveriesMessage
     */
    public MessageStatus sendDeliveriesMessage(String messageId,
                                               String workerNumber,
                                               Patient[] patients,
                                               String deliveryStatus,
                                               MediaType mediaType,
                                               Date startDate,
                                               Date endDate) {
        logger.info("Processing request...");
        return this.omiManager.createOMIService().sendDeliveriesMessage(messageId, workerNumber, patients, deliveryStatus, mediaType, startDate, endDate);
    }

    /**
     *
     * @see MessageService.sendUpcomingCaresMessage
     */
    public MessageStatus sendUpcomingCaresMessage(String messageId,
                                                  String workerNumber,
                                                  Patient patient,
                                                  MediaType mediaType,
                                                  Date startDate,
                                                  Date endDate) {
        logger.info("Processing request...");
        return this.omiManager.createOMIService().sendUpcomingCaresMessage(messageId, workerNumber, patient, mediaType, startDate, endDate);
    }

    /**
     *
     * @see MessageService.sendBulkCaresMessage
     */
    public MessageStatus sendBulkCaresMessage(String messageId,
                                              String workerNumber,
                                              Care[] cares,
                                              MediaType mediaType,
                                              Date startDate,
                                              Date endDate) {
        logger.info("Processing request...");
        return this.omiManager.createOMIService().sendBulkCaresMessage(messageId, workerNumber, cares, mediaType, startDate, endDate);
    }

    /**
     *
     * @see MessageService.sendMessage
     */
    public MessageStatus sendMessage(String content,
                                     String recipient) {
        logger.info("Processing request...");
        return this.omiManager.createOMIService().sendMessage(content, recipient);
    }

    /**
     * @return the omiManager
     */
    public OMIManager getOmiManager() {
        return omiManager;
    }

    /**
     * @param omiManager the omiManager to set
     */
    public void setOmiManager(OMIManager omiManager) {
        logger.debug("Setting MessageServiceImpl.omiManager:");
        logger.debug(omiManager);
        this.omiManager = omiManager;
    }
}
