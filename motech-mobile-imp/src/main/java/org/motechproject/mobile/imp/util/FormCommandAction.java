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
package org.motechproject.mobile.imp.util;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncMessageResponseStatus;
import org.motechproject.mobile.core.model.IncMessageSessionStatus;
import org.motechproject.mobile.core.model.IncMessageStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.core.model.IncomingMessageResponse;
import org.motechproject.mobile.core.model.IncomingMessageSession;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDefinitionDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageResponseDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageSessionDAO;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles construction and processing of a new IncomingMessageForm
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 *  Date : Dec 5, 2009
 */
@Transactional
public class FormCommandAction implements CommandAction, ApplicationContextAware {
    private FormProcessor formProcessor;
    private IncomingMessageParser parser;
    private IncomingMessageFormValidator formValidator;
    private String senderFieldName;
    private static Logger logger = Logger.getLogger(FormCommandAction.class);
    private ApplicationContext applicationContext;

    /**
     * 
     * @see CommandAction.execute
     */
    public IncomingMessageResponse execute(IncomingMessage message, String requesterPhone) {
        IncomingMessageResponse response;
        String wsResponse = null;

        logger.info("Initializing session");
        IncomingMessageSession imSession = initializeSession(message, requesterPhone);

        logger.info("Generating form");
        IncomingMessageForm form = initializeForm(message, imSession.getFormCode());
        if (form == null) {
            response = (IncomingMessageResponse) applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class);
            response.setContent("Errors: Unknown Form!\n\n'Type' parameter missing or invalid.");
            response.setIncomingMessage(message);
            response.setDateCreated(new Date());
            response.setMessageResponseStatus(IncMessageResponseStatus.SENT);
        } else {
            logger.info("Validating form");
            IncMessageFormStatus result = formValidator.validate(form, requesterPhone);

            if(result == IncMessageFormStatus.VALID)
                wsResponse = formProcessor.processForm(form);
            
            message.setIncomingMessageForm(form);

            response = prepareResponse(message, wsResponse);
            response.setMessageResponseStatus(IncMessageResponseStatus.SENT);
        }
        logger.info("Saving request");
        message.setIncomingMessageResponse(response);
        message.setMessageStatus(IncMessageStatus.PROCESSED);
        message.setLastModified(new Date());

        imSession.setDateEnded(new Date());
        imSession.setMessageSessionStatus(IncMessageSessionStatus.ENDED);

        if(message.getIncomingMessageForm() != null && message.getIncomingMessageForm().getIncomingMsgFormParameters().containsKey(getSenderFieldName()))
            imSession.setRequesterPhone(message.getIncomingMessageForm().getIncomingMsgFormParameters().get(getSenderFieldName()).getValue());


        IncomingMessageSessionDAO sessionDao = (IncomingMessageSessionDAO) applicationContext.getBean("incomingMessageSessionDAO", IncomingMessageSessionDAO.class);
    
        try {
      
            sessionDao.save(imSession);

        } catch (Exception ex) {
            logger.error("Error finalizing incoming message session", ex);
        
        }

