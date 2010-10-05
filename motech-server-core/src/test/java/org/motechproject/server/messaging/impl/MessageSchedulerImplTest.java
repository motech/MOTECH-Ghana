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

package org.motechproject.server.messaging.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import junit.framework.TestCase;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.svc.RegistrarBean;

public class MessageSchedulerImplTest extends TestCase {

	MessageSchedulerImpl messageScheduler;

	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		registrarBean = createMock(RegistrarBean.class);

		messageScheduler = new MessageSchedulerImpl();
		messageScheduler.setRegistrarBean(registrarBean);
	}

	@Override
	protected void tearDown() throws Exception {
		messageScheduler = null;

		registrarBean = null;
	}

	public void testScheduleUserPref() {
		boolean userPreferencedBased = true;
		String messageKey = "Message Key";
		String messageKeyA = "Message Key A";
		String messageKeyB = "Message Key B";
		String messageKeyC = "Message Key C";
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		Date messageDate = new Date();
		Date currentDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleInfoMessages(messageKey, messageKeyA,
				messageKeyB, messageKeyC, enrollment, messageDate,
				userPreferencedBased, currentDate);

		replay(registrarBean);

		messageScheduler.scheduleMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, currentDate);

		verify(registrarBean);
	}

	public void testScheduleNoUserPref() {
		boolean userPreferencedBased = false;
		String messageKey = "Message Key";
		String messageKeyA = "Message Key A";
		String messageKeyB = "Message Key B";
		String messageKeyC = "Message Key C";
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		Date messageDate = new Date();
		Date currentDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleInfoMessages(messageKey, messageKeyA,
				messageKeyB, messageKeyC, enrollment, messageDate,
				userPreferencedBased, currentDate);

		replay(registrarBean);

		messageScheduler.scheduleMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, currentDate);

		verify(registrarBean);
	}
}
