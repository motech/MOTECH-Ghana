<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View MoTeCH Data" otherwise="/login.htm" redirect="/module/motechmodule/viewdata.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="View Data" />
<%@ include file="localHeader.jsp" %>
<h2>View Database Contents</h2>
<div class="instructions">
	This section allows you to inspect some pertinent information from the 
	database. It is provided as a diagnostic tool for verifying the results
	of the various test operations.
</div>

<c:set var="noResultMsg" value="No results found." />

<div>
	<h3>Clinics</h3>
	<c:choose>
		<c:when test="${not empty allClinics}">
		<table><tr><th>Id</th><th>Name</th></tr>
		<c:forEach items="${allClinics}" var="clinic">
			<tr><td>${clinic.locationId}</td><td>${clinic.name}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Nurses</h3>
	<c:choose>
		<c:when test="${not empty allNurses}">
		<table><tr><th>Id</th><th>First Name</th><th>Last Name</th><th>Username</th></tr>
		<c:forEach items="${allNurses}" var="nurse">
			<tr><td>${nurse.userId}</td><td>${nurse.givenName}</td><td>${nurse.familyName}</td><td>${nurse.username}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Patients</h3>
	<c:choose>
		<c:when test="${not empty allPatients}">
		<table><tr><th>Id</th><th>First Name</th><th>Last Name</th><th>Serial</th><th>Clinic</th></tr>
		<c:forEach items="${allPatients}" var="patient">
			<tr><td>${patient.patientId}</td><td>${patient.givenName}</td><td>${patient.familyName}</td><td>${patient.patientIdentifier.identifier}</td><td>${patient.patientIdentifier.location.name}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Pregnancies</h3>
	<c:choose>
		<c:when test="${not empty allPregnancies}">
		<table><tr><th>Id</th><th>Registration Date</th><th>Patient Id</th></tr>
		<c:forEach items="${allPregnancies}" var="pregnancy">
			<tr><td>${pregnancy.encounterId}</td><td><openmrs:formatDate date="${pregnancy.encounterDatetime}"/></td><td>${pregnancy.patientId}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Maternal Visits</h3>
	<c:choose>
		<c:when test="${not empty allMaternalVisits}">
		<table><tr><th>Id</th><th>Date</th><th>Patient Id</th></tr>
		<c:forEach items="${allMaternalVisits}" var="matVisit">
			<tr><td>${matVisit.encounterId}</td><td><openmrs:formatDate date="${matVisit.encounterDatetime}"/></td><td>${matVisit.patientId}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Scheduled Messages</h3>
	<c:choose>
		<c:when test="${not empty allScheduledMessages}">
		<table><tr><th>Id</th><th>Date</th></tr>
		<c:forEach items="${allScheduledMessages}" var="message">
			<tr><td>${message.id}</td><td><openmrs:formatDate type="both" date="${message.scheduledFor}"/></td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Logs</h3>
	<c:choose>
		<c:when test="${not empty allLogs}">
		<table><tr><th>Id</th><th>Type</th><th>Date</th><th>Message</th></tr>
		<c:forEach items="${allLogs}" var="log">
			<tr><td>${log.id}</td><td>${log.type}</td><td><openmrs:formatDate type="both" date="${log.date}"/></td><td>${log.message}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>