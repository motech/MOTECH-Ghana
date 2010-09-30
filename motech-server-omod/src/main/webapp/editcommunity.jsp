<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Register MoTeCH Communities" otherwise="/login.htm"
                 redirect="/module/motechmodule/editfacility.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="Edit Community"/>
<%@ include file="localHeader.jsp" %>
<h2>Edit a Community</h2>

<div class="instructions">
    Edit community attributes and click submit to save.
</div>

<c:url value="/module/motechmodule/community/submit.form" var="submitAction"/>
<form:form method="post" modelAttribute="community" action="${submitAction}">
    <form:errors cssClass="error"/>
    <fieldset>
        <legend>Edit</legend>
        <table>
            <tr>
                <td><form:label path="communityId">Community Id:</form:label></td>
                <td><form:input path="communityId" disabled="true"/></td>
                <td><form:hidden path="communityId"/></td>
            </tr>
            <tr>
                <td><form:label path="name">Name :</form:label></td>
                <td><form:input path="name"/></td>
                <td><form:errors path="name" cssClass="error"/></td>
            </tr>
            <tr>
                <td><form:label path="facilityId">Facility</form:label></td>
                <td>
                    <form:select path="facilityId">
                        <c:forEach items="${facilities}" var="facility">
                            <form:option
                                    value="${facility.facilityId}">${facility.location.neighborhoodCell}</form:option>
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


<%@ include file="/WEB-INF/template/footer.jsp" %>