<ul id="menu">
	<openmrs:hasPrivilege privilege="View Administration Functions">
		<li	class="first">
			<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Register MoTeCH Patient">
		<li <c:if test='<%= request.getRequestURI().contains("demo-patient") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/demo-patient.form">
				<spring:message code="motechmodule.Demo.Patient.register"/>
			</a>
		</li>
		<li <c:if test='<%= request.getRequestURI().contains("demo-enrollpatient") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/demo-enrollpatient.form">
				<spring:message code="motechmodule.Demo.Patient.enroll"/>
			</a>
		</li>
		<li <c:if test='<%= request.getRequestURI().contains("message-patient") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/motechmodule/message-patient.form">
				<spring:message code="motechmodule.Demo.Patient.message"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
</ul>