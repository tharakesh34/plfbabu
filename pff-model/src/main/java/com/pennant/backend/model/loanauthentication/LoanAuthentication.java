package com.pennant.backend.model.loanauthentication;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlType(propOrder = { "finReference", "dateOfBirth", "loanEMI" })

@XmlRootElement(name = "loanAuthentication")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanAuthentication {

	@XmlElement
	private String finReference;
	@XmlElement(name = "loanInstallmentAmt")
	private BigDecimal loanEMI;
	@XmlElement
	private Date dateOfBirth;
	private boolean valid;
	@XmlElement
	private String mobileEmailId;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private String validFlag;
	private Date appDate;

	public LoanAuthentication() {
		super();
	}

	public BigDecimal getLoanEMI() {
		return loanEMI;
	}

	public void setLoanEMI(BigDecimal loanEMI) {
		this.loanEMI = loanEMI;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String isValidFlag() {
		return validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}

	public String getMobileEmailId() {
		return mobileEmailId;
	}

	public void setMobileEmailId(String mobileEmailId) {
		this.mobileEmailId = mobileEmailId;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

}
