<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) ${year} The Trustees of Columbia University in the City of
    New York and Grameen Foundation USA.  All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

    3. Neither the name of Grameen Foundation USA, Columbia University, or
    their respective contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
    AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
    FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
    USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
    OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
    EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>

<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View MoTeCH Data" otherwise="/login.htm" redirect="/module/motechmodule/viewdata.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="View Data" />
<%@ include file="localHeader.jsp" %>
<h2>View Database Contents</h2>
<div class="instructions">
	This section allows you to inspect some pertinent information from the 
	database. It is provided as a diagnostic tool for verifying the results
	of the various test operations.
</div>

<c:set var="noResultMsg" value="No results found." />

<div>
	<h3>Locations</h3>
	<c:choose>
		<c:when test="${not empty allLocations}">
		<table><tr><th>Id</th><th>Name</th></tr>
		<c:forEach items="${allLocations}" var="location">
			<tr><td>${location.locationId}</td><td>${location.name}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Staff</h3>
	<c:choose>
		<c:when test="${not empty allStaff}">
		<table><tr><th>Id</th><th>First Name</th><th>Last Name</th><th>Staff Id</th></tr>
		<c:forEach items="${allStaff}" var="staff">
			<tr><td>${staff.userId}</td><td>${staff.givenName}</td><td>${staff.familyName}</td><td>${staff.systemId}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Patients</h3>
	<c:choose>
		<c:when test="${not empty allPatients}">
		<table><tr><th>Id</th><th>First Name</th><th>Last Name</th><th>Motech Id</th><th>Clinic</th><th></th><th></th></tr>
		<c:forEach items="${allPatients}" var="patient">
			<tr><td>${patient.patientId}</td><td>${patient.givenName}</td><td>${patient.familyName}</td><td>${patient.patientIdentifier.identifier}</td><td>${patient.patientIdentifier.location.name}</td><td><a href="editpatient.form?id=${patient.patientId}">Edit</a></td><td><a href="pregnancy.form?id=${patient.patientId}">Add Pregnancy</a></td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Pregnancies</h3>
	<c:choose>
		<c:when test="${not empty allPregnancies}">
		<table><tr><th>Id</th><th>Registration Date</th><th>Patient Id</th></tr>
		<c:forEach items="${allPregnancies}" var="pregnancy">
			<tr><td>${pregnancy.obsId}</td><td><openmrs:formatDate date="${pregnancy.obsDatetime}"/></td><td>${pregnancy.personId}</td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<div>
	<h3>Scheduled Messages</h3>
	<c:choose>
		<c:when test="${not empty allScheduledMessages}">
		<table><tr><th>Id</th><th>Date</th></tr>
		<c:forEach items="${allScheduledMessages}" var="message">
			<tr><td>${message.id}</td><td><openmrs:formatDate type="both" date="${message.scheduledFor}"/></td></tr>
		</c:forEach>
		</table>
		</c:when>
		<c:otherwise>${noResultMsg}</c:otherwise>
	</c:choose>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>