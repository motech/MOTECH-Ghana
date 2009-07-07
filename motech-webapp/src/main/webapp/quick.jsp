<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<meta name="heading" content="Quick Test" />
<h2>Register Pregnant Woman and Record Maternal Visit</h2>
<div class="instructions">
	The 'quick test' is useful for testing a number of actions all at once.
	It creates:
	<ul>
		<li>A new clinic, nurse, patient, pregnancy, maternal visit, 
			future service delivery record and relationship associations 
			relating these items.
		</li>
		<li>Event log messages for each major action</li>
		<li>
			A notification event for 30 seconds after the maternal visit
			(refresh the data page to see the notification status update)
		</li>
	</ul>
	<em>
		NOTE: A nurse phone and clinic name both need to be unique.
	</em>
</div>
<f:view>
	<h:form>
		<h:panelGrid columns="2">
			<h:outputLabel for="nurse_name">Nurse Name:</h:outputLabel>
			<h:inputText id="nurse_name"
				value="#{registrationServlet.nurse.name}"></h:inputText>

			<h:outputLabel for="nurse_phone">Nurse Phone:</h:outputLabel>
			<h:inputText id="nurse_phone"
				value="#{registrationServlet.nurse.phoneNumber}"></h:inputText>

			<h:outputLabel for="clinic_name">Clinic:</h:outputLabel>
			<h:inputText id="clinic_name"
				value="#{registrationServlet.clinic.name}"></h:inputText>

			<h:outputLabel for="patient_serial">Serial Id:</h:outputLabel>
			<h:inputText id="patient_serial"
				value="#{registrationServlet.patient.serial}"></h:inputText>

			<h:outputLabel for="patient_name">Patient Name:</h:outputLabel>
			<h:inputText id="patient_name"
				value="#{registrationServlet.patient.name}"></h:inputText>

			<h:outputLabel for="patient_community">Community:</h:outputLabel>
			<h:inputText id="patient_community"
				value="#{registrationServlet.patient.community}"></h:inputText>

			<h:outputLabel for="patient_location">Location:</h:outputLabel>
			<h:inputText id="patient_location"
				value="#{registrationServlet.patient.location}"></h:inputText>

			<h:outputLabel for="patient_nhis">NHIS:</h:outputLabel>
			<h:inputText id="patient_nhis"
				value="#{registrationServlet.patient.nhis}"></h:inputText>

			<h:outputLabel for="patient_phone">Patient Phone:</h:outputLabel>
			<h:inputText id="patient_phone"
				value="#{registrationServlet.patient.phoneNumber}"></h:inputText>

			<h:outputLabel for="patient_dob">Date of Birth:</h:outputLabel>
			<h:inputText id="patient_dob"
				value="#{registrationServlet.patient.dateOfBirth}">
				<f:convertDateTime type="date" dateStyle="short" />
			</h:inputText>

			<h:outputLabel for="pregnancy_duedate">Due Date:</h:outputLabel>
			<h:inputText id="pregnancy_duedate"
				value="#{registrationServlet.pregnancy.dueDate}">
				<f:convertDateTime type="date" dateStyle="short" />
			</h:inputText>

			<h:outputLabel for="pregnancy_parity">Parity:</h:outputLabel>
			<h:inputText id="pregnancy_parity"
				value="#{registrationServlet.pregnancy.parity}"></h:inputText>

			<h:outputLabel for="pregnancy_hemoglobin">Hemoglobin:</h:outputLabel>
			<h:inputText id="pregnancy_hemoglobin"
				value="#{registrationServlet.pregnancy.hemoglobin}"></h:inputText>
		</h:panelGrid>
		<h:commandButton value="Register"
			action="#{registrationServlet.quick}"></h:commandButton>
		<h:messages />
	</h:form>
</f:view>