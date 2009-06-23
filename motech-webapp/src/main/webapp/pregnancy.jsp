<meta name="heading" content="Pregnancy Registration" />
<h2>Register a Pregnancy</h2>
<form action="${pageContext.request.contextPath}/regTest" method="post">
<table>
	<input type="hidden" name="testAction" value="pregnancy" />
	<tr>
		<td><label for="nursePhone">Nurse Phone:</label></td>
		<td><input name="nursePhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="regDate">Registration Date:</label></td>
		<td><input name="regDate" value="01/01/2001" /></td>
	</tr>
	<tr>
		<td><label for="serialId">Serial Id:</label></td>
		<td><input name="serialId" value="FGH4894894" /></td>
	</tr>
	<tr>
		<td><label for="dueDate">Due Date:</label></td>
		<td><input name="dueDate" value="08/01/2009" /></td>
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