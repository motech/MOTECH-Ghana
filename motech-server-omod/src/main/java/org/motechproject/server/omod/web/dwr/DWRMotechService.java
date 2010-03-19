package org.motechproject.server.omod.web.dwr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebModelConverterImpl;
import org.motechproject.server.omod.web.model.WebPatient;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.PatientService;

public class DWRMotechService {

	private static Log log = LogFactory.getLog(DWRMotechService.class);

	private ContextService contextService;
	private WebModelConverter webModelConverter;

	public DWRMotechService() {
		contextService = new ContextServiceImpl();
		webModelConverter = new WebModelConverterImpl();
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setWebModelConverter(WebModelConverter webModelConverter) {
		this.webModelConverter = webModelConverter;
	}

	public List<WebPatient> findMatchingPeople(String firstName,
			String lastName, String birthDate, String community,
			String phoneNumber, String patientId, String nhisNumber) {

		if (log.isDebugEnabled()) {
			log.debug("Get Matching People: " + firstName + ", " + lastName
					+ ", " + birthDate + ", " + community + ", " + phoneNumber
					+ ", " + patientId + ", " + nhisNumber);
		}

		List<WebPatient> resultList = new ArrayList<WebPatient>();

		String datePattern = "dd/MM/yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);

		Date parsedBirthDate = null;
		try {
			parsedBirthDate = dateFormat.parse(birthDate);
		} catch (ParseException e) {
		}

		List<Person> matchingPeople = contextService.getRegistrarBean()
				.getMatchingPeople(firstName, lastName, parsedBirthDate,
						community, phoneNumber, patientId, nhisNumber);

		PatientService patientService = contextService.getPatientService();

		for (Person person : matchingPeople) {
			WebPatient webPatient = new WebPatient();
			Patient patient = patientService.getPatient(person.getPersonId());
			if (patient != null) {
				webModelConverter.patientToWeb(patient, webPatient);
			} else {
				webModelConverter.personToWeb(person, webPatient);
			}
			resultList.add(webPatient);
		}
		return resultList;
	}

}
