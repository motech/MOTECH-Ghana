package org.motechproject.server.openmrs.module.impl;

import java.util.List;

import org.motechproject.server.openmrs.module.ContextService;
import org.motechproject.server.openmrs.module.MotechService;
import org.motechproject.server.openmrs.module.UserResolver;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

public class PersonAttributeUserResolverImpl implements UserResolver {

	String attributeName;

	ContextService contextService;

	public User lookupUser(Object userInfo) {
		PersonService personService = contextService.getPersonService();
		MotechService motechService = contextService.getMotechService();
		UserService userService = contextService.getUserService();

		PersonAttributeType attrType = personService
				.getPersonAttributeTypeByName(attributeName);
		List<Integer> userIds = motechService.getUserIdsByPersonAttribute(
				attrType, userInfo.toString());

		if (userIds.size() != 1)
			throw new IllegalArgumentException(
					"failed to find unique user, found " + userIds.size()
							+ " users with info: " + userInfo.toString());

		return userService.getUser(userIds.get(0));
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

}
