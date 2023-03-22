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
	private String attribute2;
	private String attribute3;
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
	private String segment1;
	private String segment2;
	private String segment3;
	private String segment4;
	private String segment5;
	private String productCode;
	private String segment6;
	private String segment7;
	private String segment8;
	private String segment9;
	private String segment10;
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
	private BigDecimal convertedDebitAmount;
	private BigDecimal convertedCreditAmount;
	private String reference1;
	private String reference2;
	private String reference3;
	private String reference4;
	private String reference5;
	private String reference6;
	private String reference7;
	private String reference8;
	private String reference9;
	private String reference10;
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
	private Date interfaceGroupIdentifier;
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
	private String reference18;
	private String reference19;
	private String reference20;
	private String attributeDate1;
	private String attributeDate2;
	private String attributeDate3;
	private String attributeDate4;
	private String attributeDate5;
	private String attributeDate6;
	private String attributeDate7;
	private String attributeDate8;
	private String attributeDate9;
	private String attributeDate10;
	private String attributeNumber1;
	private String attributeNumber2;
	private String attributeNumber3;
	private String attributeNumber4;
	private String attributeNumber5;
	private String attributeNumber6;
	private String attributeNumber7;
	private String attributeNumber8;
	private String attributeNumber9;
	private String attributeNumber10;
	private String globalAttributeCategory;
	private String globalAttribute1;
	private String globalAttribute2;
	private String globalAttribute3;
	private String globalAttribute4;
	private String globalAttribute5;
	private String globalAttribute6;
	private String globalAttribute7;
	private String globalAttribute8;
	private String globalAttribute9;
	private String globalAttribute10;
	private String globalAttribute11;
	private String globalAttribute12;
	private String globalAttribute13;
	private String globalAttribute14;
	private String globalAttribute15;
	private String globalAttribute16;
	private String globalAttribute17;
	private String globalAttribute18;
	private String globalAttribute19;
	private String globalAttribute20;
	private String globalAttributeDate1;
	private String globalAttributeDate2;
	private String globalAttributeDate3;
	private String globalAttributeDate4;
	private String globalAttributeDate5;
	private String globalAttributeNumber1;
	private String globalAttributeNumber2;
	private String globalAttributeNumber3;
	private String globalAttributeNumber4;
	private String globalAttributeNumber5;
	private String segment11;
	private boolean finIsActive;

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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
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

	public BigDecimal getConvertedDebitAmount() {
		return convertedDebitAmount;
	}

	public void setConvertedDebitAmount(BigDecimal convertedDebitAmount) {
		this.convertedDebitAmount = convertedDebitAmount;
	}

	public BigDecimal getConvertedCreditAmount() {
		return convertedCreditAmount;
	}

	public void setConvertedCreditAmount(BigDecimal convertedCreditAmount) {
		this.convertedCreditAmount = convertedCreditAmount;
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

	public String getSegment1() {
		return segment1;
	}

	public void setSegment1(String segment1) {
		this.segment1 = segment1;
	}

	public String getSegment2() {
		return segment2;
	}

	public void setSegment2(String segment2) {
		this.segment2 = segment2;
	}

	public String getSegment3() {
		return segment3;
	}

	public void setSegment3(String segment3) {
		this.segment3 = segment3;
	}

	public String getSegment4() {
		return segment4;
	}

	public void setSegment4(String segment4) {
		this.segment4 = segment4;
	}

	public String getSegment5() {
		return segment5;
	}

	public void setSegment5(String segment5) {
		this.segment5 = segment5;
	}

	public String getSegment6() {
		return segment6;
	}

	public void setSegment6(String segment6) {
		this.segment6 = segment6;
	}

	public String getSegment7() {
		return segment7;
	}

	public void setSegment7(String segment7) {
		this.segment7 = segment7;
	}

	public String getSegment8() {
		return segment8;
	}

	public void setSegment8(String segment8) {
		this.segment8 = segment8;
	}

	public String getSegment9() {
		return segment9;
	}

	public void setSegment9(String segment9) {
		this.segment9 = segment9;
	}

	public String getSegment10() {
		return segment10;
	}

	public void setSegment10(String segment10) {
		this.segment10 = segment10;
	}

	public Date getInterfaceGroupIdentifier() {
		return interfaceGroupIdentifier;
	}

	public void setInterfaceGroupIdentifier(Date interfaceGroupIdentifier) {
		this.interfaceGroupIdentifier = interfaceGroupIdentifier;
	}

	public String getReference1() {
		return reference1;
	}

	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

	public String getReference2() {
		return reference2;
	}

	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	public String getReference10() {
		return reference10;
	}

	public void setReference10(String reference10) {
		this.reference10 = reference10;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}

	public String getReference18() {
		return reference18;
	}

	public void setReference18(String reference18) {
		this.reference18 = reference18;
	}

	public String getReference19() {
		return reference19;
	}

	public void setReference19(String reference19) {
		this.reference19 = reference19;
	}

	public String getReference20() {
		return reference20;
	}

	public void setReference20(String reference20) {
		this.reference20 = reference20;
	}

	public String getAttributeDate1() {
		return attributeDate1;
	}

	public void setAttributeDate1(String attributeDate1) {
		this.attributeDate1 = attributeDate1;
	}

	public String getAttributeDate2() {
		return attributeDate2;
	}

	public void setAttributeDate2(String attributeDate2) {
		this.attributeDate2 = attributeDate2;
	}

	public String getAttributeDate3() {
		return attributeDate3;
	}

	public void setAttributeDate3(String attributeDate3) {
		this.attributeDate3 = attributeDate3;
	}

	public String getAttributeDate4() {
		return attributeDate4;
	}

	public void setAttributeDate4(String attributeDate4) {
		this.attributeDate4 = attributeDate4;
	}

	public String getAttributeDate5() {
		return attributeDate5;
	}

	public void setAttributeDate5(String attributeDate5) {
		this.attributeDate5 = attributeDate5;
	}

	public String getAttributeDate6() {
		return attributeDate6;
	}

	public void setAttributeDate6(String attributeDate6) {
		this.attributeDate6 = attributeDate6;
	}

	public String getAttributeDate7() {
		return attributeDate7;
	}

	public void setAttributeDate7(String attributeDate7) {
		this.attributeDate7 = attributeDate7;
	}

	public String getAttributeDate8() {
		return attributeDate8;
	}

	public void setAttributeDate8(String attributeDate8) {
		this.attributeDate8 = attributeDate8;
	}

	public String getAttributeDate9() {
		return attributeDate9;
	}

	public void setAttributeDate9(String attributeDate9) {
		this.attributeDate9 = attributeDate9;
	}

	public String getAttributeDate10() {
		return attributeDate10;
	}

	public void setAttributeDate10(String attributeDate10) {
		this.attributeDate10 = attributeDate10;
	}

	public String getAttributeNumber1() {
		return attributeNumber1;
	}

	public void setAttributeNumber1(String attributeNumber1) {
		this.attributeNumber1 = attributeNumber1;
	}

	public String getAttributeNumber2() {
		return attributeNumber2;
	}

	public void setAttributeNumber2(String attributeNumber2) {
		this.attributeNumber2 = attributeNumber2;
	}

	public String getAttributeNumber3() {
		return attributeNumber3;
	}

	public void setAttributeNumber3(String attributeNumber3) {
		this.attributeNumber3 = attributeNumber3;
	}

	public String getAttributeNumber4() {
		return attributeNumber4;
	}

	public void setAttributeNumber4(String attributeNumber4) {
		this.attributeNumber4 = attributeNumber4;
	}

	public String getAttributeNumber5() {
		return attributeNumber5;
	}

	public void setAttributeNumber5(String attributeNumber5) {
		this.attributeNumber5 = attributeNumber5;
	}

	public String getAttributeNumber6() {
		return attributeNumber6;
	}

	public void setAttributeNumber6(String attributeNumber6) {
		this.attributeNumber6 = attributeNumber6;
	}

	public String getAttributeNumber7() {
		return attributeNumber7;
	}

	public void setAttributeNumber7(String attributeNumber7) {
		this.attributeNumber7 = attributeNumber7;
	}

	public String getAttributeNumber8() {
		return attributeNumber8;
	}

	public void setAttributeNumber8(String attributeNumber8) {
		this.attributeNumber8 = attributeNumber8;
	}

	public String getAttributeNumber9() {
		return attributeNumber9;
	}

	public void setAttributeNumber9(String attributeNumber9) {
		this.attributeNumber9 = attributeNumber9;
	}

	public String getAttributeNumber10() {
		return attributeNumber10;
	}

	public void setAttributeNumber10(String attributeNumber10) {
		this.attributeNumber10 = attributeNumber10;
	}

	public String getGlobalAttributeCategory() {
		return globalAttributeCategory;
	}

	public void setGlobalAttributeCategory(String globalAttributeCategory) {
		this.globalAttributeCategory = globalAttributeCategory;
	}

	public String getGlobalAttribute1() {
		return globalAttribute1;
	}

	public void setGlobalAttribute1(String globalAttribute1) {
		this.globalAttribute1 = globalAttribute1;
	}

	public String getGlobalAttribute2() {
		return globalAttribute2;
	}

	public void setGlobalAttribute2(String globalAttribute2) {
		this.globalAttribute2 = globalAttribute2;
	}

	public String getGlobalAttribute3() {
		return globalAttribute3;
	}

	public void setGlobalAttribute3(String globalAttribute3) {
		this.globalAttribute3 = globalAttribute3;
	}

	public String getGlobalAttribute4() {
		return globalAttribute4;
	}

	public void setGlobalAttribute4(String globalAttribute4) {
		this.globalAttribute4 = globalAttribute4;
	}

	public String getGlobalAttribute5() {
		return globalAttribute5;
	}

	public void setGlobalAttribute5(String globalAttribute5) {
		this.globalAttribute5 = globalAttribute5;
	}

	public String getGlobalAttribute6() {
		return globalAttribute6;
	}

	public void setGlobalAttribute6(String globalAttribute6) {
		this.globalAttribute6 = globalAttribute6;
	}

	public String getGlobalAttribute7() {
		return globalAttribute7;
	}

	public void setGlobalAttribute7(String globalAttribute7) {
		this.globalAttribute7 = globalAttribute7;
	}

	public String getGlobalAttribute8() {
		return globalAttribute8;
	}

	public void setGlobalAttribute8(String globalAttribute8) {
		this.globalAttribute8 = globalAttribute8;
	}

	public String getGlobalAttribute9() {
		return globalAttribute9;
	}

	public void setGlobalAttribute9(String globalAttribute9) {
		this.globalAttribute9 = globalAttribute9;
	}

	public String getGlobalAttribute10() {
		return globalAttribute10;
	}

	public void setGlobalAttribute10(String globalAttribute10) {
		this.globalAttribute10 = globalAttribute10;
	}

	public String getGlobalAttribute11() {
		return globalAttribute11;
	}

	public void setGlobalAttribute11(String globalAttribute11) {
		this.globalAttribute11 = globalAttribute11;
	}

	public String getGlobalAttribute12() {
		return globalAttribute12;
	}

	public void setGlobalAttribute12(String globalAttribute12) {
		this.globalAttribute12 = globalAttribute12;
	}

	public String getGlobalAttribute13() {
		return globalAttribute13;
	}

	public void setGlobalAttribute13(String globalAttribute13) {
		this.globalAttribute13 = globalAttribute13;
	}

	public String getGlobalAttribute14() {
		return globalAttribute14;
	}

	public void setGlobalAttribute14(String globalAttribute14) {
		this.globalAttribute14 = globalAttribute14;
	}

	public String getGlobalAttribute15() {
		return globalAttribute15;
	}

	public void setGlobalAttribute15(String globalAttribute15) {
		this.globalAttribute15 = globalAttribute15;
	}

	public String getGlobalAttribute16() {
		return globalAttribute16;
	}

	public void setGlobalAttribute16(String globalAttribute16) {
		this.globalAttribute16 = globalAttribute16;
	}

	public String getGlobalAttribute17() {
		return globalAttribute17;
	}

	public void setGlobalAttribute17(String globalAttribute17) {
		this.globalAttribute17 = globalAttribute17;
	}

	public String getGlobalAttribute18() {
		return globalAttribute18;
	}

	public void setGlobalAttribute18(String globalAttribute18) {
		this.globalAttribute18 = globalAttribute18;
	}

	public String getGlobalAttribute19() {
		return globalAttribute19;
	}

	public void setGlobalAttribute19(String globalAttribute19) {
		this.globalAttribute19 = globalAttribute19;
	}

	public String getGlobalAttribute20() {
		return globalAttribute20;
	}

	public void setGlobalAttribute20(String globalAttribute20) {
		this.globalAttribute20 = globalAttribute20;
	}

	public String getGlobalAttributeDate1() {
		return globalAttributeDate1;
	}

	public void setGlobalAttributeDate1(String globalAttributeDate1) {
		this.globalAttributeDate1 = globalAttributeDate1;
	}

	public String getGlobalAttributeDate2() {
		return globalAttributeDate2;
	}

	public void setGlobalAttributeDate2(String globalAttributeDate2) {
		this.globalAttributeDate2 = globalAttributeDate2;
	}

	public String getGlobalAttributeDate3() {
		return globalAttributeDate3;
	}

	public void setGlobalAttributeDate3(String globalAttributeDate3) {
		this.globalAttributeDate3 = globalAttributeDate3;
	}

	public String getGlobalAttributeDate4() {
		return globalAttributeDate4;
	}

	public void setGlobalAttributeDate4(String globalAttributeDate4) {
		this.globalAttributeDate4 = globalAttributeDate4;
	}

	public String getGlobalAttributeDate5() {
		return globalAttributeDate5;
	}

	public void setGlobalAttributeDate5(String globalAttributeDate5) {
		this.globalAttributeDate5 = globalAttributeDate5;
	}

	public String getGlobalAttributeNumber1() {
		return globalAttributeNumber1;
	}

	public void setGlobalAttributeNumber1(String globalAttributeNumber1) {
		this.globalAttributeNumber1 = globalAttributeNumber1;
	}

	public String getGlobalAttributeNumber2() {
		return globalAttributeNumber2;
	}

	public void setGlobalAttributeNumber2(String globalAttributeNumber2) {
		this.globalAttributeNumber2 = globalAttributeNumber2;
	}

	public String getGlobalAttributeNumber3() {
		return globalAttributeNumber3;
	}

	public void setGlobalAttributeNumber3(String globalAttributeNumber3) {
		this.globalAttributeNumber3 = globalAttributeNumber3;
	}

	public String getGlobalAttributeNumber4() {
		return globalAttributeNumber4;
	}

	public void setGlobalAttributeNumber4(String globalAttributeNumber4) {
		this.globalAttributeNumber4 = globalAttributeNumber4;
	}

	public String getGlobalAttributeNumber5() {
		return globalAttributeNumber5;
	}

	public void setGlobalAttributeNumber5(String globalAttributeNumber5) {
		this.globalAttributeNumber5 = globalAttributeNumber5;
	}

	public String getSegment11() {
		return segment11;
	}

	public void setSegment11(String segment11) {
		this.segment11 = segment11;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

}
