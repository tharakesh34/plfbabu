package com.pennanttech.external.silien.model;

import java.util.Date;

public class LienMarkDetail {
	private long finId;
	private long custId;
	private String accNumber;
	private String lienMark;
	private int status;
	private Date createdDate;

	private String interfaceStatus;
	private String interfaceReason;

	private String errCode;
	private String errMsg;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public long getFinId() {
		return finId;
	}

	public void setFinId(long finId) {
		this.finId = finId;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getLienMark() {
		return lienMark;
	}

	public void setLienMark(String lienMark) {
		this.lienMark = lienMark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getInterfaceStatus() {
		return interfaceStatus;
	}

	public void setInterfaceStatus(String interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
	}

	public String getInterfaceReason() {
		return interfaceReason;
	}

	public void setInterfaceReason(String interfaceReason) {
		this.interfaceReason = interfaceReason;
	}

	@Override
	public String toString() {
		return "LienMarkDetail [finId=" + finId + ", custId=" + custId + ", accNumber=" + accNumber + ", lienMark="
				+ lienMark + ", status=" + status + "]";
	}

}
