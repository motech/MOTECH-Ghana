<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Register MoTeCH Facility" otherwise="/login.htm" redirect="/module/motechmodule/editfacility.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Edit Facility" />
<%@ include file="localHeader.jsp" %>
<h2>Edit a Facility</h2>
<div class="instructions">
	Edit facility attributes and click submit to save.
</div>
<form:form method="post" modelAttribute="facility">
<span style="color:green;">
	<spring:message code="${successMsg}" text="" />
</span>
<form:errors cssClass="error" />
<fieldset>
<legend>Facility Update</legend>
<table>
	<tr>
		<td>Facility Id:</td>
		<td>${facility.facilityId}</td>
	</tr>
	<tr>
		<td>Facility Name:</td>
		<td>${facility.location.neighborhoodCell}</td>
	</tr>
	<tr>
		<td><form:label path="phoneNumber">Phone Number:</form:label></td>
		<td><form:input path="phoneNumber" maxlength="50"/></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>