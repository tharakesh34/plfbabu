package com.pennanttech.ws.model.login;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoginRequest implements Serializable {

	private static final long serialVersionUID = 4240224916197625490L;

	private String userName;
	private String userPwd;
	private String registrationId;

	public LoginRequest() {
		super();
	}

	public String getUsrName() {
		return userName;
	}

	public void setUsrName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

}
