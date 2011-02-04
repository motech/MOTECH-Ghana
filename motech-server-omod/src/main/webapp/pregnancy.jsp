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
<openmrs:require privilege="Register MoTeCH Pregnancy" otherwise="/login.htm" redirect="/module/motechmodule/pregnancy.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/moduleResources/motechmodule/patientform.css" />

<meta name="heading" content="Pregnancy Registration" />
<%@ include file="localHeader.jsp" %>
<h2>Register a Pregnancy</h2>
<div class="instructions">
	This allows you to create a new pregnancy record. A pregnacy record is, in
	essence, a case record. It is initiated when a staff becomes aware of a new
	pregnancy. It, along with associated maternal visits, records information 
	about communication and service delivery during a pregnancy episode.
</div>
<form:form method="post" modelAttribute="pregnancy">
<div style="padding-top:5px; padding-bottom:5px;">
	<form:errors cssClass="error" />
</div>
<fieldset><legend>Pregnancy Information</legend>
<table>
	<tr>
		<td class="labelcolumn"><label for="dueDate">Expected Delivery Date (DD/MM/YYYY):</label></td>
		<td><form:input path="dueDate" /></td>
		<td><form:errors path="dueDate" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="dueDateConfirmed">Delivery Date confirmed by CHW:</label></td>
		<td>
			<form:select path="dueDateConfirmed">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="dueDateConfirmed" cssClass="error" /></td>
	</tr>
</table>
</fieldset>
<fieldset><legend>Mobile Midwife Enrollment</legend>
<table>
	<tr>
		<td class="labelcolumn"><label for="enroll">Enroll in Mobile Midwife:</label></td>
		<td>
			<form:select path="enroll">
				<form:option value="" label="Select Value" />
				<form:option value="true" label="Yes" />
				<form:option value="false" label="No" />
			</form:select>
		</td>
		<td><form:errors path="enroll" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="consent">Registrant has heard consent text and has consented to terms of enrollment:</label></td>
		<td><form:checkbox path="consent" /></td>
		<td><form:errors path="consent" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="phoneNumber">Phone Number:</label></td>
		<td><form:input path="phoneNumber" maxlength="50" /></td>
		<td><form:errors path="phoneNumber" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="phoneType">Phone Ownership:</label></td>
		<td>
			<form:select path="phoneType">
				<form:option value="" label="Select Value" />
				<form:option value="PERSONAL" label="Personal phone" />
				<form:option value="HOUSEHOLD" label="Owned by household" />
				<form:option value="PUBLIC" label="Public phone" />
			</form:select>
		</td>
		<td><form:errors path="phoneType" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="mediaType">Message Format:</label></td>
		<td>
			<form:select path="mediaType">
				<form:option value="" label="Select Value" />
				<form:option value="TEXT" label="Text" />
				<form:option value="VOICE" label="Voice" />
			</form:select>
		</td>
		<td><form:errors path="mediaType" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="language">Language for Messages:</label></td>
		<td>
			<form:select path="language">
				<form:option value="" label="Select Value" />
				<form:option value="en" label="English" />
				<form:option value="kas" label="Kassim" />
				<form:option value="nan" label="Nankam" />
			</form:select>
		</td>
		<td><form:errors path="language" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="dayOfWeek">Day of week to receive messages:</label></td>
		<td>
			<form:select path="dayOfWeek">
				<form:option value="" label="Select Value" />
				<form:option value="MONDAY" label="Monday" />
				<form:option value="TUESDAY" label="Tuesday" />
				<form:option value="WEDNESDAY" label="Wednesday" />
				<form:option value="THURSDAY" label="Thursday" />
				<form:option value="FRIDAY" label="Friday" />
				<form:option value="SATURDAY" label="Saturday" />
				<form:option value="SUNDAY" label="Sunday" />
			</form:select>
		</td>
		<td><form:errors path="dayOfWeek" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="timeOfDay">Time of day to receive messages (HH:MM):</label></td>
		<td><form:input path="timeOfDay" /></td>
		<td><form:errors path="timeOfDay" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="interestReason">Reason for interest in Mobile Midwife:</label></td>
		<td>
			<form:select path="interestReason">
				<form:option value="" label="Select Value" />
				<form:option value="CURRENTLY_PREGNANT" label="Currently pregnant" />
				<form:option value="RECENTLY_DELIVERED" label="Recently delivered" />
				<form:option value="FAMILY_FRIEND_PREGNANT" label="Family/ friend is pregnant" />
				<form:option value="FAMILY_FRIEND_DELIVERED" label="Family/friend recently delivered" />
				<form:option value="PLANNING_PREGNANCY_INFO" label="Thinking of getting pregnant and want more information" />
				<form:option value="KNOW_MORE_PREGNANCY_CHILDBIRTH" label="Want to know more about pregnancy and child birth" />
				<form:option value="WORK_WITH_WOMEN_NEWBORNS" label="I work with pregnant women and/or new borns" />
			</form:select>
		</td>
		<td><form:errors path="interestReason" cssClass="error" /></td>
	</tr>
	<tr>
		<td class="labelcolumn"><label for="howLearned">How they learned of Mobile Midwife:</label></td>
		<td>
			<form:select path="howLearned">
				<form:option value="" label="Select Value" />
				<form:option value="GHS_NURSE" label="GHS Nurse" />
				<form:option value="MOTECH_FIELD_AGENT" label="MoTeCH field agent" />
				<form:option value="FRIEND" label="Friend" />
				<form:option value="POSTERS_ADS" label="Posters/ads" />
				<form:option value="RADIO" label="Radio" />
			</form:select>
		</td>
		<td><form:errors path="howLearned" cssClass="error" /></td>
	</tr>
</table>
</fieldset>
<table>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>