package com.pennant.backend.model.letter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Letter implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long finID;
	private String finReference;
	private String requestType;
	private Long feeTypeID;
	private Date createdDate;
	private Date createdOn;
	private long createdBy;
	private int generated;
	private Date generatedDate;
	private Date generatedOn;
	private long generatedBy;
	private long approvedBy;
	private Timestamp approvedOn;
	private String custAcctHolderName;
	private String finBranch;
	private String product;
	private String modeofTransfer;
	private String courierAgencyName;
	private String status;
	private String email;
	private String fileName;
	private Long trackingID;
	private String remarks;
	private Long adviseID;
	private GenerateLetter befImage;
	private LoggedInUser userDetails;
	private FinanceDetail financeDetail;
	private long agreementTemplate;
	private Long emailTemplate;

	private int sequence;
	private Date appDate;
	private String letterName;
	private String letterDesc;
	private String letterType;
	private String letterMode;
	private int saveFormat;
	private byte[] content;

	private long custID;
	private String custCif;
	private String custCoreBank;
	private String custSalutationCode;
	private String custShrtName;
	private String custFullName;
	private String finType;
	private String finTypeDesc;
	private String finStartDate;
	private String strAppDate;

	private MailTemplate mailTemplate;
	private ServiceBranch serviceBranch;
	private EventProperties eventProperties;

	public Letter() {
		super();
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

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(Long feeTypeID) {
		this.feeTypeID = feeTypeID;
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

	public Long getTrackingID() {
		return trackingID;
	}

	public void setTrackingID(Long trackingID) {
		this.trackingID = trackingID;
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

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getLetterName() {
		return letterName;
	}

	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}

	public String getLetterDesc() {
		return letterDesc;
	}

	public void setLetterDesc(String letterDesc) {
		this.letterDesc = letterDesc;
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public String getLetterMode() {
		return letterMode;
	}

	public void setLetterMode(String letterMode) {
		this.letterMode = letterMode;
	}

	public int getSaveFormat() {
		return saveFormat;
	}

	public void setSaveFormat(int saveFormat) {
		this.saveFormat = saveFormat;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getCustFullName() {
		return custFullName;
	}

	public void setCustFullName(String custFullName) {
		this.custFullName = custFullName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(String finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getStrAppDate() {
		return strAppDate;
	}

	public void setStrAppDate(String strAppDate) {
		this.strAppDate = strAppDate;
	}

	public MailTemplate getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public ServiceBranch getServiceBranch() {
		return serviceBranch;
	}

	public void setServiceBranch(ServiceBranch serviceBranch) {
		this.serviceBranch = serviceBranch;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

}
