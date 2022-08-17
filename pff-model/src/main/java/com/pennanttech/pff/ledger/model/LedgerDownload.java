package com.pennanttech.pff.ledger.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class LedgerDownload implements Serializable {
	private static final long serialVersionUID = 1L;

	private long iD;
	private long custID;
	private String custCif;
	private String custShrtName;
	private long finID;
	private String finReference;
	private long linkedTranID;
	private Long receiptId;
	private String receiptMode;
	private Date appDate;
	private String postRef;
	private String statusCode;
	private String ledgerID;
	private Date tranDate;
	private String journalSource;
	private String journalCategory;
	private String currencyCode;
	private Date jeCreateDate;
	private String actualFlag;
	private String entity;
	private String branchCode;
	private String glAccount;
	private String department;
	private String lobCode;
	private String productCode;
	private String partners;
	private String interCompany;
	private String future1;
	private String future2;
	private String future3;
	private String segment12;
	private String segment13;
	private String segment14;
	private String segment15;
	private String segment16;
	private String segment17;
	private String segment18;
	private String segment19;
	private String segment20;
	private String segment21;
	private String segment22;
	private String segment23;
	private String segment24;
	private String segment25;
	private String segment26;
	private String segment27;
	private String segment28;
	private String segment29;
	private String segment30;
	private BigDecimal creditAmount;
	private BigDecimal debitAmount;
	private String convertedDebitAmount;
	private String convertedCreditAmount;
	private String loadID;
	private String systemName;
	private String reference3;
	private String reference4;
	private String reference5;
	private String reference6;
	private String reference7;
	private String reference8;
	private String reference9;
	private String lineDescription;
	private String referenceColumn1;
	private String referenceColumn2;
	private String referenceColumn3;
	private String referenceColumn4;
	private String referenceColumn5;
	private String referenceColumn6;
	private String referenceColumn7;
	private String referenceColumn8;
	private String referenceColumn9;
	private String referenceColumn10;
	private String statisticalAmount;
	private String currencyConversionType;
	private String currencyConversionDate;
	private String currencyConversionRate;
	private Date extractionDate;
	private String contextField;
	private String attribute1;
	private String attribute4;
	private String attribute5;
	private String attribute6;
	private String attribute7;
	private String attribute8;
	private String attribute9;
	private String attribute10;
	private String attribute11;
	private String attribute12;
	private String attribute13;
	private String attribute14;
	private String attribute15;
	private String attribute16;
	private String attribute17;
	private String attribute18;
	private String attribute19;
	private String attribute20;
	private String capturedInfoDFF;
	private String avgJournalFlag;
	private String clearingCompany;
	private String ledgerName;
	private String encumbranceTypeID;
	private String reconciliationReference;
	private String periodName;
	private BigDecimal postAmount;
	private String drorCr;
	private String closingStatus;

	public LedgerDownload() {
		super();
	}

	public long getID() {
		return iD;
	}

	public void setID(long ID) {
		this.iD = ID;
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

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
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

	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getPostRef() {
		return postRef;
	}

	public void setPostRef(String postRef) {
		this.postRef = postRef;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getLedgerID() {
		return ledgerID;
	}

	public void setLedgerID(String ledgerID) {
		this.ledgerID = ledgerID;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public String getJournalSource() {
		return journalSource;
	}

	public void setJournalSource(String journalSource) {
		this.journalSource = journalSource;
	}

	public String getJournalCategory() {
		return journalCategory;
	}

	public void setJournalCategory(String journalCategory) {
		this.journalCategory = journalCategory;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Date getJeCreateDate() {
		return jeCreateDate;
	}

	public void setJeCreateDate(Date jeCreateDate) {
		this.jeCreateDate = jeCreateDate;
	}

	public String getActualFlag() {
		return actualFlag;
	}

	public void setActualFlag(String actualFlag) {
		this.actualFlag = actualFlag;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getGlAccount() {
		return glAccount;
	}

	public void setGlAccount(String glAccount) {
		this.glAccount = glAccount;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getLobCode() {
		return lobCode;
	}

	public void setLobCode(String lobCode) {
		this.lobCode = lobCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getPartners() {
		return partners;
	}

	public void setPartners(String partners) {
		this.partners = partners;
	}

	public String getInterCompany() {
		return interCompany;
	}

	public void setInterCompany(String interCompany) {
		this.interCompany = interCompany;
	}

	public String getFuture1() {
		return future1;
	}

	public void setFuture1(String future1) {
		this.future1 = future1;
	}

	public String getFuture2() {
		return future2;
	}

	public void setFuture2(String future2) {
		this.future2 = future2;
	}

	public String getFuture3() {
		return future3;
	}

	public void setFuture3(String future3) {
		this.future3 = future3;
	}

	public String getSegment12() {
		return segment12;
	}

	public void setSegment12(String segment12) {
		this.segment12 = segment12;
	}

	public String getSegment13() {
		return segment13;
	}

	public void setSegment13(String segment13) {
		this.segment13 = segment13;
	}

	public String getSegment14() {
		return segment14;
	}

	public void setSegment14(String segment14) {
		this.segment14 = segment14;
	}

	public String getSegment15() {
		return segment15;
	}

	public void setSegment15(String segment15) {
		this.segment15 = segment15;
	}

	public String getSegment16() {
		return segment16;
	}

	public void setSegment16(String segment16) {
		this.segment16 = segment16;
	}

	public String getSegment17() {
		return segment17;
	}

	public void setSegment17(String segment17) {
		this.segment17 = segment17;
	}

	public String getSegment18() {
		return segment18;
	}

	public void setSegment18(String segment18) {
		this.segment18 = segment18;
	}

	public String getSegment19() {
		return segment19;
	}

	public void setSegment19(String segment19) {
		this.segment19 = segment19;
	}

	public String getSegment20() {
		return segment20;
	}

	public void setSegment20(String segment20) {
		this.segment20 = segment20;
	}

	public String getSegment21() {
		return segment21;
	}

	public void setSegment21(String segment21) {
		this.segment21 = segment21;
	}

	public String getSegment22() {
		return segment22;
	}

	public void setSegment22(String segment22) {
		this.segment22 = segment22;
	}

	public String getSegment23() {
		return segment23;
	}

	public void setSegment23(String segment23) {
		this.segment23 = segment23;
	}

	public String getSegment24() {
		return segment24;
	}

	public void setSegment24(String segment24) {
		this.segment24 = segment24;
	}

	public String getSegment25() {
		return segment25;
	}

	public void setSegment25(String segment25) {
		this.segment25 = segment25;
	}

	public String getSegment26() {
		return segment26;
	}

	public void setSegment26(String segment26) {
		this.segment26 = segment26;
	}

	public String getSegment27() {
		return segment27;
	}

	public void setSegment27(String segment27) {
		this.segment27 = segment27;
	}

	public String getSegment28() {
		return segment28;
	}

	public void setSegment28(String segment28) {
		this.segment28 = segment28;
	}

	public String getSegment29() {
		return segment29;
	}

	public void setSegment29(String segment29) {
		this.segment29 = segment29;
	}

	public String getSegment30() {
		return segment30;
	}

	public void setSegment30(String segment30) {
		this.segment30 = segment30;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getConvertedDebitAmount() {
		return convertedDebitAmount;
	}

	public void setConvertedDebitAmount(String convertedDebitAmount) {
		this.convertedDebitAmount = convertedDebitAmount;
	}

	public String getConvertedCreditAmount() {
		return convertedCreditAmount;
	}

	public void setConvertedCreditAmount(String convertedCreditAmount) {
		this.convertedCreditAmount = convertedCreditAmount;
	}

	public String getLoadID() {
		return loadID;
	}

	public void setLoadID(String loadID) {
		this.loadID = loadID;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getReference3() {
		return reference3;
	}

	public void setReference3(String reference3) {
		this.reference3 = reference3;
	}

	public String getReference4() {
		return reference4;
	}

	public void setReference4(String reference4) {
		this.reference4 = reference4;
	}

	public String getReference5() {
		return reference5;
	}

	public void setReference5(String reference5) {
		this.reference5 = reference5;
	}

	public String getReference6() {
		return reference6;
	}

	public void setReference6(String reference6) {
		this.reference6 = reference6;
	}

	public String getReference7() {
		return reference7;
	}

	public void setReference7(String reference7) {
		this.reference7 = reference7;
	}

	public String getReference8() {
		return reference8;
	}

	public void setReference8(String reference8) {
		this.reference8 = reference8;
	}

	public String getReference9() {
		return reference9;
	}

	public void setReference9(String reference9) {
		this.reference9 = reference9;
	}

	public String getLineDescription() {
		return lineDescription;
	}

	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}

	public String getReferenceColumn1() {
		return referenceColumn1;
	}

	public void setReferenceColumn1(String referenceColumn1) {
		this.referenceColumn1 = referenceColumn1;
	}

	public String getReferenceColumn2() {
		return referenceColumn2;
	}

	public void setReferenceColumn2(String referenceColumn2) {
		this.referenceColumn2 = referenceColumn2;
	}

	public String getReferenceColumn3() {
		return referenceColumn3;
	}

	public void setReferenceColumn3(String referenceColumn3) {
		this.referenceColumn3 = referenceColumn3;
	}

	public String getReferenceColumn4() {
		return referenceColumn4;
	}

	public void setReferenceColumn4(String referenceColumn4) {
		this.referenceColumn4 = referenceColumn4;
	}

	public String getReferenceColumn5() {
		return referenceColumn5;
	}

	public void setReferenceColumn5(String referenceColumn5) {
		this.referenceColumn5 = referenceColumn5;
	}

	public String getReferenceColumn6() {
		return referenceColumn6;
	}

	public void setReferenceColumn6(String referenceColumn6) {
		this.referenceColumn6 = referenceColumn6;
	}

	public String getReferenceColumn7() {
		return referenceColumn7;
	}

	public void setReferenceColumn7(String referenceColumn7) {
		this.referenceColumn7 = referenceColumn7;
	}

	public String getReferenceColumn8() {
		return referenceColumn8;
	}

	public void setReferenceColumn8(String referenceColumn8) {
		this.referenceColumn8 = referenceColumn8;
	}

	public String getReferenceColumn9() {
		return referenceColumn9;
	}

	public void setReferenceColumn9(String referenceColumn9) {
		this.referenceColumn9 = referenceColumn9;
	}

	public String getReferenceColumn10() {
		return referenceColumn10;
	}

	public void setReferenceColumn10(String referenceColumn10) {
		this.referenceColumn10 = referenceColumn10;
	}

	public String getStatisticalAmount() {
		return statisticalAmount;
	}

	public void setStatisticalAmount(String statisticalAmount) {
		this.statisticalAmount = statisticalAmount;
	}

	public String getCurrencyConversionType() {
		return currencyConversionType;
	}

	public void setCurrencyConversionType(String currencyConversionType) {
		this.currencyConversionType = currencyConversionType;
	}

	public String getCurrencyConversionDate() {
		return currencyConversionDate;
	}

	public void setCurrencyConversionDate(String currencyConversionDate) {
		this.currencyConversionDate = currencyConversionDate;
	}

	public String getCurrencyConversionRate() {
		return currencyConversionRate;
	}

	public void setCurrencyConversionRate(String currencyConversionRate) {
		this.currencyConversionRate = currencyConversionRate;
	}

	public Date getExtractionDate() {
		return extractionDate;
	}

	public void setExtractionDate(Date extractionDate) {
		this.extractionDate = extractionDate;
	}

	public String getContextField() {
		return contextField;
	}

	public void setContextField(String contextField) {
		this.contextField = contextField;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public long getLinkedTranID() {
		return linkedTranID;
	}

	public void setLinkedTranID(long linkedTranID) {
		this.linkedTranID = linkedTranID;
	}

	public String getAttribute4() {
		return attribute4;
	}

	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}

	public String getAttribute5() {
		return attribute5;
	}

	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5;
	}

	public String getAttribute6() {
		return attribute6;
	}

	public void setAttribute6(String attribute6) {
		this.attribute6 = attribute6;
	}

	public String getAttribute7() {
		return attribute7;
	}

	public void setAttribute7(String attribute7) {
		this.attribute7 = attribute7;
	}

	public String getAttribute8() {
		return attribute8;
	}

	public void setAttribute8(String attribute8) {
		this.attribute8 = attribute8;
	}

	public String getAttribute9() {
		return attribute9;
	}

	public void setAttribute9(String attribute9) {
		this.attribute9 = attribute9;
	}

	public String getAttribute10() {
		return attribute10;
	}

	public void setAttribute10(String attribute10) {
		this.attribute10 = attribute10;
	}

	public String getAttribute11() {
		return attribute11;
	}

	public void setAttribute11(String attribute11) {
		this.attribute11 = attribute11;
	}

	public String getAttribute12() {
		return attribute12;
	}

	public void setAttribute12(String attribute12) {
		this.attribute12 = attribute12;
	}

	public String getAttribute13() {
		return attribute13;
	}

	public void setAttribute13(String attribute13) {
		this.attribute13 = attribute13;
	}

	public String getAttribute14() {
		return attribute14;
	}

	public void setAttribute14(String attribute14) {
		this.attribute14 = attribute14;
	}

	public String getAttribute15() {
		return attribute15;
	}

	public void setAttribute15(String attribute15) {
		this.attribute15 = attribute15;
	}

	public String getAttribute16() {
		return attribute16;
	}

	public void setAttribute16(String attribute16) {
		this.attribute16 = attribute16;
	}

	public String getAttribute17() {
		return attribute17;
	}

	public void setAttribute17(String attribute17) {
		this.attribute17 = attribute17;
	}

	public String getAttribute18() {
		return attribute18;
	}

	public void setAttribute18(String attribute18) {
		this.attribute18 = attribute18;
	}

	public String getAttribute19() {
		return attribute19;
	}

	public void setAttribute19(String attribute19) {
		this.attribute19 = attribute19;
	}

	public String getAttribute20() {
		return attribute20;
	}

	public void setAttribute20(String attribute20) {
		this.attribute20 = attribute20;
	}

	public String getCapturedInfoDFF() {
		return capturedInfoDFF;
	}

	public void setCapturedInfoDFF(String capturedInfoDFF) {
		this.capturedInfoDFF = capturedInfoDFF;
	}

	public String getAvgJournalFlag() {
		return avgJournalFlag;
	}

	public void setAvgJournalFlag(String avgJournalFlag) {
		this.avgJournalFlag = avgJournalFlag;
	}

	public String getClearingCompany() {
		return clearingCompany;
	}

	public void setClearingCompany(String clearingCompany) {
		this.clearingCompany = clearingCompany;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getEncumbranceTypeID() {
		return encumbranceTypeID;
	}

	public void setEncumbranceTypeID(String encumbranceTypeID) {
		this.encumbranceTypeID = encumbranceTypeID;
	}

	public String getReconciliationReference() {
		return reconciliationReference;
	}

	public void setReconciliationReference(String reconciliationReference) {
		this.reconciliationReference = reconciliationReference;
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	public BigDecimal getPostAmount() {
		return postAmount;
	}

	public void setPostAmount(BigDecimal postAmount) {
		this.postAmount = postAmount;
	}

	public String getDrorCr() {
		return drorCr;
	}

	public void setDrorCr(String drorCr) {
		this.drorCr = drorCr;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

}
