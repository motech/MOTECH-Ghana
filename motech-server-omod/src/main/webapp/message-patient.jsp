<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/message-patient.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Send Message to Patient" />
<%@ include file="demoLocalHeader.jsp" %>
<h2>Send Message to Patient</h2>
<div class="instructions">
	This form allows you to send a message (text or voice) to a patient.
</div>
<form:form method="post" modelAttribute="message">
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
		<td><label for="phoneNumber">Phone Number:</label></td>
		<td><form:input path="phoneNumber" /></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>		
	</tr>
	<tr>
		<td><label for="phoneType">Phone Ownership:</label></td>
		<td>
			<form:select path="phoneType">
				<form:option value="" label="Select Value" />
				<form:option value="PERSONAL" label="Personal phone" />
				<form:option value="HOUSEHOLD" label="Owned by household" />
				<form:option value="PUBLIC" label="Public phone" />
			</form:select>
		</td>
		<td><form:errors path="phoneType" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="mediaType">Message Format:</label></td>
		<td>
			<form:select path="mediaType">
				<form:option value="" label="Select Value" />
				<form:option value="TEXT" label="Text" />
				<form:option value="VOICE" label="Voice" />
			</form:select>
		</td>
		<td><form:errors path="mediaType" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="language">Language:</label></td>
		<td>
			<form:select path="language">
				<form:option value="" label="Select Value" />
				<form:option value="en" label="English" />
				<form:option value="kas" label="Kassim" />
				<form:option value="nan" label="Nankam" />
			</form:select>
		</td>
		<td><form:errors path="language" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="notificationType">Notification Type:</label></td>
		<td>
			<form:select path="notificationType">
				<form:option value="" label="Select Value" />
				<form:options items="${notificationTypes}" itemValue="publicId" itemLabel="messageKey" />
			</form:select>
		</td>
		<td><form:errors path="notificationType" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="startDate">Start Date and Time (yyyy-MM-dd HH:mm:ss):</label></td>
		<td><form:input path="startDate" /></td>
		<td><form:errors path="startDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="endDate">End Date and Time (yyyy-MM-dd HH:mm:ss):</label></td>
		<td><form:input path="endDate" /></td>
		<td><form:errors path="endDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>