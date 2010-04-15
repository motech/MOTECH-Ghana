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
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;

public class WebModelConverterImpl implements WebModelConverter {

	private final Log log = LogFactory.getLog(WebModelConverterImpl.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEE MMM d HH:mm:ss z yyyy");

	public void patientToWeb(Patient patient, WebPatient webPatient) {

		PatientIdentifier patientId = patient
				.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
		if (patientId != null) {
			webPatient.setMotechId(patientId.getIdentifier());
		}

		webPatient.setId(patient.getPersonId());
		for (PersonName name : patient.getNames()) {
			if (!name.isPreferred() && name.getGivenName() != null) {
				webPatient.setFirstName(name.getGivenName());
				break;
			}
		}
		webPatient.setLastName(patient.getFamilyName());
		webPatient.setPrefName(patient.getGivenName());
		webPatient.setBirthDate(patient.getBirthdate());
		webPatient.setBirthDateEst(patient.getBirthdateEstimated());
		webPatient.setSex(GenderTypeConverter.valueOfOpenMRS(patient
				.getGender()));

		PersonAddress patientAddress = patient.getPersonAddress();
		if (patientAddress != null) {
			webPatient.setRegion(patientAddress.getRegion());
			webPatient.setDistrict(patientAddress.getCountyDistrict());
			webPatient.setCommunity(patientAddress.getCityVillage());
			webPatient.setAddress(patientAddress.getAddress1());
		}

		// TODO: populate registerPregProgram

		PersonAttribute primaryPhoneAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER);
		if (primaryPhoneAttr != null) {
			webPatient.setPrimaryPhone(primaryPhoneAttr.getValue());
		}

		PersonAttribute primaryPhoneTypeAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE);
		if (primaryPhoneTypeAttr != null) {
			webPatient.setPrimaryPhoneType(ContactNumberType
					.valueOf(primaryPhoneTypeAttr.getValue()));
		}

		PersonAttribute secondaryPhoneAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER);
		if (secondaryPhoneAttr != null) {
			webPatient.setSecondaryPhone(secondaryPhoneAttr.getValue());
		}

		PersonAttribute secondaryPhoneTypeAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE);
		if (secondaryPhoneTypeAttr != null) {
			webPatient.setSecondaryPhoneType(ContactNumberType
					.valueOf(secondaryPhoneTypeAttr.getValue()));
		}

		PersonAttribute mediaTypeInfoAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL);
		if (mediaTypeInfoAttr != null) {
			webPatient.setMediaTypeInfo(MediaType.valueOf(mediaTypeInfoAttr
					.getValue()));
		}

		PersonAttribute mediaTypeReminderAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER);
		if (mediaTypeReminderAttr != null) {
			webPatient.setMediaTypeReminder(MediaType
					.valueOf(mediaTypeReminderAttr.getValue()));
		}

		PersonAttribute languageVoiceAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE);
		if (languageVoiceAttr != null) {
			webPatient.setLanguageVoice(languageVoiceAttr.getValue());
		}

		PersonAttribute languageTextAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT);
		if (languageTextAttr != null) {
			webPatient.setLanguageText(languageTextAttr.getValue());
		}

		PersonAttribute whoRegisteredAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED);
		if (whoRegisteredAttr != null) {
			webPatient.setWhoRegistered(WhoRegistered.valueOf(whoRegisteredAttr
					.getValue()));
		}

		PersonAttribute religionAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_RELIGION);
		if (religionAttr != null) {
			webPatient.setReligion(religionAttr.getValue());
		}

		PersonAttribute occupationAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION);
		if (occupationAttr != null) {
			webPatient.setOccupation(occupationAttr.getValue());
		}

		PersonAttribute whyInterestedAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED);
		if (whyInterestedAttr != null) {
			webPatient.setWhyInterested(WhyInterested.valueOf(whyInterestedAttr
					.getValue()));
		}

		PersonAttribute howLearnedAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);
		if (howLearnedAttr != null) {
			webPatient.setHowLearned(howLearnedAttr.getValue());
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

		PersonAttribute regANCNumberGHSAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_GHS_ANC_REG_NUMBER);
		PersonAttribute regCWCNumberGHSAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER);
		if (regANCNumberGHSAttr != null) {
			webPatient.setRegNumberGHS(regANCNumberGHSAttr.getValue());
		} else if (regCWCNumberGHSAttr != null) {
			webPatient.setRegNumberGHS(regCWCNumberGHSAttr.getValue());
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

}
