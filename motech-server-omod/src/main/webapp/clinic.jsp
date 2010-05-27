<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Register MoTeCH Clinic" otherwise="/login.htm" redirect="/module/motechmodule/clinic.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/sarissa.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery.tree.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery.tree.xml_flat.js" />
<script type="text/javascript">
	jQuery( function() {
		jQuery('#tree').tree({
			ui : {
				theme_path : '/openmrs/moduleResources/motechmodule/themes/apple/style.css',
				theme_name : 'apple'
			},
			data : {
				type : 'xml_flat',
				opts : {
					static : '${locationsXml}'
				}
			},
			types : {
				"default" : {
					draggable : false
				}
			}
		});
		showParentSelectionInTree();
	});
	function showParentSelectionInTree() {
		var locationId = jQuery('#parent_select').val();
		if( ! locationId ) {
			return;
		}
		var nodeId = 'location_' + locationId;
		var jQueryNode = jQuery('#' + nodeId);

		jQuery.tree.reference('#tree').close_all();

		// Traverse up tree from selected Location, expanding Parent Locations
		var parentNode = jQuery.tree.reference('#tree').parent(jQueryNode);
		while( parentNode != -1 && parentNode != false ) {
			jQuery.tree.reference('#tree').open_branch(parentNode);
			parentNode = jQuery.tree.reference('#tree').parent(parentNode);
		}
	}
</script>

<meta name="heading" content="Clinic Registration" />
<%@ include file="localHeader.jsp" %>
<h2>Register a Clinic</h2>
<div class="instructions">
	This test allows you to create a new clinic record. Patients and 
	staff are associated with a specific clinic, and the clinic 
	must be registered prior to staff or patient registration.
	<em>NOTE: A clinic name needs to be unique.</em>
</div>
<table>
	<tr>
		<td valign="top">
			<form method="post">
			<table>
				<tr>
					<td><label for="name">Clinic Name:</label></td>
					<td><input name="name" value="A-Clinic" /></td>
				</tr>
				<tr>
					<td><label for="parent">Parent Location:</label></td>
					<td>
						<select id="parent_select" name="parent" onchange="showParentSelectionInTree()">
							<option value=""></option>
							<c:forEach items="${locations}" var="location">
								<option value="${location.locationId}">${location.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" /></td>
				</tr>
			</table>
			</form>
		</td>
		<td valign="top">
			<div id="tree"></div>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>