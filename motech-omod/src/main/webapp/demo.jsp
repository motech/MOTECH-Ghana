<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Use MoTeCH Demo" otherwise="/login.htm" redirect="/module/motechmodule/demo.htm" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="MoTeCH Demo" />
<%@ include file="demoLocalHeader.jsp" %>

<h2><spring:message code="motechmodule.title"/></h2>
<div class="instructions">
	Welcome MoTeCH user! To interact with the MoTeCH system demo, click one of the tabs above.
	If you don't see the functionality you expect, ask your administrator for permissions. 
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>