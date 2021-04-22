package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class DDAFTransactionLog implements Serializable, Entity {

	private static final long serialVersionUID = 2416587325149632633L;

	public DDAFTransactionLog() {

	}

	private long seqNo = Long.MIN_VALUE;
	private Date valueDate;
	private String finRefence;
	private String error;
	private String errorCode;
	private String errorDesc;
	private int noofTries;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public long getId() {
		return seqNo;
	}

	@Override
	public void setId(long id) {
		this.seqNo = id;
	}

	public long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}

	public String getFinRefence() {
		return finRefence;
	}

	public void setFinRefence(String finRefence) {
		this.finRefence = finRefence;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public int getNoofTries() {
		return noofTries;
	}

	public void setNoofTries(int noofTries) {
		this.noofTries = noofTries;
	}

}
