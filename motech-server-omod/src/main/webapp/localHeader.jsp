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

<ul id="menu">
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li	class="first">
			<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Register MoTeCH Facility">
		<li <c:if test='<%= request.getRequestURI().contains("facility") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/facility.form">
				<spring:message code="motechmodule.Facility.register"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Register MoTeCH Staff">
		<li <c:if test='<%= request.getRequestURI().contains("staff") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/staff.form">
				<spring:message code="motechmodule.Staff.register"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Register MoTeCH Patient">
		<li <c:if test='<%= request.getRequestURI().contains("patient") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/patient.form">
				<spring:message code="motechmodule.Patient.register"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="View MoTeCH Data">
		<li <c:if test='<%= request.getRequestURI().contains("search") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/search.form">
				<spring:message code="motechmodule.Patient.search"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage MoTeCH Troubled Phones">
		<li <c:if test='<%= request.getRequestURI().contains("troubledphone") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/troubledphone.form">
				<spring:message code="motechmodule.TroubledPhone.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage MoTeCH Blackout">
		<li <c:if test='<%= request.getRequestURI().contains("blackout") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/blackout.form">
				<spring:message code="motechmodule.Blackout.manage"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="View MoTeCH Data">
		<li <c:if test='<%= request.getRequestURI().contains("viewdata") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/viewdata.form">
				<spring:message code="motechmodule.Data.view"/>
			</a>
		</li>
	</openmrs:hasPrivilege>

    <openmrs:hasPrivilege privilege="Register MoTeCH Communities">
		<li <c:if test='<%= request.getRequestURI().contains("community") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/community.form">
				<spring:message code="motechmodule.Community.view"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
    <!--
        <openmrs:hasPrivilege privilege="Duplicate Patients">
        <li <c:if test='<%= request.getRequestURI().contains("duplicatepatients") %>'>class="active"</c:if>>
          <a href="${pageContext.request.contextPath}/module/motechmodule/duplicatepatients.form">
            <spring:message code="motechmodule.Tools.duplicatepatients"/>
          </a>
        </li>
    </openmrs:hasPrivilege>
    -->
</ul>
