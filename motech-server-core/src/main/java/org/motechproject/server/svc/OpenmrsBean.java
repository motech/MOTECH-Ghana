package org.motechproject.server.svc;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.util.OpenmrsConstants;

/**
 * An interface providing methods commonly required of openmrs by motech. This
 * isn't intended to provide high level operations meaningful to motech. It is
 * intended to provide a testable interface for simpler operations. It is
 * expected that this service will be used by other higher level services that
 * provide higher level operations.
 * 
 * @author batkinson
 * 
 */
public interface OpenmrsBean {

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public Patient getPatientByMotechId(String motechId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public User getStaffBySystemId(String chpsId);

}
