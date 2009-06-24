package org.motech.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Gender;
import org.motech.svc.Registrar;

public class RegistrationServlet extends HttpServlet {

	static Map<String, String> errorUrls = new HashMap<String, String>();

	static {
		errorUrls.put("quick", "/");
		errorUrls.put("nurse", "/nurse.jsp");
		errorUrls.put("patient", "/patient.jsp");
		errorUrls.put("pregnancy", "/pregnancy.jsp");
		errorUrls.put("maternalvisit", "/maternalVisit.jsp");
	}

	private static final long serialVersionUID = 4561954777101725182L;

	private static Log log = LogFactory.getLog(RegistrationServlet.class);

	@EJB
	Registrar registrationService;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Get the value that determines the action to take
		String action = req.getParameter("testAction");
		String errorUrl = errorUrls.get(action);

		try {

			// Get parameters
			String nursePhoneNumber = req.getParameter("nursePhone");
			String serialId = req.getParameter("serialId");
			String clinic = req.getParameter("clinic");
			String name = req.getParameter("name");
			String gender = req.getParameter("gender");
			String community = req.getParameter("community");
			String location = req.getParameter("location");
			String nhisStr = req.getParameter("nhis");
			Integer nhis = nhisStr == null ? null : Integer.valueOf(nhisStr);
			String patientPhoneNumber = req.getParameter("patientPhone");

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

			String dueDateStr = req.getParameter("dueDate");
			Date dueDate = dueDateStr == null ? null : dateFormat
					.parse(dueDateStr);

			String regDateStr = req.getParameter("regDate");
			Date regDate = regDateStr == null ? null : dateFormat
					.parse(regDateStr);

			String visitDateStr = req.getParameter("visitDate");
			Date visitDate = visitDateStr == null ? null : dateFormat
					.parse(visitDateStr);

			String dobStr = req.getParameter("dateOfBirth");
			Date dateOfBirth = dobStr == null ? null : dateFormat.parse(dobStr);

			String parityStr = req.getParameter("parity");
			Integer parity = parityStr == null ? null : Integer
					.valueOf(parityStr);

			String hemoglobinStr = req.getParameter("hemoglobin");
			Integer hemoglobin = hemoglobinStr == null ? null : Integer
					.valueOf(hemoglobinStr);

			String tetanusStr = req.getParameter("tetanus");
			Integer tetanus = tetanusStr == null ? null : Integer
					.valueOf(tetanusStr);

			String iptStr = req.getParameter("ipt");
			Integer ipt = iptStr == null ? null : Integer.valueOf(iptStr);

			String itnStr = req.getParameter("itn");
			Integer itn = itnStr == null ? null : Integer.valueOf(itnStr);

			String visitNoStr = req.getParameter("visitNumber");
			Integer visitNumber = visitNoStr == null ? null : Integer
					.valueOf(visitNoStr);

			String onARVStr = req.getParameter("onARV");
			Integer onARV = onARVStr == null ? null : Integer.valueOf(onARVStr);

			String prePMTCTStr = req.getParameter("prePMTCT");
			Integer prePMTCT = prePMTCTStr == null ? null : Integer
					.valueOf(prePMTCTStr);

			String testPMTTCTStr = req.getParameter("testPMTCT");
			Integer testPMTCT = testPMTTCTStr == null ? null : Integer
					.valueOf(testPMTTCTStr);

			String postPMTCTStr = req.getParameter("postPMTCT");
			Integer postPMTCT = postPMTCTStr == null ? null : Integer
					.valueOf(postPMTCTStr);

			// Dispatch: invoke action
			if ("quick".equals(action)) {
				registrationService.registerNurse("Nurse Name",
						nursePhoneNumber, "A Clinic");

				registrationService.registerMother(nursePhoneNumber,
						new Date(), serialId, name, community, location,
						dateOfBirth, nhis, patientPhoneNumber, dueDate, parity,
						hemoglobin);

				registrationService.recordMaternalVisit(nursePhoneNumber,
						new Date(), serialId, 0, 0, 0, 1, 0, 0, 0, 0, 0);
			} else if ("nurse".equals(action)) {
				registrationService.registerNurse(name, nursePhoneNumber,
						clinic);
			} else if ("patient".equals(action)) {
				registrationService.registerPatient(nursePhoneNumber, serialId,
						name, community, location, dateOfBirth, Gender
								.valueOf(gender), nhis, patientPhoneNumber);
			} else if ("pregnancy".equals(action)) {
				registrationService.registerPregnancy(nursePhoneNumber,
						regDate, serialId, dueDate, parity, hemoglobin);
			} else if ("maternalvisit".equals(action)) {
				registrationService.recordMaternalVisit(nursePhoneNumber,
						visitDate, serialId, tetanus, ipt, itn, visitNumber,
						onARV, prePMTCT, testPMTCT, postPMTCT, hemoglobin);
			} else {
				// If no action is set, forward to dataview
				req.setAttribute("allNurses", registrationService.getNurses());
				req.setAttribute("allPatients", registrationService
						.getPatients());
				req.setAttribute("allPregnancies", registrationService
						.getPregnancies());
				req.setAttribute("allMaternalVisits", registrationService
						.getMaternalVisits());
				req.setAttribute("allFutureServiceDeliveries",
						registrationService.getFutureServiceDeliveries());
				req.setAttribute("allLogs", registrationService.getLogs());

				req.getRequestDispatcher("/viewdata.jsp").forward(req, resp);

				return;
			}

			// Dispatch: redirect to self without params - sends to view
			resp.sendRedirect(req.getContextPath() + req.getServletPath());

		} catch (Exception e) {

			// Find the root cause
			Throwable t = e;
			while (t.getCause() != null)
				t = t.getCause();

			// Log the message
			String logMsg = "Failed action: " + action + " - " + t.getMessage();
			log.error(logMsg, e);

			// Save error message and forward to originating page
			req.setAttribute("errorMsg", "Error: " + t.getClass().getName()
					+ " - " + t.getMessage());
			req.getRequestDispatcher(errorUrl).forward(req, resp);
		}
	}
}
