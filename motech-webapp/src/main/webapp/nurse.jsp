<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<meta name="heading" content="Nurse Registration" />
<h2>Register a Nurse</h2>
<div class="instructions">
	This test allows you to create a new nurse record. A nurse is
	associated with an existing clinic.
	<em>
		NOTE: A clinic must already exist with the specified name 
		and a nurse phone needs to be unique.
	</em>
</div>
<f:view>
	<h:form>
		<h:panelGrid columns="2">
			<h:outputLabel for="nurse_name">Name:</h:outputLabel>
			<h:inputText id="nurse_name"
				value="#{registrationServlet.nurse.name}"></h:inputText>
				
			<h:outputLabel for="nurse_phone">Phone:</h:outputLabel>
			<h:inputText id="nurse_phone"
				value="#{registrationServlet.nurse.phoneNumber}"></h:inputText>
				
			<h:outputLabel for="clinic_name">Clinic:</h:outputLabel>
			<h:inputText id="clinic_name"
				value="#{registrationServlet.clinic.name}"></h:inputText>
		</h:panelGrid>
		<h:commandButton value="Register"
			action="#{registrationServlet.regNurse}"></h:commandButton>
		<h:messages />
	</h:form>
</f:view>