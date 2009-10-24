package org.motech.messaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.motech.event.impl.PatientObsServiceImpl;
import org.motech.util.MotechConstants;
import org.motechproject.ws.NameValuePair;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

public class MessageDefinition {

	Long id;
	String messageKey;
	Long publicId;
	List<MessageAttribute> messageAttributes = new ArrayList<MessageAttribute>();

	public MessageDefinition() {
	}

	public MessageDefinition(String messageKey, Long publicId) {
		setMessageKey(messageKey);
		setPublicId(publicId);
	}

	public Message createMessage(ScheduledMessage schedMessage) {
		Message message = new Message();
		message.setPublicId(UUID.randomUUID().toString());
		message.setSchedule(schedMessage);
		message.setAttemptStatus(MessageStatus.SHOULD_ATTEMPT);
		return message;
	}

	public NameValuePair[] getNameValueContent(Integer messageRecipientId) {
		List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();
		for (MessageAttribute attribute : messageAttributes) {
			NameValuePair pair = new NameValuePair();
			pair.setName(attribute.getName());
			if (attribute.getName().equals("PatientFirstName")) {
				Person person = Context.getPersonService().getPerson(
						messageRecipientId);
				pair.setValue(person.getGivenName());
			} else if (attribute.getName().equals("DueDate")) {
				Patient patient = Context.getPatientService().getPatient(
						messageRecipientId);
				PatientObsServiceImpl obsService = new PatientObsServiceImpl();
				Date dueDate = obsService.getLastObsValue(patient,
						MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT);
				pair.setValue(dueDate.toString());
			}
			nameValueList.add(pair);
		}
		return nameValueList.toArray(new NameValuePair[nameValueList.size()]);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public Long getPublicId() {
		return publicId;
	}

	public void setPublicId(Long publicId) {
		this.publicId = publicId;
	}

	public List<MessageAttribute> getMessageAttributes() {
		return messageAttributes;
	}

	public void setMessageAttributes(List<MessageAttribute> messageAttributes) {
		this.messageAttributes = messageAttributes;
	}

}
