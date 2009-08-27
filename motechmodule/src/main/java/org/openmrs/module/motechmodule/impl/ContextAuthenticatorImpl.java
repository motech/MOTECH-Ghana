package org.openmrs.module.motechmodule.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.motechmodule.ContextAuthenticator;

public class ContextAuthenticatorImpl implements ContextAuthenticator {

	public void authenticate(String username, String password) {

		Context.authenticate(username, password);
	}

}
