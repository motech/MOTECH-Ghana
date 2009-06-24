<meta name="heading" content="Quick Test" />
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
	<em>NOTE: A nurse phone needs to be unique.</em>
</div>
<form action="${pageContext.request.contextPath}/regTest" method="post">
<table>
	<input type="hidden" name="testAction" value="quick" />
	<tr>
		<td><label for="nursePhone">Nurse Phone:</label></td>
		<td><input name="nursePhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="serialId">Serial Id:</label></td>
		<td><input name="serialId" value="FGH4894894" /></td>
	</tr>
	<tr>
		<td><label for="name">Name:</label></td>
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