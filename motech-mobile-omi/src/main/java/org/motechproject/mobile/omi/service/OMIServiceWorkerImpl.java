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
package org.motechproject.mobile.omi.service;

import java.util.Date;
import org.apache.log4j.Logger;
import org.motechproject.mobile.core.dao.GatewayRequestDetailsDAO;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.omi.manager.MessageStoreManager;
import org.motechproject.mobile.omi.manager.StatusHandler;
import org.motechproject.mobile.omp.manager.OMPManager;
import org.motechproject.mobile.omp.service.MessagingService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kweku
 */
public class OMIServiceWorkerImpl implements OMIServiceWorker, ApplicationContextAware {

    private static Logger logger = Logger.getLogger(OMIServiceWorkerImpl.class);
    private MessageStoreManager storeManager;
    private OMPManager ompManager;
    private CoreManager coreManager;
    private StatusHandler statHandler;
    private int maxTries;
    private ApplicationContext applicationContext;

    public OMIServiceWorkerImpl() {
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processMessageRequest(MessageRequest message, Language defaultLanguage) {
        if (message != null) {
            MessageRequestDAO msgReqDao = getCoreManager().createMessageRequestDAO();
            GatewayRequest gwReq = getStoreManager().constructMessage(message, defaultLanguage);
            message.setGatewayRequestDetails(gwReq.getGatewayRequestDetails());

            if (message.getLanguage() == null) {
                message.setLanguage(defaultLanguage);
            }

            MessagingService msgSvc = getOmpManager().createMessagingService();
            msgSvc.scheduleTransactionalMessage(gwReq);

            message.setDateProcessed(new Date());
            message.setStatus(MStatus.PENDING);
            logger.debug(message);
            msgReqDao.merge(message);

        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processMessageRetry(MessageRequest message) {
        if (message != null) {
            MessageRequestDAO msgReqDao = getCoreManager().createMessageRequestDAO();
            GatewayRequestDetailsDAO gwReqDao = getCoreManager().createGatewayRequestDetailsDAO();
            MessagingService msgSvc = getOmpManager().createMessagingService();


            if (message.getTryNumber() >= getMaxTries()) {
                message.setStatus(MStatus.FAILED);
            } else {

                message.setTryNumber(message.getTryNumber() + 1);

                if (message.getGatewayRequestDetails() == null) {
                    return;
                }

                GatewayRequestDetails gwReqDet = (GatewayRequestDetails) gwReqDao.getById(message.getGatewayRequestDetails().getId());

                GatewayRequest gwReq = (GatewayRequest) applicationContext.getBean("gatewayRequest", GatewayRequest.class);
                gwReq.setDateFrom(message.getDateFrom());
                gwReq.setDateTo(message.getDateTo());
                gwReq.setMessageRequest(message);
                gwReq.setRecipientsNumber(getStoreManager().formatPhoneNumber(message.getRecipientNumber(), message.getMessageType()));
                gwReq.setRequestId(message.getRequestId());
                gwReq.setTryNumber(message.getTryNumber());
                gwReq.setMessage(gwReqDet.getMessage());
                gwReq.setMessageStatus(MStatus.SCHEDULED);

                gwReqDet.getGatewayRequests().add(gwReq);
                msgSvc.scheduleMessage(gwReqDet);

                message.setStatus(MStatus.PENDING);
            }


            msgReqDao.merge(message);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processMessageResponse(GatewayResponse response) {
        if (response != null) {
            MessageRequestDAO msgReqDao = getCoreManager().createMessageRequestDAO();

            MessageRequest message = response.getGatewayRequest().getMessageRequest();
            if (response.getMessageStatus() == MStatus.RETRY && message.getTryNumber() >= getMaxTries()) {
                response.setMessageStatus(MStatus.FAILED);
            }

            getStatHandler().handleStatus(response);

            message.setStatus(response.getMessageStatus());

            msgReqDao.merge(message);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void mergeMessageNow(MessageRequest mr) {
        if (mr != null) {
            MessageRequestDAO msgreqDao = getCoreManager().createMessageRequestDAO();

            msgreqDao.merge(mr);
        }
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
        this.ompManager = ompManager;
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
        this.coreManager = coreManager;
    }

    /**
     * @return the statHandler
     */
    public StatusHandler getStatHandler() {
        return statHandler;
    }

    /**
     * @param statHandler the statHandler to set
     */
    public void setStatHandler(StatusHandler statHandler) {
        this.statHandler = statHandler;
    }

    /**
     * @return the maxTries
     */
    public int getMaxTries() {
        return maxTries;
    }

    /**
     * @param maxTries the maxTries to set
     */
    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
