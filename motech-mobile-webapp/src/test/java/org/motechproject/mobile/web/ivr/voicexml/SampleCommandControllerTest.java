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

package org.motechproject.mobile.web.ivr.voicexml;

import static org.junit.Assert.*;
import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ( locations = { "classpath:META-INF/test-ivr-voicexml-servlet.xml" } )
public class SampleCommandControllerTest {

	@Resource
	SampleCommandController controller;
	private MockHttpServletRequest mockRequest;
	private MockHttpServletResponse mockResponse;

	@Before
	public void setUp() {
		mockRequest = new MockHttpServletRequest();
		mockResponse = new MockHttpServletResponse();
	}

	/*
	 * test response when valid ivr request is posted to the servlet
	 */
	@Test
	public void testRequestHandler() throws Exception {

		String callerid = "1234567";
		String callFile = "sampleFile";

		mockRequest.addParameter("callerid", callerid);
		mockRequest.addParameter("callFile", callFile);
		mockRequest.setMethod("GET");

		ModelAndView m = controller.handleRequest(mockRequest, mockResponse);

		assertTrue(m.getViewName().equals("vtest2"));
		assertTrue(m.getModel().containsKey("data"));
		assertTrue(((SampleData)m.getModel().get("data")).getCallerid().equals(callerid));
		assertTrue(((SampleData)m.getModel().get("data")).getCallFile().equals(callFile));


	}

}
