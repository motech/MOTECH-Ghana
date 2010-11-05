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

import org.motechproject.mobile.omp.service.CacheService;
import org.motechproject.mobile.omp.service.MessagingService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * An implementation of the OMPManager interface
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Aug 3, 2009
 */
public class OMPManagerImpl implements OMPManager, ApplicationContextAware {
    ApplicationContext context;
    private static Logger logger = Logger.getLogger(OMPManagerImpl.class);

    /**
     * sets the current application context
     * @param applicationContext the ApplicationContext object to set
     * @throws org.springframework.beans.BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * @see OMPManager.createGatewayMessageHandler()
     */
    public GatewayMessageHandler createGatewayMessageHandler() {
        try{
            return (GatewayMessageHandler)context.getBean("orserveHandler");
        }
        catch(Exception ex){
            logger.error("GatewayMessageHandler creation failed", ex);
            return null;
        }
    }

    /**
     * @see OMPManager.createSMSGatewayManager()
     */
    public GatewayManager createGatewayManager() {
        try{
            return (GatewayManager)context.getBean("orserveGateway");
        }
        catch(Exception ex){
            logger.fatal("GatewayManager creation failed", ex);
            throw new RuntimeException("Unable to create gateway");
        }
    }

    /**
     * @see OMPManager.createSMSCacheService()
     */
    public CacheService createCacheService() {
        try{
            return (CacheService)context.getBean("smsCache");
        }
        catch(Exception ex){
            logger.fatal("CacheService creation failed", ex);
            throw new RuntimeException("Unable to initialize cache");
        }
    }

    /**
     * @see OMPManager.createSMSService()
     */
    public MessagingService createMessagingService() {
        try{
            return (MessagingService)context.getBean("smsService");
        }
        catch(Exception ex){
            logger.fatal("MessagingService creation failed", ex);
            throw new RuntimeException("Unable to initialize messaging service");
        }
    }

}
