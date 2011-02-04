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

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Register MoTeCH Facility" otherwise="/login.htm" redirect="/module/motechmodule/addfacility.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery-autocomplete/jquery.autocomplete.css" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery-autocomplete/lib/jquery.bgiframe.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery-autocomplete/jquery.autocomplete.min.js" />

<script type="text/javascript">
	var $j = jQuery.noConflict();
    $j(document).ready(function(){
        $j.getJSON("/openmrs/module/motechmodule/jsonfacilitydata.form", function(data){
            $j('#country').autocomplete(data.country);
            $j('#region').autocomplete(data.regions);
            $j('#countyDistrict').autocomplete(data.districts);
            $j('#stateProvince').autocomplete(data.provinces);
        });


    });
</script>

<meta name="heading" content="Add Facility" />
<%@ include file="localHeader.jsp" %>
<h2>Add a New Facility</h2>
<div class="instructions">
	Add facility and click submit to save.
</div>
<form:form method="post" modelAttribute="facility">
<form:errors cssClass="error" />
<fieldset>
<legend>New Facility</legend>
<table>
    <tr>
        <td><form:label path="name">Name:</form:label></td>
        <td><form:input path="name" maxlength="50"/> </td>
        <td><form:errors path="name" cssClass="error"/> </td>
    </tr>
    <tr>
        <td><form:label path="country">Country:</form:label></td>
        <td><form:input path="country" maxlength="50"/></td>
        <td><form:errors path="country" cssClass="error"/> </td>
    </tr>
    <tr>
        <td><form:label path="region">Region:</form:label></td>
        <td><form:input path="region" maxlength="50"/> </td>
        <td><form:errors cssClass="error"/> </td>
    </tr>
    <tr>
        <td><form:label path="countyDistrict">District:</form:label></td>
        <td><form:input path="countyDistrict" maxlength="50"/> </td>
        <td><form:errors cssClass="error"/></td>
    </tr>
    <tr>
        <td><form:label path="stateProvince">Sub-District:</form:label></td>
        <td><form:input path="stateProvince" maxlength="50"/> </td>
        <td><form:errors cssClass="error"/> </td>
    </tr>
	<tr>
		<td><form:label path="phoneNumber">Phone Number:</form:label></td>
		<td><form:input path="phoneNumber" maxlength="50"/></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>