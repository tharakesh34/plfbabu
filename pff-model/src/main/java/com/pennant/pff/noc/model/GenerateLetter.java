package com.pennant.pff.noc.model;

import java.math.BigDecimal;
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
	private String custCIF;
	private String coreBankId;
	private String letterType;
	private String requestType;
	private Long feeID;
	private Date createdDate;
	private Date createdOn;
	private Long createdBy;
	private int generated;
	private Date generatedDate;
	private Date generatedOn;
	private Long generatedBy;
	private Long approvedBy;
	private String approverName;
	private Timestamp approvedOn;
	private String custAcctHolderName;
	private String custCoreBank;
	private String finBranch;
	private String product;
	private String modeofTransfer;
	private String courierAgencyName;
	private String status;
	private String fileName;
	private String remarks;
	private Long adviseID;
	private GenerateLetter befImage;
	private LoggedInUser userDetails;
	private FinanceDetail financeDetail;
	private long agreementTemplate;
	private Long emailTemplate;
	private Long emailNotificationID;
	private String emailID;
	private String letterName;
	private String reasonCode;
	private String courierAgency;
	private String deliveryStatus;
	private Date deliveryDate;
	private Date dispatchDate;
	private BigDecimal waiverAmt = BigDecimal.ZERO;

	public GenerateLetter() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("finReference");
		excludeFields.add("requestType");
		excludeFields.add("feeID");
		excludeFields.add("createdDate");
		excludeFields.add("createdOn");
		excludeFields.add("createdBy");
		excludeFields.add("generated");
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
		excludeFields.add("emailTemplate");
		excludeFields.add("agreementTemplate");
		excludeFields.add("emailNotificationID");
		excludeFields.add("reasonCode");
		excludeFields.add("emailID");
		excludeFields.add("letterName");
		excludeFields.add("courierAgency");
		excludeFields.add("deliveryStatus");
		excludeFields.add("deliveryDate");
		excludeFields.add("dispatchDate");
		excludeFields.add("approverName");

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

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCoreBankId() {
		return coreBankId;
	}

	public void setCoreBankId(String coreBankId) {
		this.coreBankId = coreBankId;
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

	public Long getFeeID() {
		return feeID;
	}

	public void setFeeID(Long feeID) {
		this.feeID = feeID;
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

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public int getGenerated() {
		return generated;
	}

	public void setGenerated(int generated) {
		this.generated = generated;
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

	public Long getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(Long generatedBy) {
		this.generatedBy = generatedBy;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getAdviseID() {
		return adviseID;
	}

	public void setAdviseID(Long adviseID) {
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

	public long getAgreementTemplate() {
		return agreementTemplate;
	}

	public void setAgreementTemplate(long agreementTemplate) {
		this.agreementTemplate = agreementTemplate;
	}

	public Long getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(Long emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public Long getEmailNotificationID() {
		return emailNotificationID;
	}

	public void setEmailNotificationID(Long emailNotificationID) {
		this.emailNotificationID = emailNotificationID;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getLetterName() {
		return letterName;
	}

	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}

	public String getCourierAgency() {
		return courierAgency;
	}

	public void setCourierAgency(String courierAgency) {
		this.courierAgency = courierAgency;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public BigDecimal getWaiverAmt() {
		return waiverAmt;
	}

	public void setWaiverAmt(BigDecimal waiverAmt) {
		this.waiverAmt = waiverAmt;
	}
}