package org.motech.openmrs.module.web.model;

import java.util.Date;

import org.motech.model.HIVStatus;
import org.motech.model.WhoRegistered;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;

public class WebPatient {

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
	private String motherRegNumberGHS;
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
	private String howLearned;

	public WebPatient() {
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

	public String getMotherRegNumberGHS() {
		return motherRegNumberGHS;
	}

	public void setMotherRegNumberGHS(String motherRegNumberGHS) {
		this.motherRegNumberGHS = motherRegNumberGHS;
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

	public String getHowLearned() {
		return howLearned;
	}

	public void setHowLearned(String howLearned) {
		this.howLearned = howLearned;
	}

}
