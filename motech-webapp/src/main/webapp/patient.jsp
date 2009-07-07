<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<meta name="heading" content="Patient Registration" />
<h2>Register a Patient</h2>
<div class="instructions">
	This test allows you to create a new patient record. A patient record 
	contains basic demographic information about a patient and is intended as
	a common link to a nurse, patient episodes (such as pregnancies), visit 
	records, notification events and more.
	<em>
		NOTE: A nurse must already exist with the specified
		phone, and the patient serialid must be unique to the
		nurse's clinic.
	</em>
</div>
<f:view>
	<h:form>
		<h:panelGrid columns="2">
			<h:outputLabel for="nurse_phone">Nurse Phone:</h:outputLabel>
			<h:inputText id="nurse_phone"
				value="#{registrationServlet.nurse.phoneNumber}"></h:inputText>

			<h:outputLabel for="patient_serial">Serial Id:</h:outputLabel>
			<h:inputText id="patient_serial"
				value="#{registrationServlet.patient.serial}"></h:inputText>

			<h:outputLabel for="patient_name">Name:</h:outputLabel>
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

			<h:outputLabel for="patient_phone">Phone:</h:outputLabel>
			<h:inputText id="patient_phone"
				value="#{registrationServlet.patient.phoneNumber}"></h:inputText>

			<h:outputLabel for="patient_dob">Date of Birth:</h:outputLabel>
			<h:inputText id="patient_dob"
				value="#{registrationServlet.patient.dateOfBirth}">
				<f:convertDateTime type="date" dateStyle="short" timeZone="America/New_York" />
			</h:inputText>

			<h:outputLabel for="patient_gender">Gender:</h:outputLabel>
			<h:selectOneMenu id="patient_gender" value="#{registrationServlet.patient.gender}">
				<f:selectItems value="#{registrationServlet.genders}" />
			</h:selectOneMenu>
		</h:panelGrid>
		<h:commandButton value="Register"
			action="#{registrationServlet.regPatient}"></h:commandButton>
		<h:messages />
	</h:form>
</f:view>