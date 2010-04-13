package org.motechproject.server.event.impl;

import org.apache.commons.collections.Predicate;
import org.motechproject.server.model.ExpectedObs;

public class ExpectedObsPredicate implements Predicate {

	private String group;

	public boolean evaluate(Object input) {
		if (input instanceof ExpectedObs) {
			ExpectedObs expectedObs = (ExpectedObs) input;
			return expectedObs.getGroup().equals(group);
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
