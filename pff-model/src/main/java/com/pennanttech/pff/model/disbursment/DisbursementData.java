package com.pennanttech.pff.model.disbursment;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;

public class DisbursementData implements Serializable {
	private String finType;
	private long userId;
	private List<FinAdvancePayments> disbursements;
	private String fileNamePrefix;
	private String dataEngineConfigName;

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<FinAdvancePayments> getDisbursements() {
		return disbursements;
	}

	public void setDisbursements(List<FinAdvancePayments> disbusments) {
		this.disbursements = disbusments;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public String getDataEngineConfigName() {
		return dataEngineConfigName;
	}

	public void setDataEngineConfigName(String dataEngineConfigName) {
		this.dataEngineConfigName = dataEngineConfigName;
	}
}
