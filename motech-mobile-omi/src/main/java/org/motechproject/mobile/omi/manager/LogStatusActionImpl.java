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

import org.motechproject.mobile.core.model.GatewayResponse;
import org.apache.log4j.Logger;
import org.motechproject.ws.LogType;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.ws.server.RegistrarService;

/**
 * Provides external access to OMI methods
 *
 * @author Kofi A. Asamoah (yoofi@dremoval.com)
 * Date Created: Sept 30, 2009
 */
public class LogStatusActionImpl implements StatusAction {
   private RegistrarService regWs;
   private static Logger logger = Logger.getLogger(LogStatusActionImpl.class);

   /**
    *
    * @see StatusAction.doAction
    */
   public void doAction(GatewayResponse response){       
       LogType logType;
       
       String summary = "Status of message with id "
               + response.getRequestId()
               + " is: " 
               + response.getMessageStatus().toString() 
               + ". Response from gateway was: "
               + response.getResponseText();
       
       
       if(response.getMessageStatus() == MStatus.DELIVERED){
           logType = LogType.SUCCESS;
       }
       else if(response.getMessageStatus() == MStatus.SENT){
           logType = LogType.SUCCESS;
       }
       else{
           logType = LogType.FAILURE;
       }
       
       try{
            getRegWs().log(logType, summary);
       }
       catch(Exception e){
           logger.error("Error communicating with logging service", e);
       }
   }

    public RegistrarService getRegWs() {
        return regWs;
    }

    public void setRegWs(RegistrarService regWs) {
        this.regWs = regWs;
    }
}
