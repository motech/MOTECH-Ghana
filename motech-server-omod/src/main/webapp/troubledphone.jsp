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
<openmrs:require privilege="Manage MoTeCH Troubled Phones" otherwise="/login.htm" redirect="/module/motechmodule/troubledphone.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Troubled Phones" />
<%@ include file="localHeader.jsp" %>
<h2>Manage Troubled Phones</h2>
<div class="instructions">
	Here you can lookup phone numbers that are considered 'troubled' to the
	system. This happens when a number is repeatedly found to not work for 
	whatever reason. This is essentially a messaging blacklist.
</div>
<form method="get">
<table>
	<tr>
		<td><label for="phoneNumber">Phone #:</label></td>
		<td><input name="phoneNumber" value="${troubledPhone.phoneNumber}" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>

<c:if test="${not empty troubledPhone}">
<table>
	<tr>
		<th>Phone #</th>
		<th>Added At</th>
		<th></th>
	</tr>
	<c:url value="" var="delUrl">
		<c:param name="phoneNumber" value="${troubledPhone.phoneNumber}" />
		<c:param name="remove" value="true" />
	</c:url>
	<tr>
		<td>${troubledPhone.phoneNumber}</td>
		<td>${troubledPhone.creationTime}</td>
		<td><a href="${delUrl}">Remove</a></td>
	</tr>
</table>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>