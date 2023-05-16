package com.pennant.pff.noc.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class GenerateLetter extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long finID;
	private String finReference;
	private String letterType;
	private String requestType;
	private long feeId;
	private Date createdDate;
	private Date createdOn;
	private long createdBy;
	private Date generatedDate;
	private Date generatedOn;
	private long generatedBy;
	private long approvedBy;
	private Timestamp approvedOn;
	private String custAcctHolderName;
	private String custCoreBank;
	private String finBranch;
	private String product;
	private String modeofTransfer;
	private String courierAgencyName;
	private String status;
	private String email;
	private String fileName;
	private long trackingID;
	private String remarks;
	private long adviseID;
	private GenerateLetter befImage;
	private LoggedInUser userDetails;
	private FinanceDetail financeDetail;

	public GenerateLetter() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("finReference");
		excludeFields.add("requestType");
		excludeFields.add("feeId");
		excludeFields.add("createdDate");
		excludeFields.add("createdOn");
		excludeFields.add("createdBy");
		excludeFields.add("generatedDate");
		excludeFields.add("generatedOn");
		excludeFields.add("generatedBy");
		excludeFields.add("custAcctHolderName");
		excludeFields.add("custCoreBank");
		excludeFields.add("finBranch");
		excludeFields.add("product");
		excludeFields.add("modeofTransfer");
		excludeFields.add("courierAgencyName");
		excludeFields.add("status");
		excludeFields.add("email");
		excludeFields.add("fileName");
		excludeFields.add("trackingID");
		excludeFields.add("remarks");
		excludeFields.add("adviseID");
		excludeFields.add("befImage");
		excludeFields.add("userDetails");
		excludeFields.add("financeDetail");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public long getFeeId() {
		return feeId;
	}

	public void setFeeId(long feeId) {
		this.feeId = feeId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public Date getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(Date generatedOn) {
		this.generatedOn = generatedOn;
	}

	public long getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(long generatedBy) {
		this.generatedBy = generatedBy;
	}

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getCustAcctHolderName() {
		return custAcctHolderName;
	}

	public void setCustAcctHolderName(String custAcctHolderName) {
		this.custAcctHolderName = custAcctHolderName;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getModeofTransfer() {
		return modeofTransfer;
	}

	public void setModeofTransfer(String modeofTransfer) {
		this.modeofTransfer = modeofTransfer;
	}

	public String getCourierAgencyName() {
		return courierAgencyName;
	}

	public void setCourierAgencyName(String courierAgencyName) {
		this.courierAgencyName = courierAgencyName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getTrackingID() {
		return trackingID;
	}

	public void setTrackingID(long trackingID) {
		this.trackingID = trackingID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getAdviseID() {
		return adviseID;
	}

	public void setAdviseID(long adviseID) {
		this.adviseID = adviseID;
	}

	public GenerateLetter getBefImage() {
		return befImage;
	}

	public void setBefImage(GenerateLetter befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}