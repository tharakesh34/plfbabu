package com.pennant.coreinterface.model.chequeverification;

import java.io.Serializable;
import java.util.List;

public class ChequeVerification implements Serializable {

	private static final long serialVersionUID = 120218130745689614L;

	public ChequeVerification() {
		super();
	}

	private String referenceNum;
	private String custCIF;
	private String financeRef;
	private String ChequeRangeFrom;
	private String ChequeRangeTo;
	private String remarks;
	private String BranchCode;
	private List<ChequeStatus> chequeStsList;
	private String returnCode;
	private String returnText;
	private String chequeNumber;
	private long timeStamp;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getFinanceRef() {
		return financeRef;
	}

	public void setFinanceRef(String financeRef) {
		this.financeRef = financeRef;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getChequeRangeFrom() {
		return ChequeRangeFrom;
	}

	public void setChequeRangeFrom(String chequeRangeFrom) {
		ChequeRangeFrom = chequeRangeFrom;
	}

	public String getChequeRangeTo() {
		return ChequeRangeTo;
	}

	public void setChequeRangeTo(String chequeRangeTo) {
		ChequeRangeTo = chequeRangeTo;
	}

	public String getBranchCode() {
		return BranchCode;
	}

	public void setBranchCode(String branchCode) {
		BranchCode = branchCode;
	}

	public List<ChequeStatus> getChequeStsList() {
		return chequeStsList;
	}

	public void setChequeStsList(List<ChequeStatus> chequeStsList) {
		this.chequeStsList = chequeStsList;
	}
	
	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

}
