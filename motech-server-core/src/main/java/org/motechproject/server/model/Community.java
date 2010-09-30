package org.motechproject.server.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Patient;

public class Community implements Serializable, Comparable {

	private static final long serialVersionUID = -3653103922117733357L;

	private Long id;
	private Integer communityId;
	private String name;
	private Facility facility;
	private Set<Patient> residents = new HashSet<Patient>();
	private Boolean retired = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Integer communityId) {
		this.communityId = communityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public Set<Patient> getResidents() {
		return residents;
	}

	public void setResidents(Set<Patient> residents) {
		this.residents = residents;
	}

	public Boolean getRetired() {
		return retired;
	}

	public void setRetired(Boolean retired) {
		this.retired = retired;
	}

    public int compareTo(Object community) {
        return ((Community)community).getName().compareTo(this.name);
    }

}
