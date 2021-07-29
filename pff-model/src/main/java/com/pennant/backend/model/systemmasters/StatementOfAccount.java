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
 * * FileName : Academic.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified Date :
 * 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;

/**
 * Model class for the <b>Academic table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class StatementOfAccount {

	private String finReference;
	private Date startDate;
	private Date endDate;

	////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Loan Basic Details
	//////////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	private BigDecimal loanAmount = BigDecimal.ZERO;
	private String loanAmountInWords;
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
	private Date firstDisbDate;
	private BigDecimal repayRateType;
	private Date firstDueDate;
	private Date endInstallmentDate;
	private String advInstAmt;
	private boolean finIsActive;
	private String closingStatus;
	private Date closedDate;
	private int futureInstNo = 0;
	private BigDecimal futurePri1 = BigDecimal.ZERO;
	private BigDecimal futurePri2 = BigDecimal.ZERO;
	private BigDecimal futureRpyPft1 = BigDecimal.ZERO;
	private BigDecimal futureRpyPft2 = BigDecimal.ZERO;
	private BigDecimal chargeCollCust = BigDecimal.ZERO;
	private BigDecimal upfrontIntCust = BigDecimal.ZERO;
	private int intPaidDealerUpfront = 0;
	private int preEmiIntPaid = 0;
	private String repoStatus;
	private String repoDate;
	private String saleDate;
	private String releaseDate;
	private Date latestRpyDate;
	private BigDecimal ccyMinorCcyUnits;
	private int ccyEditField;
	private Date currentDate;
	private Date maturityDate;
	private int noPaidInst = 0;
	private int noOfOutStandInst = 0;
	private BigDecimal totalPriPaid = BigDecimal.ZERO;
	private BigDecimal totalPriBal = BigDecimal.ZERO;
	private BigDecimal totalPftPaid = BigDecimal.ZERO;
	private BigDecimal totalPftBal = BigDecimal.ZERO;
	private BigDecimal paidTotal = BigDecimal.ZERO;;
	private BigDecimal totalOutStanding = BigDecimal.ZERO;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	private BigDecimal nextRpyPri = BigDecimal.ZERO;
	private BigDecimal nextRpyPft = BigDecimal.ZERO;
	private Date nextRpyDate;
	private boolean advEmiApplicable = false;
	private String repayFrq;
	private BigDecimal svamount = BigDecimal.ZERO;
	private int advEmiTerms;
	private String totalPriPaidInWords;
	private String totalPriBalInwords;
	private String totalPftPaidInWords;

	////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Customer Details
	//////////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	// Finance Profit Details
	private String finPurpose;
	private Date finStartDate;
	private String linkedFinRef;
	private String closedlinkedFinRef;
	private int activeCnt;
	private int closeCnt;
	private int tot;

	// Product
	private String finType; // productdesc
	// Branch
	private String finBranch; // branchdesc

	private String interestType = "";
	private String propertyAddress = "";
	private String propertyDetails = "";

	// Customers
	private String custShrtName;
	private long custID;
	private String custCIF;
	private String custSalutation;

	// Customer Address
	private String custAddrHNbr;
	private String custFlatNbr;
	private String custAddrStreet;
	private String custPOBox;
	private String custAddrCity;
	private String custAddrProvince;
	private String custAddrCountry;
	private String custAddrLine1;
	private String custAddrLine2;
	private String custAddrZIP;

	// Customer Phone Number
	private String phoneCountryCode;
	private String phoneAreaCode;
	private String phoneNumber;

	// Customer E-mails
	private String custEMail;

	// Product Details
	private String productId = "";

	private String productSku = "";

	private String field1 = "";

	private String loanPurpose;

	private String entityCode;
	private String entityDesc;
	private String stateCode;
	private String providerGSTIN;
	private String custGSTIN;
	private String placeOfSupply;

	@SuppressWarnings("unused")
	private BigDecimal emiReceived = BigDecimal.ZERO;

	@SuppressWarnings("unused")
	private BigDecimal prvInstAmount = BigDecimal.ZERO;

	private BigDecimal futureInstAmount = BigDecimal.ZERO;

	@SuppressWarnings("unused")
	private BigDecimal futurePrincipalComponent = BigDecimal.ZERO;

	@SuppressWarnings("unused")
	private BigDecimal futureInterestComponent = BigDecimal.ZERO;

	@SuppressWarnings("unused")
	private BigDecimal intPaidByMfgrOrDealerUpfront = BigDecimal.ZERO;

	private String advEMIAmt;
	private String advIntAmt;
	private String cashCollAmt;
	private String dsraAmt;

	// downpayment
	private BigDecimal downPayment = BigDecimal.ZERO;

	// Summary Reports List
	private List<SOASummaryReport> soaSummaryReports = new ArrayList<SOASummaryReport>();

	// Transaction Reports List
	private List<SOATransactionReport> transactionReports = new ArrayList<SOATransactionReport>();

	// Next Installment Amount
	@SuppressWarnings("unused")
	private BigDecimal nextInstAmount = BigDecimal.ZERO;

	// Other Finance Details
	private List<OtherFinanceDetail> otherFinanceDetails = new ArrayList<OtherFinanceDetail>();

	// Co-Applicant And Borrower Details
	private List<ApplicantDetail> applicantDetails = new ArrayList<ApplicantDetail>();

	// Co-Applicant And Borrower Details
	private List<InterestRateDetail> interestRateDetails = new ArrayList<InterestRateDetail>();

	// API Purpose
	private WSReturnStatus returnStatus;

	// Customer Loan Refrence Details
	private List<String> custFinRefDetails = new ArrayList<String>();

	private String tenureLabel;

	private List<Map<String, Object>> extendedDetails = null;

	// Shedule Reports List
	private List<SOAScheduleReport> sheduleReports = new ArrayList<SOAScheduleReport>();

	// Number of installments unpaid(Exclude the overdue term which is paid)
	private int noOfEmiOverDue = 0;

	// Total Amount of unpaid installments(Exclude the overdue amount which is paid)
	private BigDecimal emiAmtOverdue = BigDecimal.ZERO;

	private BigDecimal curApplicableEMI = BigDecimal.ZERO;
	private String curApplicableEMIInWords;

	// Number of total overdue terms
	private int noOfODTerms = 0;

	// Total Overdue amount
	private BigDecimal totalODAmt = BigDecimal.ZERO;

	// Unadjusted Amount
	private BigDecimal unAdjAmt = BigDecimal.ZERO;
	private String unAdjAmtInWords;

	// Net receivable
	private BigDecimal netReceivable = BigDecimal.ZERO;
	private String netRcvbleInWords;

	/**
	 * Default Constructor
	 */
	public StatementOfAccount() {
		super();
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
		return this.loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {

		if (loanAmount == null) {
			loanAmount = BigDecimal.ZERO;
		}

		this.loanAmount = loanAmount;
	}

	public String getPlrRate() {
		return plrRate;
	}

	public void setPlrRate(String plrRate) {
		this.plrRate = plrRate;
	}

	public BigDecimal getVariance() {
		return this.variance;
	}

	public void setVariance(BigDecimal variance) {

		if (variance == null) {
			variance = BigDecimal.ZERO;
		}

		this.variance = variance;
	}

	public BigDecimal getIrr() {
		return this.irr;
	}

	public void setIrr(BigDecimal irr) {

		if (irr == null) {
			irr = BigDecimal.ZERO;
		}

		this.irr = irr;
	}

	public BigDecimal getRoi() {
		return this.roi;
	}

	public void setRoi(BigDecimal roi) {

		if (roi == null) {
			roi = BigDecimal.ZERO;
		}

		this.roi = roi;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public BigDecimal getEmiReceivedPri() {
		return this.emiReceivedPri;
	}

	public void setEmiReceivedPri(BigDecimal emiReceivedPri) {

		if (emiReceivedPri == null) {
			emiReceivedPri = BigDecimal.ZERO;
		}

		this.emiReceivedPri = emiReceivedPri;
	}

	public BigDecimal getEmiReceivedPft() {
		return this.emiReceivedPft;
	}

	public void setEmiReceivedPft(BigDecimal emiReceivedPft) {

		if (emiReceivedPft == null) {
			emiReceivedPft = BigDecimal.ZERO;
		}

		this.emiReceivedPft = emiReceivedPft;
	}

	public BigDecimal getPreferredCardLimit() {
		return this.preferredCardLimit;
	}

	public void setPreferredCardLimit(BigDecimal preferredCardLimit) {

		if (preferredCardLimit == null) {
			preferredCardLimit = BigDecimal.ZERO;
		}

		this.preferredCardLimit = preferredCardLimit;
	}

	public BigDecimal getPrevInstAmtPri() {
		return this.prevInstAmtPri;
	}

	public void setPrevInstAmtPri(BigDecimal prevInstAmtPri) {

		if (prevInstAmtPri == null) {
			prevInstAmtPri = BigDecimal.ZERO;
		}

		this.prevInstAmtPri = prevInstAmtPri;
	}

	public BigDecimal getPrevInstAmtPft() {
		return this.prevInstAmtPft;
	}

	public void setPrevInstAmtPft(BigDecimal prevInstAmtPft) {

		if (prevInstAmtPft == null) {
			prevInstAmtPft = BigDecimal.ZERO;
		}

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
		return this.futurePri1;
	}

	public void setFuturePri1(BigDecimal futurePri1) {

		if (futurePri1 == null) {
			futurePri1 = BigDecimal.ZERO;
		}

		this.futurePri1 = futurePri1;
	}

	public BigDecimal getFuturePri2() {
		return this.futurePri2;
	}

	public void setFuturePri2(BigDecimal futurePri2) {

		if (futurePri2 == null) {
			futurePri2 = BigDecimal.ZERO;
		}

		this.futurePri2 = futurePri2;
	}

	public BigDecimal getFutureRpyPft1() {
		return this.futureRpyPft1;
	}

	public void setFutureRpyPft1(BigDecimal futureRpyPft1) {

		if (futureRpyPft1 == null) {
			futureRpyPft1 = BigDecimal.ZERO;
		}

		this.futureRpyPft1 = futureRpyPft1;
	}

	public BigDecimal getFutureRpyPft2() {
		return this.futureRpyPft2;
	}

	public void setFutureRpyPft2(BigDecimal futureRpyPft2) {

		if (futureRpyPft2 == null) {
			futureRpyPft2 = BigDecimal.ZERO;
		}

		this.futureRpyPft2 = futureRpyPft2;
	}

	public BigDecimal getChargeCollCust() {
		return this.chargeCollCust;
	}

	public void setChargeCollCust(BigDecimal chargeCollCust) {

		if (chargeCollCust == null) {
			chargeCollCust = BigDecimal.ZERO;
		}

		this.chargeCollCust = chargeCollCust;
	}

	public BigDecimal getUpfrontIntCust() {
		return this.upfrontIntCust;
	}

	public void setUpfrontIntCust(BigDecimal upfrontIntCust) {

		if (upfrontIntCust == null) {
			upfrontIntCust = BigDecimal.ZERO;
		}

		this.upfrontIntCust = upfrontIntCust;
	}

	public int getIntPaidDealerUpfront() {
		return intPaidDealerUpfront;
	}

	public void setIntPaidDealerUpfront(int intPaidDealerUpfront) {
		this.intPaidDealerUpfront = intPaidDealerUpfront;
	}

	public int getPreEmiIntPaid() {
		return preEmiIntPaid;
	}

	public void setPreEmiIntPaid(int preEmiIntPaid) {
		this.preEmiIntPaid = preEmiIntPaid;
	}

	public String getRepoStatus() {
		return repoStatus;
	}

	public void setRepoStatus(String repoStatus) {
		this.repoStatus = repoStatus;
	}

	public String getRepoDate() {
		return repoDate;
	}

	public void setRepoDate(String repoDate) {
		this.repoDate = repoDate;
	}

	public String getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Date getLatestRpyDate() {
		return latestRpyDate;
	}

	public void setLatestRpyDate(Date latestRpyDate) {
		this.latestRpyDate = latestRpyDate;
	}

	public BigDecimal getCcyMinorCcyUnits() {
		return this.ccyMinorCcyUnits;
	}

	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {

		if (ccyMinorCcyUnits == null) {
			ccyMinorCcyUnits = BigDecimal.ZERO;
		}

		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

	public String getFinPurpose() {
		return finPurpose;
	}

	public void setFinPurpose(String finPurpose) {
		this.finPurpose = finPurpose;
	}

	public Date getFinStartDate() {
		return this.finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getLinkedFinRef() {
		return linkedFinRef;
	}

	public void setLinkedFinRef(String linkedFinRef) {
		this.linkedFinRef = linkedFinRef;
	}

	public String getClosedlinkedFinRef() {
		return closedlinkedFinRef;
	}

	public void setClosedlinkedFinRef(String closedlinkedFinRef) {
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

	public BigDecimal getEmiReceived() {
		return this.emiReceivedPri.add(this.emiReceivedPft);
	}

	public BigDecimal getPrvInstAmount() {
		return this.prevInstAmtPri.add(this.prevInstAmtPft);
	}

	public String getStatus() {
		String status = "";

		if (this.finIsActive) {
			status = "Active";
		} else {
			if ("C".equals(this.closingStatus)) {
				status = "Cancelled";
			} else {
				status = "Closed";
			}
		}

		return status;
	}

	public BigDecimal getFutureInstAmount() {
		return (this.futurePri1.subtract(this.futurePri2)).add(this.futureRpyPft1.subtract(this.futureRpyPft2));
	}

	public BigDecimal getFuturePrincipalComponent() {
		return this.futurePri1.subtract(this.futurePri2);
	}

	public BigDecimal getFutureInterestComponent() {
		return this.futureRpyPft1.subtract(this.futureRpyPft2);
	}

	public BigDecimal getIntPaidByMfgrOrDealerUpfront() {
		return this.intPaidDealerUpfront != 0 ? new BigDecimal(this.intPaidDealerUpfront) : BigDecimal.ZERO;
	}

	public Date getClosureDate() {
		Date closureDate = null;

		if (this.finIsActive) {
			closureDate = this.endInstallmentDate;
		} else {
			closureDate = this.latestRpyDate;
		}

		return closureDate;
	}

	public String getCustAddrLine1() {
		return custAddrLine1;
	}

	public void setCustAddrLine1(String custAddrLine1) {
		this.custAddrLine1 = custAddrLine1;
	}

	public String getCustAddrLine2() {
		return custAddrLine2;
	}

	public void setCustAddrLine2(String custAddrLine2) {
		this.custAddrLine2 = custAddrLine2;
	}

	public String getCustAddrZIP() {
		return custAddrZIP;
	}

	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}

	public String getLoanPurpose() {
		return loanPurpose;
	}

	public void setLoanPurpose(String loanPurpose) {
		this.loanPurpose = loanPurpose;
	}

	public List<SOASummaryReport> getSoaSummaryReports() {
		return soaSummaryReports;
	}

	public void setSoaSummaryReports(List<SOASummaryReport> soaSummaryReports) {
		this.soaSummaryReports = soaSummaryReports;
	}

	public List<SOATransactionReport> getTransactionReports() {
		return transactionReports;
	}

	public void setTransactionReports(List<SOATransactionReport> transactionReports) {
		this.transactionReports = transactionReports;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public int getNoPaidInst() {
		return noPaidInst;
	}

	public void setNoPaidInst(int noPaidInst) {
		this.noPaidInst = noPaidInst;
	}

	public BigDecimal getTotalPriPaid() {
		return totalPriPaid;
	}

	public void setTotalPriPaid(BigDecimal totalPriPaid) {
		this.totalPriPaid = totalPriPaid;
	}

	public BigDecimal getTotalPriBal() {
		return totalPriBal;
	}

	public void setTotalPriBal(BigDecimal totalPriBal) {
		this.totalPriBal = totalPriBal;
	}

	public BigDecimal getTotalPftPaid() {
		return totalPftPaid;
	}

	public void setTotalPftPaid(BigDecimal totalPftPaid) {
		this.totalPftPaid = totalPftPaid;
	}

	public BigDecimal getTotalPftBal() {
		return totalPftBal;
	}

	public void setTotalPftBal(BigDecimal totalPftBal) {
		this.totalPftBal = totalPftBal;
	}

	public BigDecimal getPaidTotal() {
		return paidTotal;
	}

	public void setPaidTotal(BigDecimal paidTotal) {
		this.paidTotal = paidTotal;
	}

	public BigDecimal getTotalOutStanding() {
		return totalOutStanding;
	}

	public void setTotalOutStanding(BigDecimal totalOutStanding) {
		this.totalOutStanding = totalOutStanding;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public int getNoOfOutStandInst() {
		return noOfOutStandInst;
	}

	public void setNoOfOutStandInst(int noOfOutStandInst) {
		this.noOfOutStandInst = noOfOutStandInst;
	}

	public Date getNextRpyDate() {
		return nextRpyDate;
	}

	public void setNextRpyDate(Date nextRpyDate) {
		this.nextRpyDate = nextRpyDate;
	}

	public BigDecimal getNextRpyPri() {
		return nextRpyPri;
	}

	public void setNextRpyPri(BigDecimal nextRpyPri) {

		if (nextRpyPri == null) {
			nextRpyPri = BigDecimal.ZERO;
		}
		this.nextRpyPri = nextRpyPri;
	}

	public BigDecimal getNextRpyPft() {
		return nextRpyPft;
	}

	public void setNextRpyPft(BigDecimal nextRpyPft) {
		if (nextRpyPft == null) {
			nextRpyPft = BigDecimal.ZERO;
		}
		this.nextRpyPft = nextRpyPft;
	}

	public BigDecimal getNextInstAmount() {
		return this.nextRpyPri.add(this.nextRpyPft);
	}

	public List<OtherFinanceDetail> getOtherFinanceDetails() {
		return otherFinanceDetails;
	}

	public void setOtherFinanceDetails(List<OtherFinanceDetail> otherFinanceDetails) {
		this.otherFinanceDetails = otherFinanceDetails;
	}

	public List<ApplicantDetail> getApplicantDetails() {
		return applicantDetails;
	}

	public void setApplicantDetails(List<ApplicantDetail> applicantDetails) {
		this.applicantDetails = applicantDetails;
	}

	public List<InterestRateDetail> getInterestRateDetails() {
		return interestRateDetails;
	}

	public void setInterestRateDetails(List<InterestRateDetail> interestRateDetails) {
		this.interestRateDetails = interestRateDetails;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<String> getCustFinRefDetails() {
		return custFinRefDetails;
	}

	public void setCustFinRefDetails(List<String> custFinRefDetails) {
		this.custFinRefDetails = custFinRefDetails;
	}

	public boolean isAdvEmiApplicable() {
		return advEmiApplicable;
	}

	public void setAdvEmiApplicable(boolean advEmiApplicable) {
		this.advEmiApplicable = advEmiApplicable;
	}

	public String getTenureLabel() {
		return tenureLabel;
	}

	public void setTenureLabel(String tenureLabel) {
		this.tenureLabel = tenureLabel;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public Date getFirstDisbDate() {
		return firstDisbDate;
	}

	public void setFirstDisbDate(Date firstDisbDate) {
		this.firstDisbDate = firstDisbDate;
	}

	public BigDecimal getRepayRateType() {
		return repayRateType;
	}

	public void setRepayRateType(BigDecimal repayRateType) {
		this.repayRateType = repayRateType;
	}

	public List<Map<String, Object>> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<Map<String, Object>> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public List<SOAScheduleReport> getSheduleReports() {
		return sheduleReports;
	}

	public void setSheduleReports(List<SOAScheduleReport> sheduleReports) {
		this.sheduleReports = sheduleReports;
	}

	public BigDecimal getSvamount() {
		return svamount;
	}

	public void setSvamount(BigDecimal svamount) {
		this.svamount = svamount;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public int getAdvEmiTerms() {
		return advEmiTerms;
	}

	public void setAdvEmiTerms(int advEmiTerms) {
		this.advEmiTerms = advEmiTerms;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public void setFuturePrincipalComponent(BigDecimal futurePrincipalComponent) {
		this.futurePrincipalComponent = futurePrincipalComponent;
	}

	public void setFutureInterestComponent(BigDecimal futureInterestComponent) {
		this.futureInterestComponent = futureInterestComponent;
	}

	public void setFutureInstAmount(BigDecimal futureInstAmount) {
		this.futureInstAmount = futureInstAmount;
	}

	public String getAdvEMIAmt() {
		return advEMIAmt;
	}

	public void setAdvEMIAmt(String advEMIAmt) {
		this.advEMIAmt = advEMIAmt;
	}

	public String getAdvIntAmt() {
		return advIntAmt;
	}

	public void setAdvIntAmt(String advIntAmt) {
		this.advIntAmt = advIntAmt;
	}

	public String getCashCollAmt() {
		return cashCollAmt;
	}

	public void setCashCollAmt(String cashCollAmt) {
		this.cashCollAmt = cashCollAmt;
	}

	public String getDsraAmt() {
		return dsraAmt;
	}

	public void setDsraAmt(String dsraAmt) {
		this.dsraAmt = dsraAmt;
	}

	public int getNoOfEmiOverDue() {
		return noOfEmiOverDue;
	}

	public void setNoOfEmiOverDue(int noOfEmiOverDue) {
		this.noOfEmiOverDue = noOfEmiOverDue;
	}

	public BigDecimal getEmiAmtOverdue() {
		return emiAmtOverdue;
	}

	public void setEmiAmtOverdue(BigDecimal emiAmtOverdue) {
		this.emiAmtOverdue = emiAmtOverdue;
	}

	public BigDecimal getCurApplicableEMI() {
		return curApplicableEMI;
	}

	public void setCurApplicableEMI(BigDecimal curApplicableEMI) {
		this.curApplicableEMI = curApplicableEMI;
	}

	public String getLoanAmountInWords() {
		return loanAmountInWords;
	}

	public void setLoanAmountInWords(String loanAmountInWords) {
		this.loanAmountInWords = loanAmountInWords;
	}

	public String getCurApplicableEMIInWords() {
		return curApplicableEMIInWords;
	}

	public void setCurApplicableEMIInWords(String curApplicableEMIInWords) {
		this.curApplicableEMIInWords = curApplicableEMIInWords;
	}

	public String getTotalPftPaidInWords() {
		return totalPftPaidInWords;
	}

	public void setTotalPftPaidInWords(String totalPftPaidInWords) {
		this.totalPftPaidInWords = totalPftPaidInWords;
	}

	public String getTotalPriBalInwords() {
		return totalPriBalInwords;
	}

	public void setTotalPriBalInwords(String totalPriBalInwords) {
		this.totalPriBalInwords = totalPriBalInwords;
	}

	public String getTotalPriPaidInWords() {
		return totalPriPaidInWords;
	}

	public void setTotalPriPaidInWords(String totalPriPaidInWords) {
		this.totalPriPaidInWords = totalPriPaidInWords;
	}

	public int getNoOfODTerms() {
		return noOfODTerms;
	}

	public void setNoOfODTerms(int noOfODTerms) {
		this.noOfODTerms = noOfODTerms;
	}

	public BigDecimal getTotalODAmt() {
		return totalODAmt;
	}

	public void setTotalODAmt(BigDecimal totalODAmt) {
		this.totalODAmt = totalODAmt;
	}

	public String getCustSalutation() {
		return custSalutation;
	}

	public void setCustSalutation(String custSalutation) {
		this.custSalutation = custSalutation;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getProviderGSTIN() {
		return providerGSTIN;
	}

	public void setProviderGSTIN(String providerGSTIN) {
		this.providerGSTIN = providerGSTIN;
	}

	public String getCustGSTIN() {
		return custGSTIN;
	}

	public void setCustGSTIN(String custGSTIN) {
		this.custGSTIN = custGSTIN;
	}

	public String getPlaceOfSupply() {
		return placeOfSupply;
	}

	public void setPlaceOfSupply(String placeOfSupply) {
		this.placeOfSupply = placeOfSupply;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public BigDecimal getUnAdjAmt() {
		return unAdjAmt;
	}

	public void setUnAdjAmt(BigDecimal unAdjAmt) {
		this.unAdjAmt = unAdjAmt;
	}

	public BigDecimal getNetReceivable() {
		return netReceivable;
	}

	public void setNetReceivable(BigDecimal netReceivable) {
		this.netReceivable = netReceivable;
	}

	public String getUnAdjAmtInWords() {
		return unAdjAmtInWords;
	}

	public void setUnAdjAmtInWords(String unAdjAmtInWords) {
		this.unAdjAmtInWords = unAdjAmtInWords;
	}

	public String getNetRcvbleInWords() {
		return netRcvbleInWords;
	}

	public void setNetRcvbleInWords(String netRcvbleInWords) {
		this.netRcvbleInWords = netRcvbleInWords;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

}