package com.pennant.backend.model.hostglmapping.upload;

import java.util.Date;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class HostGLMappingUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String gLCode;
	private String hostGLCode;
	private String accountType;
	private String loanType;
	private String costCentreCode;
	private String profitCentreCode;
	private Date openedDate;
	private String allowManualEntries;
	private LoggedInUser userDetails;
	private Date closedDate;
	private String gLStatus;
	private Long profitCenterID;
	private Long costCenterID;
	private String gLDescription;

	public HostGLMappingUpload() {
		super();
	}

	public String getGLCode() {
		return gLCode;
	}

	public void setGLCode(String gLCode) {
		this.gLCode = gLCode;
	}

	public String getHostGLCode() {
		return hostGLCode;
	}

	public void setHostGLCode(String hostGLCode) {
		this.hostGLCode = hostGLCode;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getCostCentreCode() {
		return costCentreCode;
	}

	public void setCostCentreCode(String costCentreCode) {
		this.costCentreCode = costCentreCode;
	}

	public String getProfitCentreCode() {
		return profitCentreCode;
	}

	public void setProfitCentreCode(String profitCentreCode) {
		this.profitCentreCode = profitCentreCode;
	}

	public Date getOpenedDate() {
		return openedDate;
	}

	public void setOpenedDate(Date openedDate) {
		this.openedDate = openedDate;
	}

	public String getAllowManualEntries() {
		return allowManualEntries;
	}

	public void setAllowManualEntries(String allowManualEntries) {
		this.allowManualEntries = allowManualEntries;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public String getgLStatus() {
		return gLStatus;
	}

	public void setgLStatus(String gLStatus) {
		this.gLStatus = gLStatus;
	}

	public Long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(Long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public Long getCostCenterID() {
		return costCenterID;
	}

	public void setCostCenterID(Long costCenterID) {
		this.costCenterID = costCenterID;
	}

	public String getGLDescription() {
		return gLDescription;
	}

	public void setGLDescription(String gLDescription) {
		this.gLDescription = gLDescription;
	}

}
