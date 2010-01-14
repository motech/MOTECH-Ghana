package org.motech.openmrs.module.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.HIVStatus;
import org.motech.model.WhoRegistered;
import org.motech.util.GenderTypeConverter;
import org.motech.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;

public class WebPatient {

	private static Log log = LogFactory.getLog(WebPatient.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEE MMM d HH:mm:ss z yyyy");

	private Integer id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String prefName;
	private Date birthDate;
	private Boolean birthDateEst;
	private Gender sex;
	private Boolean registeredGHS;
	private String regNumberGHS;
	private Boolean insured;
	private String nhis;
	private Date nhisExpDate;
	private String region;
	private String district;
	private String community;
	private String address;
	private Integer clinic;
	private Date dueDate;
	private Boolean dueDateConfirmed;
	private Integer gravida;
	private Integer parity;
	private HIVStatus hivStatus;
	private Boolean registerPregProgram;
	private Boolean termsConsent;
	private String primaryPhone;
	private ContactNumberType primaryPhoneType;
	private String secondaryPhone;
	private ContactNumberType secondaryPhoneType;
	private MediaType mediaTypeInfo;
	private MediaType mediaTypeReminder;
	private String languageVoice;
	private String languageText;
	private WhoRegistered whoRegistered;
	private String religion;
	private String occupation;

	public WebPatient() {
	}

	public WebPatient(Patient patient) {
		setId(patient.getPatientId());
		setFirstName(patient.getGivenName());
		setLastName(patient.getFamilyName());
		setPrefName(patient.getMiddleName());
		setBirthDate(patient.getBirthdate());
		setBirthDateEst(patient.getBirthdateEstimated());
		setSex(GenderTypeConverter.valueOfOpenMRS(patient.getGender()));

		PersonAddress patientAddress = patient.getPersonAddress();
		if (patientAddress != null) {
			setRegion(patientAddress.getRegion());
			setDistrict(patientAddress.getCountyDistrict());
			setCommunity(patientAddress.getCityVillage());
			setAddress(patientAddress.getAddress1());
		}

		PatientIdentifier patientId = patient
				.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);
		if (patientId != null) {
			setRegNumberGHS(patientId.getIdentifier());
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
			setNhisExpDate(nhisExpDate);
		}

		PersonAttribute registeredGHSAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED);
		if (registeredGHSAttr != null) {
			setRegisteredGHS(Boolean.valueOf(registeredGHSAttr.getValue()));
		}

