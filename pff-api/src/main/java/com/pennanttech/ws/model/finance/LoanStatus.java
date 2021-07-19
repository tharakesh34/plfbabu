package com.pennanttech.ws.model.finance;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlType(propOrder = { "finReference", "roleCode", "recordStatus", "nextRoleCode", "returnStatus" })
@XmlRootElement(name = "finance")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoanStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String finReference;
	@XmlElement
	private String roleCode;
	@XmlElement
	private String recordStatus;
	@XmlElement
	private String nextRoleCode;
	@XmlElement
	private WSReturnStatus returnStatus;

	public LoanStatus() {
		super();
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
