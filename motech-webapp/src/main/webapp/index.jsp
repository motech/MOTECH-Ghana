<%@ page import="org.motech.model.*, java.util.*, java.text.SimpleDateFormat" %>
<h1>Register Pregnant Woman</h1>
<form action="${pageContext.request.contextPath}/regTest" method="post">
	<label for="nursePhone">Nurse Phone:</label>
	<input name="nursePhone" />
	
	<label for="serialId">Serial Id:</label>
	<input name="serialId" />
	
	<label for="name">Name:</label>
	<input name="name" />
	
	<label for="community">Community:</label>
	<input name="community" />
	
	<label for="location">Location:</label>
	<input name="location" />
	
	<label for="nhis">NHIS:</label>
	<input name="nhis" />
	
	<label for="patientPhone">Phone:</label>
	<input name="patientPhone" />
	
	<label for="dueDate">Due Date:</label>
	<input name="dueDate" />
	
	<label for="dateOfBirth">Date of Birth:</label>
	<input name="dateOfBirth" />
	
	<label for="parity">Parity:</label>
	<input name="parity" />
	
	<label for="hemoglobin">Hemoglobin:</label>
	<input name="hemoglobin" />
	
	<input type="submit" />

	<!-- 
				String nursePhone = req.getParameter("nursePhone");
			String serialId = req.getParameter("serialId");
			String name = req.getParameter("name");
			String community = req.getParameter("community");
			String location = req.getParameter("location");
			Integer nhis = Integer.valueOf(req.getParameter("nhis"));
			String patientPhone = req.getParameter("patientPhone");

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date dueDate = dateFormat.parse(req.getParameter("dueDate"));

			Date dateOfBirth = dateFormat
					.parse(req.getParameter("dateOfBirth"));
			Integer parity = Integer.valueOf(req.getParameter("parity"));
			Integer hemoglobin = Integer
					.valueOf(req.getParameter("hemoglobin"));
					 -->
</form>
<h1>Nurses</h1>
<% 
List<Nurse> nurses = (List<Nurse>)request.getAttribute("allNurses");
if(nurses != null) {
	out.println("<table><tr><th>Id</th><th>Name</th><th>Phone</th></tr>");	
	for(Nurse n : nurses) {
		out.println("<tr><td>" + n.getId() + "</td><td>" + n.getName() + "</td><td>" + n.getPhoneNumber() + "</td></tr>");
	}
	out.println("</table>");	
}
%>
<h1>Patients</h1>
<% 
List<Patient> patients = (List<Patient>)request.getAttribute("allPatients");
if(patients != null) {
	out.println("<table><tr><th>Id</th><th>Name</th><th>Serial</th></tr>");	
	for(Patient p : patients) {
		out.println("<tr><td>" + p.getId() + "</td><td>" + p.getName() + "</td><td>" + p.getSerial() + "</td></tr>");
	}
	out.println("</table>");	
}
%>
<h1>Pregnancies</h1>
<% 
SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
List<Pregnancy> pregnancies = (List<Pregnancy>)request.getAttribute("allPregnancies");
if(pregnancies != null) {
	out.println("<table><tr><th>Id</th><th>Patient Id</th><th>Due Date</th></tr>");	
	for(Pregnancy p : pregnancies) {
		out.println("<tr><td>" + p.getId() + "</td><td>" + p.getMaternalData().getPatient().getId() + "</td><td>" + dateFormat.format(p.getDueDate()) + "</td></tr>");
	}
	out.println("</table>");	
}
%>
<h1>Maternal Visits</h1>
<% 
List<MaternalVisit> maternalVisits = (List<MaternalVisit>)request.getAttribute("allMaternalVisits");
if(maternalVisits != null) {
	out.println("<table><tr><th>Id</th><th>Date</th><th>Patient Id</th></tr>");	
	for(MaternalVisit m : maternalVisits) {
		out.println("<tr><td>" + m.getId() + "</td><td>" + dateFormat.format(m.getDate()) + "</td><td>" + m.getMaternalData().getPatient().getId() + "</td></tr>");
	}
	out.println("</table>");	
}
%>