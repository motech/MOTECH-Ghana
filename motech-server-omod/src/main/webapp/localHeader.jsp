<ul id="menu">
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li	class="first">
			<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Register MoTeCH Clinic">
		<li <c:if test='<%= request.getRequestURI().contains("clinic") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/clinic.form">
				<spring:message code="motechmodule.Clinic.register"/>
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

</ul>