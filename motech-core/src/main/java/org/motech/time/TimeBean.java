package org.motech.time;

import java.util.Calendar;
import java.util.Date;

import org.motech.model.MessageProgramEnrollment;
import org.motech.svc.RegistrarBean;

public class TimeBean {

	private RegistrarBean registrarBean;

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public Date determineTime(TimePeriod timePeriod,
			TimeReference timeReference, Integer timeValue, Integer personId,
			MessageProgramEnrollment enrollment, String conceptName,
			String valueConceptName, String encounterTypeName) {

		if (timePeriod != null && timeReference != null && timeValue != null) {

			Calendar calendar = Calendar.getInstance();
			Date timeReferenceDate = null;

			if (personId == null && enrollment != null) {
				personId = enrollment.getPersonId();
			}

			switch (timeReference) {
			case patient_birthdate:
				if (personId != null) {
					timeReferenceDate = registrarBean
							.getPatientBirthDate(personId);
				}
				break;
			case last_obs_date:
				if (personId != null && conceptName != null
						&& valueConceptName != null) {
					timeReferenceDate = registrarBean.getLastObsDate(personId,
							conceptName, valueConceptName);
				}
				break;
			case last_obs_datevalue:
				if (personId != null && conceptName != null) {
					timeReferenceDate = registrarBean.getLastObsValue(personId,
							conceptName);
				}
				break;
			case enrollment_startdate:
				if (enrollment != null) {
					timeReferenceDate = enrollment.getStartDate();
				}
				break;
			case enrollment_obs_datevalue:
				if (enrollment != null) {
					timeReferenceDate = registrarBean.getObsValue(enrollment
							.getObsId());
				}
				break;
			}

			if (timeReferenceDate == null) {
				return null;
			}
			calendar.setTime(timeReferenceDate);

			switch (timePeriod) {
			case minute:
				calendar.add(Calendar.MINUTE, timeValue);
				break;
			case day:
				calendar.add(Calendar.DATE, timeValue);
				break;
			case week:
				// Add weeks as days
				calendar.add(Calendar.DATE, timeValue * 7);
				break;
			case month:
				calendar.add(Calendar.MONTH, timeValue);
				break;
			case year:
				calendar.add(Calendar.YEAR, timeValue);
				break;
			}

			return calendar.getTime();
		}
		return null;
	}

}
