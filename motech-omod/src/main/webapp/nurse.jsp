<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2>Register a Nurse</h2>
<div class="instructions">
	This test allows you to create a new nurse record. A nurse is
	associated with an existing clinic.
	<em>
		NOTE: A clinic must already exist with the specified name 
		and a nurse phone needs to be unique.
	</em>
</div>
<form method="post">
<table>
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

<%@ include file="/WEB-INF/template/footer.jsp"%>