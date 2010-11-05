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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the {@link IntellIVRServer} interface for requesting calls
 * be initiated to the user
 * @author fcbrooks
 *
 */
public class IntellIVRServerImpl implements IntellIVRServer {

	private String serverURL;
	private JAXBContext jaxbc;
	
	private Log log = LogFactory.getLog(IntellIVRServerImpl.class);
	
	public void init() {
		try {
			jaxbc = JAXBContext.newInstance("org.motechproject.mobile.omp.manager.intellivr");
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
	}
	
	public ResponseType requestCall(RequestType request) {
		
		AutoCreate ac = new AutoCreate();
		ac.setRequest(request);
		
		ResponseType response = null;
		
		try {
			
			Marshaller marshaller = jaxbc.createMarshaller();
			Unmarshaller unmarshaller = jaxbc.createUnmarshaller();
			
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			marshaller.marshal(ac, byteStream);

			String xml = byteStream.toString();

			URL url = new URL(this.serverURL);
			URLConnection con = url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);

			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Content-transfer-encoding", "text");

			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

			log.info("Sending request: " + xml);

			out.write(xml);
			out.flush();
			out.close();
			
			InputStream in = con.getInputStream();
			
			int len = 4096;
			byte[] buffer = new byte[len];
			int off = 0;
			int read = 0;
			while ( (read = in.read(buffer, off, len)) != -1) {
				off += read;
				len -= off;
			}
			
			String responseText = new String(buffer, 0, off);
			
			log.debug("Received response: " + responseText);
			
			Object o = unmarshaller.unmarshal(new ByteArrayInputStream(responseText.getBytes()));
			
			if ( o instanceof AutoCreate ) {
				AutoCreate acr = (AutoCreate)o;
				response = acr.getResponse();
			}
			
		} catch (MalformedURLException e) {
			log.error("",e);
		} catch (IOException e) {
			log.error("",e);
		} catch (JAXBException e) {
			log.error("",e);
		} finally {
			if ( response == null ){
				response = new ResponseType();
				response.setStatus(StatusType.ERROR);
				response.setErrorCode(ErrorCodeType.MOTECH_UNKNOWN_ERROR);
				response.setErrorString("Unknown error occurred sending request to IVR server");
			}
				
		}
		return response;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

}
