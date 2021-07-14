/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  Presentment.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.model.presentment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "utilityCode", "userName", "umrnNo", "destAccHolder", "IFSC", "micr", "accountNo",
		"txnReference", "chequeAmount", "presentationDate", "mandateType", "status", "presentment", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "presentment")
public class Presentment {
	@JsonProperty("transactionRef")
	private long txnReference;
	private long entityCode;
	private long cycleType = 1;
	private String instrumentMode;
	@JsonProperty("dateOfTxn")
	private Date presentationDate;
	private String bankCode;
	private String productCode;
	private long customerId;
	@JsonProperty("finReference")
	private String agreementNo;
	@JsonProperty("amount")
	private BigDecimal chequeAmount;
	private long emiNo;
	private long txnTypeCode = 1;
	private long sourceCode = 2;
	@XmlElement
	private String brCode;
	@JsonProperty("mandateRef")
	private String umrnNo;
	private String bankName;
	@JsonProperty("micr")
	private String micrCode;
	@JsonProperty("accountNumber")
	private String accountNo;
	@JsonProperty("custName")
	private String destAccHolder;
	@XmlElement
	private long accType;
	private String bankAddress;
	private String resubFlag = "Y";
	private String orginSystem = "2";
	private Date dataGenDate;
	private long specialHitFile;
	private String userID;
	@JsonProperty("uniqueReference")
	private String batchId;
	private long jobId;
	private long pickupBatchId = 1;
	private String accountField1;
	private String accountField2;
	private String accountField3;
	private String processedFlag;
	@JsonProperty("instDate")
	private Date cycleDate;
	private String dataGenStatus = "C";
	private long bankReportCnt;
	private long stagingTableCnt;
	private String dataTrnsfrStatus;
	private long dataTrnsfrJobid;
	private String errorMsg;
	private Date startDate;
	private Date endDate;
	private Long returnCode;
	@JsonProperty("rejectReason")
	private String returnReason;
	private String lateResponseFlag;
	private Date processedDate;
	private long responseID;
	private String partnerBankName;
	private long partnerBankId;
	@JsonProperty("ifsc")
	private String IFSC;
	private String chequeSerialNo;
	private Date chequeDate;
	private String emandateSource;
	private String clientCode;
	private String entityDesc;
	private String entCode;
	private String utilityCode;
	@XmlElement
	private Date setilmentDate;

	@XmlElement
	private String status;

	@JsonProperty("UTRNumber")
	private String utrNumber;

	@XmlElement
	private WSReturnStatus returnStatus;

