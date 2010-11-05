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

package org.motechproject.mobile.core.manager;

import org.motechproject.mobile.core.dao.*;
import org.motechproject.mobile.core.model.*;
import org.motechproject.mobile.model.dao.imp.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>Reponsible for the creation of all entities in the Core module</p>
 * 
 * @author Henry Sampson (henry@dreamoval.com)
 * @author Josepoh Djomeda (joseph@dreamoval.com)
 * Date Created: Aug 3, 2009
 */
public class CoreManagerImpl implements CoreManager, ApplicationContextAware {

    private static Logger logger = Logger.getLogger(CoreManagerImpl.class);
    ApplicationContext applicationContext;

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createLanguage() }
     */
    public Language createLanguage() {
        logger.info("Creating Language instance");
        Language result = (Language) getInstance("language", Language.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createResponseDetails() }
     */
    public GatewayResponse createGatewayResponse() {
        logger.info("Creating GatewayResponse  instance");
        GatewayResponse result = (GatewayResponse) getInstance("gatewayResponse", GatewayResponse.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createMessageRequest( ) }
     */
    public MessageRequest createMessageRequest() {
        logger.info("Creating MessageRequest instance");
        MessageRequest result = (MessageRequest) getInstance("messageRequest", MessageRequest.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createMessageTemplate( ) }
     */
    public MessageTemplate createMessageTemplate() {
        logger.info("Creating MessageTemplate instance");
        MessageTemplate result = (MessageTemplate) getInstance("messageTemplate", MessageTemplate.class);

        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createNotificationType( )  }
     */
    public NotificationType createNotificationType() {
        logger.info("Creating NotificationType instance");
        NotificationType result = (NotificationType) getInstance("notificationType", NotificationType.class);

        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageSession()   }
     */
    public IncomingMessageSession createIncomingMessageSession() {
        logger.info("Creating IncomingMessageSession instance");
        IncomingMessageSession result = (IncomingMessageSession) getInstance("incomingMessageSession", IncomingMessageSession.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessage()   }
     */
    public IncomingMessage createIncomingMessage() {
        logger.info("Creating IncomingMessage instance");
        IncomingMessage result = (IncomingMessage) getInstance("incomingMessage", IncomingMessage.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageResponse()   }
     */
    public IncomingMessageResponse createIncomingMessageResponse() {
        logger.info("Creating IncomingMessageResponse instance");
        IncomingMessageResponse result = (IncomingMessageResponse) getInstance("incomingMessageResponse", IncomingMessageResponse.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageFormDefinition()    }
     */
    public IncomingMessageFormDefinition createIncomingMessageFormDefinition() {
        logger.info("Creating IncomingMessageFormDefinition instance");
        IncomingMessageFormDefinition result = (IncomingMessageFormDefinition) getInstance("incomingMessageFormDefinition", IncomingMessageFormDefinition.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageFormParameterDefinition()    }
     */
    public IncomingMessageFormParameterDefinition createIncomingMessageFormParameterDefinition() {
        logger.info("Creating IncomingMessageFormParameterDefinition instance");
        IncomingMessageFormParameterDefinition result = (IncomingMessageFormParameterDefinition) getInstance("incomingMessageFormParameterDefinition", IncomingMessageFormParameterDefinition.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageForm()    }
     */
    public IncomingMessageForm createIncomingMessageForm() {
        logger.info("Creating IncomingMessageForm instance");
        IncomingMessageForm result = (IncomingMessageForm) getInstance("incomingMessageForm", IncomingMessageForm.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageFormParameter()    }
     */
    public IncomingMessageFormParameter createIncomingMessageFormParameter() {
        logger.info("Creating IncomingMessageFormParameter instance");
        IncomingMessageFormParameter result = (IncomingMessageFormParameter) getInstance("incomingMessageFormParameter", IncomingMessageFormParameter.class);
        return result;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createGatewayRequestDAO( )  }
     */
    public GatewayRequestDAO createGatewayRequestDAO() {
        logger.info("Creating GatewayRequestDAO instance");
        GatewayRequestDAO mdDAO = (GatewayRequestDAO) getInstance("gatewayRequestDAO", GatewayRequestDAO.class);
        return mdDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createGatewayRequestDetailsDAO( )}
     */
    public GatewayRequestDetailsDAO createGatewayRequestDetailsDAO() {
        logger.info("Creating GatewayRequestDetailsDAO instance");
        GatewayRequestDetailsDAO mdDAO = (GatewayRequestDetailsDAO) getInstance("gatewayRequestDetailsDAO", GatewayRequestDetailsDAO.class);
        return mdDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createGatewayResponseDAO( )  }
     */
    public GatewayResponseDAO createGatewayResponseDAO() {
        logger.info("Creating GatewayResponseDAO instance");
        GatewayResponseDAO rdDAO = (GatewayResponseDAO) getInstance("gatewayResponseDAO", GatewayResponseDAO.class);
        return rdDAO;
    }

    /**
     *
    @see {@link org.motechproject.mobile.core.manager.CoreManager#createMessageRequestDAO() }
     */
    public MessageRequestDAO createMessageRequestDAO() {
        logger.info("Creating ResponseDetailsDAO instance");
        MessageRequestDAO mrDAO = (MessageRequestDAO) getInstance("messageRequestDAO", MessageRequestDAO.class);
        return mrDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createLanguageDAO( )  }
     */
    public LanguageDAO createLanguageDAO() {
        logger.info("Creating LanguageDAO instance");
        LanguageDAO lDAO = (LanguageDAO) getInstance("languageDAO", LanguageDAO.class);
        return lDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createMessageTemplate( )}
     */
    public MessageTemplateDAO createMessageTemplateDAO() {
        logger.info("Creating messageTemplateDAO instance");
        MessageTemplateDAO mDAO = (MessageTemplateDAO) getInstance("messageTemplateDAO", MessageTemplateDAO.class);
        return mDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createNotificationTypeDAO( )}
     */
    public NotificationTypeDAO createNotificationTypeDAO() {
        logger.info("Creating notiticationTypeDAO instance");
        NotificationTypeDAO nDAO = (NotificationTypeDAO) getInstance("notificationTypeDAO", NotificationTypeDAO.class);
        return nDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageDAO( )  }
     */
    public IncomingMessageSessionDAO createIncomingMessageSessionDAO() {
        logger.info("Creating IncomingMessageSessionDAO instance");
        IncomingMessageSessionDAO imsDAO = (IncomingMessageSessionDAO) getInstance("incomingMessageSessionDAO", IncomingMessageSessionDAO.class);
        return imsDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageDAO( )  }
     */
    public IncomingMessageDAO createIncomingMessageDAO() {
        logger.info("Creating IncomingMessageDAO instance");
        IncomingMessageDAO imDAO = (IncomingMessageDAO) getInstance("incomingMessageDAO", IncomingMessageDAO.class);
        return imDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageResponseDAO( )  }
     */
    public IncomingMessageResponseDAO createIncomingMessageResponseDAO() {
        logger.info("Creating IncomingMessageResponseDAO instance");
        IncomingMessageResponseDAO imDAO = (IncomingMessageResponseDAO) getInstance("incomingMessageResponseDAO", IncomingMessageResponseDAO.class);
        return imDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageFromDefinitionDAO( )  }
     */
    public IncomingMessageFormDefinitionDAO createIncomingMessageFormDefinitionDAO() {
        logger.info("Creating IncomingMessageFormDefinitionDAO instance");
        IncomingMessageFormDefinitionDAO imDAO = (IncomingMessageFormDefinitionDAO) getInstance("incomingMessageFormDefinitionDAO", IncomingMessageFormDefinitionDAO.class);
        return imDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageFormParameterDefinitionDAO( )  }
     */
    public IncomingMessageFormParameterDefinitionDAO createIncomingMessageFormParameterDefinitionDAO() {
        logger.info("Creating IncomingMessageFormParameterDefinitionDAO instance");
        IncomingMessageFormParameterDefinitionDAO imDAO = (IncomingMessageFormParameterDefinitionDAO) getInstance("incomingMessageFormParameterDefinitionDAO", IncomingMessageFormParameterDefinitionDAO.class);
        return imDAO;
    }

    /**
     * @see {@link org.motechproject.mobile.core.manager.CoreManager#createIncomingMessageFormDAO( )  }
     */
    public IncomingMessageFormDAO createIncomingMessageFormDAO() {
        logger.info("Creating IncomingMessageFormDAO instance");
        IncomingMessageFormDAO imDAO = (IncomingMessageFormDAO) getInstance("incomingMessageFormDAO", IncomingMessageFormDAO.class);
        return imDAO;
    }

    /**
     *
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("Setting te applicationContext property");
        this.applicationContext = applicationContext;
    }

    private Object getInstance(String beanName, Class<?> reqType) {
        logger.debug("Calling getInstance");
        return applicationContext.getBean(beanName, reqType);
    }
}
