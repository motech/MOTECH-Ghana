package org.motech.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mothers")
@NamedQueries( {
  @NamedQuery(name = "findMotherBySerial", query = "select m from Mother m where m.patient.serial = :serial")
} )
public class Mother {

	private Long id;
	private Patient patient;
	private List<Pregnancy> pregnancies = new ArrayList<Pregnancy>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(optional = false, cascade = { PERSIST, MERGE })
	@JoinColumn(name = "patient_id")
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@OneToMany(mappedBy = "mother", cascade = { PERSIST, MERGE })
	public List<Pregnancy> getPregnancies() {
		return pregnancies;
	}

	public void setPregnancies(List<Pregnancy> pregnancies) {
		this.pregnancies = pregnancies;
	}
}
