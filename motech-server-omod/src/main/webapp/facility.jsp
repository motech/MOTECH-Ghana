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