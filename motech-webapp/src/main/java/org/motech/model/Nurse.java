package org.motech.model;

import java.util.ArrayList;
import java.util.List;

public class Nurse {

	private Long id;
	private String name;
	private Clinic clinic;
	private String phoneNumber;
	private List<Pregnancy> pregnancies = new ArrayList<Pregnancy>();
	private List<MaternalVisit> maternalVisits = new ArrayList<MaternalVisit>();
	private List<FutureServiceDelivery> futureServices = new ArrayList<FutureServiceDelivery>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<Pregnancy> getPregnancies() {
		return pregnancies;
	}

	public void setPregnancies(List<Pregnancy> pregnancies) {
		this.pregnancies = pregnancies;
	}

	public List<MaternalVisit> getMaternalVisits() {
		return maternalVisits;
	}

	public void setMaternalVisits(List<MaternalVisit> maternalVisits) {
		this.maternalVisits = maternalVisits;
	}

	public List<FutureServiceDelivery> getFutureServices() {
		return futureServices;
	}

	public void setFutureServices(List<FutureServiceDelivery> futureServices) {
		this.futureServices = futureServices;
	}

}
