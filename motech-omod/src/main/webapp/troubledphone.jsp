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