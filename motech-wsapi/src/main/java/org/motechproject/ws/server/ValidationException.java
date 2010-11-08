package org.motechproject.ws.server;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.motechproject.ws.server.ValidationErrors")
public class ValidationException extends Exception {

	private static final long serialVersionUID = 2L;

	private ValidationErrors faultInfo;

	public ValidationException() {
		super();
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, ValidationErrors faultInfo) {
		super(message);
		setFaultInfo(faultInfo);
	}

	public ValidationException(String message, ValidationErrors faultInfo,
			Throwable cause) {
		super(message, cause);
		setFaultInfo(faultInfo);
	}

	public ValidationErrors getFaultInfo() {
		return faultInfo;
	}

	public void setFaultInfo(ValidationErrors faultInfo) {
		this.faultInfo = faultInfo;
	}
}
