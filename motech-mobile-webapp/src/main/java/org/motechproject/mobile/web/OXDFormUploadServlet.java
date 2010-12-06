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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fcitmuk.epihandy.ResponseHeader;
import org.motechproject.mobile.core.model.MxFormProcessingResponse;
import org.motechproject.mobile.imp.serivce.IMPService;
import org.motechproject.mobile.imp.serivce.MessageDeserializationException;
import org.motechproject.mobile.imp.serivce.XFormDefinitionNotFoundException;
import org.motechproject.mobile.imp.serivce.oxd.IncomingMessageProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

/**
 *
 * @author Henry Sampson (henry@dreamoval.com) and Brent Atkinson Igor Opushnyev
 */
@Controller
@RequestMapping(value = "/module/motechmodule/formupload")
public class OXDFormUploadServlet {

    private static Logger log = Logger.getLogger(OXDFormUploadServlet.class);
	private static Logger rawUploadLog = Logger.getLogger(OXDFormUploadServlet.class.getName() + ".mformsraw");

    private IncomingMessageProcessor incomingMessageProcessor;
    private IMPService impService;


    public void setImpService(IMPService impService) {
        this.impService = impService;
    }


    public void setIncomingMessageProcessor(IncomingMessageProcessor incomingMessageProcessor) {
        this.incomingMessageProcessor = incomingMessageProcessor;
    }

    /**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		/*IMPService impService = (IMPService) appCtx.getBean("impService");
		StudyProcessor studyProcessor = (StudyProcessor) appCtx
				.getBean("studyProcessor");*/

		InputStream input = request.getInputStream();
		OutputStream output = response.getOutputStream();

		ZOutputStream zOutput = null; // Wrap the streams for compression

		// Wrap the streams so for logical types
		DataInputStream dataInput;
		DataOutputStream dataOutput = null;

		// Set the MIME type so clients don't misinterpret
		response.setContentType("application/octet-stream");
		
		try {
			zOutput = new ZOutputStream(output, JZlib.Z_BEST_COMPRESSION);
			dataInput = new DataInputStream(input);
			dataOutput = new DataOutputStream(zOutput);


            byte[] rawPayload = IOUtils.toByteArray(dataInput);
			String hexEncodedPayload = Hex.encodeHexString(rawPayload);
            dataInput.close();
			
			if (rawUploadLog.isInfoEnabled()) {
				//byte[] rawPayload = IOUtils.toByteArray(dataInput);
				//String hexEncodedPayload = Hex.encodeHexString(rawPayload);
				rawUploadLog.info(hexEncodedPayload);
				// Replace the original input stream with one using read payload
				//dataInput.close();
				//dataInput = new DataInputStream(new ByteArrayInputStream(rawPayload));
			}


            MxFormProcessingResponse formProcessingResponse;

            try {
                formProcessingResponse = (MxFormProcessingResponse) incomingMessageProcessor.processIncomingMessage(rawPayload, null);
            } catch (XFormDefinitionNotFoundException e) {
                String msg = "failed to deserialize forms: ";
				log.error(msg + e.getMessage());
				dataOutput.writeByte(ResponseHeader.STATUS_FORMS_STALE);
				response.setStatus(HttpServletResponse.SC_OK);
                flushResponse(zOutput, dataOutput, response);
                return;
            } catch (MessageDeserializationException e) {
                String msg = "failed to deserialize forms";
				log.error(msg, e);
				dataOutput.writeByte(ResponseHeader.STATUS_ERROR);
				response.setStatus(HttpServletResponse.SC_OK);
                flushResponse(zOutput, dataOutput, response);
                return;
            }

			// Write out usual upload response
			dataOutput.writeByte(ResponseHeader.STATUS_SUCCESS);

            dataOutput.writeInt(formProcessingResponse.getProcessedFormsNum());
			dataOutput.writeInt(formProcessingResponse.getFaultyFormsNum());

            List<List<String>> studyFormProcessingResults = formProcessingResponse.getFormProcessingResults();
            int studiesNum = studyFormProcessingResults.size();
            for (int s = 0; s < studiesNum; s++) {
                List<String> formProcessingResults = studyFormProcessingResults.get(s);
                int formsNum = formProcessingResults.size();
				for (int f = 0; f < formsNum; f++) {
                    String formProcessingResult = formProcessingResults.get(f);
					if (!impService.getFormProcessSuccess().equalsIgnoreCase(
							formProcessingResult)) {
						dataOutput.writeByte((byte) s);
						dataOutput.writeShort((short) f);
						dataOutput.writeUTF(formProcessingResult);
					}
				}
			}

			response.setStatus(HttpServletResponse.SC_OK);
		}
		catch (Exception e) {
			log.error("failure during upload",e);		
		} finally {
            
            flushResponse(zOutput, dataOutput, response);
		}
	}
    
    private void flushResponse(ZOutputStream zOutput, DataOutputStream dataOutput,
                               HttpServletResponse response) throws IOException {
        if (dataOutput != null)
				dataOutput.flush();
			if (zOutput != null)
				zOutput.finish();
			response.flushBuffer();
    }
}
