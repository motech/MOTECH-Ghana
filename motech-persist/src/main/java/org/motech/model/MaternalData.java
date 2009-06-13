package org.motech.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "maternaldata")
public class MaternalData {

	private Long id;
	private Patient patient;
	private List<Pregnancy> pregnancies = new ArrayList<Pregnancy>();
	private List<MaternalVisit> maternalVisits = new ArrayList<MaternalVisit>();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(optional = false)
	@JoinColumn(name = "patient_id", unique = true)
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@OneToMany(mappedBy = "maternalData", cascade = { PERSIST, MERGE })
	public List<Pregnancy> getPregnancies() {
		return pregnancies;
	}

	public void setPregnancies(List<Pregnancy> pregnancies) {
		this.pregnancies = pregnancies;
	}

	@OneToMany(mappedBy = "maternalData", cascade = { PERSIST, MERGE })
	public List<MaternalVisit> getMaternalVisits() {
		return maternalVisits;
	}

	public void setMaternalVisits(List<MaternalVisit> maternalVisits) {
		this.maternalVisits = maternalVisits;
	}

}
