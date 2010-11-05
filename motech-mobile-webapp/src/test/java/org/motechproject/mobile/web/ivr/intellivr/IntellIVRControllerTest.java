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

package org.motechproject.mobile.web.ivr.intellivr;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.ByteArrayInputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.omp.manager.intellivr.AudioType;
import org.motechproject.mobile.omp.manager.intellivr.AutoCreate;
import org.motechproject.mobile.omp.manager.intellivr.ErrorCodeType;
import org.motechproject.mobile.omp.manager.intellivr.GetIVRConfigRequest;
import org.motechproject.mobile.omp.manager.intellivr.GetIVRConfigRequestHandler;
import org.motechproject.mobile.omp.manager.intellivr.ReportHandler;
import org.motechproject.mobile.omp.manager.intellivr.ReportType;
import org.motechproject.mobile.omp.manager.intellivr.RequestType;
import org.motechproject.mobile.omp.manager.intellivr.ResponseType;
import org.motechproject.mobile.omp.manager.intellivr.StatusType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ( locations = { "classpath:WEB-INF/webapp-config.xml", "classpath:WEB-INF/intellivr-servlet.xml" } )
public class IntellIVRControllerTest {

	@Resource
	IntellIVRController intellivrController;
	private ReportHandler mockReportHandler;
	private GetIVRConfigRequestHandler mockIVRConfigHandler;
	private MockHttpServletRequest mockRequest;
	private MockHttpServletResponse mockResponse;
	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	
	@Before
	public void setUp() throws JAXBException {
		mockRequest = new MockHttpServletRequest();
		mockResponse = new MockHttpServletResponse();
		mockIVRConfigHandler = createMock(GetIVRConfigRequestHandler.class);
		mockReportHandler = createMock(ReportHandler.class);
		
		jaxbContext = JAXBContext.newInstance("org.motechproject.mobile.omp.manager.intellivr");
		unmarshaller = jaxbContext.createUnmarshaller();
		
	}
	
	/*
	 * test if a valid ivr config request is posted to the servlet
	 */
	@Test
	public void testGetIvrConfig() throws Exception {
		
		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid("123456789");
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);
		expectedResponse.setLanguage("ENGLISH");
		expectedResponse.setPrivate("PRIVATE");
		expectedResponse.setReportUrl("http://localhost");
		expectedResponse.setTree("TestTree");
		RequestType.Vxml vxml = new RequestType.Vxml();
		vxml.setPrompt(new RequestType.Vxml.Prompt());
		AudioType audio = new AudioType();
		audio.setSrc("test1.wav");
		vxml.getPrompt().getAudioOrBreak().add(audio);
		expectedResponse.setVxml(vxml);
		
		expect(mockIVRConfigHandler.handleRequest(request)).andReturn(expectedResponse);
		
		replay(mockIVRConfigHandler);
		
		intellivrController.setIvrConfigHandler(mockIVRConfigHandler);
		mockRequest.setContent(getIvrConfigXML.getBytes());
		intellivrController.handleRequestInternal(mockRequest, mockResponse);

		verify(mockIVRConfigHandler);
		
		Object o = unmarshaller.unmarshal(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
		
		assertTrue(o instanceof AutoCreate);
		assertEquals(expectedResponse, ((AutoCreate)o).getResponse());
		
	}

	String getIvrConfigXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><getIVRConfigRequest><userid>123456789</userid></getIVRConfigRequest>";
	
	/*
	 * test if a valid call report is posted to the servlet
	 */
	@Test
	public void testReport() throws Exception {
	
		ReportType report = ((AutoCreate)unmarshaller.unmarshal(new ByteArrayInputStream(reportXML.getBytes()))).getReport();
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);
		
		expect(mockReportHandler.handleReport(report)).andReturn(expectedResponse);
		
		replay(mockReportHandler);
		
		intellivrController.setReportHandler(mockReportHandler);
		mockRequest.setContent(reportXML.getBytes());
		intellivrController.handleRequestInternal(mockRequest, mockResponse);
		
		verify(mockReportHandler);
		
		Object o = unmarshaller.unmarshal(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
		
		assertTrue(o instanceof AutoCreate);
		assertEquals(expectedResponse, ((AutoCreate)o).getResponse());
		
	}
	
	String reportXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"			<AutoCreate>" +
			"			<Report>" +
			"				<Status>COMPLETED</Status>" +
			"				<Callee>2334921920193</Callee>" +
			"				<Duration>43</Duration>" +
			"				<ConnectTime>2010-10-01T00:00:00.000Z</ConnectTime>" +
			"				<DisconnectTime>2010-10-01T00:00:43.000Z</DisconnectTime>" +
			"				<INTELLIVREntryCount>4</INTELLIVREntryCount>" +
			"				<Private>IDENTIFIER</Private>" +
			"				<INTELLIVREntry menu=\"message1.wav\" entrytime=\"2010-10-01T00:00:01.000Z\" duration=\"7\" keypress=\"\" file=\"\"/>" +
			"				<INTELLIVREntry menu=\"message2.wav\" entrytime=\"2010-10-01T00:00:08.000Z\" duration=\"8\" keypress=\"\" file=\"\"/>" +
			"				<INTELLIVREntry menu=\"main_menu\" entrytime=\"2010-10-01T00:00:16.000Z\" duration=\"8\" keypress=\"2\" file=\"\"/>" +
			"				<INTELLIVREntry menu=\"pregnancy_info\" entrytime=\"2010-10-01T00:00:24.000Z\" duration=\"10\" keypress=\"1\" file=\"\"/>" +
			"				<INTELLIVREntry menu=\"feedback_section\" entrytime=\"2010-10-01T00:00:34.000Z\" duration=\"10\" keypress=\"\" file=\"http://ivr/file.wav\"/> " +
			"		</Report>" +
			"		</AutoCreate>";
	
	/*
	 * test if in valid non-xml content is posted to the servlet
	 * Normal to see Error '[Fatal Error] :1:1: Content is not allowed in prolog.' in logs as
	 * result of this
	 */
	@Test
	public void testGarbage() throws Exception {
		
		String garbage = "blahblahblah";
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.ERROR);
		expectedResponse.setErrorCode(ErrorCodeType.MOTECH_MALFORMED_XML);
		
		mockRequest.setContent(garbage.getBytes());
		
		intellivrController.handleRequestInternal(mockRequest, mockResponse);
		
		Object o = unmarshaller.unmarshal(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
		
		assertTrue(o instanceof AutoCreate);
		
		ResponseType response = ((AutoCreate)o).getResponse();
		
		assertEquals(response.getStatus(), StatusType.ERROR);
		assertEquals(response.getErrorCode(), ErrorCodeType.MOTECH_MALFORMED_XML);
		
		
	}
	
}
