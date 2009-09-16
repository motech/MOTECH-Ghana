package org.motech.event.impl;

import org.motech.event.BaseInterface;

public class BaseInterfaceImpl implements BaseInterface {

	private String name;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean equals(Object object) {
		BaseInterface otherBase = (BaseInterface) object;
		return getName().equals(otherBase.getName());
	}
}
