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
package org.motechproject.mobile.imp.serivce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageResponse;
import org.motechproject.mobile.imp.manager.IMPManager;
import org.motechproject.mobile.imp.util.CommandAction;
import org.motechproject.mobile.imp.util.IncomingMessageParser;
import org.motechproject.mobile.imp.util.IncomingMessageXMLParser;
import org.motechproject.mobile.imp.util.exception.MotechParseException;
import org.motechproject.mobile.model.dao.imp.IncomingMessageDAO;
import org.motechproject.mobile.omi.manager.OMIManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 *  Date : Dec 5, 2009
 */
@Transactional
public class IMPServiceImpl implements IMPService {

    private static Logger logger = Logger.getLogger(IMPServiceImpl.class);

    private IMPManager impManager;
    private OMIManager omiManager;
    private CoreManager coreManager;
    private IncomingMessageParser parser;
    private Map<String, CommandAction> cmdActionMap;
    private IncomingMessageXMLParser xmlParser;
    private String formProcessSuccess;
    private int maxConcat;
    private int charsPerSMS;
    private int concatAllowance;
    private int maxSMS;
    private String localNumberExpression;
    private String defaultCountryCode;
    private MessageRegistry messageRegistry;

    /**
     *
     * @see IMPService.processRequest
     */
    @SuppressWarnings("unchecked")
	public IncomingMessageResponse processRequest(String message, String requesterPhone, boolean isDemo) {

		IncomingMessageDAO<IncomingMessage> msgDao = coreManager.createIncomingMessageDAO();
        IncomingMessageResponse response = coreManager.createIncomingMessageResponse();

        IncomingMessage inMsg = null;
		try {
			inMsg = messageRegistry.registerMessage(message);
		} catch (DuplicateProcessingException dpe) {
			logger.info("duplicate form in process, returning wait message");
			response
					.setContent("Error: Duplicate in progress, please try again.");
			return response;
		} catch (DuplicateMessageException e) {
			logger.warn("duplicate form:\n" + message);
			response.setContent(formProcessSuccess);
			return response;
		}
		
		// Ensure object is attached to persistent context
		msgDao.save(inMsg); 
		
        String cmd = parser.getCommand(message);

        CommandAction action = cmdActionMap.get(cmd.toUpperCase());
        if (action == null) {
            //TODO change error to unknown form type or command
            response.setContent("Error: Unknown Form!\nPlease check the name of the form.");
            return response;
        }

        response = action.execute(inMsg, requesterPhone);

        if (inMsg.getIncomingMessageForm() != null && inMsg.getIncomingMessageForm().getIncomingMsgFormDefinition().getSendResponse() && inMsg.getIncomingMessageForm().getMessageFormStatus() == IncMessageFormStatus.SERVER_VALID) {
            sendResponse(response.getContent(), response.getIncomingMessage().getIncomingMsgSession().getRequesterPhone());
        }

        return response;
    }

    public String processRequest(String message) {
        IncomingMessageResponse result = null;

        //TODO We must also separate the processing of java forms - Logically
        result = processRequest(message, null, false);

        if (result.getContent().toLowerCase().indexOf("error") < 0) {
            return formProcessSuccess;
        } else {
            return result.getContent();
        }
    }

    /**
     * <p>Processes xForms as Motech Forms by converting them to SMS format. It then goes through normal
     * SMS processing.</p>
     *
     * @param xForms
     * @return a List of responses
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     * @throws org.motechproject.mobile.imp.util.exception.MotechParseException
     */
    public ArrayList<String> processXForms(ArrayList<String> xForms) throws JDOMException, IOException, MotechParseException {
        ArrayList<String> result = null;

        if (xForms != null) {
            result = new ArrayList<String>();
            ArrayList<String> smses = xmlParser.parseXML(xForms);
            for (String sms : smses) {
                result.add(processRequest(sms));
            }
        }

        return result;
    }

