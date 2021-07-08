package com.pennanttech.ws.model.login;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoginResponse implements Serializable {

	private static final long serialVersionUID = 4240224916197625490L;

	@XmlElement
	private String userId;

	@XmlElement
	private String usertoken;

	@XmlElement
	private String roleCode;

	@XmlElement
	private ArrayList<String> userRights;

	@XmlElement
	private String userBranch;

	@XmlElement
	private String status;

	@XmlElement
	private Timestamp lastLoginOn;

	@XmlElement
	private Timestamp lastFailLoginOn;

	@XmlElement
	private WSReturnStatus returnStatus;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getUsertoken() {
		return usertoken;
	}

	public void setUsertoken(String usertoken) {
		this.usertoken = usertoken;
	}

	public ArrayList<String> getUserRights() {
		return userRights;
	}

	public void setUserRights(ArrayList<String> userRights) {
		this.userRights = userRights;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getUserBranch() {
		return userBranch;
	}

	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}

	public Timestamp getLastLoginOn() {
		return lastLoginOn;
	}

	public void setLastLoginOn(Timestamp lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}

	public Timestamp getLastFailLoginOn() {
		return lastFailLoginOn;
	}

	public void setLastFailLoginOn(Timestamp lastFailLoginOn) {
		this.lastFailLoginOn = lastFailLoginOn;
	}

}
