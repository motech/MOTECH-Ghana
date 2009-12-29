package org.motech.openmrs.module;

import org.openmrs.User;

public class VerbatimUserResolverImpl implements UserResolver {

	public User lookupUser(Object userInfo) {
		return (User) userInfo;
	}

}
