package org.motechproject.server.svc;

import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Gender;

public class BirthOutcomeChild {

	BirthOutcome outcome;
	String patientId;
	Gender sex;
	String firstName;
	Boolean opv;
	Boolean bcg;

	public BirthOutcomeChild(BirthOutcome outcome, String patientId,
			Gender sex, String firstName, Boolean opv, Boolean bcg) {
		setOutcome(outcome);
		setPatientId(patientId);
		setSex(sex);
		setFirstName(firstName);
		setOpv(opv);
		setBcg(bcg);
	}

	public BirthOutcomeChild() {
	}

	public BirthOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(BirthOutcome outcome) {
		this.outcome = outcome;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Boolean getOpv() {
		return opv;
	}

	public void setOpv(Boolean opv) {
		this.opv = opv;
	}

	public Boolean getBcg() {
		return bcg;
	}

	public void setBcg(Boolean bcg) {
		this.bcg = bcg;
	}

}
