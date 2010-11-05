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

package org.motechproject.mobile.omp.manager.intellivr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a response for a test userid '123456789' for generating test content
 * when calling into the ivr system.  All other ids are pased to the real
 * {@link IntellIVRBean}
 * @author fcbrooks
 *
 */
public class IntellIVRDummyBean extends IntellIVRBean { 
	
	private String testID = "123456789";
	
	private Log log = LogFactory.getLog(IntellIVRDummyBean.class);
	
	public ResponseType handleRequest(GetIVRConfigRequest request) {
		if ( request.getUserid().equalsIgnoreCase(testID) ) {
			log.info("Received request for id " + request.getUserid());
			ResponseType rt = new ResponseType();
			rt.setStatus(StatusType.OK);
			rt.setLanguage(this.getDefaultLanguage());
			rt.setPrivate(testID);
			rt.setReportUrl(this.getReportURL());
			rt.setTree(this.getDefaultTree());
			RequestType.Vxml vxml = new RequestType.Vxml();
			vxml.setPrompt(new RequestType.Vxml.Prompt());
			AudioType audio = new AudioType();
			audio.setSrc(this.getDefaultReminder());
			vxml.getPrompt().getAudioOrBreak().add(audio);
			rt.setVxml(vxml);
			return rt;
		} else 
			return super.handleRequest(request);
	}
	
}
