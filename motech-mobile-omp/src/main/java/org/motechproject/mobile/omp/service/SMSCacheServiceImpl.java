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

import org.motechproject.mobile.core.dao.GatewayRequestDAO;
import org.motechproject.mobile.core.dao.GatewayRequestDetailsDAO;
import org.motechproject.mobile.core.dao.GatewayResponseDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.model.MessageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * An SMS specific implementation of the CacheService interface
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Jul 15, 2009
 */
@Transactional
public class SMSCacheServiceImpl implements CacheService {

    private CoreManager coreManager;
    private static Logger logger = Logger.getLogger(SMSCacheServiceImpl.class);

    /**
     *
     * @see CacheService.saveMessage
     */
    public void saveMessage(GatewayRequest messageDetails) {
        logger.debug("Initializing DAO");
        GatewayRequestDAO messageDAO = coreManager.createGatewayRequestDAO();

        logger.debug("Caching message");
        logger.debug(messageDetails);

        messageDAO.save(messageDetails);
    }

    public void mergeMessage(GatewayRequest messageDetails) {
        logger.debug("Initializing Gsteway Request DAO");
        GatewayRequestDAO messageDAO = coreManager.createGatewayRequestDAO();

        logger.debug("Caching message");
        logger.debug(messageDetails);

        messageDAO.merge(messageDetails);
    }

    //TODO refactor to conform with non-intrusive OMP pattern
    public void mergeMessage(MessageRequest messageDetails) {
        logger.debug("Initializing Message Request DAO");
        MessageRequestDAO messageDAO = coreManager.createMessageRequestDAO();

        logger.debug("Caching message request");
        logger.debug(messageDetails);

        messageDAO.merge(messageDetails);
    }

    /**
     *
     * @see CacheService.saveMessage
     */
    public void saveMessage(GatewayRequestDetails messageDetails) {
        logger.debug("Initializing DAO");
        GatewayRequestDetailsDAO messageDAO = coreManager.createGatewayRequestDetailsDAO();

        logger.debug("Caching message");
        logger.debug(messageDetails);

        messageDAO.save(messageDetails);

    }

    /**
     *
     * @see CacheService.saveResponse
     */
    public void saveResponse(GatewayResponse responseDetails) {
        logger.debug("Initializing DAO");
        GatewayResponseDAO responseDAO = coreManager.createGatewayResponseDAO();

        logger.debug("Caching response");
        logger.debug(responseDetails);

        responseDAO.merge(responseDetails);

    }

    /**
     * 
     * see CacheService.getMessages
     */
    public List<GatewayRequest> getMessages(GatewayRequest criteria) {
        GatewayRequestDAO messageDao = coreManager.createGatewayRequestDAO();
        return messageDao.findByExample(criteria);
    }

    /**
     * 
     * see CacheService.getMessagesByStatus
     */
    public List<GatewayRequest> getMessagesByStatus(MStatus criteria) {
        GatewayRequestDAO messageDao = coreManager.createGatewayRequestDAO();
        return messageDao.getByStatus(criteria);
    }

    /**
     *
     * see CacheService.getMessagesByStatus
     */
    public List<GatewayRequest> getMessagesByStatusAndSchedule(MStatus criteria, Date schedule) {
        GatewayRequestDAO messageDao = coreManager.createGatewayRequestDAO();
        return messageDao.getByStatusAndSchedule(criteria, schedule);
    }

    /**
     * 
     * see CacheService.getMessages
     */
    public List<GatewayResponse> getResponses(GatewayResponse criteria) {
        GatewayResponseDAO responseDao = coreManager.createGatewayResponseDAO();
        return responseDao.findByExample(criteria);
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
        logger.debug("Setting value of SMSCacheServiceImpl.coreManager");
        logger.debug(coreManager);
        this.coreManager = coreManager;
    }
}
