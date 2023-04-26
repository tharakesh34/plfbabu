package com.pennanttech.pff.autowriteoff.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class AutoWriteOffLoan implements Serializable {
	private static final long serialVersionUID = -2959039603130858848L;

	private long finID;
	private String finReference;
	private String code;
	private Timestamp executionDate;
	private String errorMsg;
	private String status;

	public AutoWriteOffLoan() {
		super();
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

}