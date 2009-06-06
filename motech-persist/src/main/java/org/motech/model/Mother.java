package org.motech.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mothers")
public class Mother extends Patient {

	private List<Pregnancy> pregnancies = new ArrayList<Pregnancy>();

	@OneToMany(mappedBy = "mother", cascade = { PERSIST, MERGE })
	public List<Pregnancy> getPregnancies() {
		return pregnancies;
	}

	public void setPregnancies(List<Pregnancy> pregnancies) {
		this.pregnancies = pregnancies;
	}
}
