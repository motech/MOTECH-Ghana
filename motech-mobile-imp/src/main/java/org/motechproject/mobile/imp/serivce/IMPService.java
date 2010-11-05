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

import org.motechproject.mobile.imp.util.exception.MotechParseException;
import java.io.IOException;
import java.util.ArrayList;
import org.jdom.JDOMException;
import org.motechproject.mobile.core.model.IncomingMessageResponse;

/**
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 *  Date : Dec 5, 2009
 */
public interface IMPService {

    /**
     * Processes an incoming message request
     * @param message the content of the request
     * @param requesterPhone the phone number through which the request was made
     * @return the response of the request
     */
    IncomingMessageResponse processRequest(String message, String requesterPhone, boolean isDemo);

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
    ArrayList<String> processXForms(ArrayList<String> xForms) throws JDOMException, IOException, MotechParseException;

    /**
     * Validates and processes an xForm.
     *
     * @param xForm the XForm to be validated and processed
     * @return ok if successful otherwise the specifics of the error for reporting
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     * @throws org.motechproject.mobile.imp.util.exception.MotechParseException
     */
    String processXForm(String xForm) throws JDOMException, IOException, MotechParseException;

    String formatPhoneNumber(String requesterPhone);

    /**
     * @return the formProcessSuccess
     */
    String getFormProcessSuccess();

    /**
     * @param formProcessSuccess the formProcessSuccess to set
     */
    void setFormProcessSuccess(String formProcessSuccess);

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

    /**
     * @param maxSMS the maxSMS to set
     */
    void setMaxSMS(int maxSMS);
}
