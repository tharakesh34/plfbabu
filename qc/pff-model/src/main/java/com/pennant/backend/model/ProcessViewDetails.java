package com.pennant.backend.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "processView")
@XmlAccessorType(XmlAccessType.NONE)
public class ProcessViewDetails implements Serializable {
	private static final long serialVersionUID = 6296021761692873171L;

	@XmlElement
	private List<String> roles;
	@XmlElement
	private String rolesWithCount;
	@XmlElement
	private List<String> recordStatuses;
	@XmlElement
	private List<String> visitedRoles;
	@XmlElement
	private WSReturnStatus returnStatus;

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getRolesWithCount() {
		return rolesWithCount;
	}

	public void setRolesWithCount(String rolesWithCount) {
		this.rolesWithCount = rolesWithCount;
	}

	public List<String> getRecordStatuses() {
		return recordStatuses;
	}

	public void setRecordStatuses(List<String> recordStatuses) {
		this.recordStatuses = recordStatuses;
	}

	public List<String> getVisitedRoles() {
		return visitedRoles;
	}

	public void setVisitedRoles(List<String> visitedRoles) {
		this.visitedRoles = visitedRoles;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
