package org.motechproject.server.omod.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.HIVStatus;
import org.motechproject.server.model.WhoRegistered;
import org.motechproject.server.model.WhyInterested;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;

public class WebModelConverterImpl implements WebModelConverter {

	private final Log log = LogFactory.getLog(WebModelConverterImpl.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEE MMM d HH:mm:ss z yyyy");

	public void patientToWeb(Patient patient, WebPatient webPatient) {

		personToWeb(patient, webPatient);

		PatientIdentifier patientId = patient
				.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
		if (patientId != null) {
			webPatient.setRegNumberGHS(patientId.getIdentifier());
		}

		PersonAttribute nhisExpDateAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);
		if (nhisExpDateAttr != null) {
			Date nhisExpDate = null;
			String nhisExpDateString = nhisExpDateAttr.getValue();
			try {
				nhisExpDate = dateFormat.parse(nhisExpDateString);
			} catch (ParseException e) {
				log.error("Cannot parse NHIS Expiration Date: "
						+ nhisExpDateString, e);
			}
			webPatient.setNhisExpDate(nhisExpDate);
		}

		PersonAttribute registeredGHSAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED);
		if (registeredGHSAttr != null) {
			webPatient.setRegisteredGHS(Boolean.valueOf(registeredGHSAttr
					.getValue()));
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

		PersonAttribute clinicAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER);
		if (clinicAttr != null) {
			webPatient.setClinic(Integer.valueOf(clinicAttr.getValue()));
		}

		// TODO: populate dueDate
		// TODO: populate dueDateConfirmed
		// TODO: populate gravida
		// TODO: populate parity

		PersonAttribute hivAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS);
		if (hivAttr != null) {
			webPatient.setHivStatus(HIVStatus.valueOf(hivAttr.getValue()));
		}
	}

	public void personToWeb(Person person, WebPatient webPatient) {

		webPatient.setId(person.getPersonId());
		for (PersonName name : person.getNames()) {
			if (!name.isPreferred() && name.getGivenName() != null) {
				webPatient.setFirstName(name.getGivenName());
				break;
			}
		}
		webPatient.setLastName(person.getFamilyName());
		webPatient.setPrefName(person.getGivenName());
		webPatient.setBirthDate(person.getBirthdate());
		webPatient.setBirthDateEst(person.getBirthdateEstimated());
		webPatient.setSex(GenderTypeConverter
				.valueOfOpenMRS(person.getGender()));

		PersonAddress patientAddress = person.getPersonAddress();
		if (patientAddress != null) {
			webPatient.setRegion(patientAddress.getRegion());
			webPatient.setDistrict(patientAddress.getCountyDistrict());
			webPatient.setCommunity(patientAddress.getCityVillage());
			webPatient.setAddress(patientAddress.getAddress1());
		}

		// TODO: populate registerPregProgram

		PersonAttribute primaryPhoneAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER);
		if (primaryPhoneAttr != null) {
			webPatient.setPrimaryPhone(primaryPhoneAttr.getValue());
		}

		PersonAttribute primaryPhoneTypeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE);
		if (primaryPhoneTypeAttr != null) {
			webPatient.setPrimaryPhoneType(ContactNumberType
					.valueOf(primaryPhoneTypeAttr.getValue()));
		}

		PersonAttribute secondaryPhoneAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER);
		if (secondaryPhoneAttr != null) {
			webPatient.setSecondaryPhone(secondaryPhoneAttr.getValue());
		}

		PersonAttribute secondaryPhoneTypeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE);
		if (secondaryPhoneTypeAttr != null) {
			webPatient.setSecondaryPhoneType(ContactNumberType
					.valueOf(secondaryPhoneTypeAttr.getValue()));
		}

		PersonAttribute mediaTypeInfoAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL);
		if (mediaTypeInfoAttr != null) {
			webPatient.setMediaTypeInfo(MediaType.valueOf(mediaTypeInfoAttr
					.getValue()));
		}

		PersonAttribute mediaTypeReminderAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER);
		if (mediaTypeReminderAttr != null) {
			webPatient.setMediaTypeReminder(MediaType
					.valueOf(mediaTypeReminderAttr.getValue()));
		}

		PersonAttribute languageVoiceAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE);
		if (languageVoiceAttr != null) {
			webPatient.setLanguageVoice(languageVoiceAttr.getValue());
		}

		PersonAttribute languageTextAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT);
		if (languageTextAttr != null) {
			webPatient.setLanguageText(languageTextAttr.getValue());
		}

		PersonAttribute whoRegisteredAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED);
		if (whoRegisteredAttr != null) {
			webPatient.setWhoRegistered(WhoRegistered.valueOf(whoRegisteredAttr
					.getValue()));
		}

		PersonAttribute religionAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_RELIGION);
		if (religionAttr != null) {
			webPatient.setReligion(religionAttr.getValue());
		}

		PersonAttribute occupationAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION);
		if (occupationAttr != null) {
			webPatient.setOccupation(occupationAttr.getValue());
		}

		PersonAttribute whyInterestedAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED);
		if (whyInterestedAttr != null) {
			webPatient.setWhyInterested(WhyInterested.valueOf(whyInterestedAttr
					.getValue()));
		}

		PersonAttribute howLearnedAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);
		if (howLearnedAttr != null) {
			webPatient.setHowLearned(howLearnedAttr.getValue());
		}
	}

}
