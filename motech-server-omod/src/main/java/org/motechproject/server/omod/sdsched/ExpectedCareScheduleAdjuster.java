package org.motechproject.server.omod.sdsched;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.motechproject.server.service.ExpectedCareSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;

public class ExpectedCareScheduleAdjuster implements ScheduleAdjuster {

	private List<ExpectedCareSchedule> schedules = new ArrayList<ExpectedCareSchedule>();

	private RegistrarBean registrarBean;

	public void adjustSchedule(Integer patientId) {
		Date currentDate = new Date();
		Patient patient = registrarBean.getPatientById(patientId);

		for (ExpectedCareSchedule schedule : schedules) {
			schedule.updateSchedule(patient, currentDate);
		}
	}

	public List<ExpectedCareSchedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<ExpectedCareSchedule> schedules) {
		this.schedules = schedules;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

}
