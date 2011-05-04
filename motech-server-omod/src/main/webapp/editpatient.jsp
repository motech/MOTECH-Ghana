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

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Register MoTeCH Patient" otherwise="/login.htm"
                 redirect="/module/motechmodule/editpatient.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/dynamic_combo_box.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/patient_form_events.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/country.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/validator.js"/>
<script type="text/javascript">
    var $j = jQuery.noConflict();
    $j(document).ready(function() {
        new PatientFormRegistrationEvents();
        var selectedLocation = new Location(${selectedLocation});
        new Country(${country}, selectedLocation);
        new RequiredFieldValidator($j('#patient')).addAll(
                $j('#firstName'), $j('#lastName')
                , $j('#birthDate'), $j('#birthDateEst'), $j('#sex'), $j('#insured'), $j('#region')
                , $j('#district'), $j('#subDistrict'), $j('#facility'), $j('#communityId'), $j('#address')
                , $j('#dueDate'), $j('#dueDateConfirmed'), $j('#enroll')
                );
    });
</script>
<openmrs:htmlInclude file="/moduleResources/motechmodule/patientform.css"/>
<openmrs:htmlInclude file="/dwr/util.js"/>

<meta name="heading" content="Edit Patient"/>
<%@ include file="localHeader.jsp" %>
<h2>Edit Patient</h2>

<div class="instructions">
    This form allows you to edit an existing patient record.
</div>
<form:form method="post" modelAttribute="patient">
<span style="color:green;">
	<spring:message code="${successMsg}" text=""/>
</span>
<form:errors cssClass="error"/>
<fieldset>
    <legend>Patient Information</legend>
    <table>
        <tr>
            <td class="labelcolumn"><label for="firstName">First Name:</label></td>
            <td><form:input path="firstName" maxlength="50"/></td>
            <td class="hideme"><span for="firstName" class="error"><spring:message code="motechmodule.firstName.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="middleName">Middle Name:</label></td>
            <td><form:input path="middleName" maxlength="50"/></td>
            <td><form:errors path="middleName" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="lastName">Last Name:</label></td>
            <td><form:input path="lastName" maxlength="50"/></td>
            <td class="hideme"><span for="lastName" class="error"><spring:message code="motechmodule.lastName.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="prefName">Preferred Name:</label></td>
            <td><form:input path="prefName" maxlength="50"/></td>
            <td><form:errors path="prefName" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="birthDate">Date of Birth (DD/MM/YYYY):</label></td>
            <td><form:input path="birthDate"/></td>
            <td class="hideme"><span for="birthDate" class="error"><spring:message code="motechmodule.birthDate.required"/></span></td>
            <td><form:errors path="birthDate" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="birthDateEst">Estimated Date of Birth:</label></td>
            <td>
                <form:select path="birthDateEst">
                    <form:option value="" label="Select Value"/>
                    <form:option value="true" label="Yes"/>
                    <form:option value="false" label="No"/>
                </form:select>
            </td>
            <td class="hideme"><span for="birthDateEst" class="error"><spring:message code="motechmodule.birthDateEst.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="sex">Sex:</label></td>
            <td>
                <form:select path="sex">
                    <form:option value="" label="Select Value"/>
                    <form:option value="FEMALE" label="Female"/>
                    <form:option value="MALE" label="Male"/>
                </form:select>
            </td>
            <td class="hideme"><span for="sex" class="error"><spring:message code="motechmodule.sex.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="insured">Insured:</label></td>
            <td>
                <form:select path="insured">
                    <form:option value="" label="Select Value"/>
                    <form:option value="true" label="Yes"/>
                    <form:option value="false" label="No"/>
                </form:select>
            </td>
            <td class="hideme"><span for="insured" class="error"><spring:message code="motechmodule.insured.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="nhis">NHIS Number:</label></td>
            <td><form:input path="nhis" maxlength="50"/></td>
            <td><form:errors path="nhis" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="nhisExpDate">NHIS Expiration Date (DD/MM/YYYY):</label></td>
            <td><form:input path="nhisExpDate"/></td>
            <td><form:errors path="nhisExpDate" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="motherMotechId">Mother's MoTeCH ID:</label></td>
            <td><form:input path="motherMotechId"/></td>
            <td><form:errors path="motherMotechId" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="region">Region:</label></td>
            <td>
                <form:select path="region">
                    <form:option value="" label="Select Value"/>
                </form:select>
            </td>
            <td class="hideme"><span for="region" class="error"><spring:message code="motechmodule.region.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="district">District:</label></td>
            <td>
                <form:select path="district">
                    <form:option value="" label="Select Value"/>
                </form:select>
            </td>
            <td class="hideme"><span for="district" class="error"><spring:message code="motechmodule.district.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="subDistrict">Sub District:</label></td>
            <td>
                <form:select path="subDistrict">
                    <form:option value="" label="Select Value"/>
                </form:select>
            </td>
            <td class="hideme"><span for="subDistrict" class="error"><spring:message code="motechmodule.subDistrict.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="facility">Facility:</label></td>
            <td>
                <form:select path="facility">
                    <form:option value="" label="Select Value"/>
                </form:select>
            </td>
            <td class="hideme"><span for="facility" class="error"><spring:message code="motechmodule.facility.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="communityId">Community:</label></td>
            <td>
                <form:select path="communityId">
                    <form:option value="" label="Select Value"/>
                </form:select>
            </td>
            <td class="hideme"><span for="communityId" class="error"><spring:message code="motechmodule.communityId.required"/></span></td>
            <td><form:errors path="communityId" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="address">Address/household:</label></td>
            <td><form:input path="address" maxlength="50"/></td>
            <td class="hideme"><span for="address" class="error"><spring:message code="motechmodule.address.required"/></span></td>
        </tr>
    </table>
