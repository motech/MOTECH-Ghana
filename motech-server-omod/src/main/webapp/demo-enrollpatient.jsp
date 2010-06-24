<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/demo-patient.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Enroll Patient in Messaging Program" />
<%@ include file="demoLocalHeader.jsp" %>
<h2>Register Patient in Messaging Program</h2>
<div class="instructions">
	This form allows you to enroll an existing patient into a messaging program
	that is sensitive to sms input. To enroll a patient, simply enter the 
	existing MoTeCH ID of the patient you wish to enroll.
	
	<em>
		NOTE: To stop the program you'll need to enter a tetanus observation
	    for the same patient.
	</em>
</div>
<form:form method="post" modelAttribute="enrollpatient">
<span style="color:green;">
	<spring:message code="${successMsg}" text="" />
</span>
<form:errors cssClass="error" />
<table>
	<tr>
		<td><label for="motechId">MoTeCH ID:</label></td>
		<td><form:input path="motechId" /></td>
		<td><form:errors path="motechId" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="consent">Mobile Midwife Terms Consent:</label></td>
		<td><form:checkbox path="consent" /></td>
		<td><form:errors path="consent" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>