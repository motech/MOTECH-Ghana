package org.motechproject.server.model.db;

import java.util.Date;
import java.util.List;

import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.GeneralOutpatientEncounter;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageAttribute;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;

/**
 * The interface definition for the motech server's data access requirements.
 */
public interface MotechDAO {

	List<Integer> getUsersByPersonAttribute(Integer personAttributeTypeId,
			String personAttributeValue);

	ScheduledMessage saveScheduledMessage(ScheduledMessage scheduledMessage);

	Message saveMessage(Message Message);

	MessageDefinition saveMessageDefinition(MessageDefinition messageDefinition);

	MessageAttribute saveMessageAttribute(MessageAttribute messageAttribute);

	List<ScheduledMessage> getScheduledMessages();

	List<ScheduledMessage> getScheduledMessages(Date startDate, Date endDate);

	public List<ScheduledMessage> getScheduledMessages(Integer recipientId,
			MessageDefinition definition, MessageProgramEnrollment enrollment,
			Date messageDate);

	List<Message> getMessages();

	List<Message> getMessages(Date startDate, Date endDate, MessageStatus status);

	List<Message> getMessages(MessageProgramEnrollment enrollment,
			MessageStatus status);

	List<Message> getMessages(Integer recipientId,
			MessageProgramEnrollment enrollment, MessageDefinition definition,
			Date messageDate, MessageStatus status);

	Message getMessage(String publicId);

	List<Message> getMessages(ScheduledMessage scheduledMessage);

	List<MessageDefinition> getMessageDefinitions();

	MessageDefinition getMessageDefinition(String messageKey);

	Blackout getBlackoutSettings();

	void setBlackoutSettings(Blackout blackout);

	List<MessageAttribute> getMessageAttributes();

	TroubledPhone getTroubledPhone(Long id);

	TroubledPhone getTroubledPhoneByNumber(String phoneNumber);

	void removeTroubledPhone(Long id);

	void saveTroubledPhone(TroubledPhone phone);

	MessageProgramEnrollment saveMessageProgramEnrollment(
			MessageProgramEnrollment enrollment);

	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program, Integer obsId);

	GeneralOutpatientEncounter saveGeneralOutpatientEncounter(
			GeneralOutpatientEncounter encounter);

	List<Location> getAllCountryLocations();

	List<Location> getAllRegionLocations();

	List<Location> getRegionLocationsForCountry(String country);

	List<Location> getAllDistrictLocations();

	List<Location> getDistrictLocations(String country, String region);

	List<Location> getAllCommunityLocations();

	List<Location> getCommunityLocations(String country, String region,
			String district);

	List<Location> getAllFacilityLocations();

	List<Location> getFacilityLocations(String country, String region,
			String district, String community);

	List<Obs> getActivePregnancies(Integer patientId, Concept pregnancyConcept,
			Concept pregnancyStatusConcept);

	List<Obs> getActivePregnanciesDueDateObs(Date fromDueDate, Date toDueDate,
			Concept pregnancyDueDateConcept, Concept pregnancyConcept,
			Concept pregnancyStatusConcept);

	ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

	List<ExpectedObs> getExpectedObs(Patient patient, String[] groups,
			Date minDueDate, Date maxDueDate, Date maxLateDate,
			Date minMaxDate, boolean nameOrdering);

	ExpectedEncounter saveExpectedEncounter(ExpectedEncounter expectedEncounter);

	List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			String[] groups, Date minDueDate, Date maxDueDate,
			Date maxLateDate, Date minMaxDate, boolean nameOrdering);

	List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, String community,
			String phoneNumber, PersonAttributeType phoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType,
			String patientId, PatientIdentifierType patientIdType);

	Facility getFacilityByFacilityId(Integer facilityId);

	List<Facility> getFacilityByLocation(Location location);

	List<Facility> getAllFacilities();

	Community getCommunityByCommunityId(Integer communityId);

	List<Community> getAllCommunities();
}
