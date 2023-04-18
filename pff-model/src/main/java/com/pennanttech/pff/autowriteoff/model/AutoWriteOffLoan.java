package com.pennanttech.pff.autowriteoff.model;

import java.sql.Timestamp;

public class AutoWriteOffLoan {

	private long finID;
	private String finRef;
	private String code;
	private Timestamp executionDate;
	private String errorMsg;
	private String status;

	public String getFinRef() {
		return finRef;
	}

	public void setFinRef(String finRef) {
		this.finRef = finRef;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Timestamp getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Timestamp executionDate) {
		this.executionDate = executionDate;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

}
