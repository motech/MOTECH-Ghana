<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Send SMS" otherwise="/login.htm"
                 redirect="/module/motechmodule/sms.form"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/dynamic_combo_box.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/sms.js"/>
<openmrs:htmlInclude file="/moduleResources/motechmodule/sms.css"/>
<meta name="heading" content="Send SMS"/>
<%@ include file="localHeader.jsp" %>

<script type="text/javascript">
    var $j = jQuery.noConflict();
    $j(document).ready(function() {
        var country = new Country(${country});
    });
</script>

<h3>Send SMS</h3>
<c:if test="${response != null}">
    <span style="color:${response.success ? 'green' : 'red'}">${response.text}</span>
</c:if>

<p></p>
<form:form method="post" modelAttribute="bulkMessage">

    <table>
        <tr>
            <td class="labelcolumn"><label for="region">Region:</label></td>
            <td>
                <div class='checkbox-list-wrapper'>
                    <div class='checkbox-list-selector inline-block'></div>
                    <form:select path="region" multiple="multiple" disabled="true"
                                 cssClass="checkbox-list inline-block">
                    </form:select>
                </div>
            </td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="district">District:</label></td>
            <td>
                <div class='checkbox-list-wrapper'>
                    <div class='checkbox-list-selector inline-block'></div>
                    <form:select path="district" multiple="multiple" disabled="true"
                                 cssClass="checkbox-list inline-block">
                    </form:select>
                </div>
            </td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="subDistrict">Sub District:</label></td>
            <td>
                <div class='checkbox-list-wrapper'>
                    <div class='checkbox-list-selector inline-block'></div>
                    <form:select path="subDistrict" multiple="multiple" disabled="true"
                                 cssClass="checkbox-list inline-block">
                    </form:select>
                </div>
            </td>
        </tr>
        <tr>
            <td class="labelcolumn"><label for="facility">Facility:</label></td>
            <td>
                <div class='checkbox-list-wrapper'>
                    <div class='checkbox-list-selector inline-block'></div>
                    <form:select path="facility" multiple="multiple" disabled="true"
                                 cssClass="checkbox-list inline-block">
                    </form:select>
                </div>
            </td>
        </tr>
        <tr>
            <td>Phone Numbers</td>
            <td>
                <form:textarea path="recipients" rows='6' cols='60'></form:textarea>
            </td>
            <td class="hideme"><span id="error-number-required" class="error"><spring:message code="motechmodule.phoneNumber.required"/></span></td>
            <td class="hideme"><span id="error-number-invalid" class="error"><spring:message code="motechmodule.phoneNumber.commaSeparated"/></span></td>
        </tr>
        <tr>
            <td>Message</td>
            <td>
                <input type="hidden" id="facility-phone-numbers"/>
                <form:textarea path="content" rows='6' cols='60'></form:textarea>
            </td>
            <td class="hideme"><span id="error-message-required" class="error"><spring:message code="motechmodule.message.required"/></span></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" id="submitMessage" value="Send"/></td>
        </tr>
    </table>
</form:form>
