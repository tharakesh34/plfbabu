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
 * FileName    		:  BankInfoDetail.java                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2018       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BankInfoDetail table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BankInfoDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3217987429162088120L;

	private long bankId;
	@XmlElement
	private Date monthYear;
	private BigDecimal balance = BigDecimal.ZERO;
	@XmlElement
	private int debitNo;
	@XmlElement
	private BigDecimal debitAmt = BigDecimal.ZERO;
	@XmlElement
	private int creditNo;
	@XmlElement
	private BigDecimal creditAmt = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal bounceIn = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal bounceOut = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal closingBal = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal oDCCLimit = BigDecimal.ZERO;

	private int keyValue = 0;

	private boolean newRecord = false;
	private String lovValue;
	private BankInfoDetail befImage;
	private LoggedInUser userDetails;
	@XmlElementWrapper(name = "bankInfoSubDetails")
	@XmlElement(name = "bankInfoSubDetail")
	private List<BankInfoSubDetail> bankInfoSubDetails = new ArrayList<>();
	@XmlElement
	private BigDecimal sanctionLimit = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal avgUtilization = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal peakUtilizationLevel = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal settlementCredits = BigDecimal.ZERO;
	@XmlElement
	private Integer settlementNo;
	@XmlElement
	private BigDecimal interest = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal trf = BigDecimal.ZERO;

	//Perfios
	@XmlElement
	private BigDecimal totalEmi = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal totalSalary = BigDecimal.ZERO;
	@XmlElement
	private int emiBounceNo;

	public BankInfoDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("bankInfoSubDetails");
		excludeFields.add("keyValue");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getBankId() {
		return bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public Date getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(Date monthYear) {
		this.monthYear = monthYear;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public int getDebitNo() {
		return debitNo;
	}

	public void setDebitNo(int debitNo) {
		this.debitNo = debitNo;
	}

	public BigDecimal getDebitAmt() {
		return debitAmt;
	}

	public void setDebitAmt(BigDecimal debitAmt) {
		this.debitAmt = debitAmt;
	}

	public int getCreditNo() {
		return creditNo;
	}

	public void setCreditNo(int creditNo) {
		this.creditNo = creditNo;
	}

	public BigDecimal getCreditAmt() {
		return creditAmt;
	}

	public void setCreditAmt(BigDecimal creditAmt) {
		this.creditAmt = creditAmt;
	}

	public BigDecimal getBounceIn() {
		return bounceIn;
	}

	public void setBounceIn(BigDecimal bounceIn) {
		this.bounceIn = bounceIn;
	}

	public BigDecimal getBounceOut() {
		return bounceOut;
	}

	public void setBounceOut(BigDecimal bounceOut) {
		this.bounceOut = bounceOut;
	}

	public BigDecimal getClosingBal() {
		return closingBal;
	}

	public void setClosingBal(BigDecimal closingBal) {
		this.closingBal = closingBal;
	}

	public BigDecimal getoDCCLimit() {
		return oDCCLimit;
	}

	public void setoDCCLimit(BigDecimal oDCCLimit) {
		this.oDCCLimit = oDCCLimit;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public BankInfoDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(BankInfoDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<BankInfoSubDetail> getBankInfoSubDetails() {
		return bankInfoSubDetails;
	}

	public void setBankInfoSubDetails(List<BankInfoSubDetail> bankInfoSubDetails) {
		this.bankInfoSubDetails = bankInfoSubDetails;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

	public BigDecimal getSanctionLimit() {
		return sanctionLimit;
	}

	public void setSanctionLimit(BigDecimal sanctionLimit) {
		this.sanctionLimit = sanctionLimit;
	}

	public BigDecimal getAvgUtilization() {
		return avgUtilization;
	}

	public void setAvgUtilization(BigDecimal avgUtilization) {
		this.avgUtilization = avgUtilization;
	}

	public BigDecimal getPeakUtilizationLevel() {
		return peakUtilizationLevel;
	}

	public void setPeakUtilizationLevel(BigDecimal peakUtilizationLevel) {
		this.peakUtilizationLevel = peakUtilizationLevel;
	}

	public BigDecimal getSettlementCredits() {
		return settlementCredits;
	}

	public void setSettlementCredits(BigDecimal settlementCredits) {
		this.settlementCredits = settlementCredits;
	}

	public Integer getSettlementNo() {
		return settlementNo;
	}

	public void setSettlementNo(Integer settlementNo) {
		this.settlementNo = settlementNo;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public BigDecimal getTrf() {
		return trf;
	}

	public void setTrf(BigDecimal trf) {
		this.trf = trf;
	}

	public BigDecimal getTotalEmi() {
		return totalEmi;
	}

	public void setTotalEmi(BigDecimal totalEmi) {
		this.totalEmi = totalEmi;
	}

	public BigDecimal getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(BigDecimal totalSalary) {
		this.totalSalary = totalSalary;
	}

	public int getEmiBounceNo() {
		return emiBounceNo;
	}

	public void setEmiBounceNo(int emiBounceNo) {
		this.emiBounceNo = emiBounceNo;
	}

}
