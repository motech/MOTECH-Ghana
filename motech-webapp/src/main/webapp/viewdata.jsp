<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<head>
	<meta name="heading" content="View Data" />
	<style>
		table.wideresults td,th {
			white-space: nowrap;
		}
	</style>
</head>
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

<h3>Future Service Deliveries</h3>
<table class="wideresults"><tr><th>Id</th><th>Date</th><th>Patient Id</th><th>Nurse Id</th><th>Service</th><th>Patient Notified</th><th>Nurse Notified</th></tr>
<c:forEach items="${allFutureServiceDeliveries}" var="service">
	<tr><td>${service.id}</td><td><fmt:formatDate type="both" value="${service.date}"/></td><td>${service.patient.id}</td><td>${service.nurse.id}</td>
	<td>${service.service}</td><td><fmt:formatDate type="both" value="${service.patientNotifiedDate}"/></td>
	<td><fmt:formatDate type="both" value="${service.nurseNotifiedDate}"/></td></tr>
</c:forEach>
</table>

<h3>Logs</h3>
<table class="wideresults"><tr><th>Id</th><th>Type</th><th>Date</th><th>Message</th></tr>
<c:forEach items="${allLogs}" var="log">
	<tr><td>${log.id}</td><td>${log.type}</td><td><fmt:formatDate type="both" value="${log.date}"/></td><td>${log.message}</td></tr>
</c:forEach>
</table>