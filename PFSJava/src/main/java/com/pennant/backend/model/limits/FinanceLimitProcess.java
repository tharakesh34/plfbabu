package com.pennant.backend.model.limits;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class FinanceLimitProcess implements Serializable {

	private static final long serialVersionUID = -6204271721852032496L;

	private long finLimitId;
	private long finID;
	private String finReference;
	private String requestType;
	private String referenceNum;
	private String custCIF;
	private String limitRef;
	private String resStatus;
	private String resMessage;
	private String errorCode;
	private String errorMsg;
	private Timestamp valueDate;
	private BigDecimal dealAmount = BigDecimal.ZERO;

	public FinanceLimitProcess() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getFinLimitId() {
		return finLimitId;
	}

	public void setFinLimitId(long finLimitId) {
		this.finLimitId = finLimitId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	public String getResStatus() {
		return resStatus;
	}

	public void setResStatus(String resStatus) {
		this.resStatus = resStatus;
	}

	public String getResMessage() {
		return resMessage;
	}

	public void setResMessage(String resMessage) {
		this.resMessage = resMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Timestamp getValueDate() {
		return valueDate;
	}

	public void setValueDate(Timestamp valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}

	public long getId() {
		return finLimitId;
	}

	public void setId(long id) {
		this.finLimitId = id;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

}
