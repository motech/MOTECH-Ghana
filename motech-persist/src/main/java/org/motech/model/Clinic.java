package org.motech.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

@Entity
@Name("clinic")
@Table(name = "clinics")
@NamedQueries( {
		@NamedQuery(name = "findClinicByName", query = "select c from Clinic c where c.name = :name"),
		@NamedQuery(name = "findAllClinics", query = "select c from Clinic c") })
public class Clinic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8921108818172700643L;

	private Long id;
	private String name;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
