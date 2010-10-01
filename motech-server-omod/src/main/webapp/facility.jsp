<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Register MoTeCH Facility" otherwise="/login.htm" redirect="/module/motechmodule/facility.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Facility Maintenance" />
<%@ include file="localHeader.jsp" %>
<h2>Select a Facility</h2>
<div class="instructions">
	Please select a facility to edit. Currently, a limited number of facility
	attributes are editable.
</div>
<span style="color:green;">
	<spring:message code="${successMsg}" text="" />
</span>
<c:url value="/module/motechmodule/addfacility.form" var="addFacility"/>
<h2><a href="${addFacility}">Add a new Facility</a></h2>
<table>
	<thead>
		<tr>
			<th>Facility ID</th>
			<th>Facility Name</th>
			<th>Facility Phone</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${facilities}" var="facility">
			<c:url value="/module/motechmodule/editfacility.form" var="editUrl">
				<c:param name="facilityId" value="${facility.facilityId}" />
			</c:url>
			<tr>
				<td><a href="${editUrl}">${facility.facilityId}</a></td>
				<td><a href="${editUrl}">${facility.location.neighborhoodCell}</a></td>
				<td><a href="${editUrl}">${facility.phoneNumber}</a></td>	
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>