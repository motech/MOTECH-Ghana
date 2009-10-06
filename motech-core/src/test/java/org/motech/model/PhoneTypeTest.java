package org.motech.model;

import junit.framework.TestCase;

import com.dreamoval.motech.omi.ws.client.ContactNumberType;

public class PhoneTypeTest extends TestCase {

	public void testToContactNumberType() {
		assertEquals(ContactNumberType.PERSONAL, PhoneType.personal
				.toContactNumberType());
		assertEquals(ContactNumberType.SHARED, PhoneType.shared
				.toContactNumberType());
	}

	public void testValueOfContactNumberType() {
		assertEquals(PhoneType.personal, PhoneType
				.fromContactNumberType(ContactNumberType.PERSONAL));
		assertEquals(PhoneType.shared, PhoneType
				.fromContactNumberType(ContactNumberType.SHARED));
	}

}
