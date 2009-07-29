package org.motech.svc;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.dao.SimpleDao;
import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.NotificationType;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.PhoneType;
import org.motech.model.Pregnancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarBean implements Registrar {

	private static Log log = LogFactory.getLog(RegistrarBean.class);

	Logger loggerBean;
	SimpleDao dao;

	@Autowired
	public void setLogger(Logger logger) {
		log.debug("logger set");
		this.loggerBean = logger;
	}

	@Autowired
	public void setDao(SimpleDao dao) {
		log.debug("dao set");
		this.dao = dao;
	}

	public void registerMother(String nursePhoneNumber, Date date,
			String serialId, String name, String community, String location,
			Date dateOfBirth, Integer nhis, String phoneNumber,
			PhoneType phoneType, String language,
			NotificationType notificationType, Date dueDate, Integer parity,
			Integer hemoglobin) {

		// TODO: Rely on nurse registration, needed for lookup in
		// registerPregnancy

		registerPatient(nursePhoneNumber, serialId, name, community, location,
				dateOfBirth, Gender.female, nhis, phoneNumber, phoneType,
				language, notificationType);

		registerPregnancy(nursePhoneNumber, date, serialId, dueDate, parity,
				hemoglobin);
	}

	public void registerClinic(String name) {

		Clinic c = new Clinic();
		c.setName(name);

		dao.saveClinic(c);

		loggerBean.log(LogType.success, "Clinic Registered: " + name);
	}

	public void registerNurse(String name, String phoneNumber, String clinic) {

		Clinic c = dao.getClinic(clinic);

		Nurse n = new Nurse();
		n.setName(name);
		n.setPhoneNumber(phoneNumber);
		n.setClinic(c);

		dao.saveNurse(n);

		loggerBean.log(LogType.success, "Nurse Registered: " + name + ","
				+ phoneNumber);
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber,
			PhoneType phoneType, String language,
			NotificationType notificationType) {

		Nurse n = dao.getNurse(nursePhoneNumber);

		Patient p = new Patient();
		p.setSerial(serialId);
		p.setName(name);
		p.setCommunity(community);
		p.setLocation(location);
		p.setDateOfBirth(dateOfBirth);
		p.setGender(gender);
		p.setNhis(nhis);
		p.setPhoneNumber(phoneNumber);
		p.setClinic(n.getClinic());
		p.setPhoneType(phoneType);
		p.setLanguage(language);
		p.setNotificationType(notificationType);

		dao.savePatient(p);

		loggerBean.log(LogType.success, "Patient Registered: " + serialId + ","
				+ n.getClinic().getId());
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Integer hemoglobin) {

		Nurse n = dao.getNurse(nursePhoneNumber);

		Patient a = dao.getPatient(serialId, n.getClinic().getId());

		MaternalData m = a.getMaternalData();
		// Check if a Maternal Visit or Pregnancy have been recorded previously
		if (m == null) {
			m = new MaternalData();
			m.setPatient(a);
			a.setMaternalData(m);
		}

		Pregnancy p = new Pregnancy();
		p.setRegistrationDate(date);
		p.setParity(parity);
		p.setHemoglobin(hemoglobin);
		p.setDueDate(dueDate);
		p.setMaternalData(m);

		// Hookup relationships
		n.getPregnancies().add(p);
		p.setNurse(n);

		m.getPregnancies().add(p);
		p.setMaternalData(m);

		// Persist (Mother persists pregnancy and patient transitively)
		dao.saveMaternalData(m);

		loggerBean.log(LogType.success, "Pregnancy Registered: " + serialId
				+ "," + dueDate);
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks) {

		Nurse n = dao.getNurse(nursePhoneNumber);

		Patient a = dao.getPatient(serialId, n.getClinic().getId());

		MaternalVisit v = new MaternalVisit();
		v.setDate(date);
		v.setNurse(n);
		v.setTetanus(tetanus);
		v.setIpt(ipt);
		v.setItn(itn);
		v.setVisitNumber(visitNumber);
		v.setOnARV(onARV);
		v.setPrePMTCT(prePMTCT);
		v.setTestPMTCT(testPMTCT);
		v.setPostPMTCT(postPMTCT);
		v.setHemoglobinAt36Weeks(hemoglobinAt36Weeks);

		MaternalData m = a.getMaternalData();
		// Check if a Maternal Visit or Pregnancy have been recorded previously
		if (m == null) {
			m = new MaternalData();
			m.setPatient(a);
			a.setMaternalData(m);
		}

		v.setMaternalData(m);
		m.getMaternalVisits().add(v);

		dao.saveMaternalData(m);

		loggerBean.log(LogType.success, "Maternal Visit Registered: "
				+ serialId + "," + date);

		// Date 30 seconds in future
		long nextServiceTime = 30 * 1000;
		Date nextServiceDate = new Date(System.currentTimeMillis()
				+ nextServiceTime);

		FutureServiceDelivery f = new FutureServiceDelivery();
		f.setDate(nextServiceDate);
		f.setNurse(n);
		f.setPatient(a);
		f.setService("Maternal Visit");

		dao.saveFutureServiceDelivery(f);

		loggerBean.log(LogType.success, "Future Service Delivery Scheduled: "
				+ serialId + "," + nextServiceDate);
	}
}
