<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/editpatient.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

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
<table>
	<tr>
		<td><label for="firstName">First Name:</label></td>
		<td><form:input path="firstName" /></td>
		<td><form:errors path="firstName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="lastName">Last Name:</label></td>
		<td><form:input path="lastName" /></td>
		<td><form:errors path="lastName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="prefName">Preferred Name:</label></td>
		<td><form:input path="prefName" /></td>
		<td><form:errors path="prefName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="birthDate">Date of Birth (DD/MM/YYYY):</label></td>
		<td><form:input path="birthDate" /></td>
		<td><form:errors path="birthDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="birthDateEst">Estimated Date of Birth:</label></td>
		<td>
			<form:select path="birthDateEst">
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="birthDateEst" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="sex">Sex:</label></td>
		<td>
			<form:select path="sex">
				<form:option value="FEMALE" label="Female" />
				<form:option value="MALE" label="Male" />
			</form:select>
		</td>
		<td><form:errors path="sex" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="registeredGHS">Registered with GHS:</label></td>
		<td>
			<form:select path="registeredGHS">
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="registeredGHS" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="regNumberGHS">GHS Registration Number:</label></td>
		<td><form:input path="regNumberGHS" /></td>
		<td><form:errors path="regNumberGHS" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="insured">Insured:</label></td>
		<td>
			<form:select path="insured">
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="insured" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="nhis">NHIS Number:</label></td>
		<td><form:input path="nhis" /></td>
		<td><form:errors path="nhis" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="nhisExpDate">NHIS Expiration Date (DD/MM/YYYY):</label></td>
		<td><form:input path="nhisExpDate" /></td>
		<td><form:errors path="nhisExpDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="region">Region:</label></td>
		<td>
			<form:select path="region">
				<form:options items="${locations}" itemValue="name" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="region" cssClass="error" /></td>
	</tr>
		<tr>
		<td><label for="district">District:</label></td>
		<td>
			<form:select path="district">
				<form:options items="${locations}" itemValue="name" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="district" cssClass="error" /></td>
	</tr>
		<tr>
		<td><label for="community">Community:</label></td>
		<td>
			<form:select path="community">
				<form:options items="${locations}" itemValue="name" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="community" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="address">Address:</label></td>
		<td><form:input path="address" /></td>
		<td><form:errors path="address" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="clinic">Nearest Clinic:</label></td>
		<td>
			<form:select path="clinic">
				<form:options items="${locations}" itemValue="locationId" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="clinic" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="primaryPhone">Primary Phone Number:</label></td>
		<td><form:input path="primaryPhone" /></td>
		<td><form:errors path="primaryPhone" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="primaryPhoneType">Primary Phone Ownership:</label></td>
		<td>
			<form:select path="primaryPhoneType">
				<form:option value="" label="" />
				<form:option value="PERSONAL" label="Personal phone" />
				<form:option value="HOUSEHOLD" label="Owned by household" />
				<form:option value="PUBLIC" label="Public phone" />
			</form:select>
		</td>
		<td><form:errors path="primaryPhoneType" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="secondaryPhone">Secondary Phone Number:</label></td>
		<td><form:input path="secondaryPhone" /></td>
		<td><form:errors path="secondaryPhone" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="secondaryPhoneType">Secondary Phone Ownership:</label></td>
		<td>
			<form:select path="secondaryPhoneType">
				<form:option value="" label="" />
				<form:option value="PERSONAL" label="Personal phone" />
				<form:option value="HOUSEHOLD" label="Owned by household" />
				<form:option value="PUBLIC" label="Public phone" />
			</form:select>
		</td>
		<td><form:errors path="secondaryPhoneType" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="mediaTypeInfo">Weekly Info Message Format:</label></td>
		<td>
			<form:select path="mediaTypeInfo">
				<form:option value="" label="" />
				<form:option value="TEXT" label="Text" />
				<form:option value="VOICE" label="Voice" />
			</form:select>
		</td>
		<td><form:errors path="mediaTypeInfo" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="mediaTypeReminder">Notification/Reminder Format:</label></td>
		<td>
			<form:select path="mediaTypeReminder">
				<form:option value="" label="" />
				<form:option value="TEXT" label="Text" />
				<form:option value="VOICE" label="Voice" />
			</form:select>
		</td>
		<td><form:errors path="mediaTypeReminder" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="languageVoice">Language for Voice Messages:</label></td>
		<td>
			<form:select path="languageVoice">
				<form:option value="" label="" />
				<form:option value="en" label="English" />
				<form:option value="kas" label="Kassim" />
				<form:option value="nan" label="Nankam" />
			</form:select>
		</td>
		<td><form:errors path="languageVoice" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="languageText">Language for Text Messages:</label></td>
		<td>
			<form:select path="languageText">
				<form:option value="" label="" />
				<form:option value="en" label="English" />
				<form:option value="kas" label="Kassim" />
				<form:option value="nan" label="Nankam" />
			</form:select>
		</td>
		<td><form:errors path="languageText" cssClass="error" /></td>
	</tr>
	
	<tr>
		<td><label for="whoRegistered">Who Registered:</label></td>
		<td>
			<form:select path="whoRegistered">
				<form:option value="" label="" />
				<form:option value="MOTHER" label="Mother" />
				<form:option value="FATHER" label="Father" />
				<form:option value="FAMILY_MEMBER" label="Family member" />
				<form:option value="CHPS_STAFF" label="CHPS staff" />
				<form:option value="OTHER" label="Other" />
			</form:select>
		</td>
		<td><form:errors path="whoRegistered" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" />
		</td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>