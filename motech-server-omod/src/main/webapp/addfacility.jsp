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

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Register MoTeCH Facility" otherwise="/login.htm" redirect="/module/motechmodule/addfacility.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery-autocomplete/jquery.autocomplete.css" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery-autocomplete/lib/jquery.bgiframe.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/jquery-autocomplete/jquery.autocomplete.min.js" />
<openmrs:htmlInclude file="/moduleResources/motechmodule/dynamic_combo_box.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/add_facility.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/add_facility.css"/>

<meta name="heading" content="Add Facility" />
<%@ include file="localHeader.jsp" %>

<div id="location_data">
<ul id="countries_data">
   <c:forEach items="${countries}" var="country">
   <li>${country}</li>
   </c:forEach>
</ul>
<ul id="regions_data">
   <c:forEach items="${regions}" var="country">
   <li title="${country.key}">
        <ul>
            <c:forEach items="${country.value}" var="region">
             <li>${region}</li>
            </c:forEach>
        </ul>
   </li>
   </c:forEach>
</ul>
<ul id="districts_data">
   <c:forEach items="${districts}" var="region">
   <li title="${region.key}">
        <ul>
            <c:forEach items="${region.value}" var="district">
             <li>${district}</li>
            </c:forEach>
        </ul>
   </li>
   </c:forEach>
</ul>
<ul id="provinces_data">
   <c:forEach items="${provinces}" var="district">
   <li title="${district.key}">
        <ul>
            <c:forEach items="${district.value}" var="province">
             <li>${province}</li>
            </c:forEach>
        </ul>
   </li>
   </c:forEach>
</ul>
</div>


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
        <td>
        <span id="name_err" class="error" title="err_span">
            <spring:message code="motechmodule.name.blank"/>
        </span>
        <form:errors path="name" cssClass="error"/>
        </td>
    </tr>
    <tr>
        <td><form:label path="country">Country:</form:label></td>
        <td>
            <form:select path="country">
               <form:option value="" label="Select Value"/>
            </form:select>
        </td>
        <td>
        <span id="country_err" class="error" title="err_span">
            <spring:message code="motechmodule.country.blank"/>
        </span>
        </td>
    </tr>
    <tr>
        <td><form:label path="region">Region:</form:label></td>
        <td>
            <form:select path="region">
               <form:option value="" label="Select Value"/>
            </form:select>
        </td>
        <td>
        <span id="region_err" class="error" title="err_span">
            <spring:message code="motechmodule.region.blank"/>
        </span>
        </td>
    </tr>
    <tr>
        <td><form:label path="countyDistrict">District:</form:label></td>
        <td>
            <form:select path="countyDistrict">
               <form:option value="" label="Select Value"/>
            </form:select>
        </td>
        <td>
        <span id="countyDistrict_err" class="error" title="err_span">
            <spring:message code="motechmodule.district.blank"/>
        </span>
        </td>
    </tr>
    <tr>
        <td><form:label path="stateProvince">Sub-District:</form:label></td>
        <td>
            <form:select path="stateProvince">
               <form:option value="" label="Select Value"/>
            </form:select>
        </td>
        <td>
        <span id="stateProvince_err" class="error" title="err_span"/>
        <spring:message code="motechmodule.province.blank"/>
        </td>
    </tr>
	<tr>
		<td><form:label path="phoneNumber">Phone Number:</form:label></td>
		<td><form:input path="phoneNumber" maxlength="50"/></td>
        <td><a id="addPhone1">Add Additional Phone Number</a></td>
		<td>
		<span id="phoneNumber_err" class="error" title="err_span">
		    <spring:message code="motechmodule.phoneNumber.invalid"/>
		</span>
		</td>
	</tr>
    <tr id="additional-phone1">
		<td><form:label path="additionalPhoneNumber1">Additional Phone Number1:</form:label></td>
		<td><form:input path="additionalPhoneNumber1" maxlength="50"/></td>
         <td><a id="addPhone2">Add Additional Phone Number</a></td>
		<td>
		<span id="additional_phoneNumber1_err" class="error" title="err_span">
		    <spring:message code="motechmodule.phoneNumber.invalid"/>
		</span>
		</td>
	</tr>
     <tr id="additional-phone2">
		<td><form:label path="additionalPhoneNumber2">Additional Phone Number2:</form:label></td>
		<td><form:input path="additionalPhoneNumber2" maxlength="50"/></td>
          <td><a id="addPhone3">Add Additional Phone Number</a></td>
		<td>
		<span id="additional_phoneNumber2_err" class="error" title="err_span">
		    <spring:message code="motechmodule.phoneNumber.invalid"/>
		</span>
		</td>
	</tr>
    <tr id="additional-phone3">
		<td><form:label path="additionalPhoneNumber3">Additional Phone Number3:</form:label></td>
		<td><form:input path="additionalPhoneNumber3" maxlength="50"/></td>
         <td><a id="addPhone4">Add Additional Phone Number</a></td>
		<td>
		<span id="additional_phoneNumber3_err" class="error" title="err_span">
		    <spring:message code="motechmodule.phoneNumber.invalid"/>
		</span>
		</td>
	</tr>
     <tr id="additional-phone4">
		<td><form:label path="additionalPhoneNumber4">Additional Phone Number4:</form:label></td>
		<td><form:input path="additionalPhoneNumber4" maxlength="50"/></td>
		<td>
		<span id="additional_phoneNumber4_err" class="error" title="err_span">
		    <spring:message code="motechmodule.phoneNumber.invalid"/>
		</span>
		</td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" id="submit_facility"/></td>
	</tr>
</table>
</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>