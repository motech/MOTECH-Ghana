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

	public List<WebPatient> findMatchingPatients(String firstName,
			String lastName, String prefName, String birthDate,
			String community, String phoneNumber, String nhisNumber,
			String motechId) {

		if (log.isDebugEnabled()) {
			log.debug("Get Matching Patients: " + firstName + ", " + lastName
					+ ", " + prefName + ", " + birthDate + ", " + community
					+ ", " + phoneNumber + ", " + nhisNumber + ", " + motechId);
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

		List<Patient> matchingPatients = contextService.getRegistrarBean()
				.getPatients(firstName, lastName, prefName, parsedBirthDate,
						community, phoneNumber, nhisNumber, motechId);

		for (Patient patient : matchingPatients) {
			WebPatient webPatient = new WebPatient();
			webModelConverter.patientToWeb(patient, webPatient);
			resultList.add(webPatient);
		}
		return resultList;
	}

}
