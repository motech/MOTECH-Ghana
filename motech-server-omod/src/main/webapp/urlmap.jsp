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
<form:form method="post" modelAttribute="messageProcessorURL"
           action="${pageContext.request.contextPath}/module/motechmodule/urlmap/edit.form">
    <table cellpadding="5" cellspacing="0" border="1">
        <tr>
            <td colspan="2" align="right">
                <a href="${pageContext.request.contextPath}/module/motechmodule/urlmap/add.form">
                    <spring:message code="motechmodule.url.map.add"/>
                </a>
            </td>
        </tr>
        <tr>
            <td align="center"><h4>Key</h4></td>
            <td align="center"><h4>URL</h4></td>
        </tr>
        <c:forEach items="${mappedURLs.urls}" var="url" varStatus="pStatus">
            <tr class="evenRow">
                <td align="center">
                    ${url.key}
                </td>
                <td align="center">
                    ${url.url}
                </td>
            </tr>
        </c:forEach>
        <c:if test="${addNew}">
            <tr>
                <td><form:input path="key"/></td>
                <td><form:input path="url" size="40" maxlength="50"/></td>
            </tr>
            <tr>
            <td>&nbsp;</td>
            <td align="right"><input type="submit" value="Submit"/></td>
        </tr>
        </c:if>
    </table>
</form:form>
