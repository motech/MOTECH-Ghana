<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2010 The Trustees of Columbia University in the City of
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