</fieldset>
<fieldset>
    <legend>Pregnancy Information</legend>
    <table>
        <tr>
            <td class="labelcolumn"><label for="dueDate">Expected Delivery Date (DD/MM/YYYY):</label></td>
            <td><form:input path="dueDate"/></td>
            <td class="hideme"><span for="dueDate" class="error"><spring:message code="motechmodule.dueDate.required"/></span></td>
            <td><form:errors path="dueDate" cssClass="error"/></td>
        </tr>
    </table>
</fieldset>
<fieldset>
    <legend>Enrollment Information</legend>
    <table id="mobileMidwifeInformation">
        <tr>
            <td class="labelcolumn"><label for="enroll">Enrolled in Mobile Midwife:</label></td>
            <td>
                <form:select path="enroll">
                    <form:option value="" label="Select Value"/>
                    <form:option value="true" label="Yes"/>
                    <form:option value="false" label="No"/>
                </form:select>
            </td>
            <td class="hideme"><span for="enroll" class="error"><spring:message code="motechmodule.enroll.required"/></span></td>
        </tr>
    </table>
    <table>
        <tr>
            <td class="labelcolumn"><label for="enroll">Enrolled in Mobile Midwife:</label></td>
            <td>
                <form:select path="enroll">
                    <form:option value="" label="Select Value"/>
                    <form:option value="true" label="Yes"/>
                    <form:option value="false" label="No"/>
                </form:select>
            </td>
            <td class="hideme"><span for="enroll" class="error"><spring:message code="motechmodule.enroll.required"/></span></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="consent">Registrant has heard consent text and has consented to terms of
                enrollment:</label></td>
            <td><form:checkbox path="consent"/></td>
            <td><form:errors path="consent" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="phoneNumber">Phone Number:</label></td>
            <td><form:input path="phoneNumber" maxlength="50"/></td>
            <td><form:errors path="phoneNumber" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="phoneType">Phone Ownership:</label></td>
            <td>
                <form:select path="phoneType">
                    <form:option value="" label="Select Value"/>
                    <form:option value="PERSONAL" label="Personal phone"/>
                    <form:option value="HOUSEHOLD" label="Owned by household"/>
                    <form:option value="PUBLIC" label="Public phone"/>
                </form:select>
            </td>
            <td><form:errors path="phoneType" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="mediaType">Message Format:</label></td>
            <td>
                <form:select path="mediaType">
                    <form:option value="" label="Select Value"/>
                    <form:option value="TEXT" label="Text"/>
                    <form:option value="VOICE" label="Voice"/>
                </form:select>
            </td>
            <td><form:errors path="mediaType" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="language">Language for Messages:</label></td>
            <td>
                <form:select path="language">
                    <form:option value="" label="Select Value"/>
                    <form:option value="en" label="English"/>
                    <form:option value="kas" label="Kassim"/>
                    <form:option value="nan" label="Nankam"/>
                </form:select>
            </td>
            <td><form:errors path="language" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="dayOfWeek">Day of week to receive messages:</label></td>
            <td>
                <form:select path="dayOfWeek">
                    <form:option value="" label="Select Value"/>
                    <form:option value="MONDAY" label="Monday"/>
                    <form:option value="TUESDAY" label="Tuesday"/>
                    <form:option value="WEDNESDAY" label="Wednesday"/>
                    <form:option value="THURSDAY" label="Thursday"/>
                    <form:option value="FRIDAY" label="Friday"/>
                    <form:option value="SATURDAY" label="Saturday"/>
                    <form:option value="SUNDAY" label="Sunday"/>
                </form:select>
            </td>
            <td><form:errors path="dayOfWeek" cssClass="error"/></td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="timeOfDay">Time of day to receive messages (HH:MM):</label></td>
            <td><form:input path="timeOfDay"/></td>
            <td><form:errors path="timeOfDay" cssClass="error"/></td>
        </tr>
    </table>
</fieldset>
<table>
    <tr>
        <td colspan="2">
            <input type="submit"/>
        </td>
    </tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>