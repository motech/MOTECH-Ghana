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

package org.motechproject.mobile.omi.service;

import java.util.Arrays;
import org.motechproject.mobile.core.dao.GatewayResponseDAO;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.dao.NotificationTypeDAO;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import org.motechproject.mobile.omi.manager.MessageFormatter;
import org.motechproject.mobile.omi.manager.MessageStoreManager;
import org.motechproject.mobile.omi.manager.OMIManager;
import org.motechproject.mobile.omi.manager.StatusHandler;
import org.motechproject.mobile.omp.manager.OMPManager;
import org.motechproject.mobile.omp.service.MessagingService;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of the OMIService
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Jul 31, 2009
 *
 */
@Transactional
public class OMIServiceImpl implements OMIService {

    private MessageStoreManager storeManager;
    private OMIManager omiManager;
    private OMPManager ompManager;
    private CoreManager coreManager;
    private StatusHandler statHandler;
    private static Logger logger = Logger.getLogger(OMIServiceImpl.class);
    private String defaultLang;
    private int maxTries;
    private OMIServiceWorker worker;

    public OMIServiceImpl() {
    }

    /**String
     *
     * @see OMIService.sendPatientMessage
     */
    public MessageStatus savePatientMessageRequest(String messageId,
            NameValuePair[] personalInfo,
            String patientNumber,
            ContactNumberType patientNumberType,
            String langCode,
            MediaType messageType,
            Long notificationType,
            Date startDate,
            Date endDate,
            String recipientId) {
        logger.debug("Constructing MessageRequest object...");

        if (patientNumberType == ContactNumberType.PUBLIC && messageType == MediaType.TEXT) {
            return MessageStatus.REJECTED;
        }

        if ((patientNumber == null || patientNumber.isEmpty())
                && patientNumberType != ContactNumberType.PUBLIC) {
            return MessageStatus.REJECTED;
        }

        MessageRequest messageRequest = coreManager.createMessageRequest();

        NotificationTypeDAO noteTypeDao = coreManager.createNotificationTypeDAO();
        NotificationType noteType = (NotificationType) noteTypeDao.getById(notificationType);

        Language langObject = coreManager.createLanguageDAO().getByCode(langCode);

        if (personalInfo != null) {
            HashSet<NameValuePair> details = new HashSet<NameValuePair>();
            details.addAll(Arrays.asList(personalInfo));
            messageRequest.setPersInfos(details);
        }

        messageRequest.setTryNumber(1);
        messageRequest.setRequestId(messageId);
        //VOICE messages need to have a start date to accommodate replaying DELIVERED messages
        messageRequest.setDateFrom(startDate == null && messageType == MediaType.VOICE ? new Date() : startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientNumber(patientNumber);
        messageRequest.setPhoneNumberType(patientNumberType.toString());
        messageRequest.setRecipientId(recipientId);
        messageRequest.setNotificationType(noteType);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setLanguage(langObject);
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.debug("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        if (messageRequest.getDateFrom() == null && messageRequest.getDateTo() == null) {
            return sendMessage(messageRequest);
        }

        logger.info("Saving MessageRequest...");
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();

        msgReqDao.save(messageRequest);
        return MessageStatus.valueOf(messageRequest.getStatus().toString());
    }

    /**
     *logger
     * @see OMIService.sendCHPSMessage
     */
    public MessageStatus saveCHPSMessageRequest(String messageId, NameValuePair[] personalInfo, String workerNumber, Patient[] patientList, String langCode, MediaType messageType, Long notificationType, Date startDate, Date endDate) {
        logger.info("Constructing MessageDetails object...");

        if (workerNumber == null || workerNumber.isEmpty()) {
            return MessageStatus.REJECTED;
        }

        MessageRequest messageRequest = coreManager.createMessageRequest();

        NotificationTypeDAO noteTypeDao = coreManager.createNotificationTypeDAO();
        NotificationType noteType = (NotificationType) noteTypeDao.getById(notificationType);

        Language langObject = coreManager.createLanguageDAO().getByCode(langCode);

        HashSet<NameValuePair> details = new HashSet<NameValuePair>();
        if (personalInfo != null) {
            details.addAll(Arrays.asList(personalInfo));
        }
        if (patientList != null) {
            for (Patient p : patientList) {
                if (p.getPreferredName() != null) {
                    details.add(new NameValuePair("PreferredName", p.getPreferredName()));
                }
                if (p.getLastName() != null) {
                    details.add(new NameValuePair("LastName", p.getLastName()));
                }
                if (p.getCommunity() != null) {
                    details.add(new NameValuePair("Community", p.getCommunity()));
                }
                if (p.getFirstName() != null) {
                    details.add(new NameValuePair("FirstName", p.getFirstName()));
                }
                if (p.getMotechId() != null) {
                    details.add(new NameValuePair("MotechId", p.getMotechId()));
                }
                if (p.getPhoneNumber() != null) {
                    details.add(new NameValuePair("PhoneNumber", p.getPhoneNumber()));
                }
            }
        }
        messageRequest.setPersInfos(details);

        messageRequest.setTryNumber(1);
        messageRequest.setRequestId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientNumber(workerNumber);
        messageRequest.setNotificationType(noteType);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setLanguage(langObject);
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        if (messageRequest.getDateFrom() == null && messageRequest.getDateTo() == null) {
            return sendMessage(messageRequest);
        }

        logger.info("Saving MessageRequest...");
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();

        msgReqDao.save(messageRequest);

        return MessageStatus.valueOf(messageRequest.getStatus().toString());
    }

    public MessageStatus sendMessage(MessageRequest message) {
        if ((message.getRecipientNumber() == null || message.getRecipientNumber().isEmpty())
                && !ContactNumberType.PUBLIC.toString().equals(message.getPhoneNumberType())) {
            return MessageStatus.REJECTED;
        }

        Language defaultLanguage = coreManager.createLanguageDAO().getByCode(defaultLang);

        if (message.getLanguage() == null) {
            message.setLanguage(defaultLanguage);
        }

        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();

        message.setStatus(MStatus.QUEUED);
        msgReqDao.save(message);

        logger.debug("Constructing GatewayRequest...");
        GatewayRequest gwReq = storeManager.constructMessage(message, defaultLanguage);
        message.setGatewayRequestDetails(null);

        logger.debug("Initializing OMP MessagingService...");
        MessagingService msgSvc = ompManager.createMessagingService();

        logger.info("Sending GatewayRequest...");


        Map<Boolean, Set<GatewayResponse>> responses = msgSvc.sendMessage(gwReq);

        Boolean falseBool = false;
        if (responses.containsKey(falseBool)) {
            Set<GatewayResponse> resps = responses.get(falseBool);
            for (GatewayResponse gp : resps) {
                statHandler.handleStatus(gp);
            }
        }

        logger.info("Updating MessageRequest...");
        message.setGatewayRequestDetails(gwReq.getGatewayRequestDetails());
        message.setDateProcessed(new Date());
        message.setStatus(MStatus.PENDING);
        logger.debug(message);

        msgReqDao.save(message);

        logger.info("Messages sent successfully");
        return MessageStatus.valueOf(message.getStatus().toString());
    }

    public MessageStatus sendMessage(MessageRequest message, String content) {
        if ((message.getRecipientNumber() == null || message.getRecipientNumber().isEmpty())
                && !ContactNumberType.PUBLIC.toString().equals(message.getPhoneNumberType())) {
            return MessageStatus.REJECTED;
        }

        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();

        message.setStatus(MStatus.QUEUED);
        msgReqDao.save(message);


        //TODO Check length of message and break if necessary
        logger.info("Constructing GatewayRequest...");
        GatewayRequest gwReq = storeManager.constructMessage(message, null);
        gwReq.setMessage(content);
        gwReq.getGatewayRequestDetails().setMessage(content);

        logger.info("Initializing OMP MessagingService...");
        MessagingService msgSvc = ompManager.createMessagingService();

        logger.info("Sending GatewayRequest...");
        Map<Boolean, Set<GatewayResponse>> responses = msgSvc.sendMessage(gwReq);

        Boolean falseBool = false;
        if (responses.containsKey(falseBool)) {
            Set<GatewayResponse> resps = responses.get(falseBool);
            for (GatewayResponse gp : resps) {
                statHandler.handleStatus(gp);
            }
        }

        logger.info("Updating MessageRequest...");
        message.setDateProcessed(new Date());
        message.setStatus(MStatus.PENDING);
        logger.debug(message);

        msgReqDao.save(message);
        logger.info("Messages sent successfully");
        return MessageStatus.valueOf(message.getStatus().toString());
    }

    public MessageStatus sendMessage(String content, String recipient) {

        logger.info("Constructing MessageDetails object...");

        MessageRequest messageRequest = coreManager.createMessageRequest();
        messageRequest.setTryNumber(1);
        messageRequest.setRequestId("");
        messageRequest.setDateFrom(null);
        messageRequest.setDateTo(null);
        messageRequest.setRecipientNumber(recipient);
        messageRequest.setNotificationType(null);
        messageRequest.setMessageType(MessageType.TEXT);
        messageRequest.setLanguage(null);
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        MessageStatus status = sendMessage(messageRequest, content);
        return status;
    }

    public MessageStatus scheduleMessage(String content, String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            return MessageStatus.REJECTED;
        }

        logger.info("Constructing MessageDetails object...");

        MessageRequest messageRequest = coreManager.createMessageRequest();
        messageRequest.setTryNumber(1);
        messageRequest.setRequestId("");
        messageRequest.setDateFrom(null);
        messageRequest.setDateTo(null);
        messageRequest.setRecipientNumber(recipient);
        messageRequest.setNotificationType(null);
        messageRequest.setMessageType(MessageType.TEXT);
        messageRequest.setLanguage(null);
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        MessageStatus status = scheduleMessage(messageRequest, content);
        return status;
    }

