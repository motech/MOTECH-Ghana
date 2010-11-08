package org.motechproject.ws.server;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrors {

	private List<String> errors = new ArrayList<String>();

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public void add(String errorMessage) {
		this.errors.add(errorMessage);
	}

}
