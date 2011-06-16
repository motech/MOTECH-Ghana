package org.motechproject.server.ws;

import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.ws.Care;
import org.motechproject.ws.Patient;

import java.util.*;

public class WebServiceCareModelConverterImpl extends AbstractWebServiceModelConverter implements WebServiceCareModelConverter {

    public Care[] upcomingObsToWebServiceCares(List<ExpectedObs> upcomingObs) {

		List<Care> cares = new ArrayList<Care>();

		for (ExpectedObs expectedObs : upcomingObs) {
			Care care = new Care();
			care.setName(expectedObs.getName());
			care.setDate(expectedObs.getDueObsDatetime());
			cares.add(care);
		}

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] upcomingEncountersToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters) {

		List<Care> cares = new ArrayList<Care>();

		for (ExpectedEncounter expectedEncounter : upcomingEncounters) {
			Care care = new Care();
			care.setName(expectedEncounter.getName());
			care.setDate(expectedEncounter.getDueEncounterDatetime());
			cares.add(care);
		}

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] upcomingToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters,
			List<ExpectedObs> upcomingObs, boolean includePatient) {

		List<Care> cares = new ArrayList<Care>();

		for (ExpectedEncounter expectedEncounter : upcomingEncounters) {
			Care care = new Care();
			care.setName(expectedEncounter.getName());
			care.setDate(expectedEncounter.getDueEncounterDatetime());
			if (includePatient) {
				Patient patient = patientToWebService(expectedEncounter
						.getPatient(), true);
				care.setPatients(new Patient[] { patient });
			}
			cares.add(care);
		}
		for (ExpectedObs expectedObs : upcomingObs) {
			Care care = new Care();
			care.setName(expectedObs.getName());
			care.setDate(expectedObs.getDueObsDatetime());
			if (includePatient) {
				Patient patient = patientToWebService(expectedObs.getPatient(),
						true);
				care.setPatients(new Patient[] { patient });
			}
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator());

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedObsToWebServiceCares(List<ExpectedObs> defaultedObs) {

		List<Care> cares = new ArrayList<Care>();

		Map<String, List<org.openmrs.Patient>> carePatientMap = new HashMap<String, List<org.openmrs.Patient>>();

		for (ExpectedObs expectedObs : defaultedObs) {
			List<org.openmrs.Patient> patients = carePatientMap.get(expectedObs
					.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedObs.getPatient());
			carePatientMap.put(expectedObs.getName(), patients);
		}
		for (Map.Entry<String, List<org.openmrs.Patient>> entry : carePatientMap
				.entrySet()) {
			Care care = new Care();
			care.setName(entry.getKey());
			Patient[] patients = patientToWebService(entry.getValue(), true);
			care.setPatients(patients);
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator(true));

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedEncountersToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters) {

		List<Care> cares = new ArrayList<Care>();

		Map<String, List<org.openmrs.Patient>> carePatientMap = new HashMap<String, List<org.openmrs.Patient>>();

		for (ExpectedEncounter expectedEncounter : defaultedEncounters) {
			List<org.openmrs.Patient> patients = carePatientMap
					.get(expectedEncounter.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedEncounter.getPatient());
			carePatientMap.put(expectedEncounter.getName(), patients);
		}
		for (Map.Entry<String, List<org.openmrs.Patient>> entry : carePatientMap
				.entrySet()) {
			Care care = new Care();
			care.setName(entry.getKey());
			Patient[] patients = patientToWebService(entry.getValue(), true);
			care.setPatients(patients);
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator(true));

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedToWebServiceCares(List<ExpectedEncounter> defaultedEncounters, List<ExpectedObs> defaultedObs) {



		Map<String, List<org.openmrs.Patient>> carePatientMap = new HashMap<String, List<org.openmrs.Patient>>();

		for (ExpectedEncounter expectedEncounter : defaultedEncounters) {
			List<org.openmrs.Patient> patients = carePatientMap.get(expectedEncounter.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedEncounter.getPatient());
			carePatientMap.put(expectedEncounter.getName(), patients);
		}
		for (ExpectedObs expectedObs : defaultedObs) {
			List<org.openmrs.Patient> patients = carePatientMap.get(expectedObs.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedObs.getPatient());
			carePatientMap.put(expectedObs.getName(), patients);
		}
        List<Care> cares = new ArrayList<Care>();
		for (Map.Entry<String, List<org.openmrs.Patient>> entry : carePatientMap
				.entrySet()) {
            Patient[] patients = patientToWebService(entry.getValue(), true);
            Care care = new Care();
            care.setName(entry.getKey());
			care.setPatients(patients);
			cares.add(care);
		}
		Collections.sort(cares, new CareDateComparator(true));
		return cares.toArray(new Care[cares.size()]);
	}

}
