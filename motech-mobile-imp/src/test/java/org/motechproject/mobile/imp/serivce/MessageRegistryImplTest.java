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

package org.motechproject.mobile.imp.serivce;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.Duplicatable;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncMessageStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageImpl;
import org.motechproject.mobile.imp.util.IncomingMessageParser;
import org.motechproject.mobile.model.dao.imp.IncomingMessageDAO;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/imp-test-config.xml" })
public class MessageRegistryImplTest {

	private CoreManager mockCoreMgr;
	private IncomingMessageParser mockParser;
	private MessageRegistryImpl instance;
	private IncomingMessageDAO<IncomingMessage> mockMsgDao;

	String message = "Type=EditPatient\nCHPSID=123\nPatientRegNum=123";
	String requesterPhone = "000000000000";

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		mockCoreMgr = createMock(CoreManager.class);
		mockParser = createMock(IncomingMessageParser.class);
		mockMsgDao = createMock(IncomingMessageDAO.class);
		instance = new MessageRegistryImpl();
		instance.setCoreManager(mockCoreMgr);
		instance.setParser(mockParser);
		instance.setDuplicatePeriod(5);
	}

	@Test
	public void testUniqueMessage() throws DuplicateMessageException {
		IncomingMessage newMsg = new IncomingMessageImpl();
		expect(mockCoreMgr.createIncomingMessageDAO()).andReturn(mockMsgDao);
		expect(mockMsgDao.getByContentNonDuplicatable(message)).andReturn(null);
		expect(mockParser.parseRequest(message)).andReturn(newMsg);
		expect(mockMsgDao.save(newMsg)).andReturn(newMsg);

		replay(mockCoreMgr, mockMsgDao, mockParser);
		IncomingMessage result = instance.registerMessage(message);
		verify(mockCoreMgr, mockMsgDao, mockParser);

		assertEquals(newMsg, result);
	}

	@Test
	public void testDuplicateMessageDisallowed() {

		IncomingMessage existingMsg = new IncomingMessageImpl();
		IncomingMessageForm existingForm = new IncomingMessageFormImpl();

		IncomingMessageFormDefinition existingDef = new IncomingMessageFormDefinitionImpl();
		existingDef.setDuplicatable(Duplicatable.DISALLOWED);

		existingMsg.setIncomingMessageForm(existingForm);
		existingMsg.setContent(message);

		existingForm.setIncomingMsgFormDefinition(existingDef);
		existingForm.setMessageFormStatus(IncMessageFormStatus.SERVER_VALID);

		expect(mockCoreMgr.createIncomingMessageDAO()).andReturn(mockMsgDao);
		expect(mockMsgDao.getByContentNonDuplicatable(message)).andReturn(
				existingMsg);

		replay(mockCoreMgr, mockMsgDao, mockParser);
		try {
			instance.registerMessage(message);
			fail("should fail with duplicate exception");
		} catch (DuplicateMessageException e) {
		}
		verify(mockCoreMgr, mockMsgDao, mockParser);
	}

	@Test
	public void testDuplicateInProcess() {

		IncomingMessage existingMsg = new IncomingMessageImpl();

		existingMsg.setMessageStatus(IncMessageStatus.PROCESSING);
		existingMsg.setContent(message);

		expect(mockCoreMgr.createIncomingMessageDAO()).andReturn(mockMsgDao);
		expect(mockMsgDao.getByContentNonDuplicatable(message)).andReturn(
				existingMsg);

		replay(mockCoreMgr, mockMsgDao, mockParser);
		try {
			instance.registerMessage(message);
			fail("should fail with duplicate exception");
		} catch (DuplicateProcessingException e) {
		} catch (DuplicateMessageException de) {
			fail("should fail with duplicate processing exception");
		}
		verify(mockCoreMgr, mockMsgDao, mockParser);
	}

	@Test
	public void testRejectDuplicateMessageTimeBound() {

		// Compute creation date for existing message
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -3);
		Date existingCreated = cal.getTime();

		IncomingMessage existingMsg = new IncomingMessageImpl();
		IncomingMessageForm existingForm = new IncomingMessageFormImpl();

		IncomingMessageFormDefinition existingDef = new IncomingMessageFormDefinitionImpl();
		existingDef.setDuplicatable(Duplicatable.TIME_BOUND);

		existingMsg.setIncomingMessageForm(existingForm);
		existingMsg.setContent(message);
		existingMsg.setDateCreated(existingCreated);

		existingForm.setIncomingMsgFormDefinition(existingDef);
		existingForm.setMessageFormStatus(IncMessageFormStatus.SERVER_VALID);

		expect(mockCoreMgr.createIncomingMessageDAO()).andReturn(mockMsgDao);
		expect(mockMsgDao.getByContentNonDuplicatable(message)).andReturn(
				existingMsg);

		replay(mockCoreMgr, mockMsgDao, mockParser);
		try {
			instance.registerMessage(message);
			fail("should fail with duplicate exception");
		} catch (DuplicateMessageException e) {
		}
		verify(mockCoreMgr, mockMsgDao, mockParser);
	}

	@Test
	public void testAcceptDuplicateMessageTimeBound()
			throws DuplicateMessageException {

		// Compute creation date for existing message
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -10);
		Date existingCreated = cal.getTime();

		IncomingMessage existingMsg = new IncomingMessageImpl();
		IncomingMessageForm existingForm = new IncomingMessageFormImpl();
		IncomingMessageFormDefinition existingDef = new IncomingMessageFormDefinitionImpl();
		IncomingMessage newMsg = new IncomingMessageImpl();

		existingDef.setDuplicatable(Duplicatable.TIME_BOUND);

		existingMsg.setIncomingMessageForm(existingForm);
		existingMsg.setContent(message);
		existingMsg.setDateCreated(existingCreated);

		existingForm.setIncomingMsgFormDefinition(existingDef);
		existingForm.setMessageFormStatus(IncMessageFormStatus.SERVER_VALID);

		expect(mockCoreMgr.createIncomingMessageDAO()).andReturn(mockMsgDao);
		expect(mockMsgDao.getByContentNonDuplicatable(message)).andReturn(
				existingMsg);
		expect(mockParser.parseRequest(message)).andReturn(newMsg);
		expect(mockMsgDao.save(newMsg)).andReturn(newMsg);

		replay(mockCoreMgr, mockMsgDao, mockParser);
		IncomingMessage result = instance.registerMessage(message);
		verify(mockCoreMgr, mockMsgDao, mockParser);

		assertEquals(newMsg, result);
	}
}
