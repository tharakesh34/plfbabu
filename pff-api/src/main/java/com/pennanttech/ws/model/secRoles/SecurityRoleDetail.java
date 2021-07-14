package com.pennanttech.ws.model.secRoles;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityRole;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
public class SecurityRoleDetail {

	@XmlElementWrapper(name = "securityRoles")
	@JsonProperty("securityRole")
	private List<SecurityRole> secRoleList;

	@XmlElement
	private WSReturnStatus returnStatus;

	public List<SecurityRole> getSecRoleList() {
		return secRoleList;
	}

	public void setSecRoleList(List<SecurityRole> secRoleList) {
		this.secRoleList = secRoleList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}