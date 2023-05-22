package com.pennant.backend.model.applicationmaster;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "finReference", "custID", "custCIF", "custShrtName", "pANNumber", "phoneNumber" })
@XmlAccessorType(XmlAccessType.NONE)
public class LoanPendingData extends AbstractWorkflowEntity implements java.io.Serializable {
	private static final long serialVersionUID = 7058074481411678596L;

	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private String custCIF;
	@XmlElement
	private String custShrtName;
	@XmlElement
	private String pANNumber;
	@XmlElement
	private String phoneNumber;
	@XmlElement
	private long custID;
	@XmlElement
	private String currentRole;

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getpANNumber() {
		return pANNumber;
	}

	public void setpANNumber(String pANNumber) {
		this.pANNumber = pANNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}

}