    /**
     * Validates and processes an xForm.
     *
     * @param xForm the XForm to be validated and processed
     * @return ok if successful otherwise the specifics of the error for reporting
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     * @throws org.motechproject.mobile.imp.util.exception.MotechParseException
     */
    public String processXForm(String xForm) throws JDOMException, IOException, MotechParseException {
        String result = null;

        if (xForm != null) {
            result = processXFormSMS(xmlParser.toSMSMessage(xForm));
        }
        return result;
    }

    /**
     * Processes motech mobile understandable name/value pair SMS
     *
     * @param xFormSMS 
     * @return ok if processing is successfully otherwise error message
     */
    private String processXFormSMS(String xFormSMS) {
        String result = null;

        if (xFormSMS != null) {
            result = processRequest(xFormSMS);
        }

        return result;
    }

    /**
     * Sends a response for a mobile query message
     * 
     * @param response the response message
     * @param recipient the phone number to send the response to
     */
    private void sendResponse(String response, String recipient) {
        int msgLength = (charsPerSMS - concatAllowance) * maxConcat;
        recipient = formatPhoneNumber(recipient);

        if (recipient == null || recipient.isEmpty()) {
            return;
        }

        if (response.length() <= msgLength) {
            omiManager.createOMIService().scheduleMessage(response, recipient);
        } else {
            int start = 0;
            int end = 0;

            for (byte smsNum = 1; smsNum <= maxSMS; smsNum++) {
                String currSMS = "";
                end = start + msgLength;

                if(response.length() < start)
                    break;

                if (response.length() > start + 2) {
                    currSMS = response.length() < end ? response.substring(start) : response.substring(start, end);
                }

                if (currSMS.contains("\n")) {
                    String message = "";
                    String[] lines = currSMS.split("\n");

                    for (String line : lines) {
                        int currLen = message.length() + line.length() + 1;
                        if (currLen < msgLength) {
                            message += line + "\n";
                        }
                    }
                    currSMS = message.trim();
                }
                omiManager.createOMIService().scheduleMessage(currSMS, recipient);
                start += currSMS.length();
            }
        }
    }

    public String formatPhoneNumber(
            String requesterPhone) {
        if (requesterPhone == null || requesterPhone.isEmpty()) {
            return null;
        }

        String formattedNumber = requesterPhone;
        if (Pattern.matches(localNumberExpression, requesterPhone)) {
            formattedNumber = defaultCountryCode + requesterPhone.substring(1);
        }

        return formattedNumber;
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
     * @return the cmdActionMap
     */
    public Map<String, CommandAction> getCmdActionMap() {
        return cmdActionMap;
    }

    /**
     * @param cmdActionMap the cmdActionMap to set
     */
    public void setCmdActionMap(Map<String, CommandAction> cmdActionMap) {
        this.cmdActionMap = cmdActionMap;
    }

    /**
     * @return the impManager
     */
    public IMPManager getImpManager() {
        return impManager;
    }

    /**
     * @param impManager the impManager to set
     */
    public void setImpManager(IMPManager impManager) {
        this.impManager = impManager;
    }
    
    /**
     * @return the xmlParser
     */
    public IncomingMessageXMLParser getXmlParser() {
        return xmlParser;
    }

    /**
     * @param xmlParser the xmlParser to set
     */
    public void setXmlParser(IncomingMessageXMLParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    /**
     * @return the formProcessSuccess
     */
    public String getFormProcessSuccess() {
        return formProcessSuccess;
    }

    /**
     * @param formProcessSuccess the formProcessSuccess to set
     */
    public void setFormProcessSuccess(String formProcessSuccess) {
        this.formProcessSuccess = formProcessSuccess;
    }

    /**
     * @param omiManager the omiManager to set
     */
    public void setOmiManager(OMIManager omiManager) {
        this.omiManager = omiManager;
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
     * @param maxSMS the maxSMS to set
     */
    public void setMaxSMS(int maxSMS) {
        this.maxSMS = maxSMS;
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
    
    /**
     * @param messageRegistry 
     */
    public void setMessageRegistry(MessageRegistry messageRegistry) {
		this.messageRegistry = messageRegistry;
	}
}
