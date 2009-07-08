package org.motech.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Patient {

	private Long id;
	private String serial;
	private Clinic clinic;
	private String name;
	private String location;
	private Date dateOfBirth;
	private String community;
	private Gender gender;
	private Integer nhis;
	private MaternalData maternalData;
	private String phoneNumber;
	private List<FutureServiceDelivery> futureServices = new ArrayList<FutureServiceDelivery>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Integer getNhis() {
		return nhis;
	}

	public void setNhis(Integer nhis) {
		this.nhis = nhis;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public MaternalData getMaternalData() {
		return maternalData;
	}

	public void setMaternalData(MaternalData maternalData) {
		this.maternalData = maternalData;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<FutureServiceDelivery> getFutureServices() {
		return futureServices;
	}

	public void setFutureServices(List<FutureServiceDelivery> futureServices) {
		this.futureServices = futureServices;
	}

}
