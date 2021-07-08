package com.pennant.backend.model.applicationmaster;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "finReference", "custID", "custCIF", "custShrtName", "pANNumber", "phoneNumber" })
@XmlAccessorType(XmlAccessType.NONE)
public class LoanPendingData extends AbstractWorkflowEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7058074481411678596L;

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

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
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

}
