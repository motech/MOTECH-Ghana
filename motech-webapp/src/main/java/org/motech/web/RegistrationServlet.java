package org.motech.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.Log;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;
import org.motech.svc.Registrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class RegistrationServlet {

	private Clinic clinic;
	private Nurse nurse;
	private Patient patient;
	private Pregnancy pregnancy;
	private MaternalVisit maternalVisit;
	private List<SelectItem> genders;

	@Autowired
	Registrar registrationService;

	public RegistrationServlet() {
		clinic = new Clinic();
		clinic.setName("A-Clinic");
		nurse = new Nurse();
		nurse.setName("Test-Nurse");
		nurse.setPhoneNumber("5555555555");
		patient = new Patient();
		patient.setCommunity("Community");
		patient.setDateOfBirth(new Date());
		patient.setGender(Gender.female);
		patient.setLocation("Location");
		patient.setName("Test-Patient");
		patient.setNhis(1);
		patient.setPhoneNumber("5555555556");
		patient.setSerial("FGH4894894");
		pregnancy = new Pregnancy();
		pregnancy.setDueDate(new Date());
		pregnancy.setHemoglobin(47);
		pregnancy.setParity(1);
		pregnancy.setRegistrationDate(new Date());
		maternalVisit = new MaternalVisit();
		maternalVisit.setDate(new Date());
		maternalVisit.setHemoglobinAt36Weeks(245);
		maternalVisit.setIpt(0);
		maternalVisit.setItn(1);
		maternalVisit.setOnARV(0);
		maternalVisit.setPostPMTCT(0);
		maternalVisit.setPrePMTCT(0);
		maternalVisit.setTestPMTCT(0);
		maternalVisit.setTetanus(1);
		maternalVisit.setVisitNumber(1);
		genders = new ArrayList<SelectItem>(Gender.values().length);
		for (Gender gender : Gender.values()) {
			genders.add(new SelectItem(gender, gender.toString()));
		}
	}

	public String quick() {
		registrationService.registerClinic(clinic.getName());

		registrationService.registerNurse(nurse.getName(), nurse
				.getPhoneNumber(), clinic.getName());

		registrationService.registerMother(nurse.getPhoneNumber(), new Date(),
				patient.getSerial(), patient.getName(), patient.getCommunity(),
				patient.getLocation(), patient.getDateOfBirth(), patient
						.getNhis(), patient.getPhoneNumber(), pregnancy
						.getDueDate(), pregnancy.getParity(), pregnancy
						.getHemoglobin());

		registrationService.recordMaternalVisit(nurse.getPhoneNumber(),
				new Date(), patient.getSerial(), 0, 0, 0, 1, 0, 0, 0, 0, 0);

		return "success";
	}

	public String regClinic() {
		registrationService.registerClinic(clinic.getName());

		return "success";
	}

	public String regNurse() {
		registrationService.registerNurse(nurse.getName(), nurse
				.getPhoneNumber(), clinic.getName());

		return "success";
	}

	public String regPatient() {
		registrationService.registerPatient(nurse.getPhoneNumber(), patient
				.getSerial(), patient.getName(), patient.getCommunity(),
				patient.getLocation(), patient.getDateOfBirth(), patient
						.getGender(), patient.getNhis(), patient
						.getPhoneNumber());

		return "success";
	}

	public String regPregnancy() {
		registrationService
				.registerPregnancy(nurse.getPhoneNumber(), pregnancy
						.getRegistrationDate(), patient.getSerial(), pregnancy
						.getDueDate(), pregnancy.getParity(), pregnancy
						.getHemoglobin());

		return "success";
	}

	public String regMaternalVisit() {
		registrationService.recordMaternalVisit(nurse.getPhoneNumber(),
				maternalVisit.getDate(), patient.getSerial(), maternalVisit
						.getTetanus(), maternalVisit.getIpt(), maternalVisit
						.getItn(), maternalVisit.getVisitNumber(),
				maternalVisit.getOnARV(), maternalVisit.getPrePMTCT(),
				maternalVisit.getTestPMTCT(), maternalVisit.getPostPMTCT(),
				maternalVisit.getHemoglobinAt36Weeks());

		return "success";
	}

	public List<Clinic> getClinics() {
		return registrationService.getClinics();
	}

	public List<Nurse> getNurses() {
		return registrationService.getNurses();
	}

	public List<Patient> getPatients() {
		return registrationService.getPatients();
	}

	public List<Pregnancy> getPregnancies() {
		return registrationService.getPregnancies();
	}

	public List<MaternalVisit> getMaternalVisits() {
		return registrationService.getMaternalVisits();
	}

	public List<FutureServiceDelivery> getFutureServiceDeliveries() {
		return registrationService.getFutureServiceDeliveries();
	}

	public List<Log> getLogs() {
		return registrationService.getLogs();
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Nurse getNurse() {
		return nurse;
	}

	public void setNurse(Nurse nurse) {
		this.nurse = nurse;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Pregnancy getPregnancy() {
		return pregnancy;
	}

	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}

	public MaternalVisit getMaternalVisit() {
		return maternalVisit;
	}

	public void setMaternalVisit(MaternalVisit maternalVisit) {
		this.maternalVisit = maternalVisit;
	}

	public List<SelectItem> getGenders() {
		return genders;
	}

	public void setGenders(List<SelectItem> genders) {
		this.genders = genders;
	}
}
