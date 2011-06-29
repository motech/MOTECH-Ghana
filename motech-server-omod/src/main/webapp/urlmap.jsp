<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Send SMS" otherwise="/login.htm"
                 redirect="/module/motechmodule/sms.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="URL Map"/>
<%@ include file="localHeader.jsp" %>

<h3>URL Map</h3>
<%--<c:if test="${response != null}">--%>
<%--<span style="color:${response.success ? 'green' : 'red'}">${response.text}</span>--%>
<%--</c:if>--%>

<p></p>
<form:form method="post" modelAttribute="mappedURLs">
    <table cellpadding="4" cellspacing="0">
        <c:forEach items="${mappedURLs.urls}" var="url" varStatus="pStatus">
            <tr class="<c:choose><c:when test="${pStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
                <td>
                    <form:input path="urls[${pStatus.index}].key" readonly="true"/>
                </td>
                <td>
                    <form:input path="urls[${pStatus.index}].url" size="50" readonly="true"/>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="Submit"/></td>
        </tr>
    </table>
</form:form>
