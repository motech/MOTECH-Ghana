<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View MoTeCH Data" otherwise="/login.htm" redirect="/module/motechmodule/search.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Search" />
<%@ include file="localHeader.jsp" %>
<h2>Search</h2>
<div class="instructions">
	This form allows you search for people in the database.
</div>
<form:form method="post" modelAttribute="patient">
<table>
	<tr>
		<td><form:label path="firstName">First Name:</form:label></td>
		<td><form:input path="firstName" /></td>
		<td><form:errors path="firstName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="lastName">Last Name:</form:label></td>
		<td><form:input path="lastName" /></td>
		<td><form:errors path="lastName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="birthDate">Date of Birth (DD/MM/YYYY):</form:label></td>
		<td><form:input path="birthDate" /></td>
		<td><form:errors path="birthDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="regNumberGHS">GHS Registration Number:</form:label></td>
		<td><form:input path="regNumberGHS" /></td>
		<td><form:errors path="regNumberGHS" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="nhis">NHIS Number:</form:label></td>
		<td><form:input path="nhis" /></td>
		<td><form:errors path="nhis" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="community">Community:</form:label></td>
		<td>
			<form:select path="community">
				<form:option value="" label="" />
				<form:options items="${communities}" itemValue="name" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="community" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="primaryPhone">Phone Number:</form:label></td>
		<td><form:input path="primaryPhone" /></td>
		<td><form:errors path="primaryPhone" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" />
		</td>
	</tr>
</table>
</form:form>

<div>
	<h3>Matches</h3>
	<table>
		<thead>
			<tr>
				<th>Id</th>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Birth Date</th>
				<th>Community</th>
				<th>Reg Number</th>
				<th>NHIS Number</th>
				<th>Primary Phone</th>
				<th>Secondary Phone</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${matchingPeople}" var="webPatient">
				<tr>
					<td>${webPatient.id}</td>
					<td>${webPatient.firstName}</td>
					<td>${webPatient.lastName}</td>
					<td><openmrs:formatDate date="${webPatient.birthDate}" format="dd/MM/yyyy" /></td>
					<td>${webPatient.community}</td>
					<td>${webPatient.regNumberGHS}</td>
					<td>${webPatient.nhis}</td>
					<td>${webPatient.primaryPhone}</td>
					<td>${webPatient.secondaryPhone}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>