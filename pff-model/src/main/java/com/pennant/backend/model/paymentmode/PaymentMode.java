package com.pennant.backend.model.paymentmode;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.WSReturnStatus;

@XmlRootElement(name = "paymentMode")
@XmlAccessorType(XmlAccessType.NONE)
public class PaymentMode {

	@XmlElement
	private String finReference;
	@XmlElement
	private String loanInstrumentMode;
	@XmlElement
	private Date loanDueDate;
	@XmlElement
	private String bankName;
	@XmlElement
	private String bankCityName;
	@XmlElement
	private String micr;
	@XmlElement
	private String bankBranchName;
	@XmlElement
	private String accountNo;
	@XmlElement
	private String accountHolderName;
	@XmlElement
	private String accountType;
	@XmlElement
	private Integer installmentNo;
	@XmlElement
	private String pdcType;
	@XmlElement
	private Date chqDate;
	@XmlElement
	private String chqNo;
	@XmlElement
	private String chqStatus;
	@XmlElement
	private String bounceReason;
	@XmlElement
	private WSReturnStatus returnStatus;

	public PaymentMode() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getLoanInstrumentMode() {
		return loanInstrumentMode;
	}

	public void setLoanInstrumentMode(String loanInstrumentMode) {
		this.loanInstrumentMode = loanInstrumentMode;
	}

	public Date getLoanDueDate() {
		return loanDueDate;
	}

	public void setLoanDueDate(Date loanDueDate) {
		this.loanDueDate = loanDueDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCityName() {
		return bankCityName;
	}

	public void setBankCityName(String bankCityName) {
		this.bankCityName = bankCityName;
	}

	public String getMicr() {
		return micr;
	}

	public void setMicr(String micr) {
		this.micr = micr;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public int getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(int installmentNo) {
		this.installmentNo = installmentNo;
	}

	public String getPdcType() {
		return pdcType;
	}

	public void setPdcType(String pdcType) {
		this.pdcType = pdcType;
	}

	public Date getChqDate() {
		return chqDate;
	}

	public void setChqDate(Date chqDate) {
		this.chqDate = chqDate;
	}

	public String getChqNo() {
		return chqNo;
	}

	public void setChqNo(String chqNo) {
		this.chqNo = chqNo;
	}

	public String getChqStatus() {
		return chqStatus;
	}

	public void setChqStatus(String chqStatus) {
		this.chqStatus = chqStatus;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
