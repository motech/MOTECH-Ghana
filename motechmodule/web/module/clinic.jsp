<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Clinic Registration" />
<h2>Register a Clinic</h2>
<div class="instructions">
	This test allows you to create a new clinic record. Patients and 
	nurses are associated with a specific clinic, and the clinic 
	must be registered prior to nurse or patient registration.
	<em>NOTE: A clinic name needs to be unique.</em>
</div>
<form method="post">
<table>
	<tr>
		<td><label for="name">Clinic Name:</label></td>
		<td><input name="name" value="A-Clinic" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>