	public Presentment() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("status");
		excludeFields.add("returnStatus");
		excludeFields.add("userName");
		return excludeFields;
	}

	public String getChequeSerialNo() {
		return chequeSerialNo;
	}

	public void setChequeSerialNo(String chequeSerialNo) {
		this.chequeSerialNo = chequeSerialNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public String getIFSC() {
		return IFSC;
	}

	public void setIFSC(String iFSC) {
		IFSC = iFSC;
	}

	public Long getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(Long returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnReason() {
		return returnReason;
	}

	public void setReturnReason(String returnReason) {
		this.returnReason = returnReason;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getBankReportCnt() {
		return bankReportCnt;
	}

	public void setBankReportCnt(long bankReportCnt) {
		this.bankReportCnt = bankReportCnt;
	}

	public long getStagingTableCnt() {
		return stagingTableCnt;
	}

	public void setStagingTableCnt(long stagingTableCnt) {
		this.stagingTableCnt = stagingTableCnt;
	}

	public long getDataTrnsfrJobid() {
		return dataTrnsfrJobid;
	}

	public void setDataTrnsfrJobid(long dataTrnsfrJobid) {
		this.dataTrnsfrJobid = dataTrnsfrJobid;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public long getTxnReference() {
		return txnReference;
	}

	public void setTxnReference(long txnReference) {
		this.txnReference = txnReference;
	}

	public long getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(long entityCode) {
		this.entityCode = entityCode;
	}

	public long getCycleType() {
		return cycleType;
	}

	public void setCycleType(long cycleType) {
		this.cycleType = cycleType;
	}

	public String getInstrumentMode() {
		return instrumentMode;
	}

	public void setInstrumentMode(String instrumentMode) {
		this.instrumentMode = instrumentMode;
	}

	public Date getPresentationDate() {
		return presentationDate;
	}

	public void setPresentationDate(Date presentationDate) {
		this.presentationDate = presentationDate;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getAgreementNo() {
		return agreementNo;
	}

	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}

	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(BigDecimal chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public long getEmiNo() {
		return emiNo;
	}

	public void setEmiNo(long emiNo) {
		this.emiNo = emiNo;
	}

	public long getTxnTypeCode() {
		return txnTypeCode;
	}

	public void setTxnTypeCode(long txnTypeCode) {
		this.txnTypeCode = txnTypeCode;
	}

	public long getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(long sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getBrCode() {
		return brCode;
	}

	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}

	public String getUmrnNo() {
		return umrnNo;
	}

	public void setUmrnNo(String umrnNo) {
		this.umrnNo = umrnNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getDestAccHolder() {
		return destAccHolder;
	}

	public void setDestAccHolder(String destAccHolder) {
		this.destAccHolder = destAccHolder;
	}

	public long getAccType() {
		return accType;
	}

	public void setAccType(long accType) {
		this.accType = accType;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getResubFlag() {
		return resubFlag;
	}

	public void setResubFlag(String resubFlag) {
		this.resubFlag = resubFlag;
	}

	public String getOrginSystem() {
		return orginSystem;
	}

	public void setOrginSystem(String orginSystem) {
		this.orginSystem = orginSystem;
	}

	public Date getDataGenDate() {
		return dataGenDate;
	}

	public void setDataGenDate(Date dataGenDate) {
		this.dataGenDate = dataGenDate;
	}

	public long getSpecialHitFile() {
		return specialHitFile;
	}

	public void setSpecialHitFile(long specialHitFile) {
		this.specialHitFile = specialHitFile;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public long getPickupBatchId() {
		return pickupBatchId;
	}

	public void setPickupBatchId(long pickupBatchId) {
		this.pickupBatchId = pickupBatchId;
	}

	public String getAccountField1() {
		return accountField1;
	}

	public void setAccountField1(String accountField1) {
		this.accountField1 = accountField1;
	}

	public String getAccountField2() {
		return accountField2;
	}

	public void setAccountField2(String accountField2) {
		this.accountField2 = accountField2;
	}

	public String getAccountField3() {
		return accountField3;
	}

	public void setAccountField3(String accountField3) {
		this.accountField3 = accountField3;
	}

	public String getProcessedFlag() {
		return processedFlag;
	}

	public void setProcessedFlag(String processedFlag) {
		this.processedFlag = processedFlag;
	}

	public Date getCycleDate() {
		return cycleDate;
	}

	public void setCycleDate(Date cycleDate) {
		this.cycleDate = cycleDate;
	}

	public String getDataGenStatus() {
		return dataGenStatus;
	}

	public void setDataGenStatus(String dataGenStatus) {
		this.dataGenStatus = dataGenStatus;
	}

	public String getDataTrnsfrStatus() {
		return dataTrnsfrStatus;
	}

	public void setDataTrnsfrStatus(String dataTrnsfrStatus) {
		this.dataTrnsfrStatus = dataTrnsfrStatus;
	}

	public String getLateResponseFlag() {
		return lateResponseFlag;
	}

	public void setLateResponseFlag(String lateResponseFlag) {
		this.lateResponseFlag = lateResponseFlag;
	}

	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

	public long getResponseID() {
		return responseID;
	}

	public void setResponseID(long responseID) {
		this.responseID = responseID;
	}

	public String getEmandateSource() {
		return emandateSource;
	}

	public void setEmandateSource(String emandateSource) {
		this.emandateSource = emandateSource;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getEntCode() {
		return entCode;
	}

	public void setEntCode(String entCode) {
		this.entCode = entCode;
	}

	public String getUtilityCode() {
		return utilityCode;
	}

	public void setUtilityCode(String utilityCode) {
		this.utilityCode = utilityCode;
	}

	public Date getSetilmentDate() {
		return setilmentDate;
	}

	public void setSetilmentDate(Date setilmentDate) {
		this.setilmentDate = setilmentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

}
