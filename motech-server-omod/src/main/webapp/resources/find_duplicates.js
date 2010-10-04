/*
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var birthDateRegex = /\d{2}\/\d{2}\/\d{4}/;
var numberOfMatches = 0;

function findDuplicates() {
	var motechId = dwr.util.getValue('motechId');
	var firstName = dwr.util.getValue('firstName');
	var lastName = dwr.util.getValue('lastName');
	var prefName = dwr.util.getValue('prefName');
	var birthDate = dwr.util.getValue('birthDate');
	var communityId = dwr.util.getValue('communityId');
	var phoneNumber = dwr.util.getValue('phoneNumber');
	var nhisNumber = dwr.util.getValue('nhis');
	
	if( motechId != '' || nhisNumber != '' || 
			(((firstName != '' && lastName != '') || (prefName != '' && lastName != '')) && 
			((birthDate != '' && birthDateRegex.test(birthDate)) || 
			communityId != '' || phoneNumber != ''))) {
		DWRMotechService.findMatchingPatients(firstName, lastName, prefName,
			birthDate, communityId, phoneNumber, nhisNumber, motechId,
			displayMatchesFunction);
	}
}

var tableColumnFunctions = [
	function(webPatient) { return webPatient.motechId; },
	function(webPatient) { return webPatient.firstName; },
	function(webPatient) { return webPatient.lastName; },
	function(webPatient) { return formatDate(webPatient.birthDate); },
	function(webPatient) { return webPatient.communityName; },
	function(webPatient) { return webPatient.nhis; },
	function(webPatient) { return webPatient.phoneNumber; }
];

function displayMatchesFunction(webPatientList) {
	numberOfMatches = webPatientList.length;
	if( numberOfMatches > 0 ) {
		dwr.util.removeAllRows('matchingPatientsBody');
		dwr.util.addRows('matchingPatientsBody', webPatientList, tableColumnFunctions);
		dwr.util.byId('matchingPatientsSection').style.display = 'block';
	} else {
		dwr.util.byId('matchingPatientsSection').style.display = 'none';
	}
}

function formatDate(date) {
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	return (month<10?"0":"") + month + '/' + 
		(day<10?"0":"") + day + '/' + year;
}

function confirmRegistrationOnMatches() {
	return (numberOfMatches == 0) || 
		confirm('Continue registration despite conflicts with existing patients?');
}