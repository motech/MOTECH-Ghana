package org.openmrs.module.motechmodule;

public interface ContextAuthenticator {

	public void authenticate(String username, String password);
}
