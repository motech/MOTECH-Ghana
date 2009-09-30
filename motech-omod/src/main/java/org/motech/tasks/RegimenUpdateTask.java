package org.motech.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.event.Regimen;
import org.motech.openmrs.module.MotechService;
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

		log
				.debug("Regimen Task - Update Tetanus Information and Immuniztion Regimens");

		try {
			Context.openSession();
			Context
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);

			Regimen tetanusInformationRegimen = Context.getService(
					MotechService.class).getRegimen("tetanusInfo");

			Regimen tetanusImmunizationRegimen = Context.getService(
					MotechService.class).getRegimen("tetanusImmunization");

			PatientService patientService = Context.getPatientService();

			// Get all Patients with the Ghana Clinic Id Type
			PatientIdentifierType serialIdType = patientService
					.getPatientIdentifierTypeByName("Ghana Clinic Id");
			List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
			idTypes.add(serialIdType);
			List<Patient> patients = patientService.getPatients(null, null,
					idTypes, true);

			// Update Regimen state for all matching patients
			for (Patient patient : patients) {
				tetanusInformationRegimen.determineState(patient);
				tetanusImmunizationRegimen.determineState(patient);
			}
		} finally {
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			Context.closeSession();
		}
	}

}
