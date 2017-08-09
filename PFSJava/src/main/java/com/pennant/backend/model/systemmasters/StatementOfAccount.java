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
 * FileName    		:  Academic.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Model class for the <b>Academic table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StatementOfAccount {

	private String finReference;
	private Date startDate;
	private Date endDate;

	private BigDecimal loanAmount = BigDecimal.ZERO;
	private String plrRate;
	private BigDecimal variance = BigDecimal.ZERO;
	private BigDecimal irr = BigDecimal.ZERO;
	private BigDecimal roi = BigDecimal.ZERO;
	private int tenure = 0;
	private BigDecimal emiReceivedPri = BigDecimal.ZERO;
	private BigDecimal emiReceivedPft = BigDecimal.ZERO;
	private BigDecimal preferredCardLimit = BigDecimal.ZERO;
	private BigDecimal prevInstAmtPri = BigDecimal.ZERO;
	private BigDecimal prevInstAmtPft = BigDecimal.ZERO;
	private String intRateType;
	private Date lastDisbursalDate;
	private Date firstDueDate;
	private Date endInstallmentDate;
	private String advInstAmt;
	private boolean finIsActive;
	private String closingStatus;
	private int futureInstNo = 0;
	private BigDecimal futurePri1 = BigDecimal.ZERO;
	private BigDecimal futurePri2 = BigDecimal.ZERO;
	private BigDecimal futureRpyPft1 = BigDecimal.ZERO;
	private BigDecimal futureRpyPft2 = BigDecimal.ZERO;
	// private BigDecimal chargeCollCust = BigDecimal.ZERO;
	// private BigDecimal upfrontIntCust = BigDecimal.ZERO;
	// private int intPaidDealerUpfront = 0;
	// private int preEmiIntPaid = 0;
	// private String repoStatus;
	// private String repoDate;
	// private String saleDate;
	// private String releaseDate;
	private BigDecimal charge_coll_cust = BigDecimal.ZERO;
	private BigDecimal upfront_int_cust = BigDecimal.ZERO;
	private int int_paid_Dealer_upfront = 0;
	private int pre_emi_Int_Paid = 0;
	private String repo_Status;
	private String repo_Date;
	private String sale_Date;
	private String release_Date;
	private Date latestRpyDate;
	private BigDecimal ccyMinorCcyUnits;
	private int ccyEditField;
	
	// Finance Profit Details
	private String FinPurpose;
	private Date FinStartDate;
	private BigDecimal linkedFinRef = BigDecimal.ZERO;
	private BigDecimal closedlinkedFinRef = BigDecimal.ZERO;
	private int activeCnt;
	private int closeCnt;
	private int tot;	
	
	//Product 
	private String finType;	//productdesc
	//Branch
	private String finBranch;	//branchdesc
	
	private String interestType = "";
	private String propertyAddress = "";
	private String propertyDetails = "";	
	
	//Customers
	private String custShrtName;
	private long custID;
	private String custCIF;
	
	//Customer Address
	private String custAddrHNbr;
	private String custFlatNbr;
	private String custAddrStreet;
	private String custPOBox;
	private String custAddrCity;
	private String custAddrProvince;
	private String custAddrCountry;
	
	//Customer Phone Number
	private String phoneCountryCode;
	private String phoneAreaCode;
	private String phoneNumber;
	
	//Customer E-mails
	private String custEMail;
	
	private List<SOASummaryReport> soaSummaryReports = new ArrayList<SOASummaryReport>();
	private List<SOATransactionReport> transactionReports = new ArrayList<SOATransactionReport>();

	public StatementOfAccount() {
		super();
	}

	public List<SOATransactionReport> getTransactionReports() {
		return transactionReports;
	}

	public void setTransactionReports(List<SOATransactionReport> transactionReports) {
		this.transactionReports = transactionReports;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getPlrRate() {
		return plrRate;
	}

	public void setPlrRate(String plrRate) {
		this.plrRate = plrRate;
	}

	public BigDecimal getVariance() {
		return variance;
	}

	public void setVariance(BigDecimal variance) {
		this.variance = variance;
	}

	public BigDecimal getIrr() {
		return irr;
	}

	public void setIrr(BigDecimal irr) {
		this.irr = irr;
	}

	public BigDecimal getRoi() {
		return roi;
	}

	public void setRoi(BigDecimal roi) {
		this.roi = roi;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public BigDecimal getEmiReceivedPri() {
		return emiReceivedPri;
	}

	public void setEmiReceivedPri(BigDecimal emiReceivedPri) {
		this.emiReceivedPri = emiReceivedPri;
	}

	public BigDecimal getEmiReceivedPft() {
		return emiReceivedPft;
	}

	public void setEmiReceivedPft(BigDecimal emiReceivedPft) {
		this.emiReceivedPft = emiReceivedPft;
	}

	public BigDecimal getPreferredCardLimit() {
		return preferredCardLimit;
	}

	public void setPreferredCardLimit(BigDecimal preferredCardLimit) {
		this.preferredCardLimit = preferredCardLimit;
	}

	public BigDecimal getPrevInstAmtPri() {
		return prevInstAmtPri;
	}

	public void setPrevInstAmtPri(BigDecimal prevInstAmtPri) {
		this.prevInstAmtPri = prevInstAmtPri;
	}

	public BigDecimal getPrevInstAmtPft() {
		return prevInstAmtPft;
	}

	public void setPrevInstAmtPft(BigDecimal prevInstAmtPft) {
		this.prevInstAmtPft = prevInstAmtPft;
	}

	public String getIntRateType() {
		return intRateType;
	}

	public void setIntRateType(String intRateType) {
		this.intRateType = intRateType;
	}

	public Date getLastDisbursalDate() {
		return lastDisbursalDate;
	}

	public void setLastDisbursalDate(Date lastDisbursalDate) {
		this.lastDisbursalDate = lastDisbursalDate;
	}

	public Date getFirstDueDate() {
		return firstDueDate;
	}

	public void setFirstDueDate(Date firstDueDate) {
		this.firstDueDate = firstDueDate;
	}

	public Date getEndInstallmentDate() {
		return endInstallmentDate;
	}

	public void setEndInstallmentDate(Date endInstallmentDate) {
		this.endInstallmentDate = endInstallmentDate;
	}

	public String getAdvInstAmt() {
		return advInstAmt;
	}

	public void setAdvInstAmt(String advInstAmt) {
		this.advInstAmt = advInstAmt;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public int getFutureInstNo() {
		return futureInstNo;
	}

	public void setFutureInstNo(int futureInstNo) {
		this.futureInstNo = futureInstNo;
	}

	public BigDecimal getFuturePri1() {
		return futurePri1;
	}

	public void setFuturePri1(BigDecimal futurePri1) {
		this.futurePri1 = futurePri1;
	}

	public BigDecimal getFuturePri2() {
		return futurePri2;
	}

	public void setFuturePri2(BigDecimal futurePri2) {
		this.futurePri2 = futurePri2;
	}

	public BigDecimal getFutureRpyPft1() {
		return futureRpyPft1;
	}

	public void setFutureRpyPft1(BigDecimal futureRpyPft1) {
		this.futureRpyPft1 = futureRpyPft1;
	}

	public BigDecimal getFutureRpyPft2() {
		return futureRpyPft2;
	}

	public void setFutureRpyPft2(BigDecimal futureRpyPft2) {
		this.futureRpyPft2 = futureRpyPft2;
	}

	public BigDecimal getCharge_coll_cust() {
		return charge_coll_cust;
	}

	public void setCharge_coll_cust(BigDecimal charge_coll_cust) {
		this.charge_coll_cust = charge_coll_cust;
	}

	public BigDecimal getUpfront_int_cust() {
		return upfront_int_cust;
	}

	public void setUpfront_int_cust(BigDecimal upfront_int_cust) {
		this.upfront_int_cust = upfront_int_cust;
	}

	public int getInt_paid_Dealer_upfront() {
		return int_paid_Dealer_upfront;
	}

	public void setInt_paid_Dealer_upfront(int int_paid_Dealer_upfront) {
		this.int_paid_Dealer_upfront = int_paid_Dealer_upfront;
	}

	public int getPre_emi_Int_Paid() {
		return pre_emi_Int_Paid;
	}

	public void setPre_emi_Int_Paid(int pre_emi_Int_Paid) {
		this.pre_emi_Int_Paid = pre_emi_Int_Paid;
	}

	public String getRepo_Status() {
		return repo_Status;
	}

	public void setRepo_Status(String repo_Status) {
		this.repo_Status = repo_Status;
	}

	public String getRepo_Date() {
		return repo_Date;
	}

	public void setRepo_Date(String repo_Date) {
		this.repo_Date = repo_Date;
	}

	public String getSale_Date() {
		return sale_Date;
	}

	public void setSale_Date(String sale_Date) {
		this.sale_Date = sale_Date;
	}

	public String getRelease_Date() {
		return release_Date;
	}

	public void setRelease_Date(String release_Date) {
		this.release_Date = release_Date;
	}

	public Date getLatestRpyDate() {
		return latestRpyDate;
	}

	public void setLatestRpyDate(Date latestRpyDate) {
		this.latestRpyDate = latestRpyDate;
	}

	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}

	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public String getFinPurpose() {
		return FinPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		FinPurpose = finPurpose;
	}

	public Date getFinStartDate() {
		return FinStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		FinStartDate = finStartDate;
	}

	public BigDecimal getLinkedFinRef() {
		return linkedFinRef;
	}

	public void setLinkedFinRef(BigDecimal linkedFinRef) {
		this.linkedFinRef = linkedFinRef;
	}

	public BigDecimal getClosedlinkedFinRef() {
		return closedlinkedFinRef;
	}

	public void setClosedlinkedFinRef(BigDecimal closedlinkedFinRef) {
		this.closedlinkedFinRef = closedlinkedFinRef;
	}

	public int getActiveCnt() {
		return activeCnt;
	}

	public void setActiveCnt(int activeCnt) {
		this.activeCnt = activeCnt;
	}

	public int getCloseCnt() {
		return closeCnt;
	}

	public void setCloseCnt(int closeCnt) {
		this.closeCnt = closeCnt;
	}

	public int getTot() {
		return tot;
	}

	public void setTot(int tot) {
		this.tot = tot;
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

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public String getPropertyDetails() {
		return propertyDetails;
	}

	public void setPropertyDetails(String propertyDetails) {
		this.propertyDetails = propertyDetails;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}

	public void setCustAddrHNbr(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}

	public String getCustFlatNbr() {
		return custFlatNbr;
	}

	public void setCustFlatNbr(String custFlatNbr) {
		this.custFlatNbr = custFlatNbr;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCustPOBox() {
		return custPOBox;
	}

	public void setCustPOBox(String custPOBox) {
		this.custPOBox = custPOBox;
	}

	public String getCustAddrCity() {
		return custAddrCity;
	}

	public void setCustAddrCity(String custAddrCity) {
		this.custAddrCity = custAddrCity;
	}

	public String getCustAddrProvince() {
		return custAddrProvince;
	}

	public void setCustAddrProvince(String custAddrProvince) {
		this.custAddrProvince = custAddrProvince;
	}

	public String getCustAddrCountry() {
		return custAddrCountry;
	}

	public void setCustAddrCountry(String custAddrCountry) {
		this.custAddrCountry = custAddrCountry;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}

	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCustEMail() {
		return custEMail;
	}

	public void setCustEMail(String custEMail) {
		this.custEMail = custEMail;
	}

	public List<SOASummaryReport> getSoaSummaryReports() {
		return soaSummaryReports;
	}

	public void setSoaSummaryReports(List<SOASummaryReport> soaSummaryReports) {
		this.soaSummaryReports = soaSummaryReports;
	}
}