    public MessageStatus scheduleMessage(MessageRequest message, String content) {
        if ((message.getRecipientNumber() == null || message.getRecipientNumber().isEmpty())
                && !ContactNumberType.PUBLIC.toString().equals(message.getPhoneNumberType())) {
            return MessageStatus.REJECTED;
        }

        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();

        msgReqDao.save(message);


        //TODO Check length of message and break if necessary
        logger.info("Constructing GatewayRequest...");
        GatewayRequest gwReq = storeManager.constructMessage(message, null);
        gwReq.setMessage(content);
        gwReq.getGatewayRequestDetails().setMessage(content);

        logger.info("Initializing OMP MessagingService...");
        MessagingService msgSvc = ompManager.createMessagingService();

        logger.info("Scheduling GatewayRequest...");

        msgSvc.scheduleMessage(gwReq);

        logger.info("Updating MessageRequest...");
        message.setDateProcessed(new Date());
        message.setStatus(MStatus.PENDING);
        logger.debug(message);

        msgReqDao.save(message);
        logger.info("Messages sent successfully");
        return MessageStatus.valueOf(message.getStatus().toString());
    }

    /**
     * @see OMIService.sendDefaulterMessage
     */
    public MessageStatus sendDefaulterMessage(String messageId, String workerNumber, Care[] cares, MediaType messageType, Date startDate, Date endDate) {
        if (workerNumber == null || workerNumber.isEmpty()) {
            return MessageStatus.REJECTED;
        }

        logger.info("Constructing MessageDetails object...");

        MessageFormatter formatter = omiManager.createMessageFormatter();
        MessageRequest messageRequest = coreManager.createMessageRequest();

        String content = formatter.formatDefaulterMessage(cares);

        messageRequest.setTryNumber(1);
        messageRequest.setRequestId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientNumber(workerNumber);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        MessageStatus status = scheduleMessage(messageRequest, content);
        return status;
    }

