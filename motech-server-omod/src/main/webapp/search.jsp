<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View MoTeCH Data" otherwise="/login.htm" redirect="/module/motechmodule/search.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Search" />
<%@ include file="localHeader.jsp" %>
<h2>Search</h2>
<div class="instructions">
	This form allows you search for patients in the database.
</div>
<form:form method="post" modelAttribute="patient">
<table>
	<tr>
		<td><form:label path="motechId">MoTeCH ID:</form:label></td>
		<td><form:input path="motechId" /></td>
		<td><form:errors path="motechId" cssClass="error" /></td>
	</tr>
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
		<td><form:label path="prefName">Preferred Name:</form:label></td>
		<td><form:input path="prefName" /></td>
		<td><form:errors path="prefName" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="birthDate">Date of Birth (DD/MM/YYYY):</form:label></td>
		<td><form:input path="birthDate" /></td>
		<td><form:errors path="birthDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="nhis">NHIS Number:</form:label></td>
		<td><form:input path="nhis" /></td>
		<td><form:errors path="nhis" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="communityId">Community:</form:label></td>
		<td>
			<form:select path="communityId">
				<form:option value="" label="" />
				<form:options items="${communities}" itemValue="communityId" itemLabel="name" />
			</form:select>
		</td>
		<td><form:errors path="communityId" cssClass="error" /></td>
	</tr>
	<tr>
		<td><form:label path="phoneNumber">Phone Number:</form:label></td>
		<td><form:input path="phoneNumber" /></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>
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
				<th>MoTeCH ID</th>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Preferred Name</th>
				<th>Birth Date</th>
				<th>Community</th>
				<th>NHIS Number</th>
				<th>Phone Number</th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${matchingPatients}" var="webPatient">
				<tr>
					<td>${webPatient.motechId}</td>
					<td>${webPatient.firstName}</td>
					<td>${webPatient.lastName}</td>
					<td>${webPatient.prefName}</td>
					<td><openmrs:formatDate date="${webPatient.birthDate}" format="dd/MM/yyyy" /></td>
					<td>${webPatient.communityName}</td>
					<td>${webPatient.nhis}</td>
					<td>${webPatient.phoneNumber}</td>
					<td>
						<a href="editpatient.form?id=${webPatient.id}">Edit</a>
					</td>
					<td>
						<a href="pregnancy.form?id=${webPatient.id}">Add Pregnancy</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>