function encrypt(val, randomKey) {
	var key = CryptoJS.MD5(randomKey);
	var iv  = CryptoJS.MD5(randomKey);
	
	var encrypted = CryptoJS.AES.encrypt(val, key, { iv: iv, padding: CryptoJS.pad.Pkcs7, mode: CryptoJS.mode.CBC});
	
	return encrypted;
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
	var txtbox_Password = zk.Widget.$('$txtbox_Password');
	var txtbox_confirm_Password = zk.Widget.$('$txtbox_confirm_Password');
	var randomKey = zk.Widget.$('$txtbox_randomKey');
	
	
	var txtbox_Password1 = zk.Widget.$('$txtbox_Password1');
	txtbox_Password1.$n().value=(encrypt(txtbox_Password.getValue(), randomKey.getValue()));
	txtbox_Password1.updateChange_();
	
	var txtbox_confirm_Password1 = zk.Widget.$('$txtbox_confirm_Password1');
	txtbox_confirm_Password1.$n().value=(encrypt(txtbox_confirm_Password.getValue(), randomKey.getValue()));
	txtbox_confirm_Password1.updateChange_();
	
	txtbox_Password.$n().value=("");
	txtbox_Password.updateChange_();
	
	txtbox_confirm_Password.$n().value=("");
	txtbox_confirm_Password.updateChange_();	

}

function encryptSecUserPasswordRest() {
	var password = zk.Widget.$('$password');
	var newPassword = zk.Widget.$('$newPassword');
	var retypeNewPassword = zk.Widget.$('$retypeNewPassword');
	var randomKey = zk.Widget.$('$txtbox_randomKey');
	
	var password1 = zk.Widget.$('$password1');
	password1.$n().value=(encrypt(password.getValue(), randomKey.getValue()));
	password1.updateChange_();
	
	var newPassword1 = zk.Widget.$('$newPassword1');
	newPassword1.$n().value=(encrypt(newPassword.getValue(), randomKey.getValue()));
	newPassword1.updateChange_();
	
	var retypeNewPassword1 = zk.Widget.$('$retypeNewPassword1');
	retypeNewPassword1.$n().value=(encrypt(retypeNewPassword.getValue(), randomKey.getValue()));
	retypeNewPassword1.updateChange_();
	
	password.$n().value=("");
	password.updateChange_();
	
	newPassword.$n().value=("");
	newPassword.updateChange_();	
	
	retypeNewPassword.$n().value=("");
	retypeNewPassword.updateChange_();	
}

function passwordRestByAdmin() {
	var newPassword = zk.Widget.$('$newPassword');
	var retypeNewPassword = zk.Widget.$('$retypeNewPassword');
	var randomKey = zk.Widget.$('$txtbox_randomKey');
	
	
	var newPassword1 = zk.Widget.$('$newPassword1');
	newPassword1.$n().value=(encrypt(newPassword.getValue(), randomKey.getValue()));
	newPassword1.updateChange_();
	
	var retypeNewPassword1 = zk.Widget.$('$retypeNewPassword1');
	retypeNewPassword1.$n().value=(encrypt(retypeNewPassword.getValue(), randomKey.getValue()));
	retypeNewPassword1.updateChange_();
	
	newPassword.$n().value=("");
	newPassword.fire('onChange', {"value": "", "start": "".length}, {toServer:true});
	newPassword.updateChange_();
	
	retypeNewPassword.$n().value=("");
	retypeNewPassword.fire('onChange', {"value": "", "start": "".length}, {toServer:true});
	retypeNewPassword.updateChange_();	
	
}

function onSubmitOK() {
	// Suppress for security
}