package com.pennant.backend.model.pennydrop;

public class PennyDropStatus {

	private long ID = Long.MIN_VALUE;
	private String acctNum;
	private String iFSC;
	private String initiateType;
	private boolean status;
	private String reason;

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getiFSC() {
		return iFSC;
	}

	public void setiFSC(String iFSC) {
		this.iFSC = iFSC;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getInitiateType() {
		return initiateType;
	}

	public void setInitiateType(String initiateType) {
		this.initiateType = initiateType;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getAcctNum() {
		return acctNum;
	}

	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}

}
