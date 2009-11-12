package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.motech.messaging.ScheduledMessage;
import org.motech.model.Log;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * The major internal service interface for the motech server project at this
 * point, the RegistrarBean interface includes all major operations necessary to
 * support the current project requirements. It is intended that these
 * operations are transactional, and we fully intend to refactor this interface
 * into multiple (more appropriate) service interfaces if/when it becomes
 * necessary. The name is a bit of a misnomer, as it is a vestige of the early
 * prototypes that handled only registration.
 */
public interface RegistrarBean {

	public void registerClinic(String name);

	public void registerNurse(String name, String phoneNumber, String clinic);

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime, String[] regimen);

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks);

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Double hemoglobin);

	public void log(LogType type, String message);

	public void setMessageStatus(String messageId, Boolean success);

	public List<String> getActiveRegimenEnrollment(Integer personId);

	public User getUserByPhoneNumber(String phoneNumber);

	public List<Location> getAllClinics();

	public List<User> getAllNurses();

	public List<Patient> getAllPatients();

	public List<Encounter> getAllPregnancyVisits();

	public List<Encounter> getAllMaternalVisits();

	public List<ScheduledMessage> getAllScheduledMessages();

	public List<Log> getAllLogs();

	public int getNumberOfObs(Patient patient, String conceptName,
			String conceptValue);

	public Date getLastObsDate(Patient patient, String conceptName,
			String conceptValue);

	public Date getLastObsValue(Patient patient, String conceptName);

	public void removeRegimenEnrollment(Integer personId, String regimenName);

	public void scheduleMessage(String messageKey, String messageGroup,
			Integer messageRecipientId, Date messageDate,
			boolean userPreferenceBased);

	public void removeAllUnsentMessages(Integer recipientId, String messageGroup);

	public void addInitialData();

	public void removeAllTasks();

	public void updateRegimenState(Obs obs);

	public void sendMessages(Date startDate, Date endDate, boolean sendImmediate);

	public void updateAllRegimenState();
}