        return response;
    }

    /**
     * Initializes a request session conversation
     * @param message Incoming message
     * @param requesterPhone phone number of requester
     * @param context the context of the request
     * @return the initialized session
     */
    public IncomingMessageSession initializeSession(IncomingMessage message, String requesterPhone) {
        String formCode = parser.getFormCode(message.getContent());

        IncomingMessageSession imSession = (IncomingMessageSession) applicationContext.getBean("incomingMessageSession", IncomingMessageSession.class);
        imSession.setFormCode(formCode);
        imSession.setRequesterPhone(requesterPhone);
        imSession.setMessageSessionStatus(IncMessageSessionStatus.STARTED);
        imSession.setDateStarted(new Date());
        imSession.setLastActivity(new Date());
        imSession.addIncomingMessage(message);

        IncomingMessageSessionDAO sessionDao = (IncomingMessageSessionDAO) applicationContext.getBean("incomingMessageSessionDAO", IncomingMessageSessionDAO.class);
     

        try {
   
            sessionDao.save(imSession);

        } catch (Exception ex) {
            logger.error("Error initializing incoming message session", ex);
      
        }

        return imSession;
    }

    /**
     * Initializes a request form
     * @param message the request message
     * @param formCode the type of form
     * @param context the context of the request
     * @return
     */
    public IncomingMessageForm initializeForm(IncomingMessage message, String formCode) {
        IncomingMessageFormDefinition formDefn = ((IncomingMessageFormDefinitionDAO)applicationContext.getBean("incomingMessageFormDefinitionDAO", IncomingMessageFormDefinitionDAO.class)).getByCode(formCode);

        if (formDefn == null) {
            return null;
        }

        IncomingMessageForm form = (IncomingMessageForm) applicationContext.getBean("incomingMessageForm", IncomingMessageForm.class);
        form.setIncomingMsgFormDefinition(formDefn);
        form.setMessageFormStatus(IncMessageFormStatus.NEW);
        form.setDateCreated(new Date());
        form.setIncomingMsgFormParameters(new HashMap<String, IncomingMessageFormParameter>());
        form.getIncomingMsgFormParameters().putAll(parser.getParams(message.getContent()));

        IncomingMessageFormDAO formDao = (IncomingMessageFormDAO) applicationContext.getBean("incomingMessageFormDAO", IncomingMessageFormDAO.class);


        try {
   
            formDao.save(form);
       
        } catch (Exception ex) {
            logger.error("Error initializing form", ex);
         
        }

        return form;
    }

    /**
     * Prepares a response to a request message
     * @param message the message to respond to
     * @return the response to the message
     */
    public IncomingMessageResponse prepareResponse(IncomingMessage message, String wsResponse) {
        IncomingMessageForm form = message.getIncomingMessageForm();

        IncomingMessageResponse response = (IncomingMessageResponse) applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class);
        response.setDateCreated(new Date());
        response.setIncomingMessage(message);

        if (form == null) {
            response.setContent("Invalid request");
            return response;
        }

        if (form.getMessageFormStatus().equals(IncMessageFormStatus.SERVER_VALID)) {
            if(wsResponse == null || wsResponse.isEmpty())
                response.setContent("Data saved successfully");
            else
                response.setContent(wsResponse);
        } else {
            String responseText = "Errors:";
            for (Entry<String, IncomingMessageFormParameter> entry : form.getIncomingMsgFormParameters().entrySet()) {
                if (entry.getValue().getMessageFormParamStatus().equals(IncMessageFormParameterStatus.INVALID) || entry.getValue().getMessageFormParamStatus().equals(IncMessageFormParameterStatus.SERVER_INVALID)) {
                    responseText += '\n' + entry.getValue().getName() + "=" + entry.getValue().getErrText();
                }
            }
            for (String error : form.getErrors()) {
                    responseText += '\n' + error;
            }
            if (responseText.equals("Errors:")) {
                responseText = "An unexpected error occurred! Please try again.";
            }
            response.setContent(responseText);
        }
        response.setMessageResponseStatus(IncMessageResponseStatus.SAVED);

        IncomingMessageResponseDAO responseDao = (IncomingMessageResponseDAO) applicationContext.getBean("incomingMessageResponseDAO", IncomingMessageResponseDAO.class);
  

        try {

            responseDao.save(response);
    
        } catch (Exception ex) {
            logger.error("Error initializing form", ex);
         
        }

        return response;
    }

    /**
     * @return the parser
     */
    public IncomingMessageParser getParser() {
        return parser;
    }

    /**
     * @param parser the parser to set
     */
    public void setParser(IncomingMessageParser parser) {
        this.parser = parser;
    }

    /**
     * @return the formValidator
     */
    public IncomingMessageFormValidator getFormValidator() {
        return formValidator;
    }

    /**
     * @param formValidator the formValidator to set
     */
    public void setFormValidator(IncomingMessageFormValidator formValidator) {
        this.formValidator = formValidator;
    }

    /**
     * @param formProcessor the formProcessor to set
     */
    public void setFormProcessor(FormProcessor formProcessor) {
        this.formProcessor = formProcessor;
    }

    /**
     * @return the senderFieldName
     */
    public String getSenderFieldName() {
        return senderFieldName;
    }

    /**
     * @param senderFieldName the senderFieldName to set
     */
    public void setSenderFieldName(String senderFieldName) {
        this.senderFieldName = senderFieldName;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
