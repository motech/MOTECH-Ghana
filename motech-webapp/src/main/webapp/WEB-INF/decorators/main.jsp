<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Motech Server - <decorator:title default="Untitled"/></title>
<c:url value="/styles/style.css" var="styleUrl" />
<c:url value="/" var="quickTestUrl" />
<c:url value="/nurse.jsp" var="regNurseUrl" />
<c:url value="/patient.jsp" var="regPatientUrl" />
<c:url value="/pregnancy.jsp" var="regPregUrl" />
<c:url value="/maternalVisit.jsp" var="regMatVisitUrl" />
<c:url value="/regTest" var="viewDataUrl" />
<link href="${styleUrl}" rel="stylesheet" type="text/css" media="screen" />
<decorator:head />
</head>
<body>
<div id="wrapper">
	<div id="header">
		<div id="logo">
			<h1><a href="#">Motech Server</a></h1>
			<p>Prototype Server Test Application</p>
		</div>
	</div>
	<!-- end #header -->
	<div id="menu">
		<ul>
			<li class="current_page_item"><a href="${quickTestUrl}">Home</a></li>
		</ul>
	</div>
	<!-- end #menu -->
	<div id="page">
	<div id="page-bgtop">
	<div id="page-bgbtm">
		<div id="content">
			<div class="post">
			<div class="post-bgtop">
			<div class="post-bgbtm">
				<h2 class="title"><a href="#"><decorator:getProperty property="meta.heading"/></a></h2>
				<div class="entry">
					<decorator:body />
				</div>
			</div>
			</div>
			</div>
		<div style="clear: both;">&nbsp;</div>
		</div>
		<!-- end #content -->
		<div id="sidebar">
			<ul>
				<li>
					<h2>Sections</h2>
					<ul>
						<li><a href="${quickTestUrl}">Quick Test</a> <span>Test many things at once</span></li>
						<li><a href="${regNurseUrl}">Register Nurse</a> <span>Register a new Nurse</span></li>
						<li><a href="${regPatientUrl}">Register Patient</a> <span>Register a new Patient</span> </li>
						<li><a href="${regPregUrl}">Register Pregnancy</a> <span>Register a new Pregnancy</span> </li>
						<li><a href="${regMatVisitUrl}">Record Maternal Visit</a> <span>Record a Maternal Visit</span></li>
						<li><a href="${viewDataUrl}">View Data</a> <span>View a summary of the data</span></li>
					</ul>
				</li>
			</ul>
		</div>
		<!-- end #sidebar -->
		<div style="clear: both;">&nbsp;</div>
	</div>
	</div>
	</div>
	<!-- end #page -->
</div>
	<div id="footer">
		<p> </p>
	</div>
	<!-- end #footer -->
</body>
</html>
