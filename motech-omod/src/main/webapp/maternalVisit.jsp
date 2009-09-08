<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Record Maternal Visit" />
<h2>Record a Maternal Visit Event</h2>
<form method="post">
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
		<td>
			<select name="tetanus">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="ipt">IPT:</label></td>
		<td>
			<select name="ipt">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="itn">ITN:</label></td>
		<td>
			<select name="itn">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="visitNumber">Visit Number:</label></td>
		<td><input name="visitNumber" value="1" /></td>
	</tr>
	<tr>
		<td><label for="onARV">ARV:</label></td>
		<td>
			<select name="onARV">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="prePMTCT">prePMTCT:</label></td>
		<td>
			<select name="prePMTCT">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="testPMTCT">testPMTCT:</label></td>
		<td>
			<select name="testPMTCT">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
	</tr>
	<tr>
		<td><label for="postPMTCT">postPMTCT:</label></td>
		<td>
			<select name="postPMTCT">
				<option value="true">True</option>
				<option value="false">False</option>
			</select>
		</td>
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

<%@ include file="/WEB-INF/template/footer.jsp"%>