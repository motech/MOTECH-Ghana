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
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageType;
import java.util.Set;

import org.motechproject.ws.NameValuePair;

/**
 * An interface for manipulating stored message templates
 *
 * @author Kofi A. Asamoah
 * @email yoofi@dreamoval.com
 * @date 30-Apr-2009
 *
 */
public interface MessageStoreManager {

    /**
     * Builds a GatewayRequest object out of a MessageRequest
     * 
     * @param messageData the MessageRequest object containing details of the message to construct
     * @return the constructed GatewayRequest object
     */
    GatewayRequest constructMessage(MessageRequest messageData, Language defaultLang);

    /**
     * Constructs a personalized message from the provided template and parameters
     * 
     * @param template
     * @param templateParams
     * @return
     */
    String parseTemplate(String template, Set<NameValuePair> templateParams);
    
    /**
     * Fetches a template for specific message types from the message store
     * 
     * @param messageData information on the template to select
     * @return the template matching the message information
     */
    String fetchTemplate(MessageRequest messageData, Language defaultLang);

    /**
     * Converts a phone number in local format to international format
     * @param requesterPhone number to format
     * @return formatted number
     */
    String formatPhoneNumber(String requesterPhone, MessageType type);
    
    /**
     * 
     * @return the coreManager
     */
    CoreManager getCoreManager();

    /**
     * 
     * @param coreManager the CoreManager to set
     */
    void setCoreManager(CoreManager coreManager);

    /**
     * @param maxConcat the maxConcat to set
     */
    void setMaxConcat(int maxConcat);

    /**
     * @param charsPerSMS the charsPerSMS to set
     */
    void setCharsPerSMS(int charsPerSMS);

    /**
     * @param concatAllowance the concatAllowance to set
     */
    void setConcatAllowance(int concatAllowance);
}