    /**
     * @see OMIService.sendDeliveriesMessage
     */
    public MessageStatus sendDeliveriesMessage(String messageId, String workerNumber, Patient[] patients, String deliveryStatus, MediaType messageType, Date startDate, Date endDate) {
        if (workerNumber == null || workerNumber.isEmpty()) {
            return MessageStatus.REJECTED;
        }

        logger.info("Constructing MessageDetails object...");


        MessageFormatter formatter = omiManager.createMessageFormatter();
        MessageRequest messageRequest = coreManager.createMessageRequest();

        String content = formatter.formatDeliveriesMessage(deliveryStatus, patients);

        messageRequest.setTryNumber(1);
        messageRequest.setRequestId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientNumber(workerNumber);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        MessageStatus status = scheduleMessage(messageRequest, content);
        return status;
    }

    /**
     * @see OMIService.sendUpcomingCaresMessage
     */
    public MessageStatus sendUpcomingCaresMessage(String messageId, String workerNumber, Patient patient, MediaType messageType, Date startDate, Date endDate) {
        if (workerNumber == null || workerNumber.isEmpty()) {
            return MessageStatus.REJECTED;
        }

        logger.info("Constructing MessageDetails object...");


        MessageFormatter formatter = omiManager.createMessageFormatter();
        MessageRequest messageRequest = coreManager.createMessageRequest();

        String content = formatter.formatUpcomingCaresMessage(patient);

        messageRequest.setTryNumber(1);
        messageRequest.setRequestId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientNumber(workerNumber);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        MessageStatus status = scheduleMessage(messageRequest, content);
        return status;
    }

    /**
     * @see OMIService.sendBulkCaresMessage
     */
    public MessageStatus sendBulkCaresMessage(String messageId, String workerNumber, Care[] cares, MediaType messageType, Date startDate, Date endDate) {
        if (workerNumber == null || workerNumber.isEmpty()) {
            return MessageStatus.REJECTED;
        }

        logger.info("Constructing MessageDetails object...");


        MessageFormatter formatter = omiManager.createMessageFormatter();
        MessageRequest messageRequest = coreManager.createMessageRequest();

        String content = formatter.formatBulkCaresMessage(cares);

        messageRequest.setTryNumber(1);
        messageRequest.setRequestId(messageId);
        messageRequest.setDateFrom(startDate);
        messageRequest.setDateTo(endDate);
        messageRequest.setRecipientNumber(workerNumber);
        messageRequest.setMessageType(MessageType.valueOf(messageType.toString()));
        messageRequest.setStatus(MStatus.QUEUED);
        messageRequest.setDateCreated(new Date());

        logger.info("MessageRequest object successfully constructed");
        logger.debug(messageRequest);

        MessageStatus status = scheduleMessage(messageRequest, content);
        return status;
    }

