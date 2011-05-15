<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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
		<td>${facility.location.name}</td>
	</tr>
	<tr>
		<td><form:label path="phoneNumber">Phone Number:</form:label></td>
		<td><form:input path="phoneNumber" maxlength="50"/></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>
	</tr>
    <tr>
		<td><form:label path="additionalPhoneNumber1">Additional Phone Number1:</form:label></td>
		<td><form:input path="additionalPhoneNumber1" maxlength="50"/></td>
		<td><form:errors path="additionalPhoneNumber1" cssClass="error" /></td>
	</tr>
     <tr>
		<td><form:label path="additionalPhoneNumber2">Additional Phone Number2:</form:label></td>
		<td><form:input path="additionalPhoneNumber2" maxlength="50"/></td>
		<td><form:errors path="additionalPhoneNumber2" cssClass="error" /></td>
	</tr>
     <tr>
		<td><form:label path="additionalPhoneNumber3">Additional Phone Number3:</form:label></td>
		<td><form:input path="additionalPhoneNumber3" maxlength="50"/></td>
		<td><form:errors path="additionalPhoneNumber3" cssClass="error" /></td>
	</tr>
     <tr>
		<td><form:label path="additionalPhoneNumber4">Additional Phone Number4:</form:label></td>
		<td><form:input path="additionalPhoneNumber4" maxlength="50"/></td>
		<td><form:errors path="additionalPhoneNumber4" cssClass="error" /></td>
	</tr>

	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>