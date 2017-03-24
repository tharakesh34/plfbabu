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

function onSubmitOK() {
	// Suppress for security
}