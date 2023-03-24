package com.pennanttech.external.ucic.model;

public class ExtUcicData {
	private long id;
	private String ucicId;
	private String custId;
	private int processStatus;
	private String processDesc;

	public String getProcessDesc() {
		return processDesc;
	}

	public void setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUcicId() {
		return ucicId;
	}

	public void setUcicId(String ucicId) {
		this.ucicId = ucicId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}

}