    /**
     * @see OMIService.processMessageRequests
     */
    @Transactional(readOnly = true)
    public void processMessageRequests() {
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();

        List<MessageRequest> messages = msgReqDao.getMsgByStatus(MStatus.QUEUED);

        int numMsgs = (messages == null) ? 0 : messages.size();
        logger.info("MessageRequest fetched: " + numMsgs);
        logger.debug(messages);

        Language defaultLanguage = coreManager.createLanguageDAO().getByCode(defaultLang);

        logger.info("Building GatewayRequests...");
        for (MessageRequest message : messages) {
            try {
                processMessageRequest(message, defaultLanguage);
            } catch (Exception e) {
                logger.error("Error while processing message requests: ", e);
            }
        }

        logger.info("Messages processed successfully");
    }

    public void processMessageRequest(MessageRequest message, Language defaultLanguage) {
        worker.processMessageRequest(message, defaultLanguage);
    }

    /**
     * @see OMIService.processMessageRetries
     */
    @Transactional(readOnly = true)
    public void processMessageRetries() {
        MessageRequestDAO msgReqDao = coreManager.createMessageRequestDAO();
        List<MessageRequest> messages = msgReqDao.getMsgRequestByStatusAndTryNumber(MStatus.RETRY, maxTries);

        if (messages == null || messages.isEmpty()) {
            logger.info("No message request to retry");
            return;
        }
        logger.info("Fetched " + messages.size() + " message requests for retry");

        logger.info("Processing messages...");
        for (MessageRequest message : messages) {
            try {
                processMessageRetry(message);
            } catch (Exception e) {
                logger.error("Error while retrying message requests: ", e);
            }
        }

        logger.info("Messages processed successfully");
    }

    public void processMessageRetry(MessageRequest message) {
        worker.processMessageRetry(message);
    }

    /**
     * @see OMIService.processMessageResponses
     */
    @Transactional(readOnly = true)
    public void processMessageResponses() {
        GatewayResponseDAO gwRespDao = coreManager.createGatewayResponseDAO();

        List<GatewayResponse> responses = gwRespDao.getByPendingMessageAndMaxTries(maxTries);
        if (responses == null || responses.isEmpty()) {
            logger.info("No updated gateway responses to process");
        }
        logger.info("Fetched " + responses.size() + "updated message responses");

        if (responses != null) {
            logger.info("Processing GatewayResponses...");
            for (GatewayResponse response : responses) {
                try {
                    processMessageResponse(response);
                } catch (Exception e) {
                    logger.error("Error while getting message responses: ", e);
                }
            }
        }
        logger.info("GatewayResponses processed successfully");
    }

    public void processMessageResponse(GatewayResponse response) {
        worker.processMessageResponse(response);
    }

    /**
     * @return the storeManager
     */
    public MessageStoreManager getStoreManager() {
        return storeManager;
    }

    /**
     * @param storeManager the storeManager to set
     */
    public void setStoreManager(MessageStoreManager storeManager) {
        logger.debug("Setting OMIServiceImpl.storeManager");
        logger.debug(storeManager);
        this.storeManager = storeManager;
    }

    /**
     * @return the ompManager
     */
    public OMPManager getOmpManager() {
        return ompManager;
    }

    /**
     * @param ompManager the ompManager to set
     */
    public void setOmpManager(OMPManager ompManager) {
        logger.debug("Setting OMIServiceImpl.ompmanager");
        logger.debug(ompManager);
        this.ompManager = ompManager;
    }

    /**
     * @return the ompManager
     */
    public CoreManager getCoreManager() {
        return coreManager;
    }

    /**
     * @param ompManager the ompManager to set
     */
    public void setCoreManager(CoreManager coreManager) {
        logger.debug("Setting OMIServiceImpl.coreManager");
        logger.debug(coreManager);
        this.coreManager = coreManager;
    }

    public StatusHandler getStatHandler() {
        return statHandler;
    }

    public void setStatHandler(StatusHandler statHandler) {
        logger.debug("Setting OMIServiceImpl.statHandler");
        logger.debug(coreManager);
        this.statHandler = statHandler;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public int getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(int maxRetries) {
        this.maxTries = maxRetries;
    }

    /**
     * @param omiManager the omiManager to set
     */
    public void setOmiManager(OMIManager omiManager) {
        this.omiManager = omiManager;
    }

    /**
     * @return the worker
     */
    public OMIServiceWorker getWorker() {
        return worker;
    }

    /**
     * @param worker the worker to set
     */
    public void setWorker(OMIServiceWorker worker) {
        this.worker = worker;
    }
}
