<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Pregnancy" otherwise="/login.htm" redirect="/module/motechmodule/pregnancy.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Pregnancy Registration" />
<%@ include file="localHeader.jsp" %>
<h2>Register a Pregnancy</h2>
<div class="instructions">
	This allows you to create a new pregnancy record. A pregnacy record is, in
	essence, a case record. It is initiated when a nurse becomes aware of a new
	pregnancy. It, along with associated maternal visits, records information 
	about communication and service delivery during a pregnancy episode.
</div>
<form:form method="post" modelAttribute="pregnancy">
<div style="padding-top:5px; padding-bottom:5px;">
	<form:errors cssClass="error" />
</div>
<table>
	<tr>
		<td><form:label path="termsConsent">Info Service Terms Consent:</form:label></td>
		<td><form:checkbox path="termsConsent" /></td>
		<td><form:errors path="termsConsent" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="dueDate">Expected Delivery Date (DD/MM/YYYY):</form:label></td>
		<td><form:input path="dueDate" /></td>
		<td><form:errors path="dueDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="dueDateConfirmed">Delivery Date confirmed by CHW:</form:label></td>
		<td>
			<form:select path="dueDateConfirmed">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="dueDateConfirmed" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="registerPregProgram">Register in Pregnant Parents Info Service:</form:label></td>
		<td>
			<form:select path="registerPregProgram">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="registerPregProgram" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="phoneNumber">Phone Number:</label></td>
		<td><form:input path="phoneNumber" onchange="findDuplicates()" /></td>
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
		<td><label for="language">Language for Messages:</label></td>
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
		<td><form:label path="howLearned">How Learned of Service:</form:label></td>
		<td><form:input path="howLearned" /></td>
		<td><form:errors path="howLearned" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>