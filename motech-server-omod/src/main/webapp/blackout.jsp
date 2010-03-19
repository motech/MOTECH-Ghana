<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage MoTeCH Blackout" otherwise="/login.htm" redirect="/module/motechmodule/blackout.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Blackout Settings" />
<%@ include file="localHeader.jsp" %>
<h2>Configure Blackout Periods</h2>
<div class="instructions">
	Configures a system wide time when messages cannot be sent. The system wide 
	sleep time is to ensure that we never push messages that are set to be sent 
	'immediately' or retry failed messages during intrusive hours of the day 
	(late at night, early morning).
</div>
<form method="post">
<table>
	<tr>
		<td><label for="startTime">Start Time:</label></td>
		<td><input name="startTime" value="${startTime}" /></td>
	</tr>
	<tr>
		<td><label for="endTime">End Time:</label></td>
		<td><input name="endTime" value="${endTime}" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>