package org.motechproject.server.model.db;

import java.util.Date;
import java.util.List;

import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.GeneralPatientEncounter;
import org.motechproject.server.model.Log;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageAttribute;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.Service;
import org.motechproject.server.model.ServiceStatus;
import org.motechproject.server.model.TroubledPhone;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;

/**
 * The interface definition for the motech server's data access requirements.
 */
public interface MotechDAO {

	Log saveLog(Log log);

	List<Log> getLogs();

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

	List<MessageProgramEnrollment> getAllActiveMessageProgramEnrollments();

	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId);

	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program);

	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program, Integer obsId);

	GeneralPatientEncounter saveGeneralPatientEncounter(
			GeneralPatientEncounter encounter);

	List<Location> getAllCountries();

	List<Location> getAllRegions();

	List<Location> getRegions(String country);

	List<Location> getAllDistricts();

	List<Location> getDistricts(String country, String region);

	List<Location> getAllCommunities();

	List<Location> getCommunities(String country, String region, String district);

	List<Location> getAllClinics();

	List<Location> getClinics(String country, String region, String district,
			String community);

	List<Integer> getMatchingPeople(String firstName, String lastName,
			Date birthDate, String community, String phoneNumber,
			Integer primaryPhoneNumberAttrTypeId,
			Integer secondaryPhoneNumberAttrTypeId, String patientId,
			String nhisNumber, Integer nhisAttrTypeId);

	List<Obs> getActivePregnancies(Integer patientId, Concept pregnancyConcept,
			Concept pregnancyStatusConcept);

	List<Obs> getActivePregnanciesDueDateObs(Date fromDueDate, Date toDueDate,
			Concept pregnancyDueDateConcept, Concept pregnancyConcept,
			Concept pregnancyStatusConcept);

	Service saveService(Service service);

	List<Service> getServices(Integer patientId, String sequence,
			ServiceStatus status);

	List<Service> getServices(Integer patientId, String service,
			String sequence, ServiceStatus status);

	ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

	void removeExpectedObs(ExpectedObs expectedObs);

	List<ExpectedObs> getExpectedObs(Person person, Concept concept,
			Concept valueCoded, Double valueNumeric, Date obsDatetime);

	ExpectedEncounter saveExpectedEncounter(ExpectedEncounter expectedEncounter);

	void removeExpectedEncounter(ExpectedEncounter expectedEncounter);

	List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			EncounterType encounterType, Date encounterDatetime);

	List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, String community,
			String phoneNumber, PersonAttributeType primaryPhoneNumberAttrType,
			PersonAttributeType secondaryPhoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType);
}
