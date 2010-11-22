package org.motechproject.ws.server;

import java.util.Date;

import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;

/**
 * An interface for the motech server patient services.
 */
public interface RegistrarService {

	public void recordPatientHistory(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer lastIPT, Date lastIPTDate,
			Integer lastTT, Date lastTTDate, Date bcgDate, Integer lastOPV,
			Date lastOPVDate, Integer lastPenta, Date lastPentaDate,
			Date measlesDate, Date yellowFeverDate, Integer lastIPTI,
			Date lastIPTIDate, Date lastVitaminADate)
			throws ValidationException;

	public void recordMotherANCVisit(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer visitNumber, Integer location,
			String house, String community, Date estDeliveryDate,
			Integer bpSystolic, Integer bpDiastolic, Double weight,
			Integer ttDose, Integer iptDose, Boolean iptReactive,
			Boolean itnUse, Double fht, Integer fhr, Integer urineTestProtein,
			Integer urineTestGlucose, Double hemoglobin, Boolean vdrlReactive,
			Boolean vdrlTreatment, Boolean dewormer, Boolean maleInvolved,
			Boolean pmtct, Boolean preTestCounseled, HIVResult hivTestResult,
			Boolean postTestCounseled, Boolean pmtctTreatment,
			Boolean referred, Date nextANCDate, String comments)
			throws ValidationException;

	public void recordPregnancyTermination(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer terminationType,
			Integer procedure, Integer[] complications, Boolean maternalDeath,
			Boolean referred, Boolean postAbortionFPCounseled,
			Boolean postAbortionFPAccepted, String comments)
			throws ValidationException;

	public Patient[] recordPregnancyDelivery(Integer staffId,
			Integer facilityId, Date datetime, Integer motechId, Integer mode,
			Integer outcome, Integer deliveryLocation, Integer deliveredBy,
			Boolean maleInvolved, Integer[] complications, Integer vvf,
			Boolean maternalDeath, String comments, BirthOutcome child1Outcome,
			RegistrationMode child1RegistrationType, Integer child1MotechId,
			Gender child1Sex, String child1FirstName, Double child1Weight,
			BirthOutcome child2Outcome,
			RegistrationMode child2RegistrationType, Integer child2MotechId,
			Gender child2Sex, String child2FirstName, Double child2Weight,
			BirthOutcome child3Outcome,
			RegistrationMode child3RegistrationType, Integer child3MotechId,
			Gender child3Sex, String child3FirstName, Double child3Weight)
			throws ValidationException;

	public void recordDeliveryNotification(Integer staffId, Integer facilityId,
			Date datetime, Integer motechId) throws ValidationException;

	public void recordMotherPNCVisit(Integer staffId, Integer facilityId,
			Date datetime, Integer motechId, Integer visitNumber,
			Integer location, String house, String community, Boolean referred,
			Boolean maleInvolved, Boolean vitaminA, Integer ttDose,
			Integer lochiaColour, Boolean lochiaAmountExcess,
			Boolean lochiaOdourFoul, Double temperature, Double fht,
			String comments) throws ValidationException;

	public void recordDeath(Integer staffId, Integer facilityId, Date date,
			Integer motechId) throws ValidationException;

	public void recordTTVisit(Integer staffId, Integer facilityId, Date date,
			Integer motechId, Integer ttDose) throws ValidationException;

	public void recordChildPNCVisit(Integer staffId, Integer facilityId,
			Date datetime, Integer motechId, Integer visitNumber,
			Integer location, String house, String community, Boolean referred,
			Boolean maleInvolved, Double weight, Double temperature,
			Boolean bcg, Boolean opv0, Integer respiration,
			Boolean cordConditionNormal, Boolean babyConditionGood,
			String comments) throws ValidationException;

	public void recordChildCWCVisit(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer cwcLocation, String house,
			String community, Boolean bcg, Integer opvDose, Integer pentaDose,
			Boolean measles, Boolean yellowFever, Boolean csm,
			Integer iptiDose, Boolean vitaminA, Boolean dewormer,
			Double weight, Double muac, Double height, Boolean maleInvolved,
			String comments) throws ValidationException;

	public Patient registerPatient(Integer staffId, Integer facilityId,
			Date date, RegistrationMode registrationMode, Integer motechId,
			RegistrantType registrantType, String firstName, String middleName,
			String lastName, String preferredName, Date dateOfBirth,
			Boolean estimatedBirthDate, Gender sex, Boolean insured,
			String nhis, Date nhisExpires, Integer motherMotechId,
			Integer community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean deliveryDateConfirmed,
			Boolean enroll, Boolean consent, ContactNumberType ownership,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, InterestReason reason, HowLearned howLearned,
			Integer messagesStartWeek) throws ValidationException;

	public void registerPregnancy(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Date estDeliveryDate, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) throws ValidationException;

	public void registerANCMother(Integer staffId, Integer facilityId,
			Date date, Integer motechId, String ancRegNumber,
			Date estDeliveryDate, Double height, Integer gravida,
			Integer parity, Boolean enroll, Boolean consent,
			ContactNumberType ownership, String phoneNumber, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			HowLearned howLearned) throws ValidationException;

	public void registerCWCChild(Integer staffId, Integer facilityId,
			Date date, Integer motechId, String cwcRegNumber, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) throws ValidationException;

	public void editPatient(Integer staffId, Integer facilityId, Date date,
			Integer motechId, String phoneNumber,
			ContactNumberType phoneOwnership, String nhis, Date nhisExpires,
			Boolean stopEnrollment) throws ValidationException;

	public void recordGeneralVisit(Integer staffId, Integer facilityId,
			Date date, String serialNumber, Gender sex, Date dateOfBirth,
			Boolean insured, Integer diagnosis, Integer secondDiagnosis,
			Boolean rdtGiven, Boolean rdtPositive, Boolean actTreated,
			Boolean newCase, Boolean referred, String comments)
			throws ValidationException;

	public void recordChildVisit(Integer staffId, Integer facilityId,
			Date date, String serialNumber, Integer motechId, Boolean insured,
			Integer diagnosis, Integer secondDiagnosis, Boolean rdtGiven,
			Boolean rdtPositive, Boolean actTreated, Boolean newCase,
			Boolean referred, String comments) throws ValidationException;

	public void recordMotherVisit(Integer staffId, Integer facilityId,
			Date date, String serialNumber, Integer motechId, Boolean insured,
			Integer diagnosis, Integer secondDiagnosis, Boolean rdtGiven,
			Boolean rdtPositive, Boolean actTreated, Boolean newCase,
			Boolean referred, String comments) throws ValidationException;

	public Care[] queryANCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Care[] queryTTDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Care[] queryMotherPNCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Care[] queryChildPNCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Care[] queryCWCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Patient[] queryUpcomingDeliveries(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Patient[] queryRecentDeliveries(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Patient[] queryOverdueDeliveries(Integer staffId, Integer facilityId)
			throws ValidationException;

	public Patient queryUpcomingCare(Integer staffId, Integer facilityId,
			Integer motechId) throws ValidationException;

	public Patient[] queryMotechId(Integer staffId, Integer facilityId,
			String firstName, String lastName, String preferredName,
			Date birthDate, String nhis, String phoneNumber)
			throws ValidationException;

	public Patient queryPatient(Integer staffId, Integer facilityId,
			Integer motechId) throws ValidationException;

	public String[] getPatientEnrollments(Integer motechId)
			throws ValidationException;

	public void log(LogType type, String message);

	public void setMessageStatus(String messageId, Boolean success);
}
