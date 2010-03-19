package org.motech.model.db.hibernate;

import org.motech.model.ServiceStatus;

/**
 * An instantiated usertype, allowing storing readable values for the
 * ServiceStatus enumeration in the database.
 */
public class ServiceStatusEnumType extends EnumUserType<ServiceStatus> {

	public ServiceStatusEnumType() {
		super(ServiceStatus.class);
	}
}
