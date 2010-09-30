<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Register MoTeCH Communities" otherwise="/login.htm" redirect="/module/motechmodule/community.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<meta name="heading" content="Community" />
<%@ include file="localHeader.jsp" %>

<table>
    <tbody>
    <tr>
        <c:url value="/module/motechmodule/community/add.form" var="addCommunity" />
        <a href="${addCommunity}"> Add new Community</a>
    </tr>
    </tbody>
    <tbody style="border: 2px; border-color: black;">
    <c:forEach items="${communities}" var="community">
        <c:url value="/module/motechmodule/community/editcommunity.form" var="editUrl">
		    <c:param name="communityId" value="${community.communityId}" />
		</c:url>
        <tr>
            <td><a href="${editUrl}"><c:out value = "${community.communityId}" /></a></td>
            <td><a href="${editUrl}"><c:out value = "${community.name}" /></a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>


<%@ include file="/WEB-INF/template/footer.jsp"%>