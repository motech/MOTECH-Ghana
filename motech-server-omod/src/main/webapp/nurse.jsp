<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Staff" otherwise="/login.htm" redirect="/module/motechmodule/nurse.form" />
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
	<form method="post">
	<table>
		<tr>
			<td><label for="firstName">First Name:</label></td>
			<td><input name="firstName" value="First Name" maxlength="50" /></td>
		</tr>
		<tr>
			<td><label for="lastName">Last Name:</label></td>
			<td><input name="lastName" value="Last Name" maxlength="50" /></td>
		</tr>
		<tr>
			<td><label for="phone">Phone Number:</label></td>
			<td><input name="phone" value="5555555555" maxlength="50" /></td>
		</tr>
		<tr>
			<td><label for="type">Staff Type:</label></td>
			<td>
				<select name="type">
					<c:forEach items="${staffTypes}" var="staffType">
						<option value="${staffType}">${staffType}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" /></td>
		</tr>
	</table>
	</form>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>