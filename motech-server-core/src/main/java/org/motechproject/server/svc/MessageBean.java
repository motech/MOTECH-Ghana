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

package org.motechproject.server.svc;

import java.util.Date;
import java.util.List;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.ScheduledMessage;
import org.openmrs.Patient;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * The service interface for messaging in the motech server project.
 */
public interface MessageBean {

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void setMessageStatus(String messageId, Boolean success);

	public List<ScheduledMessage> getScheduledMessages(
			MessageProgramEnrollment enrollment);

	public void removeMessageProgramEnrollment(
			MessageProgramEnrollment enrollment);

	public void removeAllMessageProgramEnrollments(Integer personId);

	public void addMessageProgramEnrollment(Integer personId, String program,
			Integer obsId);

	public String[] getActiveMessageProgramEnrollmentNames(Patient patient);

	public void scheduleInfoMessages(String messageKey, String messageKeyA,
			String messageKeyB, String messageKeyC,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, Date currentDate);

	public ScheduledMessage scheduleCareMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, String care, Date currentDate);

	public void removeAllUnsentMessages(MessageProgramEnrollment enrollment);

	public void removeUnsentMessages(List<ScheduledMessage> scheduledMessages);

	public void addMessageAttempt(ScheduledMessage scheduledMessage,
			Date attemptDate, Date maxAttemptDate, boolean userPreferenceBased,
			Date currentDate);

	public Date determineUserPreferredMessageDate(Integer recipientId,
			Date messageDate);

	public void verifyMessageAttemptDate(ScheduledMessage scheduledMessage,
			boolean userPreferenceBased, Date currentDate);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS })
	public void updateMessageProgramState(Integer personId, String conceptName);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS, OpenmrsConstants.PRIV_VIEW_OBS })
	public void sendMessages(Date startDate, Date endDate, boolean sendImmediate);

	public void sendDeliveryNotification(Patient patient);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_USERS })
	public void sendStaffCareMessages(Date startDate, Date endDate,
			Date deliveryDate, Date deliveryTime, String[] careGroups,
			boolean sendUpcoming, boolean avoidBlackout);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_OBS, OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_LOCATIONS,
			OpenmrsConstants.PRIV_ADD_OBS, OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_MANAGE_SCHEDULER })
	public TaskDefinition updateAllMessageProgramsState(Integer batchSize,
			Long batchPreviousId, Long batchMaxId);

}
