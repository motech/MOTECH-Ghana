package org.motechproject.server.service.impl;

import org.apache.commons.collections.Predicate;
import org.motechproject.server.model.ExpectedObs;

public class ExpectedObsPredicate implements Predicate {

	private String name;

	public boolean evaluate(Object input) {
		if (input instanceof ExpectedObs) {
			ExpectedObs expectedObs = (ExpectedObs) input;
			return expectedObs.getName().equals(name);
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