		PersonAttribute insuredAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_INSURED);
		if (insuredAttr != null) {
			setInsured(Boolean.valueOf(insuredAttr.getValue()));
		}

		PersonAttribute nhisAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);
		if (nhisAttr != null) {
			setNhis(nhisAttr.getValue());
		}

		PersonAttribute clinicAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER);
		if (clinicAttr != null) {
			setClinic(Integer.valueOf(clinicAttr.getValue()));
		}

		// TODO: populate dueDate
		// TODO: populate dueDateConfirmed
		// TODO: populate gravida
		// TODO: populate parity

		PersonAttribute hivAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS);
		if (hivAttr != null) {
			setHivStatus(HIVStatus.valueOf(hivAttr.getValue()));
		}

		// TODO: populate registerPregProgram

		PersonAttribute primaryPhoneAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER);
		if (primaryPhoneAttr != null) {
			setPrimaryPhone(primaryPhoneAttr.getValue());
		}

		PersonAttribute primaryPhoneTypeAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE);
		if (primaryPhoneTypeAttr != null) {
			setPrimaryPhoneType(ContactNumberType.valueOf(primaryPhoneTypeAttr
					.getValue()));
		}

		PersonAttribute secondaryPhoneAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER);
		if (secondaryPhoneAttr != null) {
			setSecondaryPhone(secondaryPhoneAttr.getValue());
		}

		PersonAttribute secondaryPhoneTypeAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE);
		if (secondaryPhoneTypeAttr != null) {
			setSecondaryPhoneType(ContactNumberType
					.valueOf(secondaryPhoneTypeAttr.getValue()));
		}

		PersonAttribute mediaTypeInfoAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL);
		if (mediaTypeInfoAttr != null) {
			setMediaTypeInfo(MediaType.valueOf(mediaTypeInfoAttr.getValue()));
		}

		PersonAttribute mediaTypeReminderAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER);
		if (mediaTypeReminderAttr != null) {
			setMediaTypeReminder(MediaType.valueOf(mediaTypeReminderAttr
					.getValue()));
		}

		PersonAttribute languageVoiceAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE);
		if (languageVoiceAttr != null) {
			setLanguageVoice(languageVoiceAttr.getValue());
		}

		PersonAttribute languageTextAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT);
		if (languageTextAttr != null) {
			setLanguageText(languageTextAttr.getValue());
		}

		PersonAttribute whoRegisteredAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED);
		if (whoRegisteredAttr != null) {
			setWhoRegistered(WhoRegistered
					.valueOf(whoRegisteredAttr.getValue()));
		}

		PersonAttribute religionAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_RELIGION);
		if (religionAttr != null) {
			setReligion(religionAttr.getValue());
		}

		PersonAttribute occupationAttr = patient
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION);
		if (occupationAttr != null) {
			setOccupation(occupationAttr.getValue());
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPrefName() {
		return prefName;
	}

	public void setPrefName(String prefName) {
		this.prefName = prefName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Boolean getBirthDateEst() {
		return birthDateEst;
	}

	public void setBirthDateEst(Boolean birthDateEst) {
		this.birthDateEst = birthDateEst;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public Boolean getRegisteredGHS() {
		return registeredGHS;
	}

	public void setRegisteredGHS(Boolean registeredGHS) {
		this.registeredGHS = registeredGHS;
	}

	public String getRegNumberGHS() {
		return regNumberGHS;
	}

	public void setRegNumberGHS(String regNumberGHS) {
		this.regNumberGHS = regNumberGHS;
	}

	public Boolean getInsured() {
		return insured;
	}

	public void setInsured(Boolean insured) {
		this.insured = insured;
	}

	public String getNhis() {
		return nhis;
	}

	public void setNhis(String nhis) {
		this.nhis = nhis;
	}

	public Date getNhisExpDate() {
		return nhisExpDate;
	}

	public void setNhisExpDate(Date nhisExpDate) {
		this.nhisExpDate = nhisExpDate;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getClinic() {
		return clinic;
	}

	public void setClinic(Integer clinic) {
		this.clinic = clinic;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Boolean getDueDateConfirmed() {
		return dueDateConfirmed;
	}

	public void setDueDateConfirmed(Boolean dueDateConfirmed) {
		this.dueDateConfirmed = dueDateConfirmed;
	}

	public Integer getGravida() {
		return gravida;
	}

	public void setGravida(Integer gravida) {
		this.gravida = gravida;
	}

	public Integer getParity() {
		return parity;
	}

	public void setParity(Integer parity) {
		this.parity = parity;
	}

	public HIVStatus getHivStatus() {
		return hivStatus;
	}

	public void setHivStatus(HIVStatus hivStatus) {
		this.hivStatus = hivStatus;
	}

	public Boolean getRegisterPregProgram() {
		return registerPregProgram;
	}

	public void setRegisterPregProgram(Boolean registerPregProgram) {
		this.registerPregProgram = registerPregProgram;
	}

	public Boolean getTermsConsent() {
		return termsConsent;
	}

	public void setTermsConsent(Boolean termsConsent) {
		this.termsConsent = termsConsent;
	}

	public String getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public ContactNumberType getPrimaryPhoneType() {
		return primaryPhoneType;
	}

	public void setPrimaryPhoneType(ContactNumberType primaryPhoneType) {
		this.primaryPhoneType = primaryPhoneType;
	}

	public String getSecondaryPhone() {
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public ContactNumberType getSecondaryPhoneType() {
		return secondaryPhoneType;
	}

	public void setSecondaryPhoneType(ContactNumberType secondaryPhoneType) {
		this.secondaryPhoneType = secondaryPhoneType;
	}

	public MediaType getMediaTypeInfo() {
		return mediaTypeInfo;
	}

	public void setMediaTypeInfo(MediaType mediaTypeInfo) {
		this.mediaTypeInfo = mediaTypeInfo;
	}

	public MediaType getMediaTypeReminder() {
		return mediaTypeReminder;
	}

	public void setMediaTypeReminder(MediaType mediaTypeReminder) {
		this.mediaTypeReminder = mediaTypeReminder;
	}

	public String getLanguageVoice() {
		return languageVoice;
	}

	public void setLanguageVoice(String languageVoice) {
		this.languageVoice = languageVoice;
	}

	public String getLanguageText() {
		return languageText;
	}

	public void setLanguageText(String languageText) {
		this.languageText = languageText;
	}

	public WhoRegistered getWhoRegistered() {
		return whoRegistered;
	}

	public void setWhoRegistered(WhoRegistered whoRegistered) {
		this.whoRegistered = whoRegistered;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

}
