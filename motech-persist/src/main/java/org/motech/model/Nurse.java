package org.motech.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "nurses")
@NamedQueries( {
		@NamedQuery(name = "findNurseByPhoneNumber", query = "select n from Nurse n where n.phoneNumber = :phoneNumber"),
		@NamedQuery(name = "findAllNurses", query = "select n from Nurse n") })
public class Nurse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private Clinic clinic;
	private String phoneNumber;
	private List<Pregnancy> pregnancies = new ArrayList<Pregnancy>();
	private List<MaternalVisit> maternalVisits = new ArrayList<MaternalVisit>();
	private List<FutureServiceDelivery> futureServices = new ArrayList<FutureServiceDelivery>();

	@Id
	@GeneratedValue
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

	@ManyToOne
	@JoinColumn(name = "clinic_id")
	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	@Column(unique = true)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@OneToMany(mappedBy = "nurse", cascade = { PERSIST, MERGE })
	public List<Pregnancy> getPregnancies() {
		return pregnancies;
	}

	public void setPregnancies(List<Pregnancy> pregnancies) {
		this.pregnancies = pregnancies;
	}

	@OneToMany(mappedBy = "nurse", cascade = { PERSIST, MERGE })
	public List<MaternalVisit> getMaternalVisits() {
		return maternalVisits;
	}

	public void setMaternalVisits(List<MaternalVisit> maternalVisits) {
		this.maternalVisits = maternalVisits;
	}

	@OneToMany(mappedBy = "nurse", cascade = { PERSIST, MERGE })
	public List<FutureServiceDelivery> getFutureServices() {
		return futureServices;
	}

	public void setFutureServices(List<FutureServiceDelivery> futureServices) {
		this.futureServices = futureServices;
	}

}
