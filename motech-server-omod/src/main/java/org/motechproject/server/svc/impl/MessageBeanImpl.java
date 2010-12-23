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

package org.motechproject.server.svc.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.messaging.MessageDefDate;
import org.motechproject.server.messaging.MessageNotFoundException;
import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.ExpectedCareBean;
import org.motechproject.server.svc.MessageBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.server.ws.WebServiceModelConverter;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.PatientMessage;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of the MessageBean interface.
 */
public class MessageBeanImpl implements MessageBean {

	private static Log log = LogFactory.getLog(RegistrarBeanImpl.class);

	private ContextService contextService;
	private OpenmrsBean openmrsBean;
	private ExpectedCareBean expectedCareBean;
	private WebServiceModelConverter modelConverter;
	private MessageService mobileService;
	private Map<String, MessageProgram> messagePrograms;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	public void setExpectedCareBean(ExpectedCareBean expectedCareBean) {
		this.expectedCareBean = expectedCareBean;
	}

	public void setModelConverter(WebServiceModelConverter modelConverter) {
		this.modelConverter = modelConverter;
	}

	public void setMobileService(MessageService mobileService) {
		this.mobileService = mobileService;
	}

	public void setMessagePrograms(Map<String, MessageProgram> messagePrograms) {
		this.messagePrograms = messagePrograms;
	}

	protected MessageProgram getMessageProgram(String programName) {
		return messagePrograms.get(programName);
	}

