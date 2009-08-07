package org.motech.mobile.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dreamoval.motech.omi.service.ContactNumberType;
import com.dreamoval.motech.omi.service.MessageType;
import com.dreamoval.motech.omi.service.PatientImpl;
import com.dreamoval.motech.webapp.webservices.MessageService;

public class MessageServiceStub implements MessageService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(MessageServiceStub.class);
	
	private DateFormat dateFormatter;
	
	public MessageServiceStub() {
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}
	
	public Long sendCHPSMessage(Long messageId, String workerName,
			String workerNumber, List<PatientImpl> patientList) {

		log.info("Motech Mobile Web Service Message\n" +
				"---------------------------\n" +
				"<sendCHPSMessage>\n" +
				"<messageId>"+messageId+"</messageId>\n" +
				"<workerName>"+workerName+"</workerName>\n" +
				"<workerNumber>"+workerNumber+"</workerNumber>\n" +
				"<patientList></patientList>\n" +
				"</sendCHPSMessage>\n" +
				"--------------------------------------");
		return new Long(1);
	}

	public Long sendPatientMessage(Long messageId, String clinic,
			Date serviceDate, String patientNumber,
			ContactNumberType patientNumberType, MessageType messageType) {
		
		String serviceDateString = null;
		if( serviceDate != null ) {
			serviceDateString = dateFormatter.format(serviceDate);
		}
		log.info("Motech Mobile Web Service Message\n" +
				"---------------------------\n" +
				"<sendPatientMessage>\n" +
				"<messageId>"+messageId+"</messageId>\n" +
				"<clinic>"+clinic+"</clinic>\n" +
				"<serviceDate>"+serviceDateString+"</serviceDate>\n" +
				"<patientNumber>"+patientNumber+"</patientNumber>\n" +
				"<patientNumberType>"+patientNumberType+"</patientNumberType>\n" +
				"<messageType>"+messageType+"</messageType>\n" +
				"</sendPatientMessage>\n" +
				"--------------------------------------");
		return new Long(1);
	}

}
