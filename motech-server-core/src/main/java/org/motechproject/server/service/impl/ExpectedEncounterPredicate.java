package org.motechproject.server.service.impl;

import org.apache.commons.collections.Predicate;
import org.motechproject.server.model.ExpectedEncounter;

public class ExpectedEncounterPredicate implements Predicate {

	private String name;

	public boolean evaluate(Object input) {
		if (input instanceof ExpectedEncounter) {
			ExpectedEncounter expectedEncounter = (ExpectedEncounter) input;
			return expectedEncounter.getName().equals(name);
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
