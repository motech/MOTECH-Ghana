package org.motechproject.server.omod.impl;

import org.motechproject.server.omod.UserResolver;
import org.openmrs.User;

public class VerbatimUserResolverImpl implements UserResolver {

	public User lookupUser(Object userInfo) {
		return (User) userInfo;
	}

}
