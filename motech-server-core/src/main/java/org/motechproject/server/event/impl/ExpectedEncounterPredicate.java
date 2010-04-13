package org.motechproject.server.event.impl;

import org.apache.commons.collections.Predicate;
import org.motechproject.server.model.ExpectedEncounter;

public class ExpectedEncounterPredicate implements Predicate {

	private String group;

	public boolean evaluate(Object input) {
		if (input instanceof ExpectedEncounter) {
			ExpectedEncounter expectedEncounter = (ExpectedEncounter) input;
			return expectedEncounter.getGroup().equals(group);
		}
		return false;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
