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

import java.util.Map;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.omi.manager.OMIManager;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationException;

/**
 *
 * @author user
 */
public interface FormProcessor {
    String processForm(IncomingMessageForm form);

    void parseValidationErrors(IncomingMessageForm form, ValidationException ex);

    /**
     * @param coreManager the coreManager to set
     */
    void setCoreManager(CoreManager coreManager);

    /**
     * @param dateFormat the dateFormat to set
     */
    void setDefaultDateFormat(String dateFormat);

    /**
     * @param omiManager the omiManager to set
     */
    void setOmiManager(OMIManager omiManager);

    /**
     * @param regWS the regWS to set
     */
    void setRegWS(RegistrarService regWS);

    /**
     * @param serverErrors the serverErrors to set
     */
    void setServerErrors(Map<Integer, String> serverErrors);

    /**
     * @param serviceMethods the serviceMethods to set
     */
    Map<String, MethodSignature> getServiceMethods();

    /**
     * @param serviceMethods the serviceMethods to set
     */
    void setServiceMethods(Map<String, MethodSignature> serviceMethods);

}
