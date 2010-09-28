<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Communities" otherwise="/login.htm" redirect="/module/motechmodule/community.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Community" />
<%@ include file="localHeader.jsp" %>

<table>
    <tbody>
    <tr>
        <c:url value="/module/motechmodule/community/add.form" var="addCommunity"></c:url>
        <a href="<c:out value="${addCommunity}"></c:out>"> Add new Community</a>
    </tr>
    </tbody>
    <tbody style="border: 2px; border-color: black;">
    <c:forEach items="${communities}" var="community">
        <tr>
            <td><c:out value = "${community.communityId}" /></td>
            <td><c:out value = "${community.name}" /></td>
        </tr>
    </c:forEach>
    </tbody>
</table>


