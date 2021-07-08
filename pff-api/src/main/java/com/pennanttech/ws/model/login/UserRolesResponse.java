package com.pennanttech.ws.model.login;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserRolesResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private String roleCodes;
	@XmlElement
	private WSReturnStatus returnStatus;

	public UserRolesResponse() {
		super();
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
