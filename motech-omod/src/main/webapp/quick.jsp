<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Run MoTeCH Test" otherwise="/login.htm" redirect="/module/motechmodule/quicktest.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Quick Test" />
<%@ include file="localHeader.jsp" %>
<h2>Register Pregnant Woman and Record Maternal Visit</h2>
<div class="instructions">
	The 'quick test' is useful for testing a number of actions all at once.
	It creates:
	<ul>
		<li>A new clinic, nurse, patient, pregnancy, maternal visit, 
			future service delivery record and relationship associations 
			relating these items.
		</li>
		<li>Event log messages for each major action</li>
		<li>
			A notification event for 30 seconds after the maternal visit
			(refresh the data page to see the notification status update)
		</li>
	</ul>
	<em>
		NOTE: A nurse phone and clinic name both need to be unique.
	</em>
</div>
<form method="post">
<table>
	<tr>
		<td><label for="nurseName">Nurse Name:</label></td>
		<td><input name="nurseName" value="Nurse Name" /></td>
	</tr>
	<tr>
		<td><label for="nurseId">Nurse Id:</label></td>
		<td><input name="nurseId" value="123ABC" /></td>
	</tr>
	<tr>
		<td><label for="nursePhone">Nurse Phone:</label></td>
		<td><input name="nursePhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="clinicName">Clinic Name:</label></td>
		<td><input name="clinicName" value="A Clinic" /></td>
	</tr>
	<tr>
		<td><label for="serialId">Serial Id:</label></td>
		<td><input name="serialId" value="FGH4894894" /></td>
	</tr>
	<tr>
		<td><label for="name">Patient Name:</label></td>
		<td><input name="name" value="Patient Name" /></td>
	</tr>
	<tr>
		<td><label for="community">Community:</label></td>
		<td><input name="community" value="Community" /></td>
	</tr>
	<tr>
		<td><label for="location">Location:</label></td>
		<td><input name="location" value="Location" /></td>
	</tr>
	<tr>
		<td><label for="nhis">NHIS:</label></td>
		<td><input name="nhis" value="478" /></td>
	</tr>
	<tr>
		<td><label for="patientPhone">Phone:</label></td>
		<td><input name="patientPhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="patientPhoneType">Phone Type:</label></td>
		<td>
			<select name="patientPhoneType">
				<option value="PERSONAL">Personal</option>
				<option value="SHARED">Shared</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="language">Language:</label></td>
		<td><input name="language" value="en" /></td>
	</tr>
	<tr>
		<td><label for="mediaType">Preferred Media Type:</label></td>
		<td>
			<select name="mediaType">
				<option value="TEXT">Text</option>
				<option value="VOICE">Voice</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="deliveryTime">Preferred Delivery Time:</label></td>
		<td>
			<select name="deliveryTime">
				<option value="ANYTIME">No Preference</option>
				<option value="MORNING">Morning  9AM-12PM</option>
				<option value="AFTERNOON">Afternoon 1PM-5PM</option>
				<option value="EVENING">Evening 6PM-9PM</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="messagePrograms">Message Program Enrollment:</label></td>
		<td>
			<select name="messagePrograms" multiple="multiple">
				<option value="dailyPregnancy">Day-by-day Pregnancy</option>
				<option value="minuteTetanus">Minute-by-minute Tetanus</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="dueDate">Due Date:</label></td>
		<td><input name="dueDate" value="08/01/2009" /></td>
	</tr>
	<tr>
		<td><label for="dateOfBirth">Date of Birth:</label></td>
		<td><input name="dateOfBirth" value="08/01/1983" /></td>
	</tr>
	<tr>
		<td><label for="parity">Parity:</label></td>
		<td><input name="parity" value="4" /></td>
	</tr>
	<tr>
		<td><label for="hemoglobin">Hemoglobin:</label></td>
		<td><input name="hemoglobin" value="47" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>