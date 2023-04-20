function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}

function showErrorModal(message) {
	showModalDialog("Error", message);
}

function showSuccessModal(message) {
	showModalDialog("Notification", message);
}

function showWarningModal(message) {
	showModalDialog("Warning", message);
}	