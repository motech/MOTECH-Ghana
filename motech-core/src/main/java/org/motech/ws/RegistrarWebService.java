package org.motech.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.server.RegistrarService;

/**
 * This is the service enpoint implementation for the major motech server web
 * service.
 * 
 * This can be accessed via /openmrs/ws/RegistrarService since we mapped it to
 * /ws/RegistrarService in the metadata/moduleApplicationContext.xml file.
 */

@WebService(targetNamespace = "http://server.ws.motechproject.org/")
public class RegistrarWebService implements RegistrarService {

	Log log = LogFactory.getLog(RegistrarWebService.class);

	RegistrarBean registrarBean;

	// TODO: Add OpenMRS API Exceptions as WebFaults ?

	@WebMethod
	public void registerPatient(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "name") String name,
			@WebParam(name = "community") String community,
			@WebParam(name = "location") String location,
			@WebParam(name = "dateOfBirth") Date dateOfBirth,
			@WebParam(name = "gender") Gender gender,
			@WebParam(name = "nhis") Integer nhis,
			@WebParam(name = "phoneNumber") String phoneNumber,
			@WebParam(name = "contactNumberType") ContactNumberType contactNumberType,
			@WebParam(name = "language") String language,
			@WebParam(name = "mediaType") MediaType mediaType,
			@WebParam(name = "deliveryTime") DeliveryTime deliveryTime,
			@WebParam(name = "messagePrograms") String[] messagePrograms) {

		registrarBean.registerPatient(nursePhoneNumber, serialId, name,
				community, location, dateOfBirth, gender, nhis, phoneNumber,
				contactNumberType, language, mediaType, deliveryTime,
				messagePrograms);
	}

	@WebMethod
	public void recordMaternalVisit(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "tetanus") Boolean tetanus,
			@WebParam(name = "ipt") Boolean ipt,
			@WebParam(name = "itn") Boolean itn,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "onARV") Boolean onARV,
			@WebParam(name = "prePMTCT") Boolean prePMTCT,
			@WebParam(name = "testPMTCT") Boolean testPMTCT,
			@WebParam(name = "postPMTCT") Boolean postPMTCT,
			@WebParam(name = "hemoglobinAt36Weeks") Double hemoglobinAt36Weeks) {

		registrarBean.recordMaternalVisit(nursePhoneNumber, date, serialId,
				tetanus, ipt, itn, visitNumber, onARV, prePMTCT, testPMTCT,
				postPMTCT, hemoglobinAt36Weeks);
	}

	@WebMethod
	public void registerPregnancy(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "dueDate") Date dueDate,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "hemoglobin") Double hemoglobin) {

		registrarBean.registerPregnancy(nursePhoneNumber, date, serialId,
				dueDate, parity, hemoglobin);
	}

	@WebMethod
	public void log(@WebParam(name = "type") LogType type,
			@WebParam(name = "message") String message) {

		registrarBean.log(type, message);
	}

	@WebMethod
	public void setMessageStatus(
			@WebParam(name = "messageId") String messageId,
			@WebParam(name = "success") Boolean success) {

		registrarBean.setMessageStatus(messageId, success);
	}

	@WebMethod(exclude = true)
	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

}
