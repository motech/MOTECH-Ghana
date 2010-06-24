<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<script type="text/javascript">
var regionDistsMap = {};
<c:forEach var="regionVar" items="${regionMap}">
  regionDistsMap['${regionVar.key}'] = new Array();
  <c:forEach var="dist" items="${regionVar.value}" varStatus="status">
    regionDistsMap['${regionVar.key}'][${status.index}] = '${dist}';
  </c:forEach>
</c:forEach>

var distCommsMap = {};
<c:forEach var="districtVar" items="${districtMap}">
  distCommsMap['${districtVar.key}'] = new Array();
  <c:forEach var="comm" items="${districtVar.value}" varStatus="status">
    distCommsMap['${districtVar.key}'][${status.index}] = {id:'${comm.communityId}',name:'${comm.name}'};
  </c:forEach>
</c:forEach>

var emptyDistrictOption = {'':'Select Region'};
var emptyCommunityOption = {'':'Select District'};
var initialOption = {'':'Select Value'};

function regionDistrictUpdated() {
	var region = dwr.util.getValue('region');
	var district = dwr.util.getValue('district');
	var communityId = dwr.util.getValue('communityId');
	
	dwr.util.removeAllOptions('district');
	if( region == '' ) {
		dwr.util.addOptions('district', emptyDistrictOption );
	} else {
		dwr.util.addOptions('district', initialOption );
		dwr.util.addOptions('district', regionDistsMap[region] );
		dwr.util.setValue('district', district);
	}
	dwr.util.removeAllOptions('communityId');
	if( region == '' || district == '' ) {
		dwr.util.addOptions('communityId', emptyCommunityOption );
	} else {
		dwr.util.addOptions('communityId', initialOption );
		dwr.util.addOptions('communityId', distCommsMap[district], 'id','name'  );
		dwr.util.setValue('communityId', communityId);
	}
}
jQuery( function() {
	regionDistrictUpdated();
});
</script>