<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) ${year} The Trustees of Columbia University in the City of
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