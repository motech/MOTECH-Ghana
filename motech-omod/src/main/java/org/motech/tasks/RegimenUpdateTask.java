package org.motech.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.event.Regimen;
import org.motech.openmrs.module.MotechService;
import org.motech.util.MotechConstants;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants;

public class RegimenUpdateTask extends AbstractTask {

	private static Log log = LogFactory.getLog(RegimenUpdateTask.class);

	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {

		log.debug("Regimen Task - Update Enrolled Regimens for all Patients");

		try {
			Context.openSession();
			Context
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_OBS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);

			PatientService patientService = Context.getPatientService();
			MotechService motechService = Context
					.getService(MotechService.class);

			// Get all Patients with the Ghana Clinic Id Type
			PatientIdentifierType serialIdType = patientService
					.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);
			List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
			idTypes.add(serialIdType);
			List<Patient> patients = patientService.getPatients(null, null,
					idTypes, true);

			// Update Regimen state for enrolled Regimen of all matching
			// patients
			for (Patient patient : patients) {
				List<String> patientRegimens = motechService
						.getRegimenEnrollment(patient.getPatientId());

				for (String regimenName : patientRegimens) {
					Regimen regimen = motechService.getRegimen(regimenName);

					log.debug("Regimen Update - Update State: regimen: "
							+ regimenName + ", patient: "
							+ patient.getPatientId());

					regimen.determineState(patient);
				}
			}
		} finally {
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_OBS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context.closeSession();
		}
	}

}
