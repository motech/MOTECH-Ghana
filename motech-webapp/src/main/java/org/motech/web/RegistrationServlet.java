package org.motech.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.svc.Registrar;

public class RegistrationServlet extends HttpServlet {

	private static final long serialVersionUID = 4561954777101725182L;

	private static Log log = LogFactory.getLog(RegistrationServlet.class);

	@EJB
	Registrar registrationService;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String nursePhone = req.getParameter("nursePhone");
			String serialId = req.getParameter("serialId");
			String name = req.getParameter("name");
			String community = req.getParameter("community");
			String location = req.getParameter("location");
			Integer nhis = Integer.valueOf(req.getParameter("nhis"));
			String patientPhone = req.getParameter("patientPhone");

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date dueDate = dateFormat.parse(req.getParameter("dueDate"));

			Integer age = Integer.valueOf(req.getParameter("age"));
			Integer parity = Integer.valueOf(req.getParameter("parity"));
			Integer hemoglobin = Integer
					.valueOf(req.getParameter("hemoglobin"));

			registrationService.registerNurse("Nurse Name", nursePhone,
					"A Clinic");

			registrationService.registerMother(nursePhone, new Date(),
					serialId, name, community, location, age, nhis,
					patientPhone, dueDate, parity, hemoglobin);
		} catch (Exception e) {
			log.error("Failed to register", e);
			throw new ServletException("Failed to register", e);
		}
	}
}
