package org.motechproject.server.openmrs.module.impl;

import org.motechproject.server.openmrs.module.UserResolver;
import org.openmrs.User;

public class VerbatimUserResolverImpl implements UserResolver {

	public User lookupUser(Object userInfo) {
		return (User) userInfo;
	}

}
