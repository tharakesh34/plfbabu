function encrypt(val, randomKey) {
	var key = CryptoJS.MD5(randomKey);
	var iv  = CryptoJS.MD5(randomKey);
	
	var encrypted = CryptoJS.AES.encrypt(val, key, { iv: iv, padding: CryptoJS.pad.Pkcs7, mode: CryptoJS.mode.CBC});
	
	return encrypted;
}

function encryptEventValue(event) {
    event.data.value = encrypt(event.data.value, randomKey.getValue());
}

function encryptPassword() {
    zAu.cmd0.showBusy('Processing...');
	var userNameField = zk.Widget.$('$txtbox_Username');
	var userNameField1 = zk.Widget.$('$txtbox_Username1');
	var passwordField = zk.Widget.$('$txtbox_Password');
	var passwordField1 = zk.Widget.$('$txtbox_Password1');
	var randomKey = zk.Widget.$('$txtbox_randomKey');
	var loginButton = zk.Widget.$('$btnLogin');
	
	loginButton.focus();
	userNameField.focus();
	userNameField.select();
	
	userNameField1.setValue(encrypt(userNameField.getValue(), randomKey.getValue()));
	passwordField1.setValue(encrypt(passwordField.getValue(), randomKey.getValue()));
	
	userNameField.setValue("");
	passwordField.setValue("");
}

function encryptSecUserPassword() {
	var txtbox_Password = document.getElementById('txtbox_Password');
	var txtbox_confirm_Password = document.getElementById('txtbox_confirm_Password');
	var randomKey = zk.Widget.$('$txtbox_randomKey');
	
	
	var txtbox_Password1 = zk.Widget.$('$txtbox_Password1');
	txtbox_Password1.$n().value = ("");
	txtbox_Password1.updateChange_();
	if (txtbox_Password.value.trim().length > 0) {
		txtbox_Password1.$n().value=(encrypt(txtbox_Password.value, randomKey.getValue()));
		txtbox_Password1.updateChange_();
		txtbox_Password.value = "";
	}
	var txtbox_confirm_Password1 = zk.Widget.$('$txtbox_confirm_Password1');
	txtbox_confirm_Password1.$n().value = ("");
	txtbox_confirm_Password1.updateChange_();
	if (txtbox_confirm_Password.value.trim().length > 0) {
		txtbox_confirm_Password1.$n().value=(encrypt(txtbox_confirm_Password.value, randomKey.getValue()));
		txtbox_confirm_Password1.updateChange_();
		txtbox_confirm_Password.value = "";
	}
}

function encryptSecUserPasswordRest() {
	var password = document.getElementById('password');
	var newPassword = document.getElementById('newPassword');
	var retypeNewPassword = document.getElementById('retypeNewPassword');

	var randomKey = zk.Widget.$('$txtbox_randomKey');
	
	var password1 = zk.Widget.$('$password1');
	password1.$n().value = ("");
	password1.updateChange_();
	if (password.value.trim().length > 0) {
		password1.$n().value=(encrypt(password.value, randomKey.getValue()));
		password1.updateChange_();
		password.value = "";
	}
	
	var newPassword1 = zk.Widget.$('$newPassword1');
	newPassword1.$n().value = ("");
	newPassword1.updateChange_();
	if (newPassword.value.trim().length > 0) {
		newPassword1.$n().value=(encrypt(newPassword.value, randomKey.getValue()));
		newPassword1.updateChange_();
		newPassword.value = "";
	}
	
	var retypeNewPassword1 = zk.Widget.$('$retypeNewPassword1');
	retypeNewPassword1.$n().value = ("");
	retypeNewPassword1.updateChange_();
	if (retypeNewPassword.value.trim().length > 0) {
		retypeNewPassword1.$n().value=(encrypt(retypeNewPassword.value, randomKey.getValue()));
		retypeNewPassword1.updateChange_();
		retypeNewPassword.value = "";
	}
}

function passwordRestByAdmin() {
	var newPassword = document.getElementById('newPassword');
	var retypeNewPassword = document.getElementById('retypeNewPassword');
	var randomKey = zk.Widget.$('$txtbox_randomKey');
	
	var newPassword1 = zk.Widget.$('$newPassword1');
	newPassword1.$n().value = ("");
	newPassword1.updateChange_();
	if (newPassword.value.trim().length > 0) {
		newPassword1.$n().value=(encrypt(newPassword.value, randomKey.getValue()));
		newPassword1.updateChange_();
		newPassword.value = "";
	}
	var retypeNewPassword1 = zk.Widget.$('$retypeNewPassword1');
	retypeNewPassword1.updateChange_();
	retypeNewPassword1.$n().value = ("");
	if (retypeNewPassword.value.trim().length > 0) {
		retypeNewPassword1.$n().value=(encrypt(retypeNewPassword.value, randomKey.getValue()));
		retypeNewPassword1.updateChange_();
		retypeNewPassword.value = "";
	}
}

function onSubmitOK() {
	// Suppress for security
}
