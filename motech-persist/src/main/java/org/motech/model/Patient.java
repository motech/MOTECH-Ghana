package org.motech.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {

	private Long id;
	private String serial;
	private Clinic clinic;
	private String name;
	private String location;
	private Integer age;
	private String community;
	private Gender gender;
	private Integer nhis;
	private MaternalData maternalData;
	private String phoneNumber;
	private List<FutureServiceDelivery> futureServices = new ArrayList<FutureServiceDelivery>();

	@Id
	@GeneratedValue
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

	@ManyToOne
	@JoinColumn(name = "clinic_id")
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
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

	@OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
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

	@OneToMany(mappedBy = "patient", cascade = { PERSIST, MERGE })
	public List<FutureServiceDelivery> getFutureServices() {
		return futureServices;
	}

	public void setFutureServices(List<FutureServiceDelivery> futureServices) {
		this.futureServices = futureServices;
	}

}
