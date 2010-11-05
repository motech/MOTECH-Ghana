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

package org.motechproject.mobile.web;

import org.motechproject.mobile.imp.manager.IMPManager;
import org.motechproject.mobile.imp.serivce.IMPService;
import java.util.Map;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncomingMessageResponse;

/**
 *
 * @author Henry Sampson (henry@dreamoval.com)
 * Date Created: Dec 18, 2009
 */
public class IncomingMessageRequestWorkerImpl implements IncomingMessageRequestWorker{
    private IMPManager impManager;
    private CoreManager coreManager;
    private MotechWebSettings webSettings;

    public String doRequest(Map<String, String[]> params) {
        IncomingMessageResponse response = getCoreManager().createIncomingMessageResponse();
        response.setContent("No response");

        if(params != null){
            if(params.containsKey(webSettings.getIncomingMessageFromParam())
                    &&
                    params.containsKey(webSettings.getIncomingMessageTextParam())){

                IMPService impService = getImpManager().createIMPService();

                String[] number = params.get(webSettings.getIncomingMessageFromParam());
                String[] text = params.get(webSettings.getIncomingMessageTextParam());
                
                //TODO Check the array length so it doesn't break disgracefully
                response = impService.processRequest(text[0], number[0], false);
            }else{
                response.setContent(webSettings.getUnknownIMRMessage());
            }
        }

        return response.getContent();
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
     * @return the webSettings
     */
    public MotechWebSettings getWebSettings() {
        return webSettings;
    }

    /**
     * @param webSettings the webSettings to set
     */
    public void setWebSettings(MotechWebSettings webSettings) {
        this.webSettings = webSettings;
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

}
