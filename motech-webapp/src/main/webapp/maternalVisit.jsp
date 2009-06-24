<meta name="heading" content="Record Maternal Visit" />
<h2>Record a Maternal Visit Event</h2>
<form action="${pageContext.request.contextPath}/regTest" method="post">
<div class="instructions">
	This test allows you to create a maternal visit record. A
	maternal visit contains information about observed conditions,
	administered diagnostics and delivered services such as 
	immunizations during a single visit. 
	<em>
		NOTE: A nurse with the specified phone and a patient with 
		the specified serial id must already exist.
	</em>
</div>
<table>
	<input type="hidden" name="testAction" value="maternalvisit" />
	<tr>
		<td><label for="nursePhone">Nurse Phone:</label></td>
		<td><input name="nursePhone" value="5555555555" /></td>
	</tr>
	<tr>
		<td><label for="visitDate">Date of Visit:</label></td>
		<td><input name="visitDate" value="01/01/2007" /></td>
	</tr>
	<tr>
		<td><label for="serialId">Serial Id:</label></td>
		<td><input name="serialId" value="FGH4894894" /></td>
	</tr>
	<tr>
		<td><label for="tetanus">Tetanus:</label></td>
		<td><input name="tetanus" value="47" /></td>
	</tr>
	<tr>
		<td><label for="ipt">IPT:</label></td>
		<td><input name="ipt" value="47" /></td>
	</tr>
	<tr>
		<td><label for="itn">ITN:</label></td>
		<td><input name="itn" value="47" /></td>
	</tr>
	<tr>
		<td><label for="visitNumber">Visit Number:</label></td>
		<td><input name="visitNumber" value="1" /></td>
	</tr>
	<tr>
		<td><label for="onARV">ARV:</label></td>
		<td><input name="onARV" value="47" /></td>
	</tr>
	<tr>
		<td><label for="prePMTCT">prePMTCT:</label></td>
		<td><input name="prePMTCT" value="47" /></td>
	</tr>
	<tr>
		<td><label for="testPMTCT">testPMTCT:</label></td>
		<td><input name="testPMTCT" value="47" /></td>
	</tr>
	<tr>
		<td><label for="postPMTCT">postPMTCT:</label></td>
		<td><input name="postPMTCT" value="47" /></td>
	</tr>
	<tr>
		<td><label for="hemoglobin">Hemoglobin:</label></td>
		<td><input name="hemoglobin" value="4" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>