<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Use MoTeCH Demo" otherwise="/login.htm" redirect="/module/motechmodule/demo.htm" />

<%@ include file="/WEB-INF/template/header.jsp" %>

<meta name="heading" content="MoTeCH Demo - Success" />
<%@ include file="demoLocalHeader.jsp" %>

<h2><spring:message code="motechmodule.title"/></h2>
<div class="instructions">
	Your submission was successful! To demonstrate another MoTeCH system 
	function, select the function from the menu above. 
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>