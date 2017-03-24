package com.pennanttech.ws.model.secRoles;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.administration.SecurityRole;

@XmlAccessorType(XmlAccessType.NONE)
public class SecurityRoleDetail {

	@XmlElementWrapper(name="securityRoles")
	@XmlElement(name="securityRole")
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