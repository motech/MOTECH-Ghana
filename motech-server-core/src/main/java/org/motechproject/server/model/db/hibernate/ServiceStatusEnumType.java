package org.motechproject.server.model.db.hibernate;

import org.motechproject.server.model.ServiceStatus;

/**
 * An instantiated usertype, allowing storing readable values for the
 * ServiceStatus enumeration in the database.
 */
public class ServiceStatusEnumType extends EnumUserType<ServiceStatus> {

	public ServiceStatusEnumType() {
		super(ServiceStatus.class);
	}
}