	@Transactional
	public void setMessageStatus(String messageId, Boolean success) {

		log.debug("setMessageStatus WS: messageId: " + messageId
				+ ", success: " + success);

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		Message message = motechService.getMessage(messageId);
		if (message == null) {
			throw new MessageNotFoundException();
		}

		Integer recipientId = message.getSchedule().getRecipientId();
		Person messageRecipient = personService.getPerson(recipientId);
		String phoneNumber = openmrsBean.getPersonPhoneNumber(messageRecipient);
		TroubledPhone troubledPhone = motechService
				.getTroubledPhone(phoneNumber);

		if (success) {
			message.setAttemptStatus(MessageStatus.DELIVERED);

			if (troubledPhone != null) {
				motechService.removeTroubledPhone(phoneNumber);
			}
		} else {
			message.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);

			if (troubledPhone == null) {
				motechService.addTroubledPhone(phoneNumber);
			} else {
				Integer sendFailures = troubledPhone.getSendFailures() + 1;
				troubledPhone.setSendFailures(sendFailures);
				motechService.saveTroubledPhone(troubledPhone);
			}
		}
		motechService.saveMessage(message);
	}

	public List<ScheduledMessage> getScheduledMessages(
			MessageProgramEnrollment enrollment) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getScheduledMessages(null, null, enrollment, null);
	}

	public void scheduleInfoMessages(String messageKey, String messageKeyA,
			String messageKeyB, String messageKeyC,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, Date currentDate) {

		PersonService personService = contextService.getPersonService();

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();
		Person recipient = personService.getPerson(messageRecipientId);
		MediaType mediaType = openmrsBean.getPersonMediaType(recipient);

		// Schedule multiple messages if media type preference is text, or no
		// preference exists, using A/B/C message keys
		if (mediaType == MediaType.TEXT) {
			scheduleMultipleInfoMessages(messageKeyA, messageKeyB, messageKeyC,
					enrollment, messageDate, userPreferenceBased, currentDate);
		} else {
			scheduleSingleInfoMessage(messageKey, enrollment, messageDate,
					userPreferenceBased, currentDate);
		}
	}

	void scheduleMultipleInfoMessages(String messageKeyA, String messageKeyB,
			String messageKeyC, MessageProgramEnrollment enrollment,
			Date messageDate, boolean userPreferenceBased, Date currentDate) {
		// Return existing message definitions
		MessageDefinition messageDefinitionA = this
				.getMessageDefinition(messageKeyA);
		MessageDefinition messageDefinitionB = this
				.getMessageDefinition(messageKeyB);
		MessageDefinition messageDefinitionC = this
				.getMessageDefinition(messageKeyC);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Expecting message date to already be preference adjusted
		// Determine dates for second and third messages
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(messageDate);
		calendar.add(Calendar.DATE, 2);
		Date messageDateB = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		Date messageDateC = calendar.getTime();

		MessageDefDate messageA = new MessageDefDate(messageDefinitionA,
				messageDate);
		MessageDefDate messageB = new MessageDefDate(messageDefinitionB,
				messageDateB);
		MessageDefDate messageC = new MessageDefDate(messageDefinitionC,
				messageDateC);
		MessageDefDate[] messageDefDates = { messageA, messageB, messageC };

		// Cancel any unsent messages for the same enrollment and not matching
		// the messages to schedule
		this.removeUnsentMessages(messageRecipientId, enrollment,
				messageDefDates);

		// Create new scheduled message (with pending attempt) for all 3
		// messages, for this enrollment, if no matching message already exist
		this.createScheduledMessage(messageRecipientId, messageDefinitionA,
				enrollment, messageDate, currentDate);
		this.createScheduledMessage(messageRecipientId, messageDefinitionB,
				enrollment, messageDateB, currentDate);
		this.createScheduledMessage(messageRecipientId, messageDefinitionC,
				enrollment, messageDateC, currentDate);
	}

	void scheduleSingleInfoMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, Date currentDate) {

		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Expecting message date to already be preference adjusted

		// Cancel any unsent messages for the same enrollment and not matching
		// the message to schedule
		this.removeUnsentMessages(messageRecipientId, enrollment,
				messageDefinition, messageDate);

		// Create new scheduled message (with pending attempt) for enrollment
		// if none matching already exist
		this.createScheduledMessage(messageRecipientId, messageDefinition,
				enrollment, messageDate, currentDate);
	}

	public ScheduledMessage scheduleCareMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, String care, Date currentDate) {
		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Create new scheduled message (with pending attempt) for enrollment
		// Does not check if one already exists
		return this.createCareScheduledMessage(messageRecipientId,
				messageDefinition, enrollment, messageDate, care,
				userPreferenceBased, currentDate);
	}

	private MessageDefinition getMessageDefinition(String messageKey) {
		MotechService motechService = contextService.getMotechService();
		MessageDefinition messageDefinition = motechService
				.getMessageDefinition(messageKey);
		if (messageDefinition == null) {
			log.error("Invalid message key for message definition: "
					+ messageKey);
		}
		return messageDefinition;
	}

	protected void removeUnsentMessages(Integer recipientId,
			MessageProgramEnrollment enrollment,
			MessageDefDate[] messageDefDates) {
		MotechService motechService = contextService.getMotechService();
		// Get Messages matching the recipient, enrollment, and status, but
		// not matching the list of message definitions and message dates
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				enrollment, messageDefDates, MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found during scheduling: "
				+ unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {
			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled to schedule new: Id: "
					+ unsentMessage.getId());
		}
	}

	protected void removeUnsentMessages(Integer recipientId,
			MessageProgramEnrollment enrollment,
			MessageDefinition messageDefinition, Date messageDate) {
		MotechService motechService = contextService.getMotechService();
		// Get Messages matching the recipient, enrollment, and status, but
		// not matching the message definition and message date
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				enrollment, messageDefinition, messageDate,
				MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found during scheduling: "
				+ unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {
			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled to schedule new: Id: "
					+ unsentMessage.getId());
		}
	}

	public void removeUnsentMessages(List<ScheduledMessage> scheduledMessages) {
		MotechService motechService = contextService.getMotechService();

		for (ScheduledMessage scheduledMessage : scheduledMessages) {
			for (Message unsentMessage : scheduledMessage.getMessageAttempts()) {
				if (MessageStatus.SHOULD_ATTEMPT == unsentMessage
						.getAttemptStatus()) {

					unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
					motechService.saveMessage(unsentMessage);

					log
							.debug("Message cancelled: Id: "
									+ unsentMessage.getId());
				}
			}
		}
	}

	public void addMessageAttempt(ScheduledMessage scheduledMessage,
			Date attemptDate, Date maxAttemptDate, boolean userPreferenceBased,
			Date currentDate) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		MessageDefinition messageDefinition = scheduledMessage.getMessage();
		Person recipient = personService.getPerson(scheduledMessage
				.getRecipientId());

		Date adjustedMessageDate = adjustCareMessageDate(recipient,
				attemptDate, userPreferenceBased, currentDate);
		// Prevent scheduling reminders too far in future
		// Only schedule one reminder ahead
		if (!adjustedMessageDate.after(maxAttemptDate)) {
			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(attemptDate);
			scheduledMessage.getMessageAttempts().add(message);

			if (log.isDebugEnabled()) {
				log.debug("Added ScheduledMessage Attempt: recipient: "
						+ scheduledMessage.getRecipientId() + ", message key: "
						+ messageDefinition.getMessageKey() + ", date: "
						+ adjustedMessageDate);
			}

			motechService.saveScheduledMessage(scheduledMessage);
		}
	}

	public void verifyMessageAttemptDate(ScheduledMessage scheduledMessage,
			boolean userPreferenceBased, Date currentDate) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();
		Person recipient = personService.getPerson(scheduledMessage
				.getRecipientId());

		List<Message> messages = scheduledMessage.getMessageAttempts();
		if (!messages.isEmpty()) {
			Message recentMessage = messages.get(0);
			if (recentMessage.getAttemptStatus() == MessageStatus.SHOULD_ATTEMPT) {
				Date attemptDate = recentMessage.getAttemptDate();
				// Check if current message date is valid for user
				// preferences or blackout incase these have changed
				if (userPreferenceBased) {
					attemptDate = determinePreferredMessageDate(recipient,
							attemptDate, currentDate, true);
				} else {
					attemptDate = adjustForBlackout(attemptDate);
				}
				if (!attemptDate.equals(recentMessage.getAttemptDate())) {
					// Recompute from original scheduled message date
					// Allows possibly adjusting to an earlier week or day
					Date adjustedMessageDate = adjustCareMessageDate(recipient,
							scheduledMessage.getScheduledFor(),
							userPreferenceBased, currentDate);

					if (log.isDebugEnabled()) {
						log.debug("Updating message id="
								+ recentMessage.getId() + " date from="
								+ recentMessage.getAttemptDate() + " to="
								+ adjustedMessageDate);
					}

					recentMessage.setAttemptDate(adjustedMessageDate);
					scheduledMessage.getMessageAttempts().set(0, recentMessage);
					motechService.saveScheduledMessage(scheduledMessage);
				}
			}
		}
	}

	public void removeAllUnsentMessages(MessageProgramEnrollment enrollment) {
		MotechService motechService = contextService.getMotechService();
		List<Message> unsentMessages = motechService.getMessages(enrollment,
				MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found to cancel: " + unsentMessages.size()
				+ ", for enrollment: " + enrollment.getId());

		for (Message unsentMessage : unsentMessages) {
			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled: Id: " + unsentMessage.getId());
		}
	}

	public Date determineUserPreferredMessageDate(Integer recipientId,
			Date messageDate) {
		PersonService personService = contextService.getPersonService();
		Person recipient = personService.getPerson(recipientId);

		return determinePreferredMessageDate(recipient, messageDate, null,
				false);
	}

	private void createScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition,
			MessageProgramEnrollment enrollment, Date messageDate,
			Date currentDate) {

		MotechService motechService = contextService.getMotechService();

		List<ScheduledMessage> scheduledMessages = motechService
				.getScheduledMessages(recipientId, messageDefinition,
						enrollment, messageDate);

		// Add scheduled message and message attempt is none matching exists
		if (scheduledMessages.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Creating ScheduledMessage: recipient: "
						+ recipientId + ", enrollment: " + enrollment.getId()
						+ ", message key: " + messageDefinition.getMessageKey()
						+ ", date: " + messageDate);
			}

			ScheduledMessage scheduledMessage = new ScheduledMessage();
			scheduledMessage.setScheduledFor(messageDate);
			scheduledMessage.setRecipientId(recipientId);
			scheduledMessage.setMessage(messageDefinition);
			scheduledMessage.setEnrollment(enrollment);

			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(messageDate);
			scheduledMessage.getMessageAttempts().add(message);

			motechService.saveScheduledMessage(scheduledMessage);
		} else {
			if (scheduledMessages.size() > 1 && log.isWarnEnabled()) {
				log.warn("Multiple matching scheduled messages: recipient: "
						+ recipientId + ", enrollment: " + enrollment.getId()
						+ ", message key: " + messageDefinition.getMessageKey()
						+ ", date: " + messageDate);
			}
			// Add message attempt to existing scheduled message if not exist
			boolean matchFound = false;
			ScheduledMessage scheduledMessage = scheduledMessages.get(0);
			for (Message message : scheduledMessage.getMessageAttempts()) {
				if ((MessageStatus.SHOULD_ATTEMPT == message.getAttemptStatus()
						|| MessageStatus.ATTEMPT_PENDING == message
								.getAttemptStatus()
						|| MessageStatus.DELIVERED == message
								.getAttemptStatus() || MessageStatus.REJECTED == message
						.getAttemptStatus())
						&& messageDate.equals(message.getAttemptDate())) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound && !currentDate.after(messageDate)) {
				if (log.isDebugEnabled()) {
					log.debug("Creating Message: recipient: " + recipientId
							+ ", enrollment: " + enrollment.getId()
							+ ", message key: "
							+ messageDefinition.getMessageKey() + ", date: "
							+ messageDate);
				}

				Message message = messageDefinition
						.createMessage(scheduledMessage);
				message.setAttemptDate(messageDate);
				scheduledMessage.getMessageAttempts().add(message);

				motechService.saveScheduledMessage(scheduledMessage);
			}
		}
	}

	private ScheduledMessage createCareScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition,
			MessageProgramEnrollment enrollment, Date messageDate, String care,
			boolean userPreferenceBased, Date currentDate) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		ScheduledMessage scheduledMessage = new ScheduledMessage();
		scheduledMessage.setScheduledFor(messageDate);
		scheduledMessage.setRecipientId(recipientId);
		scheduledMessage.setMessage(messageDefinition);
		scheduledMessage.setEnrollment(enrollment);
		// Set care field on scheduled message (not set on informational
		// messages)
		scheduledMessage.setCare(care);

		Person recipient = personService.getPerson(recipientId);
		Date adjustedMessageDate = adjustCareMessageDate(recipient,
				messageDate, userPreferenceBased, currentDate);

		Message message = messageDefinition.createMessage(scheduledMessage);
		message.setAttemptDate(adjustedMessageDate);
		scheduledMessage.getMessageAttempts().add(message);

		if (log.isDebugEnabled()) {
			log.debug("Creating ScheduledMessage: recipient: " + recipientId
					+ ", enrollment: " + enrollment.getId() + ", message key: "
					+ messageDefinition.getMessageKey() + ", date: "
					+ adjustedMessageDate);
		}

		return motechService.saveScheduledMessage(scheduledMessage);
	}

	Date adjustCareMessageDate(Person person, Date messageDate,
			boolean userPreferenceBased, Date currentDate) {
		Date adjustedDate = verifyFutureDate(messageDate);
		if (userPreferenceBased) {
			adjustedDate = determinePreferredMessageDate(person, adjustedDate,
					currentDate, true);
		} else {
			adjustedDate = adjustForBlackout(adjustedDate);
		}
		return adjustedDate;
	}

	Date verifyFutureDate(Date messageDate) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.getTime().after(messageDate)) {
			// If date in past, return date 10 minutes in future
			calendar.add(Calendar.MINUTE, 10);
			return calendar.getTime();
		}
		return messageDate;
	}

	public void updateMessageProgramState(Integer personId, String conceptName) {

		// Only determine message program state for active enrolled programs
		// concerned with an observed concept and matching the concept of this
		// obs

		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> patientActiveEnrollments = motechService
				.getActiveMessageProgramEnrollments(personId, null, null, null,
						null, null);

		Date currentDate = new Date();

		for (MessageProgramEnrollment enrollment : patientActiveEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			if (program.getConceptName() != null) {
				if (program.getConceptName().equals(conceptName)) {
					log
							.debug("Save Obs - Obs matches Program concept, update Program: "
									+ enrollment.getProgram());

					program.determineState(enrollment, currentDate);
				}
			}
		}
	}

	/* MessageProgramUpdateTask method */
	public TaskDefinition updateAllMessageProgramsState(Integer batchSize,
			Long batchPreviousId, Long batchMaxId) {

		MotechService motechService = contextService.getMotechService();
		SchedulerService schedulerService = contextService
				.getSchedulerService();

		if (batchMaxId == null) {
			batchMaxId = motechService.getMaxMessageProgramEnrollmentId();
		}

		List<MessageProgramEnrollment> activeEnrollments = motechService
				.getActiveMessageProgramEnrollments(null, null, null,
						batchPreviousId, batchMaxId, batchSize);

		Date currentDate = new Date();

		for (MessageProgramEnrollment enrollment : activeEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			log.debug("MessageProgram Update - Update State: enrollment: "
					+ enrollment.getId());

			program.determineState(enrollment, currentDate);

			batchPreviousId = enrollment.getId();
			if (batchPreviousId >= batchMaxId) {
				log.info("Completed updating all enrollments up to max: "
						+ batchMaxId);
				batchMaxId = null;
				batchPreviousId = null;
				break;
			}
		}

		// Update task properties
		TaskDefinition task = schedulerService
				.getTaskByName(MotechConstants.TASK_MESSAGEPROGRAM_UPDATE);
		if (task != null) {
			Map<String, String> properties = task.getProperties();
			if (batchPreviousId != null) {
				properties.put(MotechConstants.TASK_PROPERTY_BATCH_PREVIOUS_ID,
						batchPreviousId.toString());
			} else {
				properties
						.remove(MotechConstants.TASK_PROPERTY_BATCH_PREVIOUS_ID);
			}
			if (batchMaxId != null) {
				properties.put(MotechConstants.TASK_PROPERTY_BATCH_MAX_ID,
						batchMaxId.toString());
			} else {
				properties.remove(MotechConstants.TASK_PROPERTY_BATCH_MAX_ID);
			}
			schedulerService.saveTask(task);
		}
		return task;
	}

	public void sendDeliveryNotification(Patient patient) {
		// Send message to phone number of facility serving patient's community
		Community community = openmrsBean.getCommunityByPatient(patient);
		if (community != null && community.getFacility() != null) {
			String phoneNumber = community.getFacility().getPhoneNumber();
			if (phoneNumber != null) {

				MessageDefinition messageDef = getMessageDefinition("pregnancy.notification");
				if (messageDef == null) {
					log.error("Pregnancy delivery notification message "
							+ "does not exist");
					return;
				}

				String messageId = null;
				NameValuePair[] nameValues = new NameValuePair[0];
				MediaType mediaType = MediaType.TEXT;
				String languageCode = "en";

				// Send immediately if not during blackout,
				// otherwise adjust time to after the blackout period
				Date currentDate = new Date();
				Date messageStartDate = adjustForBlackout(currentDate);
				if (currentDate.equals(messageStartDate)) {
					messageStartDate = null;
				}

				org.motechproject.ws.Patient wsPatient = modelConverter
						.patientToWebService(patient, true);
				org.motechproject.ws.Patient[] wsPatients = new org.motechproject.ws.Patient[] { wsPatient };

				sendStaffMessage(messageId, nameValues, phoneNumber,
						languageCode, mediaType, messageDef.getPublicId(),
						messageStartDate, null, wsPatients);
			}
		}
	}

	public void sendStaffCareMessages(Date startDate, Date endDate,
			Date deliveryDate, Date deliveryTime, String[] careGroups,
			boolean sendUpcoming, boolean avoidBlackout) {

		if (avoidBlackout && isDuringBlackout(deliveryDate)) {
			log.debug("Cancelling nurse messages during blackout");
			return;
		}

		MotechService motechService = contextService.getMotechService();
		List<Facility> facilities = motechService.getAllFacilities();

		// All staff messages sent as SMS
		MediaType mediaType = MediaType.TEXT;
		// No corresponding message stored for staff care messages
		String messageId = null;
		// Set the time on the delivery date if needed
		deliveryDate = adjustTime(deliveryDate, deliveryTime);

		for (Facility facility : facilities) {
			String phoneNumber = facility.getPhoneNumber();
			Location facilityLocation = facility.getLocation();
			if (phoneNumber == null
					|| facilityLocation == null
					|| !MotechConstants.LOCATION_KASSENA_NANKANA_WEST
							.equals(facilityLocation.getCountyDistrict())) {
				// Skip facilities without a phone number or
				// not in KNDW district
				continue;
			}

			// Send Defaulted Care Message
			List<ExpectedEncounter> defaultedEncounters = expectedCareBean
					.getDefaultedExpectedEncounters(facility, careGroups,
							startDate);
			List<ExpectedObs> defaultedObs = expectedCareBean
					.getDefaultedExpectedObs(facility, careGroups, startDate);
			if (!defaultedEncounters.isEmpty() || !defaultedObs.isEmpty()) {
				Care[] defaultedCares = modelConverter
						.defaultedToWebServiceCares(defaultedEncounters,
								defaultedObs);
				sendStaffDefaultedCareMessage(messageId, phoneNumber,
						mediaType, deliveryDate, null, defaultedCares);
			}

			if (sendUpcoming) {
				// Send Upcoming Care Messages
				List<ExpectedEncounter> upcomingEncounters = expectedCareBean
						.getUpcomingExpectedEncounters(facility, careGroups,
								startDate, endDate);
				List<ExpectedObs> upcomingObs = expectedCareBean
						.getUpcomingExpectedObs(facility, careGroups,
								startDate, endDate);
				if (!upcomingEncounters.isEmpty() || !upcomingObs.isEmpty()) {
					Care[] upcomingCares = modelConverter
							.upcomingToWebServiceCares(upcomingEncounters,
									upcomingObs, true);

					sendStaffUpcomingCareMessage(messageId, phoneNumber,
							mediaType, deliveryDate, null, upcomingCares);
				}
			}
		}
	}

	/* NotificationTask methods start */
	public void sendMessages(Date startDate, Date endDate, boolean sendImmediate) {
		try {
			MotechService motechService = contextService.getMotechService();

			List<Message> shouldAttemptMessages = motechService.getMessages(
					startDate, endDate, MessageStatus.SHOULD_ATTEMPT);

			if (log.isDebugEnabled()) {
				log
						.debug("Notification Task executed, Should Attempt Messages found: "
								+ shouldAttemptMessages.size());
			}

			if (!shouldAttemptMessages.isEmpty()) {
				PatientMessage[] messages = constructPatientMessages(
						shouldAttemptMessages, sendImmediate);

				if (messages.length > 0) {
					mobileService.sendPatientMessages(messages);
				}
			}
		} catch (Exception e) {
			log.error("Failure to send patient messages", e);
		}
	}

	public PatientMessage[] constructPatientMessages(List<Message> messages,
			boolean sendImmediate) {
		MotechService motechService = contextService.getMotechService();

		List<PatientMessage> patientMessages = new ArrayList<PatientMessage>();

		for (Message message : messages) {
			PatientMessage patientMessage = constructPatientMessage(message);
			if (patientMessage != null) {
				if (sendImmediate) {
					patientMessage.setStartDate(null);
					patientMessage.setEndDate(null);
				}
				patientMessages.add(patientMessage);

				message.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
			} else {
				message.setAttemptStatus(MessageStatus.REJECTED);
			}
			motechService.saveMessage(message);
		}
		return patientMessages.toArray(new PatientMessage[patientMessages
				.size()]);
	}

	public PatientMessage constructPatientMessage(Message message) {
		try {
			PersonService personService = contextService.getPersonService();
			PatientService patientService = contextService.getPatientService();

			Long notificationType = message.getSchedule().getMessage()
					.getPublicId();
			Integer recipientId = message.getSchedule().getRecipientId();
			Person person = personService.getPerson(recipientId);

			String phoneNumber = openmrsBean.getPersonPhoneNumber(person);

			// Cancel message if phone number is considered troubled
			if (isPhoneTroubled(phoneNumber)) {
				if (log.isDebugEnabled()) {
					log.debug("Attempt to send to Troubled Phone, Phone: "
							+ phoneNumber + ", Notification: "
							+ notificationType);
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Scheduled Message, Phone: " + phoneNumber
							+ ", Notification: " + notificationType);
				}

				String messageId = message.getPublicId();
				MediaType mediaType = openmrsBean.getPersonMediaType(person);
				String languageCode = openmrsBean.getPersonLanguageCode(person);
				NameValuePair[] personalInfo = new NameValuePair[0];

				Date messageStartDate = message.getAttemptDate();
				Date messageEndDate = null;

				Patient patient = patientService.getPatient(recipientId);

				if (patient != null) {
					ContactNumberType contactNumberType = openmrsBean
							.getPersonPhoneType(person);
					String motechId = patient.getPatientIdentifier(
							MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID)
							.getIdentifier();

					PatientMessage patientMessage = new PatientMessage();
					patientMessage.setMessageId(messageId);
					patientMessage.setPersonalInfo(personalInfo);
					patientMessage.setPatientNumber(phoneNumber);
					patientMessage.setPatientNumberType(contactNumberType);
					patientMessage.setLangCode(languageCode);
					patientMessage.setMediaType(mediaType);
					patientMessage.setNotificationType(notificationType);
					patientMessage.setStartDate(messageStartDate);
					patientMessage.setEndDate(messageEndDate);
					patientMessage.setRecipientId(motechId);
					return patientMessage;

				} else {
					log
							.error("Attempt to send message to non-existent Patient: "
									+ recipientId);
				}
			}
		} catch (Exception e) {
			log.error("Error creating patient message", e);
		}
		return null;
	}

	protected boolean isPhoneTroubled(String phoneNumber) {
		TroubledPhone troubledPhone = contextService.getMotechService()
				.getTroubledPhone(phoneNumber);
		Integer maxFailures = openmrsBean.getMaxPhoneNumberFailures();
		if (maxFailures == null) {
			return false;
		}
		return troubledPhone != null
				&& troubledPhone.getSendFailures() >= maxFailures;
	}

	public boolean sendPatientMessage(String messageId,
			NameValuePair[] personalInfo, String motechId, String phoneNumber,
			String languageCode, MediaType mediaType, Long notificationType,
			Date messageStartDate, Date messageEndDate,
			ContactNumberType contactType) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendPatientMessage(messageId, personalInfo, phoneNumber,
							contactType, languageCode, mediaType,
							notificationType, messageStartDate, messageEndDate,
							motechId);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS patient message failure", e);
			return false;
		}
	}

	public boolean sendStaffMessage(String messageId,
			NameValuePair[] personalInfo, String phoneNumber,
			String languageCode, MediaType mediaType, Long notificationType,
			Date messageStartDate, Date messageEndDate,
			org.motechproject.ws.Patient[] patients) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendCHPSMessage(messageId, personalInfo, phoneNumber,
							patients, languageCode, mediaType,
							notificationType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS staff message failure", e);
			return false;
		}
	}

	public boolean sendStaffDefaultedCareMessage(String messageId,
			String phoneNumber, MediaType mediaType, Date messageStartDate,
			Date messageEndDate, Care[] cares) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendDefaulterMessage(messageId, phoneNumber, cares,
							mediaType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS staff defaulted care message failure", e);
			return false;
		}
	}

	public boolean sendStaffUpcomingCareMessage(String messageId,
			String phoneNumber, MediaType mediaType, Date messageStartDate,
			Date messageEndDate, Care[] cares) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendBulkCaresMessage(messageId, phoneNumber, cares,
							mediaType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS staff upcoming care message failure", e);
			return false;
		}
	}

	/* NotificationTask methods end */

	/* Factored out methods start */
	public String[] getActiveMessageProgramEnrollmentNames(Patient patient) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(patient.getPatientId(),
						null, null, null, null, null);

		List<String> enrollmentNames = new ArrayList<String>();
		for (MessageProgramEnrollment enrollment : enrollments) {
			enrollmentNames.add(enrollment.getProgram());
		}
		return enrollmentNames.toArray(new String[enrollmentNames.size()]);
	}

	public void addMessageProgramEnrollment(Integer personId, String program,
			Integer obsId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, program, obsId,
						null, null, null);
		if (enrollments.size() == 0) {
			MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
			enrollment.setPersonId(personId);
			enrollment.setProgram(program);
			enrollment.setStartDate(new Date());
			enrollment.setObsId(obsId);
			motechService.saveMessageProgramEnrollment(enrollment);
		}
	}

	public void removeMessageProgramEnrollment(
			MessageProgramEnrollment enrollment) {

		MotechService motechService = contextService.getMotechService();
		removeAllUnsentMessages(enrollment);
		if (enrollment.getEndDate() == null) {
			enrollment.setEndDate(new Date());
			motechService.saveMessageProgramEnrollment(enrollment);
		}
	}

	public void removeMessageProgramEnrollment(Integer personId,
			String program, Integer obsId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, program, obsId,
						null, null, null);
		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	public void removeAllMessageProgramEnrollments(Integer personId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, null, null, null,
						null, null);

		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	public Date determinePreferredMessageDate(Person person, Date messageDate,
			Date currentDate, boolean checkInFuture) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(messageDate);

		Date time = openmrsBean.getPersonMessageTimeOfDay(person);
		if (time == null) {
			time = openmrsBean.getDefaultPatientTimeOfDay();
		}
		if (time != null) {
			Calendar timeCalendar = Calendar.getInstance();
			timeCalendar.setTime(time);
			calendar.set(Calendar.HOUR_OF_DAY, timeCalendar
					.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		}
		calendar.set(Calendar.SECOND, 0);

		DayOfWeek day = openmrsBean.getPersonMessageDayOfWeek(person);
		if (day == null) {
			day = openmrsBean.getDefaultPatientDayOfWeek();
		}
		if (day != null) {
			calendar.set(Calendar.DAY_OF_WEEK, day.getCalendarValue());
			if (checkInFuture && calendar.getTime().before(currentDate)) {
				// Add a week if date in past after setting the day of week
				calendar.add(Calendar.DATE, 7);
			}
		}

		return calendar.getTime();
	}

	Date adjustTime(Date date, Date time) {
		if (date == null || time == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, 0);
		if (calendar.getTime().before(date)) {
			// Add a day if before original date
			// after setting the time of day
			calendar.add(Calendar.DATE, 1);
		}
		return calendar.getTime();
	}

	Date adjustForBlackout(Date date) {
		if (date == null) {
			return date;
		}
		MotechService motechService = contextService.getMotechService();
		Blackout blackout = motechService.getBlackoutSettings();
		if (blackout == null) {
			return date;
		}

		Calendar blackoutCalendar = Calendar.getInstance();
		blackoutCalendar.setTime(date);

		Calendar timeCalendar = Calendar.getInstance();

		timeCalendar.setTime(blackout.getStartTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		if (date.before(blackoutCalendar.getTime())) {
			// Remove a day if blackout start date before the message date
			blackoutCalendar.add(Calendar.DATE, -1);
		}
		Date blackoutStart = blackoutCalendar.getTime();

		timeCalendar.setTime(blackout.getEndTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		if (blackoutCalendar.getTime().before(blackoutStart)) {
			// Add a day if blackout end date before start date
			// after setting time
			blackoutCalendar.add(Calendar.DATE, 1);
		}
		Date blackoutEnd = blackoutCalendar.getTime();

		if (date.after(blackoutStart) && date.before(blackoutEnd)) {
			return blackoutEnd;
		}
		return date;
	}

	boolean isDuringBlackout(Date date) {
		if (date == null) {
			// If date is missing, checks if current date is during blackout
			date = new Date();
		}
		MotechService motechService = contextService.getMotechService();
		Blackout blackout = motechService.getBlackoutSettings();
		if (blackout == null) {
			return false;
		}

		Calendar blackoutCalendar = Calendar.getInstance();
		blackoutCalendar.setTime(date);

		Calendar timeCalendar = Calendar.getInstance();

		timeCalendar.setTime(blackout.getStartTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		if (date.before(blackoutCalendar.getTime())) {
			// Remove a day if blackout start date before the message date
			blackoutCalendar.add(Calendar.DATE, -1);
		}
		Date blackoutStart = blackoutCalendar.getTime();

		timeCalendar.setTime(blackout.getEndTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		if (blackoutCalendar.getTime().before(blackoutStart)) {
			// Add a day if blackout end date before start date
			// after setting time
			blackoutCalendar.add(Calendar.DATE, 1);
		}
		Date blackoutEnd = blackoutCalendar.getTime();

		if (date.after(blackoutStart) && date.before(blackoutEnd)) {
			return true;
		}
		return false;
	}

}
