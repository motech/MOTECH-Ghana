var birthDateRegex = /\d{2}\/\d{2}\/\d{4}/;
var numberOfMatches = 0;

function findDuplicates() {
	var motechId = dwr.util.getValue('motechId');
	var firstName = dwr.util.getValue('firstName');
	var lastName = dwr.util.getValue('lastName');
	var prefName = dwr.util.getValue('prefName');
	var birthDate = dwr.util.getValue('birthDate');
	var community = dwr.util.getValue('community');
	var phoneNumber = dwr.util.getValue('phoneNumber');
	var nhisNumber = dwr.util.getValue('nhis');
	
	if( motechId != '' || nhisNumber != '' || 
			(((firstName != '' && lastName != '') || (prefName != '' && lastName != '')) && 
			((birthDate != '' && birthDateRegex.test(birthDate)) || 
			community != '' || phoneNumber != ''))) {
		DWRMotechService.findMatchingPatients(firstName, lastName, prefName,
			birthDate, community, phoneNumber, nhisNumber, motechId,
			displayMatchesFunction);
	}
}

function findDuplicatesForPerson() {
	var motechId = dwr.util.getValue('motechId');
	var firstName = dwr.util.getValue('firstName');
	var lastName = dwr.util.getValue('lastName');
	var prefName = dwr.util.getValue('prefName');
	var birthDate = dwr.util.getValue('birthDate');
	var community = dwr.util.getValue('community');
	var phoneNumber = dwr.util.getValue('phoneNumber');
	
	if(motechId != '' || 
			(((firstName != '' && lastName != '') || (prefName != '' && lastName != '')) && 
			((birthDate != '' && birthDateRegex.test(birthDate)) || 
			community != '' || phoneNumber != ''))) {
		DWRMotechService.findMatchingPatients(firstName, lastName, prefName,
			birthDate, community, phoneNumber, null, motechId, 
			displayMatchesFunctionForPerson);
	}
}

var tableColumnFunctions = [
	function(webPatient) { return webPatient.motechId; },
	function(webPatient) { return webPatient.firstName; },
	function(webPatient) { return webPatient.lastName; },
	function(webPatient) { return formatDate(webPatient.birthDate); },
	function(webPatient) { return webPatient.community; },
	function(webPatient) { return webPatient.regNumberGHS; },
	function(webPatient) { return webPatient.nhis; },
	function(webPatient) { return webPatient.phoneNumber; }
];

var tableColumnFunctionsForPerson = [
	function(webPatient) { return webPatient.motechId; },
	function(webPatient) { return webPatient.firstName; },
	function(webPatient) { return webPatient.lastName; },
	function(webPatient) { return formatDate(webPatient.birthDate); },
	function(webPatient) { return webPatient.community; },
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

function displayMatchesFunctionForPerson(webPatientList) {
	numberOfMatches = webPatientList.length;
	if( numberOfMatches > 0 ) {
		dwr.util.removeAllRows('matchingPatientsBody');
		dwr.util.addRows('matchingPatientsBody', webPatientList, tableColumnFunctionsForPerson);
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