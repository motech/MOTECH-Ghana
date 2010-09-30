<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Register MoTeCH Communities" otherwise="/login.htm"
                 redirect="/module/motechmodule/community.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="Community"/>
<%@ include file="localHeader.jsp" %>

<h2> Add a new Community:</h2>
<c:url value="/module/motechmodule/community/submit.form" var="submitAction" />
<form:form method="post" modelAttribute="community" action="${submitAction}">
    <form:errors cssClass="error"/>
    <fieldset>
        <legend>Add New Community</legend>
    <table>
    <tr>
        <td><form:label path="name">Name :</form:label></td>
        <td><form:input path="name"/></td>
        <td><form:errors path="name"/></td>
    </tr>
    <tr>
        <td><form:label path="facilityId">Facility</form:label></td>
        <td>
            <form:select path="facilityId">
                <c:forEach items="${facilities}" var="facility">
                    <form:option value="${facility.facilityId}"> ${facility.location.neighborhoodCell} </form:option>
                </c:forEach>
            </form:select>
        </td>
    </tr>
    <tr>
        <td>
            <input type="submit" value="Submit">
        </td>
    </tr>
</table>

</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>