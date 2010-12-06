package org.motech.model.db.hibernate;

import org.motech.model.LogType;

public class LogTypeEnumType extends EnumUserType<LogType> {

	public LogTypeEnumType() {
		super(LogType.class);
	}
}
