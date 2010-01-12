package org.motech.openmrs.module.impl;

import org.motech.openmrs.module.UserResolver;
import org.openmrs.User;

public class VerbatimUserResolverImpl implements UserResolver {

	public User lookupUser(Object userInfo) {
		return (User) userInfo;
	}

}
