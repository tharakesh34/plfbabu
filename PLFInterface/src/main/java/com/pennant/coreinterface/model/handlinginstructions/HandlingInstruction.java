package com.pennant.coreinterface.model.handlinginstructions;

import java.io.Serializable;
import java.util.Date;

public class HandlingInstruction implements Serializable {

	private static final long serialVersionUID = -4800702577266933596L;

	public HandlingInstruction() {
		super();
	}

	private String referenceNum;
	private String maintenanceCode;
	private String financeRef;
	private Date installmentDate;
	private Date newMaturityDate;
	private String remarks;
	private String returnCode;
	private String returnText;
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

	public String getMaintenanceCode() {
		return maintenanceCode;
	}

	public void setMaintenanceCode(String maintenanceCode) {
		this.maintenanceCode = maintenanceCode;
	}

	public String getFinanceRef() {
		return financeRef;
	}

	public void setFinanceRef(String financeRef) {
		this.financeRef = financeRef;
	}

	public Date getInstallmentDate() {
		return installmentDate;
	}

	public void setInstallmentDate(Date installmentDate) {
		this.installmentDate = installmentDate;
	}

	public Date getNewMaturityDate() {
		return newMaturityDate;
	}

	public void setNewMaturityDate(Date newMaturityDate) {
		this.newMaturityDate = newMaturityDate;
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
}
