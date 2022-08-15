/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceMaintenance.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-01-2018 * * Modified
 * Date : 30-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class for the <b>FinanceMain_GLView </b>.<br>
 *
 */
public class FinanceMaintenance implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String finType;
	private String finBranch;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal finRepaymentAmount = BigDecimal.ZERO;
	private Date maturityDate;
	private Date finStartDate;
	private String requestStage;
	private String recordStatus;
	private long custID;
	private String custCIF;
	private String mobileNum;
	private String panNumber;
	private String custName;
	private String custFName;
	private String custMName;
	private String custLName;
	private long schemeId = 0;
	private String schemeCode;
	private String profitDaysBasis;
	private String currency;
	private int minInterestPeriod;
	private boolean finIsActive;
	private String finTypeDesc;
	private String packetNumber;
	private String rackNumber;
	private String nextRoleCode;
	private String roleDesc;
	private String recordType;
	private String finBranchDesc;
	private String finCcy;
	private String scheduleMethod;
	private String rcdMaintainSts;
	private String receiptModeStatus;
	private String repledge;
	private String transactionType;
	private String cashUserBranch;
	private String entityCode;
	private String lovDescFinDivision;
	private Date receivedDate;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getRequestStage() {
		return requestStage;
	}

	public void setRequestStage(String requestStage) {
		this.requestStage = requestStage;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustMName() {
		return custMName;
	}

	public void setCustMName(String custMName) {
		this.custMName = custMName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
		this.schemeId = schemeId;
	}

	public String getSchemeCode() {
		return schemeCode;
	}

	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getMinInterestPeriod() {
		return minInterestPeriod;
	}

	public void setMinInterestPeriod(int minInterestPeriod) {
		this.minInterestPeriod = minInterestPeriod;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(String packetNumber) {
		this.packetNumber = packetNumber;
	}

	public String getRackNumber() {
		return rackNumber;
	}

	public void setRackNumber(String rackNumber) {
		this.rackNumber = rackNumber;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}

	public String getFinBranchDesc() {
		return finBranchDesc;
	}

	public void setFinBranchDesc(String finBranchDesc) {
		this.finBranchDesc = finBranchDesc;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getReceiptModeStatus() {
		return receiptModeStatus;
	}

	public void setReceiptModeStatus(String receiptModeStatus) {
		this.receiptModeStatus = receiptModeStatus;
	}

	public String getRcdMaintainSts() {
		return rcdMaintainSts;
	}

	public void setRcdMaintainSts(String rcdMaintainSts) {
		this.rcdMaintainSts = rcdMaintainSts;
	}

	public String getRepledge() {
		return repledge;
	}

	public void setRepledge(String repledge) {
		this.repledge = repledge;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCashUserBranch() {
		return cashUserBranch;
	}

	public void setCashUserBranch(String cashUserBranch) {
		this.cashUserBranch = cashUserBranch;
	}

	public String getLovDescFinDivision() {
		return lovDescFinDivision;
	}

	public void setLovDescFinDivision(String lovDescFinDivision) {
		this.lovDescFinDivision = lovDescFinDivision;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}
}
