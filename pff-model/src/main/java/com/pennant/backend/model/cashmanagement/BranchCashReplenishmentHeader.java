package com.pennant.backend.model.cashmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlTransient;

public class BranchCashReplenishmentHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 2067917529733168733L;
	private long processId;
	private Date transactionDate;
	private String RequestType;
	private String branchCode;
	private int recordCount;
	private String downLoadStatus;
	private long partnerBankId = 0;
	private long downloadBatchId = 0;
	private String downloadFile;
	private long downLoadedBy = 0;
	private Date DownloadedDate;
	private String uploadStatus;
	private long uploadBatchId = 0;
	private String uploadFile;
	private long uploadedBy = 0;
	private Date uploadedDate;

	@XmlTransient
	private LoggedInUser userDetails;

	private List<BranchCashReplenishmentDetail> cashReplenishmentDetails;

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getRequestType() {
		return RequestType;
	}

	public void setRequestType(String requestType) {
		RequestType = requestType;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public String getDownLoadStatus() {
		return downLoadStatus;
	}

	public void setDownLoadStatus(String downLoadStatus) {
		this.downLoadStatus = downLoadStatus;
	}

	public long getDownloadBatchId() {
		return downloadBatchId;
	}

	public void setDownloadBatchId(long downloadBatchId) {
		this.downloadBatchId = downloadBatchId;
	}

	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	public long getDownLoadedBy() {
		return downLoadedBy;
	}

	public void setDownLoadedBy(long downLoadedBy) {
		this.downLoadedBy = downLoadedBy;
	}

	public Date getDownloadedDate() {
		return DownloadedDate;
	}

	public void setDownloadedDate(Date downloadedDate) {
		DownloadedDate = downloadedDate;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public long getUploadBatchId() {
		return uploadBatchId;
	}

	public void setUploadBatchId(long uploadBatchId) {
		this.uploadBatchId = uploadBatchId;
	}

	public String getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(String uploadFile) {
		this.uploadFile = uploadFile;
	}

	public long getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public List<BranchCashReplenishmentDetail> getCashReplenishmentDetails() {
		return cashReplenishmentDetails;
	}

	public void setCashReplenishmentDetails(List<BranchCashReplenishmentDetail> cashReplenishmentDetails) {
		this.cashReplenishmentDetails = cashReplenishmentDetails;
	}

	public void setCashReplenishmentDetails(BranchCashReplenishmentDetail cashReplenishmentDetail) {
		if (cashReplenishmentDetails == null) {
			cashReplenishmentDetails = new ArrayList<BranchCashReplenishmentDetail>();
		}
		this.cashReplenishmentDetails.add(cashReplenishmentDetail);
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getId() {
		return processId;
	}

	@Override
	public void setId(long id) {
		processId = id;

	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

}
