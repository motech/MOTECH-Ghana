<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<head>
	<meta name="heading" content="View Data" />
	<style>
		table.wideresults td,th {
			white-space: nowrap;
		}
		.results table td,th {
			padding-left: 2px;
			padding-right: 2px;
		}
		.results table {
			margin-top: 2px;
			margin-bottom: 5px;
		}
		.results h3 {
			margin-bottom: .5em;
			padding-bottom: 2px;
			border-bottom: 1px solid black;
		}
		div.results {
			margin-bottom: .5em;
		}
	</style>
</head>
<h2>View Database Contents</h2>
<div class="instructions">
	This section allows you to inspect some pertinent information from the 
	database. It is provided as a diagnostic tool for verifying the results
	of the various test operations.
</div>

<c:set var="noResultMsg" value="No results found." />

<f:view>
	<div class="results">
	<h3>Clinics</h3>
	<c:choose>
		<c:when test="${not empty registrationServlet.clinics}">
			<h:dataTable value="#{registrationServlet.clinics}" var="clinic">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{clinic.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Name" />
					</f:facet>
					<h:outputText value="#{clinic.name}" />
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>

	<div class="results">
	<h3>Nurses</h3>
	<c:choose>
		<c:when test="${not empty registrationServlet.nurses}">
			<h:dataTable value="#{registrationServlet.nurses}" var="nurse">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{nurse.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Name" />
					</f:facet>
					<h:outputText value="#{nurse.name}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Phone" />
					</f:facet>
					<h:outputText value="#{nurse.phoneNumber}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Clinic Id" />
					</f:facet>
					<h:outputText value="#{nurse.clinic.id}" />
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>

	<div class="results">
	<h3>Patients</h3>
	<c:choose>
		<c:when test="${not empty registrationServlet.patients}">
			<h:dataTable value="#{registrationServlet.patients}" var="patient"
				styleClass="results">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{patient.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Name" />
					</f:facet>
					<h:outputText value="#{patient.name}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Serial" />
					</f:facet>
					<h:outputText value="#{patient.serial}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Clinic Id" />
					</f:facet>
					<h:outputText value="#{patient.clinic.id}" />
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>

	<div class="results">
	<h3>Pregnancies</h3>
	<c:choose>
		<c:when test="${not empty registrationServlet.pregnancies}">
			<h:dataTable value="#{registrationServlet.pregnancies}"
				var="pregnancy" styleClass="results">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{pregnancy.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Patient Id" />
					</f:facet>
					<h:outputText value="#{pregnancy.maternalData.patient.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Due Date" />
					</f:facet>
					<h:outputText value="#{pregnancy.dueDate}">
						<f:convertDateTime type="date" timeZone="America/New_York" />
					</h:outputText>
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>

	<div class="results">
	<h3>Maternal Visits</h3>
	<c:choose>
		<c:when test="${not empty registrationServlet.maternalVisits}">
			<h:dataTable value="#{registrationServlet.maternalVisits}"
				var="maternalVisit" styleClass="results">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{maternalVisit.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Date" />
					</f:facet>
					<h:outputText value="#{maternalVisit.date}">
						<f:convertDateTime type="date" timeZone="America/New_York" />
					</h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Patient Id" />
					</f:facet>
					<h:outputText value="#{maternalVisit.maternalData.patient.id}" />
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>

	<div class="results">
	<h3>Future Service Deliveries</h3>
	<c:choose>
		<c:when
			test="${not empty registrationServlet.futureServiceDeliveries}">
			<h:dataTable value="#{registrationServlet.futureServiceDeliveries}"
				var="service" styleClass="results wideresults">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{service.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Date" />
					</f:facet>
					<h:outputText value="#{service.date}">
						<f:convertDateTime type="both" timeZone="America/New_York" />
					</h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Patient Id" />
					</f:facet>
					<h:outputText value="#{service.patient.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Nurse Id" />
					</f:facet>
					<h:outputText value="#{service.nurse.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Service" />
					</f:facet>
					<h:outputText value="#{service.service}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Patient Notified" />
					</f:facet>
					<h:outputText value="#{service.patientNotifiedDate}">
						<f:convertDateTime type="both" timeZone="America/New_York" />
					</h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Nurse Notified" />
					</f:facet>
					<h:outputText value="#{service.nurseNotifiedDate}">
						<f:convertDateTime type="both" timeZone="America/New_York" />
					</h:outputText>
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>

	<div class="results">
	<h3>Logs</h3>
	<c:choose>
		<c:when test="${not empty registrationServlet.logs}">
			<h:dataTable value="#{registrationServlet.logs}" var="log"
				styleClass="wideresults">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Id" />
					</f:facet>
					<h:outputText value="#{log.id}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Type" />
					</f:facet>
					<h:outputText value="#{log.type}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Date" />
					</f:facet>
					<h:outputText value="#{log.date}">
						<f:convertDateTime type="both" timeZone="America/New_York" />
					</h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Message" />
					</f:facet>
					<h:outputText value="#{log.message}" />
				</h:column>
			</h:dataTable>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose></div>
</f:view>