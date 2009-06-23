<meta name="heading" content="Nurse Registration" />
<h2>Register a Nurse</h2>
<form action="${pageContext.request.contextPath}/regTest" method="post">
<table>
	<input type="hidden" name="testAction" value="nurse" />
	<tr>
		<td><label for="name">Name:</label></td>
		<td><input name="name" value="Nurse Name" /></td>
	</tr>
	<tr>
		<td><label for="nursePhone">Nurse Phone:</label></td>
		<td><input name="nursePhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="clinic">Clinic:</label></td>
		<td><input name="clinic" value="A-Clinic" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>