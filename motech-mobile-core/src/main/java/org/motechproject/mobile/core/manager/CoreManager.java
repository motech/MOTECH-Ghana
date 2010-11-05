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

import java.io.Serializable;
import org.hibernate.SessionFactory;

/**
 * This interface is responsible for the creation of all objects
 * in the core module.
 *
 * @author Henry Sampson (henry@dreamoval.com)
 * @author Joseph Djomeda (joseph@dreamoval.com)
 * Date Created: Aug 3, 2009
 */
public interface CoreManager extends Serializable {

    /**
     * Creates a new instance of GatewayResponse
     * @return The newly created GatewayResponse
     */
    public GatewayResponse createGatewayResponse();


    /**
     *  Creates a new instance of MessageRequest
     * @param motechContext takes a instance of MotechContext
     * @return the newly created MessageRequest
     */
    public MessageRequest createMessageRequest();

    /**
     *  Creates a new instance of Language
     * @param motechContext takes a instance of MotechContext
     * @return the newly created Language
     */
    public Language createLanguage();

    /**
     *  Creates a new instance of MessageTemplate
     * @param motechContext takes a instance of MotechContext
     * @return the newly created MessageTemplate
     */
    public MessageTemplate createMessageTemplate();

    /**
     *  Creates a new instance of NotificationType
     * @param motechContext takes a instance of MotechContext
     * @return the newly created NotificationType
     */
    public NotificationType createNotificationType();

    /**
     *  Creates a new instance of IncomingMessageSession
     * @return the newly created IncomingMessageSession
     */
    public IncomingMessageSession createIncomingMessageSession();

    /**
     *  Creates a new instance of IncomingMessage
     * @return the newly created IncomingMessage
     */
    public IncomingMessage createIncomingMessage();

    /**
     *  Creates a new instance of IncomingMessageResponse
     * @return the newly created IncomingMessageResponse
     */
    public IncomingMessageResponse createIncomingMessageResponse();

    /**
     *  Creates a new instance of IncomingMessageFormDefinition
     * @return the newly created IncomingMessageFormDefinition
     */
    public IncomingMessageFormDefinition createIncomingMessageFormDefinition();

    /**
     *  Creates a new instance of IncomingMessageFormParameterDefinition
     * @return the newly created IncomingMessageFormParameterDefinition
     */
    public IncomingMessageFormParameterDefinition createIncomingMessageFormParameterDefinition();

    /**
     *  Creates a new instance of IncomingMessageForm
     * @return the newly created IncomingMessageForm
     */
    public IncomingMessageForm createIncomingMessageForm();

    /**
     *  Creates a new instance of IncomingMessageFormParameter
     * @return the newly created IncomingMessageFormParameter
     */
    public IncomingMessageFormParameter createIncomingMessageFormParameter();

    /**
     * Creates a new instance of GatewayRequestDAO
     * @param motechContext takes a instance of MotechContext
     * @return The newly created instance of GatewayRequestDAO
     */
    public GatewayRequestDAO createGatewayRequestDAO();

    /**
     * Creates a new instance of GatewayRequestDetailsDAO
     * @param motechContext takes a instance of MotechContext
     * @return The newly created instance of GatewayRequestDetailsDAO
     */
    public GatewayRequestDetailsDAO createGatewayRequestDetailsDAO();

    /**
     * Creates a new instance of GatewayResponseDAO
     * @param motechContext takes a instance of MotechContext
     * @return The newly created GatewayResponseDAO
     */
    public GatewayResponseDAO createGatewayResponseDAO();


    /**
     * Creates a new instance of MessageRequestDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created MessageRequestDAO
     */
    public MessageRequestDAO createMessageRequestDAO();

    /**
     * Creates a new instance of LanguageDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created LanguageDAO
     */
    public LanguageDAO createLanguageDAO();

    /**
     * Creates a new instance of MessateTemplateDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created MessageTemplateDAO
     */
    public MessageTemplateDAO createMessageTemplateDAO();

    /**
     * Creates a new instance of NotificationTypeDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created NotificationTypeDAO
     */
    public NotificationTypeDAO createNotificationTypeDAO();

    /**
     * Creates a new instance of IncomingMessageSessionDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created IncomingMessageSessionDAO
     */
    public IncomingMessageSessionDAO createIncomingMessageSessionDAO();

    /**
     * Creates a new instance of IncomingMessageDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created IncomingMessageDAO
     */
    public IncomingMessageDAO createIncomingMessageDAO();

    /**
     * Creates a new instance of IncomingMessageResponseDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created IncomingMessageResponseDAO
     */
    public IncomingMessageResponseDAO createIncomingMessageResponseDAO();

    /**
     * Creates a new instance of IncomingMessageFormDefintionDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created IncomingMessageFormDefinitionDAO
     */
    public IncomingMessageFormDefinitionDAO createIncomingMessageFormDefinitionDAO();

    /**
     * Creates a new instance of IncomingMessageFormParameterDefintionDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created IncomingMessageFormParameterDefinitionDAO
     */
    public IncomingMessageFormParameterDefinitionDAO createIncomingMessageFormParameterDefinitionDAO();

    /**
     * Creates a new instance of IncomingMessageFormDAO
     * @param motechContext takes a instance of MotechContext
     * @return the newly created IncomingMessageFormDAO
     */
    public IncomingMessageFormDAO createIncomingMessageFormDAO();

}
