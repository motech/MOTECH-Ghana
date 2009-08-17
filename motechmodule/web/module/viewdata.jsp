<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>View Database Contents</h2>
<div class="instructions">
	This section allows you to inspect some pertinent information from the 
	database. It is provided as a diagnostic tool for verifying the results
	of the various test operations.
</div>

<c:set var="noResultMsg" value="No results found." />

<div class="results">
	<h3>Clinics</h3>
	<c:choose>
		<c:when test="${not empty allClinics}">
		<table><tr><th>Id</th><th>Name</th></tr>
		<c:forEach items="${allClinics}" var="clinic">
			<tr><td>${clinic.id}</td><td>${clinic.name}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div class="results">
	<h3>Nurses</h3>
	<c:choose>
		<c:when test="${not empty allNurses}">
		<table><tr><th>Id</th><th>First Name</th><th>Last Name</th><th>Username</th></tr>
		<c:forEach items="${allNurses}" var="nurse">
			<tr><td>${nurse.id}</td><td>${nurse.givenName}</td><td>${nurse.familyName}</td><td>${nurse.username}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div class="results">
	<h3>Patients</h3>
	<c:choose>
		<c:when test="${not empty allPatients}">
		<table class="results"><tr><th>Id</th><th>First Name</th><th>Last Name</th><th>Serial</th><th>Clinic</th></tr>
		<c:forEach items="${allPatients}" var="patient">
			<tr><td>${patient.id}</td><td>${patient.givenName}</td><td>${patient.familyName}</td><td>${patient.patientIdentifier.identifier}</td><td>${patient.patientIdentifier.location.name}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div class="results">
	<h3>Pregnancies</h3>
	<c:choose>
		<c:when test="${not empty allPregnancies}">
		<table class="results"><tr><th>Id</th><th>Registration Date</th><th>Patient Id</th></tr>
		<c:forEach items="${allPregnancies}" var="pregnancy">
			<tr><td>${pregnancy.id}</td><td><openmrs:formatDate date="${pregnancy.encounterDatetime}"/></td><td>${pregnancy.patient.id}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div class="results">
	<h3>Maternal Visits</h3>
	<c:choose>
		<c:when test="${not empty allMaternalVisits}">
		<table class="results"><tr><th>Id</th><th>Date</th><th>Patient Id</th></tr>
		<c:forEach items="${allMaternalVisits}" var="matVisit">
			<tr><td>${matVisit.id}</td><td><openmrs:formatDate date="${matVisit.encounterDatetime}"/></td><td>${matVisit.patient.id}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div class="results">
	<h3>Future Service Deliveries</h3>
	<c:choose>
		<c:when test="${not empty allFutureServiceDeliveries}">
		<table class="results wideresults"><tr><th>Id</th><th>Date</th><th>Patient Id</th><th>Nurse Id</th><th>Service</th><th>Patient Notified</th><th>Nurse Notified</th></tr>
		<c:forEach items="${allFutureServiceDeliveries}" var="service">
			<tr><td>${service.id}</td><td><openmrs:formatDate type="both" date="${service.date}"/></td><td>${service.patient.id}</td><td>${service.user.id}</td>
			<td>${service.service.name.name}</td><td><openmrs:formatDate type="both" date="${service.patientNotifiedDate}"/></td>
			<td><openmrs:formatDate type="both" date="${service.userNotifiedDate}"/></td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div class="results">
	<h3>Logs</h3>
	<c:choose>
		<c:when test="${not empty allLogs}">
		<table class="wideresults"><tr><th>Id</th><th>Type</th><th>Date</th><th>Message</th></tr>
		<c:forEach items="${allLogs}" var="log">
			<tr><td>${log.id}</td><td>${log.type}</td><td><openmrs:formatDate type="both" date="${log.date}"/></td><td>${log.message}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>