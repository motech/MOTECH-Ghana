<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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
<openmrs:require privilege="View RCT Patients" otherwise="/login.htm"
                 redirect="/module/motechmodule/viewrctpatients.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="RCT Patients"/>
<%@ include file="localHeader.jsp" %>

<h1>RCT Patients</h1>
<c:if test="${not empty rctpatients}">
    <div style="background: #8FABC7; padding: 2px; text-align: center; width: 300px;">RCT Patients</div>
    <table cellpadding="4" cellspacing="0">
        <tr>
            <th>
                Client Name
            </th>
            <th>
                MoTeCH ID
            </th>
            <th>
                RCT Group
            </th>
            <th>
                Staff ID
            </th>
            <th>
                Staff Name
            </th>
            <th>
                Facility ID
            </th>
            <th>
                Facility Name
            </th>
        </tr>
        <c:forEach items="${rctpatients}" var="rctpatient">
            <tr>
                <td>
                ${rctpatient.firstName} ${rctpatient.lastName}
                </td>
                <td>
                    ${rctpatient.studyId}
                </td>
                <td>
                    ${rctpatient.controlGroup}
                </td>
                <td>
                    ${rctpatient.staffId}
                </td>
                <td>
                    ${rctpatient.staffFirstName} ${rctpatient.staffLastName}
                </td>
                <td>
                    ${rctpatient.facilityId}
                </td>
                <td>
                    ${rctpatient.facilityName}
                </td>
            </tr>
        </c:forEach>
    </table>
    <br/> <br/>
</c:if>