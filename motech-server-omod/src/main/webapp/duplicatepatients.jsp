<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Duplicate Patients" otherwise="/login.htm" redirect="/module/motechmodule/demo.htm"/>
<%@ include file="/WEB-INF/template/header.jsp" %>
<meta name="heading" content="Duplicate Patients"/>
<%@ include file="localHeader.jsp" %>

<c:if test="${empty duplicate_patients}">
    There are no duplicate patients
</c:if>

<c:if test="${fn:length(duplicate_patients) gt 0}">
<c:url value="/module/motechmodule/duplicatepatients.form" var="submitAction"/>
<form:form method="post" modelAttribute="webDuplicatePatients" action="${submitAction}">
    <c:forEach items="${duplicate_patients}" var="patient">
        <tr>
            <td>
                <form:checkbox path="uuid" value="${patient.uuid}"/>${patient.givenName}
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td>
            <input type="submit" value="Submit">
        </td>
    </tr>
</form:form>
</c:if>

