<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<meta name="heading" content="View Data" />
<h2>View Database Contents</h2>

<h3>Nurses</h3>
<table><tr><th>Id</th><th>Name</th><th>Phone</th></tr>
<c:forEach items="${allNurses}" var="nurse">
	<tr><td>${nurse.id}</td><td>${nurse.name}</td><td>${nurse.phoneNumber}</td></tr>
</c:forEach>
</table>

<h3>Patients</h3>
<table><tr><th>Id</th><th>Name</th><th>Serial</th></tr>
<c:forEach items="${allPatients}" var="patient">
	<tr><td>${patient.id}</td><td>${patient.name}</td><td>${patient.serial}</td></tr>
</c:forEach>
</table>

<h3>Pregnancies</h3>
<table><tr><th>Id</th><th>Patient Id</th><th>Due Date</th></tr>
<c:forEach items="${allPregnancies}" var="pregnancy">
	<tr><td>${pregnancy.id}</td><td>${pregnancy.maternalData.patient.id}</td><td><fmt:formatDate value="${pregnancy.dueDate}"/></td></tr>
</c:forEach>
</table>

<h3>Maternal Visits</h3>
<table><tr><th>Id</th><th>Date</th><th>Patient Id</th></tr>
<c:forEach items="${allMaternalVisits}" var="matVisit">
	<tr><td>${matVisit.id}</td><td><fmt:formatDate value="${matVisit.date}"/></td><td>${matVisit.maternalData.patient.id}</td></tr>
</c:forEach>
</table>
