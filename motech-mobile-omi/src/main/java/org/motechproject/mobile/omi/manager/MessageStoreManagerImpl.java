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

package org.motechproject.mobile.omi.manager;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageTemplate;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.util.MotechException;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.motechproject.ws.NameValuePair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * An implementation of the MessageStore interface
 *
 * @author Kofi A. Asamoah
 * @email yoofi@dreamoval.com
 * @date 30-JULY-2009
 *
 */
public class MessageStoreManagerImpl implements MessageStoreManager, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(MessageStoreManagerImpl.class);
    private CoreManager coreManager;
    private int maxConcat;
    private int charsPerSMS;
    private int concatAllowance;
    private String localNumberExpression = "";
    private String defaultCountryCode = "";
    private ApplicationContext applicationContext;

    /**
     * 
     * @see MessageStoreManager.constructMessage
     */
    public GatewayRequest constructMessage(MessageRequest messageData, Language defaultLang) {

        GatewayRequest gwReq = (GatewayRequest) applicationContext.getBean("gatewayRequest", GatewayRequest.class);
        gwReq.setDateFrom(messageData.getDateFrom());
        gwReq.setDateTo(messageData.getDateTo());
        gwReq.setRecipientsNumber(formatPhoneNumber(messageData.getRecipientNumber(), messageData.getMessageType()));
        gwReq.setRequestId(messageData.getRequestId());
        gwReq.setTryNumber(messageData.getTryNumber());
        gwReq.setMessageRequest(messageData);
        
        GatewayRequestDetails gatewayDetails = (GatewayRequestDetails) applicationContext.getBean("gatewayRequestDetails", GatewayRequestDetails.class);
        gatewayDetails.setMessageType(messageData.getMessageType());
                
        try{
        	
        	if ( messageData.getMessageType() == MessageType.TEXT ) {

        		String template = fetchTemplate(messageData, defaultLang);
        		logger.debug("message template fetched");
        		logger.debug(template);

        		String message = parseTemplate(template, messageData.getPersInfos());
        		logger.debug("message contructed");
        		logger.debug(message);

        		int maxLength = (charsPerSMS - concatAllowance) * maxConcat - 1;
        		message = message.length() <= maxLength ? message : message.substring(0, maxLength);

        		int numPages = (int)Math.ceil(message.length() % (charsPerSMS - concatAllowance));
        		gatewayDetails.setNumberOfPages(numPages);

        		gwReq.setMessage(message);
                        gatewayDetails.setMessage(message);
                
        	}

            gwReq.setMessageStatus(MStatus.SCHEDULED);
            gwReq.setGatewayRequestDetails(gatewayDetails);

        }
        catch(MotechException ex){
        	logger.error("MotechException: " + ex.getMessage());
            gwReq.setMessageStatus(MStatus.FAILED);
            gwReq.setMessage(null);
            
            GatewayResponse gwResp = coreManager.createGatewayResponse();
            gwResp.setGatewayRequest(gwReq);
            gwResp.setMessageStatus(MStatus.FAILED);
            gwResp.setResponseText(ex.getMessage());
            
            gwReq.getResponseDetails().add(gwResp);
        }       
        
        gatewayDetails.getGatewayRequests().add(gwReq);
        logger.debug("GatewayRequest object successfully constructed");
        logger.debug(gatewayDetails);
            
////        messageData.setGatewayRequestDetails(gatewayDetails);

        return gwReq;
    }

    /**
     * 
     * @see MessageStoreManager.parseTemplate
     */
    public String parseTemplate(String template, Set<NameValuePair> templateParams) {
        String tag, value;  
        
        if(templateParams == null){
            return template;
        }
        
        for(NameValuePair detail : templateParams){
            tag = "<"+ detail.getName() + ">";
            value = detail.getValue();

            if(value != null && !value.isEmpty())
                template = template.replaceAll(tag, value);
        }
        
        return template.trim();
    }

    /**
     * 
     * @see MessageStoreManager.fetchTemplate
     */
    public String fetchTemplate(MessageRequest messageData, Language defaultLang) {        
        if(messageData.getNotificationType() == null)
            return "";
        
        MessageTemplate template = coreManager.createMessageTemplateDAO().getTemplateByLangNotifMType(messageData.getLanguage(), messageData.getNotificationType(), messageData.getMessageType(), defaultLang);
        
        if(template == null)
            throw new MotechException("No such NotificationType found");
            
        return template.getTemplate();
    }

    public String formatPhoneNumber(String requesterPhone, MessageType type) {
        if (requesterPhone == null || requesterPhone.isEmpty()) {
            return null;
        }

        String formattedNumber = requesterPhone;
        if (Pattern.matches(localNumberExpression, requesterPhone)) {
        	if ( type == MessageType.VOICE )
        		formattedNumber = requesterPhone.substring(1);
        	else
        		formattedNumber = defaultCountryCode + requesterPhone.substring(1);
        }

        return formattedNumber;
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public void setCoreManager(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    /**
     * @param maxConcat the maxConcat to set
     */
    public void setMaxConcat(int maxConcat) {
        this.maxConcat = maxConcat;
    }

    /**
     * @param charsPerSMS the charsPerSMS to set
     */
    public void setCharsPerSMS(int charsPerSMS) {
        this.charsPerSMS = charsPerSMS;
    }

    /**
     * @param concatAllowance the concatAllowance to set
     */
    public void setConcatAllowance(int concatAllowance) {
        this.concatAllowance = concatAllowance;
    }

    /**
     * @param localNumberExpression the localNumberExpression to set
     */
    public void setLocalNumberExpression(String localNumberExpression) {
        this.localNumberExpression = localNumberExpression;
    }

    /**
     * @param defaultCountryCode the defaultCountryCode to set
     */
    public void setDefaultCountryCode(String defaultCountryCode) {
        this.defaultCountryCode = defaultCountryCode;
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
