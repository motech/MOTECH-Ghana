<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>	
<meta name="heading" content="Pregnancy Registration" />
<h2>Register a Pregnancy</h2>
<div class="instructions">
	This allows you to create a new pregnancy record. A pregnacy record is, in
	essence, a case record. It is initiated when a nurse becomes aware of a new
	pregnancy. It, along with associated maternal visits, records information 
	about communication and service delivery during a pregnancy episode.
	<em>
		NOTE: A nurse must already exist with the specified phone, and
		a patient must exist with the specified serial id.
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

			<h:outputLabel for="pregnancy_regdate">Registration Date:</h:outputLabel>
			<h:inputText id="pregnancy_regdate"
				value="#{registrationServlet.pregnancy.registrationDate}">
				<f:convertDateTime type="date" dateStyle="short" timeZone="America/New_York" />
			</h:inputText>

			<h:outputLabel for="pregnancy_duedate">Due Date:</h:outputLabel>
			<h:inputText id="pregnancy_duedate"
				value="#{registrationServlet.pregnancy.dueDate}">
				<f:convertDateTime type="date" dateStyle="short" timeZone="America/New_York" />
			</h:inputText>

			<h:outputLabel for="pregnancy_parity">Parity:</h:outputLabel>
			<h:inputText id="pregnancy_parity"
				value="#{registrationServlet.pregnancy.parity}"></h:inputText>

			<h:outputLabel for="pregnancy_hemoglobin">Hemoglobin:</h:outputLabel>
			<h:inputText id="pregnancy_hemoglobin"
				value="#{registrationServlet.pregnancy.hemoglobin}"></h:inputText>
		</h:panelGrid>
		<h:commandButton value="Register"
			action="#{registrationServlet.regPregnancy}"></h:commandButton>
		<h:messages />
	</h:form>
</f:view>