package org.motechproject.ws;

import java.util.Date;

public class Patient {

	String motechId;
	String preferredName;
	String firstName;
	String lastName;
	Date birthDate;
	Integer age;
	Gender sex;
	String community;
	String phoneNumber;
	Date estimateDueDate;
	Date deliveryDate;
	Care[] cares;

	public String getMotechId() {
		return motechId;
	}

	public void setMotechId(String motechId) {
		this.motechId = motechId;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getEstimateDueDate() {
		return estimateDueDate;
	}

	public void setEstimateDueDate(Date estimateDueDate) {
		this.estimateDueDate = estimateDueDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Care[] getCares() {
		return cares;
	}

	public void setCares(Care[] cares) {
		this.cares = cares;
	}

}
