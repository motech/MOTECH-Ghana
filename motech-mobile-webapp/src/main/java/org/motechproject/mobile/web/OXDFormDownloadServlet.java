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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobile.imp.serivce.oxd.FormDefinitionService;
import org.motechproject.mobile.imp.serivce.oxd.StudyDefinitionService;
import org.motechproject.mobile.imp.serivce.oxd.UserDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

/**
 * Handles study, form and user downloads directly. This enables motech-mobile
 * to service download requests directly rather than relying on openxdata
 * server.
 * 
 * @author Brent Atkinson
 */
@Controller
@RequestMapping(value = "/formdownload", method = RequestMethod.POST)
public class OXDFormDownloadServlet {

	private static final long serialVersionUID = -1584248665268894165L;

	private static Logger log = Logger.getLogger(OXDFormDownloadServlet.class);

	public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;
	public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;

	public static final byte RESPONSE_ERROR = 0;
	public static final byte RESPONSE_SUCCESS = 1;
	public static final byte RESPONSE_ACCESS_DENIED = 2;

	private FormDefinitionService formService;
	
	private UserDefinitionService userService;
	
	private StudyDefinitionService studyService;
	
	public FormDefinitionService getFormService() {
		return formService;
	}

	public void setFormService(FormDefinitionService formService) {
		this.formService = formService;
	}

	public UserDefinitionService getUserService() {
		return userService;
	}

	public void setUserService(UserDefinitionService userService) {
		this.userService = userService;
	}

	public StudyDefinitionService getStudyService() {
		return studyService;
	}

	public void setStudyService(StudyDefinitionService studyService) {
		this.studyService = studyService;
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

		// Get our raw input and output streams
		InputStream input = request.getInputStream();
		OutputStream output = response.getOutputStream();

		// Wrap the streams for compression
		ZOutputStream zOutput = new ZOutputStream(output,
				JZlib.Z_BEST_COMPRESSION);

		// Wrap the streams so we can use logical types
		DataInputStream dataInput = new DataInputStream(input);
		DataOutputStream dataOutput = new DataOutputStream(zOutput);

		try {

			// Read the common submission data from mobile phone
			String name = dataInput.readUTF();
			String password = dataInput.readUTF();
			String serializer = dataInput.readUTF();
			String locale = dataInput.readUTF();

			byte action = dataInput.readByte();

			// TODO: add authentication, possible M6 enhancement

			log.info("downloading: name=" + name + ", password=" + password
					+ ", serializer=" + serializer + ", locale=" + locale
					+ ", action=" + action);

			EpihandyXformSerializer serObj = new EpihandyXformSerializer();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Perform the action specified by the mobile phone
			try {
				if (action == ACTION_DOWNLOAD_STUDY_LIST) {
					serObj.serializeStudies(baos, studyService.getStudies());
				} else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS) {

					serObj.serializeUsers(baos, userService.getUsers());

					int studyId = dataInput.readInt();
					String studyName = studyService.getStudyName(studyId);
					List<String> studyForms = formService
							.getStudyForms(studyId);

					serObj.serializeForms(baos, studyForms, studyId, studyName);

				}
			} catch (Exception e) {
				dataOutput.writeByte(RESPONSE_ERROR);
				throw new ServletException("failed to serialize data", e);
			}

			// Write out successful upload response
			dataOutput.writeByte(RESPONSE_SUCCESS);
			dataOutput.write(baos.toByteArray());
			response.setStatus(HttpServletResponse.SC_OK);

		} finally {
			// Should always do this
			dataOutput.flush();
			zOutput.finish();
			response.flushBuffer();
		}
	}

}
