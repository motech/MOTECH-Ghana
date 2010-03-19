package org.motechproject.server.openmrs.module;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.openmrs.User;
import org.openmrs.util.OpenmrsConstants;

public interface UserResolver {

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_USERS })
	User lookupUser(Object userInfo);

}
