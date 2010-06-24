package org.motechproject.server.omod.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;

public class WebModelConverterImpl implements WebModelConverter {

	private final Log log = LogFactory.getLog(WebModelConverterImpl.class);

	RegistrarBean registrarBean;

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public void patientToWeb(Patient patient, WebPatient webPatient) {

		PatientIdentifier patientId = patient
				.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
		if (patientId != null) {
			Integer motechId = null;
			try {
				motechId = Integer.parseInt(patientId.getIdentifier());
			} catch (Exception e) {
				log.error("Unable to parse Motech ID: "
						+ patientId.getIdentifier() + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setMotechId(motechId);
		}

		webPatient.setId(patient.getPatientId());
		for (PersonName name : patient.getNames()) {
			if (name.isPreferred()) {
				webPatient.setPrefName(name.getGivenName());
			} else {
				webPatient.setFirstName(name.getGivenName());
			}
		}
		webPatient.setMiddleName(patient.getMiddleName());
		webPatient.setLastName(patient.getFamilyName());
		webPatient.setBirthDate(patient.getBirthdate());
		webPatient.setBirthDateEst(patient.getBirthdateEstimated());
		webPatient.setSex(GenderTypeConverter.valueOfOpenMRS(patient
				.getGender()));

		PersonAddress patientAddress = patient.getPersonAddress();
		if (patientAddress != null) {
			webPatient.setAddress(patientAddress.getAddress1());
		}

		Community community = registrarBean.getCommunityByPatient(patient);
		if (community != null) {
			webPatient.setCommunityId(community.getCommunityId());
			webPatient.setCommunityName(community.getName());

			if (community.getFacility() != null
					&& community.getFacility().getLocation() != null) {
				Location facilityLocation = community.getFacility()
						.getLocation();
				webPatient.setRegion(facilityLocation.getRegion());
				webPatient.setDistrict(facilityLocation.getCountyDistrict());
			}
		}

		String[] enrollments = registrarBean
				.getActiveMessageProgramEnrollmentNames(patient);
		if (enrollments != null && enrollments.length > 0) {
			webPatient.setEnroll(true);
			webPatient.setConsent(true);
		} else {
			webPatient.setEnroll(false);
			webPatient.setConsent(false);
		}

		PersonAttribute phoneNumberAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
		if (phoneNumberAttr != null) {
			webPatient.setPhoneNumber(phoneNumberAttr.getValue());
		}

		PersonAttribute phoneTypeAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
		if (phoneTypeAttr != null) {
			ContactNumberType phoneType = null;
			try {
				phoneType = ContactNumberType.valueOf(phoneTypeAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse phone type: "
						+ phoneTypeAttr.getValue() + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setPhoneType(phoneType);
		}

		PersonAttribute mediaTypeAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		if (mediaTypeAttr != null) {
			MediaType mediaType = null;
			try {
				mediaType = MediaType.valueOf(mediaTypeAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse media type: "
						+ mediaTypeAttr.getValue() + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setMediaType(mediaType);
		}

		PersonAttribute languageAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);
		if (languageAttr != null) {
			webPatient.setLanguage(languageAttr.getValue());
		}

		PersonAttribute dayOfWeekAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);
		if (dayOfWeekAttr != null) {
			DayOfWeek dayOfWeek = null;
			try {
				dayOfWeek = DayOfWeek.valueOf(dayOfWeekAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse day of week: "
						+ dayOfWeekAttr.getValue() + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setDayOfWeek(dayOfWeek);
		}

		PersonAttribute timeOfDayAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
		if (timeOfDayAttr != null) {
			Date timeOfDay = null;
			String timeOfDayString = timeOfDayAttr.getValue();
			try {
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						MotechConstants.TIME_FORMAT_DELIVERY_TIME);
				timeOfDay = timeFormat.parse(timeOfDayString);
			} catch (ParseException e) {
				log.error("Cannot parse time of day Date: " + timeOfDayString
						+ ", for Patient ID:" + patient.getPatientId(), e);
			}
			webPatient.setTimeOfDay(timeOfDay);
		}

		PersonAttribute interestReasonAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON);
		if (interestReasonAttr != null) {
			InterestReason interestReason = null;
			try {
				interestReason = InterestReason.valueOf(interestReasonAttr
						.getValue());
			} catch (Exception e) {
				log.error("Unable to parse interest reason: "
						+ interestReasonAttr.getValue() + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setInterestReason(interestReason);
		}

		PersonAttribute howLearnedAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);
		if (howLearnedAttr != null) {
			HowLearned howLearned = null;
			try {
				howLearned = HowLearned.valueOf(howLearnedAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse how learned: "
						+ howLearnedAttr.getValue() + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setHowLearned(howLearned);
		}

		PersonAttribute nhisExpDateAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);
		if (nhisExpDateAttr != null) {
			Date nhisExpDate = null;
			String nhisExpDateString = nhisExpDateAttr.getValue();
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						MotechConstants.DATE_FORMAT);
				nhisExpDate = dateFormat.parse(nhisExpDateString);
			} catch (ParseException e) {
				log.error("Cannot parse NHIS Expiration Date: "
						+ nhisExpDateString + ", for Patient ID:"
						+ patient.getPatientId(), e);
			}
			webPatient.setNhisExpDate(nhisExpDate);
		}

		PersonAttribute insuredAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_INSURED);
		if (insuredAttr != null) {
			webPatient.setInsured(Boolean.valueOf(insuredAttr.getValue()));
		}

		PersonAttribute nhisAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);
		if (nhisAttr != null) {
			webPatient.setNhis(nhisAttr.getValue());
		}

		webPatient.setDueDate(registrarBean.getActivePregnancyDueDate(patient
				.getPatientId()));
	}

}
