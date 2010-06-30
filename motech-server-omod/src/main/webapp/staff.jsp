<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Staff" otherwise="/login.htm" redirect="/module/motechmodule/staff.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Staff Registration" />
<%@ include file="localHeader.jsp" %>
<h2>Register a Staff Member</h2>
<c:choose>
	<c:when test="${not empty successMsg}">
	<span style="color:green;">
		<spring:message text="${successMsg}" />
	</span>
	</c:when>
	<c:otherwise>
	<div class="instructions">
		This form allows you register a staff member.
	</div>
	<form:form method="post" modelAttribute="staff">
	<form:errors cssClass="error" />
	<table>
		<tr>
			<td><form:label path="firstName">First Name:</form:label></td>
			<td><form:input path="firstName" maxlength="50" /></td>
			<td><form:errors path="firstName" cssClass="error" /></td>
		</tr>
		<tr>
			<td><form:label path="lastName">Last Name:</form:label></td>
			<td><form:input path="lastName" maxlength="50" /></td>
			<td><form:errors path="lastName" cssClass="error" /></td>
		</tr>
		<tr>
			<td><form:label path="phone">Phone Number:</form:label></td>
			<td><form:input path="phone" maxlength="50" /></td>
			<td><form:errors path="phone" cssClass="error" /></td>
		</tr>
		<tr>
			<td><form:label path="type">Staff Type:</form:label></td>
			<td>
				<form:select path="type">
					<form:option value="" label="Select Value" />
					<form:options items="${staffTypes}" />
				</form:select>
			</td>
			<td><form:errors path="type" cssClass="error" /></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" /></td>
		</tr>
	</table>
	</form:form>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>