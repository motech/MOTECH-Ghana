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

import java.util.Calendar;
import java.util.Date;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.Duplicatable;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncMessageStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.imp.util.IncomingMessageParser;
import org.motechproject.mobile.model.dao.imp.IncomingMessageDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of a simplistic message registry. The intended purpose is to
 * register messages as they come in and to prevent duplicate submissions.
 * 
 * @author batkinson
 * 
 */
public class MessageRegistryImpl implements MessageRegistry {

	private int duplicatePeriod;
	private CoreManager coreManager;
	private IncomingMessageParser parser;

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = DuplicateMessageException.class)
	public IncomingMessage registerMessage(String message)
			throws DuplicateMessageException {

		IncomingMessageDAO<IncomingMessage> msgDao = coreManager
				.createIncomingMessageDAO();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 0 - duplicatePeriod);
		Date beforeDate = cal.getTime();
		IncomingMessage existingMsg = msgDao
				.getByContentNonDuplicatable(message);

		if (existingMsg != null) {

			if (existingMsg.getMessageStatus() == IncMessageStatus.PROCESSING)
				throw new DuplicateProcessingException(
						"message is already processing");

			IncomingMessageForm existingMsgForm = existingMsg
					.getIncomingMessageForm();
			if (existingMsgForm != null
					&& (existingMsgForm.getMessageFormStatus() == IncMessageFormStatus.SERVER_VALID)
					&& (existingMsgForm.getIncomingMsgFormDefinition()
							.getDuplicatable() == Duplicatable.DISALLOWED || (existingMsgForm
							.getIncomingMsgFormDefinition().getDuplicatable() == Duplicatable.TIME_BOUND && existingMsg
							.getDateCreated().after(beforeDate)))) {
				throw new DuplicateMessageException("message exists");
			}
		}

		IncomingMessage newMsg = parser.parseRequest(message);

		return msgDao.save(newMsg);
	}

	/**
	 * @param duplicatePeriod
	 *            the duplicatePeriod to set
	 */
	public void setDuplicatePeriod(int duplicatePeriod) {
		this.duplicatePeriod = duplicatePeriod;
	}

	/**
	 * @param coreManager
	 *            the coreManager to set
	 */
	public void setCoreManager(CoreManager coreManager) {
		this.coreManager = coreManager;
	}

	/**
	 * @param parser
	 */
	public void setParser(IncomingMessageParser parser) {
		this.parser = parser;
	}
}
