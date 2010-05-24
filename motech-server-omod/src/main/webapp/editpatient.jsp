<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/editpatient.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/motechmodule/patientform.css" />

<meta name="heading" content="Edit Patient" />
<%@ include file="localHeader.jsp" %>
<h2>Edit Patient</h2>
<div class="instructions">
	This form allows you to edit an existing patient record.
</div>
<form:form method="post" modelAttribute="patient">
<span style="color:green;">
	<spring:message code="${successMsg}" text="" />
</span>
<form:errors cssClass="error" />
<fieldset><legend>Patient Information</legend>
<table>
	<tr>
		<td class="labelcolumn"><label for="firstName">First Name:</label></td>
		<td><form:input path="firstName" maxlength="50" /></td>
		<td><form:errors path="firstName" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="middleName">Middle Name:</label></td>
		<td><form:input path="middleName" maxlength="50" /></td>
		<td><form:errors path="middleName" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="lastName">Last Name:</label></td>
		<td><form:input path="lastName" maxlength="50" /></td>
		<td><form:errors path="lastName" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="prefName">Preferred Name:</label></td>
		<td><form:input path="prefName" maxlength="50" /></td>
		<td><form:errors path="prefName" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="birthDate">Date of Birth (DD/MM/YYYY):</label></td>
		<td><form:input path="birthDate" /></td>
		<td><form:errors path="birthDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="birthDateEst">Estimated Date of Birth:</label></td>
		<td>
			<form:select path="birthDateEst">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="birthDateEst" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="sex">Sex:</label></td>
		<td>
			<form:select path="sex">
				<form:option value="" label="Select Value" />
				<form:option value="FEMALE" label="Female"/>
				<form:option value="MALE" label="Male"/>
			</form:select>
		</td>
		<td><form:errors path="sex" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="insured">Insured:</label></td>
		<td>
			<form:select path="insured">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="insured" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="nhis">NHIS Number:</label></td>
		<td><form:input path="nhis" maxlength="50" /></td>
		<td><form:errors path="nhis" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="nhisExpDate">NHIS Expiration Date (DD/MM/YYYY):</label></td>
		<td><form:input path="nhisExpDate" /></td>
		<td><form:errors path="nhisExpDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="region">Region:</label></td>
		<td>
			<form:select path="region">
				<form:option value="" label="Select Value" />
				<form:options items="${regions}" />
			</form:select>
		</td>
		<td><form:errors path="region" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="district">District:</label></td>
		<td>
			<form:select path="district">
				<form:option value="" label="Select Value" />
				<form:options items="${districts}" />
			</form:select>
		</td>
		<td><form:errors path="district" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="communityId">Community:</label></td>
		<td>
			<form:select path="communityId">
				<form:option value="" label="Select Value" />
				<form:options items="${communities}" itemValue="communityId" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="communityId" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="address">Address/household:</label></td>
		<td><form:input path="address" maxlength="50" /></td>
		<td><form:errors path="address" cssClass="error" /></td>
	</tr>
</table>
</fieldset>
<fieldset><legend>Pregnancy Information</legend>
<table>
	<tr>
		<td class="labelcolumn"><label for="dueDate">Expected Delivery Date (DD/MM/YYYY):</label></td>
		<td><form:input path="dueDate" /></td>
		<td><form:errors path="dueDate" cssClass="error" /></td>
	</tr>
</table>
</fieldset>
<fieldset><legend>Enrollment Information</legend>
<table>
	<tr>
		<td class="labelcolumn"><label for="enroll">Registered in Info Service:</label></td>
		<td>
			<form:select path="enroll">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="enroll" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="consent">Registrant has heard consent text and has consented to terms of enrollment:</label></td>
		<td><form:checkbox path="consent" /></td>
		<td><form:errors path="consent" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="phoneNumber">Phone Number:</label></td>
		<td><form:input path="phoneNumber" maxlength="50" /></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="phoneType">Phone Ownership:</label></td>
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
		<td class="labelcolumn"><label for="mediaType">Message Format:</label></td>
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
		<td class="labelcolumn"><label for="language">Language for Messages:</label></td>
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
		<td class="labelcolumn"><label for="dayOfWeek">Day of week to receive messages:</label></td>
		<td>
			<form:select path="dayOfWeek">
				<form:option value="" label="Select Value" />
				<form:option value="MONDAY" label="Monday" />
				<form:option value="TUESDAY" label="Tuesday" />
				<form:option value="WEDNESDAY" label="Wednesday" />
				<form:option value="THURSDAY" label="Thursday" />
				<form:option value="FRIDAY" label="Friday" />
				<form:option value="SATURDAY" label="Saturday" />
				<form:option value="SUNDAY" label="Sunday" />
			</form:select>
		</td>
		<td><form:errors path="dayOfWeek" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="timeOfDay">Time of day to receive messages (HH:MM):</label></td>
		<td><form:input path="timeOfDay" /></td>
		<td><form:errors path="timeOfDay" cssClass="error" /></td>
	</tr>
</table>
</fieldset>
<table>
	<tr>
		<td colspan="2">
			<input type="submit" />
		</td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>