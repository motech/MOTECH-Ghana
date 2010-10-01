<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Register MoTeCH Facility" otherwise="/login.htm" redirect="/module/motechmodule/addfacility.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Add Facility" />
<%@ include file="localHeader.jsp" %>
<h2>Add a New Facility</h2>
<div class="instructions">
	Add facility and click submit to save.
</div>
<form:form method="post" modelAttribute="facility">
<form:errors cssClass="error" />
<fieldset>
<legend>New Facility</legend>
<table>
	<tr>
		<td><form:label path="phoneNumber">Phone Number:</form:label></td>
		<td><form:input path="phoneNumber" maxlength="50"/></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>
	</tr>
    <tr>
        <td><form:label path="uuid"/></td>
        <td>
            <form:select path="uuid">
                <c:forEach items="${locations}" var="location">
                    <form:option value="${location.uuid}">${location.neighborhoodCell}</form:option>
                </c:forEach>
            </form:select>
        </td>
        <td><form:errors path="uuid" cssClass="error" /></td>
    </tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>