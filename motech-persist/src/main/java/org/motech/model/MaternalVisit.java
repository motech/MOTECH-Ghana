package org.motech.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "maternalvisits")
public class MaternalVisit {

	private Long id;
	private Date date;
	private Nurse nurse;
	private MaternalData maternalData;
	private Integer tetanus;
	private Integer ipt;
	private Integer itn;
	private Integer visitNumber;
	private Integer onARV;
	private Integer prePMTCT;
	private Integer testPMTCT;
	private Integer postPMTCT;
	private Integer hemoglobinAt36Weeks;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	@JoinColumn(name = "nurse_id")
	public Nurse getNurse() {
		return nurse;
	}

	public void setNurse(Nurse nurse) {
		this.nurse = nurse;
	}

	@ManyToOne
	@JoinColumn(name = "maternaldata_id")
	public MaternalData getMaternalData() {
		return maternalData;
	}

	public void setMaternalData(MaternalData maternalData) {
		this.maternalData = maternalData;
	}

	public Integer getTetanus() {
		return tetanus;
	}

	public void setTetanus(Integer tetanus) {
		this.tetanus = tetanus;
	}

	public Integer getIpt() {
		return ipt;
	}

	public void setIpt(Integer ipt) {
		this.ipt = ipt;
	}

	public Integer getItn() {
		return itn;
	}

	public void setItn(Integer itn) {
		this.itn = itn;
	}

	public Integer getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(Integer visitNumber) {
		this.visitNumber = visitNumber;
	}

	public Integer getOnARV() {
		return onARV;
	}

	public void setOnARV(Integer onARV) {
		this.onARV = onARV;
	}

	public Integer getPrePMTCT() {
		return prePMTCT;
	}

	public void setPrePMTCT(Integer prePMTCT) {
		this.prePMTCT = prePMTCT;
	}

	public Integer getTestPMTCT() {
		return testPMTCT;
	}

	public void setTestPMTCT(Integer testPMTCT) {
		this.testPMTCT = testPMTCT;
	}

	public Integer getPostPMTCT() {
		return postPMTCT;
	}

	public void setPostPMTCT(Integer postPMTCT) {
		this.postPMTCT = postPMTCT;
	}

	public Integer getHemoglobinAt36Weeks() {
		return hemoglobinAt36Weeks;
	}

	public void setHemoglobinAt36Weeks(Integer hemoglobinAt36Weeks) {
		this.hemoglobinAt36Weeks = hemoglobinAt36Weeks;
	}

}
