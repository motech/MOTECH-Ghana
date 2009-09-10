package org.motech.model;

import com.dreamoval.motech.omi.service.ContactNumberType;

public enum PhoneType {
	personal, shared;

	public ContactNumberType toContactNumberType() {
		switch (this) {
		case personal:
			return ContactNumberType.PERSONAL;
		case shared:
			return ContactNumberType.SHARED;
		default:
			return null;
		}
	}

	public static PhoneType fromContactNumberType(
			ContactNumberType contactNumberType) {
		switch (contactNumberType) {
		case PERSONAL:
			return PhoneType.personal;
		case SHARED:
			return PhoneType.shared;
		default:
			return null;
		}
	}

}
