<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Troubled Phones" />
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
	<tr>
		<td>${troubledPhone.phoneNumber}</td>
		<td>${troubledPhone.creationTime}</td>
		<td><a href="?phoneNumber=${troubledPhone.phoneNumber}&remove=true">Remove</a></td>
	</tr>
</table>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>