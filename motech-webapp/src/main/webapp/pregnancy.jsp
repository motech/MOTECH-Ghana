<meta name="heading" content="Pregnancy Registration" />
<h2>Register a Pregnancy</h2>
<div class="instructions">
	This allows you to create a new pregnancy record. A pregnacy record is, in
	essence, a case record. It is initiated when a nurse becomes aware of a new
	pregnancy. It, along with associated maternal visits, records information 
	about communication and service delivery during a pregnancy episode.
	<em>
		NOTE: A nurse must already exist with the specified phone, and
		a patient must exist with the specified serial id.
	</em>
</div>
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