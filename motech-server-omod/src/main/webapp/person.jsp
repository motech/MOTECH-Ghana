<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/person.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRMotechService.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/find_duplicates.js" />

<meta name="heading" content="Register Person" />
<%@ include file="localHeader.jsp" %>
<h2>Register Person</h2>
<div class="instructions">
	This form allows you to create a new non-patient record, 
	including optional enrollment in the pregnant parents information service.
</div>
<form:form method="post" modelAttribute="person" onsubmit="return confirmRegistrationOnMatches()">
<span style="color:green;">
	<spring:message code="${successMsg}" text="" />
</span>
<form:errors cssClass="error" />
<table>
	<tr>
		<td><label for="whyInterested">Why Interested in Service:</label></td>
		<td>
			<form:select path="whyInterested">
				<form:option value="" label="Select Value" />
				<form:option value="IN_HOUSEHOLD_PREGNANCY" label="Family member in household currently pregnant" />
				<form:option value="OUT_HOUSEHOLD_PREGNANCY" label="Family member outside of household currently pregnant" />
				<form:option value="IN_HOUSEHOLD_BIRTH" label="Family member in household recently had new baby" />
			</form:select>
		</td>
		<td><form:errors path="whyInterested" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="firstName">First Name:</label></td>
		<td><form:input path="firstName" onchange="findDuplicatesForPerson()" /></td>
		<td><form:errors path="firstName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="middleName">Middle Name:</label></td>
		<td><form:input path="middleName" /></td>
		<td><form:errors path="middleName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="lastName">Last Name:</label></td>
		<td><form:input path="lastName" onchange="findDuplicatesForPerson()" /></td>
		<td><form:errors path="lastName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="prefName">Preferred Name:</label></td>
		<td><form:input path="prefName" /></td>
		<td><form:errors path="prefName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="birthDate">Date of Birth (DD/MM/YYYY):</label></td>
		<td><form:input path="birthDate" onchange="findDuplicatesForPerson()" /></td>
		<td><form:errors path="birthDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="birthDateEst">Estimated Date of Birth:</label></td>
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
		<td><label for="sex">Sex:</label></td>
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
		<td><label for="region">Region:</label></td>
		<td>
			<form:select path="region">
				<form:option value="" label="Select Value" />
				<form:options items="${regions}" itemValue="name" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="region" cssClass="error" /></td>
	</tr>
		<tr>
		<td><label for="district">District:</label></td>
		<td>
			<form:select path="district">
				<form:option value="" label="Select Value" />
				<form:options items="${districts}" itemValue="name" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="district" cssClass="error" /></td>
	</tr>
		<tr>
		<td><label for="community">Community:</label></td>
		<td>
			<form:select path="community" onchange="findDuplicatesForPerson()">
				<form:option value="" label="Select Value" />
				<form:options items="${communities}" itemValue="name" itemLabel="name" />
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
				<form:option value="" label="Select Value" />
				<form:options items="${clinics}" itemValue="locationId" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="clinic" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="registerPregProgram">Register in Pregnant Parents Info Service:</label></td>
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
		<td><label for="termsConsent">Info Service Terms Consent:</label></td>
		<td><form:checkbox path="termsConsent" /></td>
		<td><form:errors path="termsConsent" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="messagesStartWeek">Week to begin messages:</label></td>
		<td>
			<form:select path="messagesStartWeek">
				<form:option value="" label="Select Value" />
				<c:forEach var="i" begin="4" end="40">
					<form:option value="${i}" label="Pregnancy week ${i}" />
				</c:forEach>
				<c:forEach var="i" begin="1" end="4">
					<form:option value="${i + 40}" label="Newborn week ${i}" />
				</c:forEach>
			</form:select>
		</td>
		<td><form:errors path="messagesStartWeek" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="primaryPhone">Primary Phone Number:</label></td>
		<td><form:input path="primaryPhone" onchange="findDuplicatesForPerson()" /></td>
		<td><form:errors path="primaryPhone" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="primaryPhoneType">Primary Phone Ownership:</label></td>
		<td>
			<form:select path="primaryPhoneType">
				<form:option value="" label="Select Value" />
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
				<form:option value="" label="Select Value" />
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
				<form:option value="" label="Select Value" />
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
				<form:option value="" label="Select Value" />
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
				<form:option value="" label="Select Value" />
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
				<form:option value="" label="Select Value" />
				<form:option value="en" label="English" />
				<form:option value="kas" label="Kassim" />
				<form:option value="nan" label="Nankam" />
			</form:select>
		</td>
		<td><form:errors path="languageText" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="howLearned">How Learned of Service:</form:label></td>
		<td><form:input path="howLearned" /></td>
		<td><form:errors path="howLearned" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="religion">Religion:</label></td>
		<td><form:input path="religion" /></td>
		<td><form:errors path="religion" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="occupation">Occupation:</label></td>
		<td><form:input path="occupation" /></td>
		<td><form:errors path="occupation" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form:form>
<div id="matchingPatientsSection" style="color:red;display:none;">
	<h3>Conflicting Patients</h3>
	<table id="matchingPatients">
		<thead>
			<tr>
				<th>MoTeCH ID</th>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Birth Date</th>
				<th>Community</th>
				<th>Primary Phone</th>
				<th>Secondary Phone</th>
			</tr>
		</thead>
		<tbody id="matchingPatientsBody" />
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>