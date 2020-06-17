package com.pennant.datamigration.model;

import java.io.Serializable;
import java.util.Date;

public class TabAgreementDate implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private long finID = Long.MIN_VALUE;
	private String agreementNo;
	private Date proposalDate;
	private Date agreementDate;
	private Date disbursalDate;
	private Date intStartDate;
	private boolean qdpFlag = false;
	public String getAgreementNo() {
		return agreementNo;
	}
	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}
	public Date getProposalDate() {
		return proposalDate;
	}
	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}
	public Date getAgreementDate() {
		return agreementDate;
	}
	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}
	public Date getDisbursalDate() {
		return disbursalDate;
	}
	public void setDisbursalDate(Date disbursalDate) {
		this.disbursalDate = disbursalDate;
	}
	public Date getIntStartDate() {
		return intStartDate;
	}
	public void setIntStartDate(Date intStartDate) {
		this.intStartDate = intStartDate;
	}
	public boolean isQdpFlag() {
		return qdpFlag;
	}
	public void setQdpFlag(boolean qdpFlag) {
		this.qdpFlag = qdpFlag;
	}
	public long getFinID() {
		return finID;
	}
	public void setFinID(long finID) {
		this.finID = finID;
	}

}
