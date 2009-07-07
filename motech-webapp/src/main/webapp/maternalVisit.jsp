<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<meta name="heading" content="Record Maternal Visit" />
<h2>Record a Maternal Visit Event</h2>
<div class="instructions">
	This test allows you to create a maternal visit record. A
	maternal visit contains information about observed conditions,
	administered diagnostics and delivered services such as 
	immunizations during a single visit. 
	<em>
		NOTE: A nurse with the specified phone and a patient with 
		the specified serial id must already exist.
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

			<h:outputLabel for="maternalVisit_date">Date of Visit:</h:outputLabel>
			<h:inputText id="maternalVisit_date"
				value="#{registrationServlet.maternalVisit.date}">
				<f:convertDateTime type="date" dateStyle="short" timeZone="America/New_York" />
			</h:inputText>

			<h:outputLabel for="maternalVisit_tetanus">Tetanus:</h:outputLabel>
			<h:inputText id="maternalVisit_tetatnus"
				value="#{registrationServlet.maternalVisit.tetanus}"></h:inputText>

			<h:outputLabel for="maternalVisit_ipt">IPT:</h:outputLabel>
			<h:inputText id="maternalVisit_ipt"
				value="#{registrationServlet.maternalVisit.ipt}"></h:inputText>

			<h:outputLabel for="maternalVisit_itn">ITN:</h:outputLabel>
			<h:inputText id="maternalVisit_itn"
				value="#{registrationServlet.maternalVisit.itn}"></h:inputText>

			<h:outputLabel for="maternalVisit_visitNumber">Visit Number:</h:outputLabel>
			<h:inputText id="maternalVisit_visitNumber"
				value="#{registrationServlet.maternalVisit.visitNumber}"></h:inputText>

			<h:outputLabel for="maternalVisit_onARV">ARV:</h:outputLabel>
			<h:inputText id="maternalVisit_onARV"
				value="#{registrationServlet.maternalVisit.onARV}"></h:inputText>

			<h:outputLabel for="maternalVisit_prePMTCT">Pre PMTCT:</h:outputLabel>
			<h:inputText id="maternalVisit_prePMTCT"
				value="#{registrationServlet.maternalVisit.prePMTCT}"></h:inputText>
				
			<h:outputLabel for="maternalVisit_testPMTCT">Test PMTCT:</h:outputLabel>
			<h:inputText id="maternalVisit_testPMTCT"
				value="#{registrationServlet.maternalVisit.testPMTCT}"></h:inputText>
				
			<h:outputLabel for="maternalVisit_postPMTCT">Post PMTCT:</h:outputLabel>
			<h:inputText id="maternalVisit_postPMTCT"
				value="#{registrationServlet.maternalVisit.postPMTCT}"></h:inputText>

			<h:outputLabel for="maternalVisit_hemoglobin36">Week 36 Hemoglobin:</h:outputLabel>
			<h:inputText id="maternalVisit_hemoglobin36"
				value="#{registrationServlet.maternalVisit.hemoglobinAt36Weeks}"></h:inputText>
		</h:panelGrid>
		<h:commandButton value="Register"
			action="#{registrationServlet.regMaternalVisit}"></h:commandButton>
		<h:messages />
	</h:form>
</f:view>