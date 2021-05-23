package com.pennanttech.pff.core.disbursement.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlAccessorType(XmlAccessType.NONE)
public class DisbursementRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Long batchId;
	@XmlElement
	private Long disbursementId;
	@XmlElement
	private String custCIF;
	@XmlElement
	private String finReference;
	@XmlElement
	private BigDecimal disbursementAmount;
	private String disbursementType;
	@XmlElement
	private Date disbursementDate;
	@XmlElement
	private String draweeLocation;
	@XmlElement
	private String printLocation;
	private String customerName;
	private String customerMobile;
	private String customerEmail;
	private String customerState;
	private String customerCity;
	private String customerAddress1;
	private String customerAddress2;
	private String customerAddress3;
	private String customerAddress4;
	private String customerAddress5;
	@XmlElement
	private String benficiaryBank;
	private String benficiaryBranch;
	private String benficiaryBranchState;
	private String benficiaryBranchCity;
	@XmlElement
	private String micrCode;
	@XmlElement
	private String ifscCode;
	@XmlElement
	private String benficiaryAccount;
	@XmlElement
	private String benficiaryName;
	private String benficiaryMobile;
	@XmlElement
	private String benficiryEmail;
	private String benficiryState;
	private String benficiryCity;
	private String benficiaryAddress1;
	private String benficiaryAddress2;
	private String benficiaryAddress3;
	private String benficiaryAddress4;
	private String benficiaryAddress5;
	private String paymentDetail1;
	private String paymentDetail2;
	private String paymentDetail3;
	private String paymentDetail4;
	private String paymentDetail5;
	private String paymentDetail6;
	private String paymentDetail7;
	private Long respBatchId;
	@XmlElement
	private String transactionref;
	@XmlElement
	private String chequeNumber;
	private String ddChequeCharge;
	@XmlElement
	private String paymentDate;
	@XmlElement
	private String status;
	private String remarks;
	@XmlElement
	private String rejectReason;
	@XmlElement
	private String channel;
	private boolean autoDownload;
	private Date realizationDate;
	private long headerId;
	private boolean alwFileDownload;
	@XmlElement
	private String finType;
	private long partnerBankId;
	@XmlElement
	private String partnerBankCode;
	private long userId;
	private Date createdOn;
	private String fileNamePrefix;
	private String dataEngineConfigName;
	private List<DisbursementRequest> disbursementRequests = new ArrayList<>();
	private List<FinAdvancePayments> finAdvancePayments = new ArrayList<>();
	private Date appValueDate;

	private long dataEngineConfig;
	private String postEvents;
	private String targetType;
	private String fileName;
	private String fileLocation;
	private String localRepLocation;
	private int processFlag;
	private Date processedOn;
	private String failureReason;
	private boolean disbursements;
	private boolean payments;
	private String bankCode;
	private LoggedInUser loggedInUser;
	private String partnerBankAccount;
	@XmlElement
	private String entityCode;
	@XmlElement
	private String paymentType;
	@XmlElement
	private String disbCCy;
	@XmlElement
	private WSReturnStatus returnStatus = null;
	private Date fromDate;
	private Date toDate;
	@XmlElement
	private long disbReqId;
	@XmlElement
	private long disbInstId;
	@XmlElement
	private Date clearingDate;
	@XmlElement
	private String disbType;
	private String requestSource = "OFF_LINE";
	private Long paymentId;
	@XmlElement // FIXME This field needs to be included in API Specification
	private String disbParty;
	private Date downloadedOn;

	public DisbursementRequest() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public Long getDisbursementId() {
		return disbursementId;
	}

	public void setDisbursementId(Long disbursementId) {
		this.disbursementId = disbursementId;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getDisbursementAmount() {
		return disbursementAmount;
	}

	public void setDisbursementAmount(BigDecimal disbursementAmount) {
		this.disbursementAmount = disbursementAmount;
	}

	public String getDisbursementType() {
		return disbursementType;
	}

	public void setDisbursementType(String disbursementType) {
		this.disbursementType = disbursementType;
	}

	public Date getDisbursementDate() {
		return disbursementDate;
	}

	public void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	public String getDraweeLocation() {
		return draweeLocation;
	}

	public void setDraweeLocation(String draweeLocation) {
		this.draweeLocation = draweeLocation;
	}

	public String getPrintLocation() {
		return printLocation;
	}

	public void setPrintLocation(String printLocation) {
		this.printLocation = printLocation;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerState() {
		return customerState;
	}

	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}

	public String getCustomerCity() {
		return customerCity;
	}

	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}

	public String getCustomerAddress1() {
		return customerAddress1;
	}

	public void setCustomerAddress1(String customerAddress1) {
		this.customerAddress1 = customerAddress1;
	}

	public String getCustomerAddress2() {
		return customerAddress2;
	}

	public void setCustomerAddress2(String customerAddress2) {
		this.customerAddress2 = customerAddress2;
	}

	public String getCustomerAddress3() {
		return customerAddress3;
	}

	public void setCustomerAddress3(String customerAddress3) {
		this.customerAddress3 = customerAddress3;
	}

	public String getCustomerAddress4() {
		return customerAddress4;
	}

	public void setCustomerAddress4(String customerAddress4) {
		this.customerAddress4 = customerAddress4;
	}

	public String getCustomerAddress5() {
		return customerAddress5;
	}

	public void setCustomerAddress5(String customerAddress5) {
		this.customerAddress5 = customerAddress5;
	}

	public String getBenficiaryBank() {
		return benficiaryBank;
	}

	public void setBenficiaryBank(String benficiaryBank) {
		this.benficiaryBank = benficiaryBank;
	}

	public String getBenficiaryBranch() {
		return benficiaryBranch;
	}

	public void setBenficiaryBranch(String benficiaryBranch) {
		this.benficiaryBranch = benficiaryBranch;
	}

	public String getBenficiaryBranchState() {
		return benficiaryBranchState;
	}

	public void setBenficiaryBranchState(String benficiaryBranchState) {
		this.benficiaryBranchState = benficiaryBranchState;
	}

	public String getBenficiaryBranchCity() {
		return benficiaryBranchCity;
	}

	public void setBenficiaryBranchCity(String benficiaryBranchCity) {
		this.benficiaryBranchCity = benficiaryBranchCity;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBenficiaryAccount() {
		return benficiaryAccount;
	}

	public void setBenficiaryAccount(String benficiaryAccount) {
		this.benficiaryAccount = benficiaryAccount;
	}

	public String getBenficiaryName() {
		return benficiaryName;
	}

	public void setBenficiaryName(String benficiaryName) {
		this.benficiaryName = benficiaryName;
	}

	public String getBenficiaryMobile() {
		return benficiaryMobile;
	}

	public void setBenficiaryMobile(String benficiaryMobile) {
		this.benficiaryMobile = benficiaryMobile;
	}

	public String getBenficiryEmail() {
		return benficiryEmail;
	}

	public void setBenficiryEmail(String benficiryEmail) {
		this.benficiryEmail = benficiryEmail;
	}

	public String getBenficiryState() {
		return benficiryState;
	}

	public void setBenficiryState(String benficiryState) {
		this.benficiryState = benficiryState;
	}

	public String getBenficiryCity() {
		return benficiryCity;
	}

	public void setBenficiryCity(String benficiryCity) {
		this.benficiryCity = benficiryCity;
	}

	public String getBenficiaryAddress1() {
		return benficiaryAddress1;
	}

	public void setBenficiaryAddress1(String benficiaryAddress1) {
		this.benficiaryAddress1 = benficiaryAddress1;
	}

	public String getBenficiaryAddress2() {
		return benficiaryAddress2;
	}

	public void setBenficiaryAddress2(String benficiaryAddress2) {
		this.benficiaryAddress2 = benficiaryAddress2;
	}

	public String getBenficiaryAddress3() {
		return benficiaryAddress3;
	}

	public void setBenficiaryAddress3(String benficiaryAddress3) {
		this.benficiaryAddress3 = benficiaryAddress3;
	}

	public String getBenficiaryAddress4() {
		return benficiaryAddress4;
	}

	public void setBenficiaryAddress4(String benficiaryAddress4) {
		this.benficiaryAddress4 = benficiaryAddress4;
	}

	public String getBenficiaryAddress5() {
		return benficiaryAddress5;
	}

	public void setBenficiaryAddress5(String benficiaryAddress5) {
		this.benficiaryAddress5 = benficiaryAddress5;
	}

	public String getPaymentDetail1() {
		return paymentDetail1;
	}

	public void setPaymentDetail1(String paymentDetail1) {
		this.paymentDetail1 = paymentDetail1;
	}

	public String getPaymentDetail2() {
		return paymentDetail2;
	}

	public void setPaymentDetail2(String paymentDetail2) {
		this.paymentDetail2 = paymentDetail2;
	}

	public String getPaymentDetail3() {
		return paymentDetail3;
	}

	public void setPaymentDetail3(String paymentDetail3) {
		this.paymentDetail3 = paymentDetail3;
	}

	public String getPaymentDetail4() {
		return paymentDetail4;
	}

	public void setPaymentDetail4(String paymentDetail4) {
		this.paymentDetail4 = paymentDetail4;
	}

	public String getPaymentDetail5() {
		return paymentDetail5;
	}

	public void setPaymentDetail5(String paymentDetail5) {
		this.paymentDetail5 = paymentDetail5;
	}

	public String getPaymentDetail6() {
		return paymentDetail6;
	}

	public void setPaymentDetail6(String paymentDetail6) {
		this.paymentDetail6 = paymentDetail6;
	}

	public String getPaymentDetail7() {
		return paymentDetail7;
	}

	public void setPaymentDetail7(String paymentDetail7) {
		this.paymentDetail7 = paymentDetail7;
	}

	public Long getRespBatchId() {
		return respBatchId;
	}

	public void setRespBatchId(Long respBatchId) {
		this.respBatchId = respBatchId;
	}

	public String getTransactionref() {
		return transactionref;
	}

	public void setTransactionref(String transactionref) {
		this.transactionref = transactionref;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getDdChequeCharge() {
		return ddChequeCharge;
	}

	public void setDdChequeCharge(String ddChequeCharge) {
		this.ddChequeCharge = ddChequeCharge;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean isAutoDownload() {
		return autoDownload;
	}

	public void setAutoDownload(boolean autoDownload) {
		this.autoDownload = autoDownload;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public boolean isAlwFileDownload() {
		return alwFileDownload;
	}

	public void setAlwFileDownload(boolean alwFileDownload) {
		this.alwFileDownload = alwFileDownload;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
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

	public List<DisbursementRequest> getDisbursementRequests() {
		return disbursementRequests;
	}

	public void setDisbursementRequests(List<DisbursementRequest> disbursementRequests) {
		this.disbursementRequests = disbursementRequests;
	}

	public List<FinAdvancePayments> getFinAdvancePayments() {
		return finAdvancePayments;
	}

	public void setFinAdvancePayments(List<FinAdvancePayments> finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public Date getAppValueDate() {
		return appValueDate;
	}

	public void setAppValueDate(Date appValueDate) {
		this.appValueDate = appValueDate;
	}

	public long getDataEngineConfig() {
		return dataEngineConfig;
	}

	public void setDataEngineConfig(long dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public String getPostEvents() {
		return postEvents;
	}

	public void setPostEvents(String postEvents) {
		this.postEvents = postEvents;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getLocalRepLocation() {
		return localRepLocation;
	}

	public void setLocalRepLocation(String localRepLocation) {
		this.localRepLocation = localRepLocation;
	}

	public int getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(int processFlag) {
		this.processFlag = processFlag;
	}

	public Date getProcessedOn() {
		return processedOn;
	}

	public void setProcessedOn(Date processedOn) {
		this.processedOn = processedOn;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public boolean isDisbursements() {
		return disbursements;
	}

	public void setDisbursements(boolean disbursements) {
		this.disbursements = disbursements;
	}

	public boolean isPayments() {
		return payments;
	}

	public void setPayments(boolean payments) {
		this.payments = payments;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public LoggedInUser getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(LoggedInUser loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public String getPartnerBankAccount() {
		return partnerBankAccount;
	}

	public void setPartnerBankAccount(String partnerBankAccount) {
		this.partnerBankAccount = partnerBankAccount;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getDisbCCy() {
		return disbCCy;
	}

	public void setDisbCCy(String disbCCy) {
		this.disbCCy = disbCCy;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public long getDisbReqId() {
		return disbReqId;
	}

	public void setDisbReqId(long disbReqId) {
		this.disbReqId = disbReqId;
	}

	public long getDisbInstId() {
		return disbInstId;
	}

	public void setDisbInstId(long disbInstId) {
		this.disbInstId = disbInstId;
	}

	public Date getClearingDate() {
		return clearingDate;
	}

	public void setClearingDate(Date clearingDate) {
		this.clearingDate = clearingDate;
	}

	public String getDisbType() {
		return disbType;
	}

	public void setDisbType(String disbType) {
		this.disbType = disbType;
	}

	public String getDisbParty() {
		return disbParty;
	}

	public void setDisbParty(String disbParty) {
		this.disbParty = disbParty;
	}

	public Date getDownloadedOn() {
		return downloadedOn;
	}

	public void setDownloadedOn(Date downloadedOn) {
		this.downloadedOn = downloadedOn;
	}
}
