package org.motech.model;

import java.util.ArrayList;
import java.util.List;

public class MaternalData {

	private Long id;
	private Patient patient;
	private List<Pregnancy> pregnancies = new ArrayList<Pregnancy>();
	private List<MaternalVisit> maternalVisits = new ArrayList<MaternalVisit>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
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

}
