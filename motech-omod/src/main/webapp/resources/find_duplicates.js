var birthDateRegex = /\d{2}\/\d{2}\/\d{4}/;
var numberOfMatches = 0;

function findDuplicates() {
	var firstName = dwr.util.getValue('firstName');
	var lastName = dwr.util.getValue('lastName');
	var birthDate = dwr.util.getValue('birthDate');
	var community = dwr.util.getValue('community');
	var primaryPhone = dwr.util.getValue('primaryPhone');
	var patientRegNum = dwr.util.getValue('regNumberGHS');
	var nhisNumber = dwr.util.getValue('nhis');
	
	if( nhisNumber != '' || ((firstName != '' && lastName != '') && 
			((birthDate != '' && birthDateRegex.test(birthDate)) || 
			community != '' || primaryPhone != '' || patientRegNum != ''))) {
		DWRMotechService.findMatchingPeople(firstName, lastName, 
			birthDate, community, primaryPhone, patientRegNum, nhisNumber, 
			displayMatchesFunction);
	}
}

function findDuplicatesForPerson() {
	var firstName = dwr.util.getValue('firstName');
	var lastName = dwr.util.getValue('lastName');
	var birthDate = dwr.util.getValue('birthDate');
	var community = dwr.util.getValue('community');
	var primaryPhone = dwr.util.getValue('primaryPhone');
	
	if((firstName != '' && lastName != '') && 
			((birthDate != '' && birthDateRegex.test(birthDate)) || 
			community != '' || primaryPhone != '')) {
		DWRMotechService.findMatchingPeople(firstName, lastName, 
			birthDate, community, primaryPhone, null, null, 
			displayMatchesFunctionForPerson);
	}
}

var tableColumnFunctions = [
	function(webPatient) { return webPatient.id; },
	function(webPatient) { return webPatient.firstName; },
	function(webPatient) { return webPatient.lastName; },
	function(webPatient) { return formatDate(webPatient.birthDate); },
	function(webPatient) { return webPatient.community; },
	function(webPatient) { return webPatient.regNumberGHS; },
	function(webPatient) { return webPatient.nhis; },
	function(webPatient) { return webPatient.primaryPhone; },
	function(webPatient) { return webPatient.secondaryPhone; }
];

var tableColumnFunctionsForPerson = [
	function(webPatient) { return webPatient.id; },
	function(webPatient) { return webPatient.firstName; },
	function(webPatient) { return webPatient.lastName; },
	function(webPatient) { return formatDate(webPatient.birthDate); },
	function(webPatient) { return webPatient.community; },
	function(webPatient) { return webPatient.primaryPhone; },
	function(webPatient) { return webPatient.secondaryPhone; }
];

function displayMatchesFunction(webPatientList) {
	numberOfMatches = webPatientList.length;
	if( numberOfMatches > 0 ) {
		dwr.util.removeAllRows('matchingPeopleBody');
		dwr.util.addRows('matchingPeopleBody', webPatientList, tableColumnFunctions);
		dwr.util.byId('matchingPeopleSection').style.display = 'block';
	} else {
		dwr.util.byId('matchingPeopleSection').style.display = 'none';
	}
}

function displayMatchesFunctionForPerson(webPatientList) {
	numberOfMatches = webPatientList.length;
	if( numberOfMatches > 0 ) {
		dwr.util.removeAllRows('matchingPeopleBody');
		dwr.util.addRows('matchingPeopleBody', webPatientList, tableColumnFunctionsForPerson);
		dwr.util.byId('matchingPeopleSection').style.display = 'block';
	} else {
		dwr.util.byId('matchingPeopleSection').style.display = 'none';
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
		confirm('Continue registration despite conflicts with existing people?');
}