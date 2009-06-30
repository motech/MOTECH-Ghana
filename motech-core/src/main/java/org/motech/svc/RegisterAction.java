package org.motech.svc;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.motech.model.Clinic;
import org.motech.model.Nurse;
import org.motech.model.Patient;

@Stateless
@Name("register")
public class RegisterAction implements Register {

	@In
	private Clinic clinic;

	@In
	private Nurse nurse;

	@In
	private Patient patient;

	@EJB
	private Registrar registrar;

	public String register() {

		try {
			registrar.registerClinic(clinic.getName());
			registrar.registerNurse(nurse.getName(), nurse.getPhoneNumber(),
					clinic.getName());
			registrar.registerPatient(nurse.getPhoneNumber(), patient
					.getSerial(), patient.getName(), patient.getCommunity(),
					patient.getLocation(), patient.getDateOfBirth(), patient
							.getGender(), patient.getNhis(), patient
							.getPhoneNumber());

			return "/registered.xhtml";

		} catch (Exception e) {

			// Find the root cause
			Throwable t = e;
			while (t.getCause() != null) {
				t = t.getCause();
			}

			FacesMessages.instance()
					.add(
							"Error: " + t.getClass().getName() + " - "
									+ t.getMessage());
			return null;
		}
	}

}