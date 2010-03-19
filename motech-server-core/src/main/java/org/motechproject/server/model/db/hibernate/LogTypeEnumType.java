package org.motechproject.server.model.db.hibernate;

import org.motechproject.ws.LogType;

/**
 * An instantiated usertype, allowing storing readable values for the LogType
 * enumeration in the database.
 */
public class LogTypeEnumType extends EnumUserType<LogType> {

	public LogTypeEnumType() {
		super(LogType.class);
	}
}
