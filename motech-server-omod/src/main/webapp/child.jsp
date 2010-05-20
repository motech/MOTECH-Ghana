<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/child.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRMotechService.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/find_duplicates.js" />

<meta name="heading" content="Child Registration" />
<%@ include file="localHeader.jsp" %>
<h2>Register Child</h2>
<div class="instructions">
	This form allows you to create a new child patient record,
	including optional enroll the patient in the pregnant 
	parents information service.
</div>
<form:form method="post" modelAttribute="child" onsubmit="return confirmRegistrationOnMatches()">
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
		<td><label for="firstName">First Name:</label></td>
		<td><form:input path="firstName" onchange="findDuplicates()" /></td>
		<td><form:errors path="firstName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="middleName">Middle Name:</label></td>
		<td><form:input path="middleName" /></td>
		<td><form:errors path="middleName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="lastName">Last Name:</label></td>
		<td><form:input path="lastName" onchange="findDuplicates()" /></td>
		<td><form:errors path="lastName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="prefName">Preferred Name:</label></td>
		<td><form:input path="prefName" /></td>
		<td><form:errors path="prefName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="birthDate">Date of Birth (DD/MM/YYYY):</label></td>
		<td><form:input path="birthDate" onchange="findDuplicates()" /></td>
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
		<td><label for="motherMotechId">Mother's MoTeCH ID:</label></td>
		<td><form:input path="motherMotechId" /></td>
		<td><form:errors path="motherMotechId" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="registeredGHS">Registered with GHS:</label></td>
		<td>
			<form:select path="registeredGHS">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="registeredGHS" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="regNumberGHS">GHS CWC Registration Number:</label></td>
		<td><form:input path="regNumberGHS" onchange="findDuplicates()" /></td>
		<td><form:errors path="regNumberGHS" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="insured">Insured:</label></td>
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
		<td><label for="nhis">NHIS Number:</label></td>
		<td><form:input path="nhis" onchange="findDuplicates()" /></td>
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
				<form:option value="" label="Select Value" />
				<form:options items="${regions}" />
			</form:select>
		</td>
		<td><form:errors path="region" cssClass="error" /></td>
	</tr>
	<tr>
		<td><label for="district">District:</label></td>
		<td>
			<form:select path="district">
				<form:option value="" label="Select Value" />
				<form:options items="${districts}" />
			</form:select>
		</td>
		<td><form:errors path="district" cssClass="error" /></td>
	</tr>
		<tr>
		<td><label for="community">Community:</label></td>
		<td>
			<form:select path="community" onchange="findDuplicates()">
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
		<td><label for="registerPregProgram">Register in Healthy Children Info Service:</label></td>
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
				<th>Reg Number</th>
				<th>NHIS Number</th>
				<th>Phone Number</th>
			</tr>
		</thead>
		<tbody id="matchingPatientsBody" />
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>