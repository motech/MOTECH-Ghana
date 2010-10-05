<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2010 The Trustees of Columbia University in the City of
    New York and Grameen Foundation USA.  All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

    3. Neither the name of Grameen Foundation USA, Columbia University, or
    their respective contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
    AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
    FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
    USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
    OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
    EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Register MoTeCH Communities" otherwise="/login.htm"
                 redirect="/module/motechmodule/community.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="Community"/>
<%@ include file="localHeader.jsp" %>
<c:url value="/module/motechmodule/community/add.form" var="addCommunity"/>

<div style="margin-bottom:4px;">
    <h><a href="${addCommunity}">Add a new Community</a></h>
</div>
<div>
    <c:forEach items="${facilities}" var="facility">
        <c:if test="${not empty facility.communities}">
        <div style="background: #8FABC7; padding: 2px; text-align: center; width: 300px;">Facility - ${facility.location.neighborhoodCell}</div>
        <table cellpadding="4" cellspacing="0">
            <tr>
                <th>
                    Community Id
                </th>
                <th>
                    Community Name
                </th>
            </tr>
            <c:forEach items="${facility.communities}" var="community" varStatus="status">

                <c:url value="/module/motechmodule/community/editcommunity.form" var="editUrl">
                    <c:param name="communityId" value="${community.communityId}"/>  
                </c:url>
                <tr class="<c:choose><c:when test="${status.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
                    <td><a href="${editUrl}">${community.communityId}</a></td>
                    <td><a href="${editUrl}">${community.name}</a></td>
                </tr>

            </c:forEach>
        </table>
        <br/> <br/>
        </c:if>
    </c:forEach>
</div>


<%@ include file="/WEB-INF/template/footer.jsp" %>