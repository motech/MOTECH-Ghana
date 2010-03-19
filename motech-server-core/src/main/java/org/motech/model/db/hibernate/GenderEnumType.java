package org.motech.model.db.hibernate;

import org.motechproject.ws.Gender;

public class GenderEnumType extends EnumUserType<Gender> {

	public GenderEnumType() {
		super(Gender.class);
	}
}
