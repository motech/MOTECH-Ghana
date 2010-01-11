<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm" redirect="/module/motechmodule/mother.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Register Pregnant Mother" />
<%@ include file="localHeader.jsp" %>
<h2>Register Pregnant Mother</h2>
<div class="instructions">
	This form allows you to create a new pregnant mother patient record, 
	including pregnancy information and optionally enroll the patient
	in the pregnant parents information service.
</div>
<form method="post">
<table>
	<tr>
		<td><label for="firstName">First Name:</label></td>
		<td><input name="firstName" value="FirstName" /></td>
	</tr>
	<tr>
		<td><label for="lastName">Last Name:</label></td>
		<td><input name="lastName" value="LastName" /></td>
	</tr>
	<tr>
		<td><label for="prefName">Preferred Name:</label></td>
		<td><input name="prefName" value="PreferredName" /></td>
	</tr>
	<tr>
		<td><label for="birthDate">Date of Birth (DD/MM/YYYY):</label></td>
		<td><input name="birthDate" value="30/08/1983" /></td>
	</tr>
	<tr>
		<td><label for="birthDateEst">Estimated Date of Birth:</label></td>
		<td>
			<select name="birthDateEst">
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="registeredGHS">Registered with GHS:</label></td>
		<td>
			<select name="registeredGHS">
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="regNumberGHS">GHS ANC Registration Number:</label></td>
		<td><input name="regNumberGHS" value="ABC123" /></td>
	</tr>
	<tr>
		<td><label for="insured">Insured:</label></td>
		<td>
			<select name="insured">
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="nhis">NHIS Number:</label></td>
		<td><input name="nhis" value="DEF456" /></td>
	</tr>
	<tr>
		<td><label for="nhisExpDate">NHIS Expiration Date (DD/MM/YYYY):</label></td>
		<td><input name="nhisExpDate" value="15/05/2011" /></td>
	</tr>
	<tr>
		<td><label for="region">Region:</label></td>
		<td>
			<select name="region">
				<c:forEach items="${regions}" var="location">
					<option value="${location.name}">${location.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
		<tr>
		<td><label for="district">District:</label></td>
		<td>
			<select name="district">
				<c:forEach items="${districts}" var="location">
					<option value="${location.name}">${location.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
		<tr>
		<td><label for="community">Community:</label></td>
		<td>
			<select name="community">
				<c:forEach items="${communities}" var="location">
					<option value="${location.name}">${location.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="address">Address:</label></td>
		<td><input name="address" value="House on left" /></td>
	</tr>
	<tr>
		<td><label for="clinic">Nearest Clinic:</label></td>
		<td>
			<select name="clinic">
				<c:forEach items="${clinics}" var="location">
					<option value="${location.locationId}">${location.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="dueDate">Expected Delivery Date (DD/MM/YYYY):</label></td>
		<td><input name="dueDate" value="20/04/2010" /></td>
	</tr>
	<tr>
		<td><label for="dueDateConfirmed">Delivery Date confirmed by CHW:</label></td>
		<td>
			<select name="dueDateConfirmed">
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="gravida">Previous Pregnancies (gravida):</label></td>
		<td><input name="gravida" value="2" /></td>
	</tr>
	<tr>
		<td><label for="parity">Previous Births (parity):</label></td>
		<td><input name="parity" value="1" /></td>
	</tr>
	<tr>
		<td><label for="hivStatus">HIV Status:</label></td>
		<td>
			<select name="hivStatus">
				<option value="POSITIVE">Positive</option>
				<option value="NEGATIVE">Negative</option>
				<option value="UNKNOWN">Unknown</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="registerPregProgram">Register in Pregnant Parents Info Service:</label></td>
		<td>
			<select name="registerPregProgram">
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="primaryPhone">Primary Phone Number:</label></td>
		<td><input name="primaryPhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="primaryPhoneType">Primary Phone Ownership:</label></td>
		<td>
			<select name="primaryPhoneType">
				<option value="PERSONAL">Personal phone</option>
				<option value="HOUSEHOLD">Owned by household</option>
				<option value="PUBLIC">Public phone</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="secondaryPhone">Secondary Phone Number:</label></td>
		<td><input name="secondaryPhone" value="5555555556" /></td>
	</tr>
	<tr>
		<td><label for="secondaryPhoneType">Secondary Phone Ownership:</label></td>
		<td>
			<select name="secondaryPhoneType">
				<option value="PERSONAL">Personal phone</option>
				<option value="HOUSEHOLD">Owned by household</option>
				<option value="PUBLIC">Public phone</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="mediaTypeInfo">Weekly Info Message Format:</label></td>
		<td>
			<select name="mediaTypeInfo">
				<option value="TEXT">Text</option>
				<option value="VOICE">Voice</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="mediaTypeReminder">Notification/Reminder Format:</label></td>
		<td>
			<select name="mediaTypeReminder">
				<option value="TEXT">Text</option>
				<option value="VOICE">Voice</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="languageVoice">Language for Voice Messages:</label></td>
		<td>
			<select name="languageVoice">
				<option value="en">English</option>
				<option value="kas">Kassim</option>
				<option value="nan">Nankam</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="languageText">Language for Text Messages:</label></td>
		<td>
			<select name="languageText">
				<option value="en">English</option>
				<option value="kas">Kassim</option>
				<option value="nan">Nankam</option>
			</select>
		</td>
	</tr>
	
	<tr>
		<td><label for="whoRegistered">Who Registered:</label></td>
		<td>
			<select name="whoRegistered">
				<option value="MOTHER">Mother</option>
				<option value="FATHER">Father</option>
				<option value="FAMILY_MEMBER">Family member</option>
				<option value="CHPS_STAFF">CHPS staff</option>
				<option value="OTHER">Other</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="religion">Religion:</label></td>
		<td><input name="religion" value="Unspecified" /></td>
	</tr>
	<tr>
		<td><label for="occupation">Occupation:</label></td>
		<td><input name="occupation" value="Unspecified" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>