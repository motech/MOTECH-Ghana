<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<meta name="heading" content="Clinic Registration" />
<h2>Register a Clinic</h2>
<div class="instructions">
	This test allows you to create a new clinic record. Patients and 
	nurses are associated with a specific clinic, and the clinic 
	must be registered prior to nurse or patient registration.
	<em>NOTE: A clinic name needs to be unique.</em>
</div>
<f:view>
	<h:form>
		<h:panelGrid columns="2">
			<h:outputLabel for="clinic_name">Name:</h:outputLabel>
			<h:inputText id="clinic_name"
				value="#{registrationServlet.clinic.name}"></h:inputText>
		</h:panelGrid>
		<h:commandButton value="Register"
			action="#{registrationServlet.regClinic}"></h:commandButton>
		<h:messages />
	</h:form>
</f:view>