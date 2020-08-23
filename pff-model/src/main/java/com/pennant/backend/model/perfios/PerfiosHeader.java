package com.pennant.backend.model.perfios;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerBankInfo;

public class PerfiosHeader implements Serializable {

	private static final long serialVersionUID = 1L;
	private long referenceId;
	private String transactionId;
	private String financeReference;
	private String customerCIF;
	private BigDecimal financeAmount = BigDecimal.ZERO;
	private int financeTenor;
	private String financeType;
	private String processingType;
	private String institutionId;
	private boolean scannedDocument = false;
	private String statementFrom;
	private String statementTo;
	private String processStage;
	private String statusCode;
	private String statusDesc;
	private boolean active = false;
	private String perfiosTransId;
	private Long docRefId;
	private String branch;

	/* Below fields are excluded */
	private String employmentType;
	private String employerNames;
	private String companyName;
	private String facility;
	private boolean limitType = false;
	private BigDecimal sanctionLimit = BigDecimal.ZERO;
	private BigDecimal drawingPower = BigDecimal.ZERO;
	private List<PerfiousDetail> detailList;
	private int monthRange;
	private List<CustomerBankInfo> custBankInfoList = new ArrayList<CustomerBankInfo>();
	private byte[] docImage;
	private String docName;
	private Timestamp perfiosInitOn;
	private Timestamp perfiosUpdateOn;
	private String offerId;
	private String applicationNo;

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}

	public String getFinanceReference() {
		return financeReference;
	}

	public void setFinanceReference(String financeReference) {
		this.financeReference = financeReference;
	}

	public String getCustomerCIF() {
		return customerCIF;
	}

	public void setCustomerCIF(String customerCIF) {
		this.customerCIF = customerCIF;
	}

	public BigDecimal getFinanceAmount() {
		return financeAmount;
	}

	public void setFinanceAmount(BigDecimal financeAmount) {
		this.financeAmount = financeAmount;
	}

	public int getFinanceTenor() {
		return financeTenor;
	}

	public void setFinanceTenor(int financeTenor) {
		this.financeTenor = financeTenor;
	}

	public String getFinanceType() {
		return financeType;
	}

	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}

	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public boolean isScannedDocument() {
		return scannedDocument;
	}

	public void setScannedDocument(boolean scannedDocument) {
		this.scannedDocument = scannedDocument;
	}

	public String getStatementFrom() {
		return statementFrom;
	}

	public void setStatementFrom(String statementFrom) {
		this.statementFrom = statementFrom;
	}

	public String getStatementTo() {
		return statementTo;
	}

	public void setStatementTo(String statementTo) {
		this.statementTo = statementTo;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getEmployerNames() {
		return employerNames;
	}

	public void setEmployerNames(String employerNames) {
		this.employerNames = employerNames;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public boolean isLimitType() {
		return limitType;
	}

	public void setLimitType(boolean limitType) {
		this.limitType = limitType;
	}

	public BigDecimal getSanctionLimit() {
		return sanctionLimit;
	}

	public void setSanctionLimit(BigDecimal sanctionLimit) {
		this.sanctionLimit = sanctionLimit;
	}

	public BigDecimal getDrawingPower() {
		return drawingPower;
	}

	public void setDrawingPower(BigDecimal drawingPower) {
		this.drawingPower = drawingPower;
	}

	public String getProcessStage() {
		return processStage;
	}

	public void setProcessStage(String processStage) {
		this.processStage = processStage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getPerfiosTransId() {
		return perfiosTransId;
	}

	public void setPerfiosTransId(String perfiosTransId) {
		this.perfiosTransId = perfiosTransId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<PerfiousDetail> getDetailList() {
		if (this.detailList == null) {
			detailList = new ArrayList<>();
		}
		return detailList;
	}

	public void setDetailList(List<PerfiousDetail> detailList) {
		this.detailList = detailList;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public int getMonthRange() {
		return monthRange;
	}

	public void setMonthRange(int monthRange) {
		this.monthRange = monthRange;
	}

	public List<CustomerBankInfo> getCustBankInfoList() {
		return custBankInfoList;
	}

	public void setCustBankInfoList(List<CustomerBankInfo> custBankInfoList) {
		this.custBankInfoList = custBankInfoList;
	}

	public Long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(Long docRefId) {
		this.docRefId = docRefId;
	}

	public byte[] getDocImage() {
		return docImage;
	}

	public void setDocImage(byte[] docImage) {
		this.docImage = docImage;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public Timestamp getPerfiosInitOn() {
		return perfiosInitOn;
	}

	public void setPerfiosInitOn(Timestamp perfiosInitOn) {
		this.perfiosInitOn = perfiosInitOn;
	}

	public Timestamp getPerfiosUpdateOn() {
		return perfiosUpdateOn;
	}

	public void setPerfiosUpdateOn(Timestamp perfiosUpdateOn) {
		this.perfiosUpdateOn = perfiosUpdateOn;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

}
