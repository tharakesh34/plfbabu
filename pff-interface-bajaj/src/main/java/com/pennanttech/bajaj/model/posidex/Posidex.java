package com.pennanttech.bajaj.model.posidex;

import java.io.Serializable;

public class Posidex implements Serializable {
	static final long serialVersionUID = 1L;

	private long batchID;
	private String customerId;
	private String customerNo;
	private String sourceSysId;
	private String processType;
	private String processFlag = "N";
	private String errorCode;
	private String errorDesc;
	private String sourceSystem;
	private String psxBatchID;
	private String eodBatchID;
	private String segment;

	private PosidexCustomer posidexCustomer;

	public Posidex() {
		super();
	}

	public long getBatchID() {
		return batchID;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public void setBatchID(long batchID) {
		this.batchID = batchID;
	}

	public String getSourceSysId() {
		return sourceSysId;
	}

	public void setSourceSysId(String sourceSysId) {
		this.sourceSysId = sourceSysId;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(String processFlag) {
		this.processFlag = processFlag;
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

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getPsxBatchID() {
		return psxBatchID;
	}

	public void setPsxBatchID(String psxBatchID) {
		this.psxBatchID = psxBatchID;
	}

	public String getEodBatchID() {
		return eodBatchID;
	}

	public void setEodBatchID(String eodBatchID) {
		this.eodBatchID = eodBatchID;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public PosidexCustomer getPosidexCustomer() {
		return posidexCustomer;
	}

	public void setPosidexCustomer(PosidexCustomer posidexCustomer) {
		this.posidexCustomer = posidexCustomer;
	}

}