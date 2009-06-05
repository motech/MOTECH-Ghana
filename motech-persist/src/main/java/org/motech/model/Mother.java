package org.motech.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mothers")
public class Mother extends Patient {

	private List<Pregnancy> pregnancies;

	@OneToMany(mappedBy = "mother")
	public List<Pregnancy> getPregnancies() {
		return pregnancies;
	}

	public void setPregnancies(List<Pregnancy> pregnancies) {
		this.pregnancies = pregnancies;
	}
}
