package com.pennanttech.pff.model.disbursment;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DisbursementData implements Serializable {
	private static final long serialVersionUID = 1L;
	private String finType;
	private long userId;
	private LoggedInUser userDetails;
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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
