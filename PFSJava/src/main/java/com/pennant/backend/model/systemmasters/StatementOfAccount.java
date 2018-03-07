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

	////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////		Loan Basic Details		////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	private Date                currentDate;
	private Date				maturityDate;
	private int					NOPaidInst			= 0;
	private int					noOfOutStandInst	= 0;
	private BigDecimal			totalPriPaid		= BigDecimal.ZERO;
	private BigDecimal			totalPriBal			= BigDecimal.ZERO;
	private BigDecimal			totalPftPaid		= BigDecimal.ZERO;
	private BigDecimal			totalPftBal			= BigDecimal.ZERO;
	private BigDecimal 			paidTotal			= BigDecimal.ZERO;;
	private BigDecimal 			totalOutStanding 	= BigDecimal.ZERO;
	private BigDecimal          finCurrAssetValue 	= BigDecimal.ZERO;

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////		Customer Details		////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Finance Profit Details
	private String finPurpose;
	private Date finStartDate;
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
	private String custAddrLine1;
	private String custAddrLine2;
	private String custAddrZIP;
	
	//Customer Phone Number
	private String phoneCountryCode;
	private String phoneAreaCode;
	private String phoneNumber;
	
	//Customer E-mails
	private String custEMail;
	
	private String loanPurpose;
	
	@SuppressWarnings("unused")
	private BigDecimal emiReceived = BigDecimal.ZERO;
	
	@SuppressWarnings("unused")
	private BigDecimal prvInstAmount = BigDecimal.ZERO;
	
	@SuppressWarnings("unused")
	private BigDecimal futureInstAmount = BigDecimal.ZERO;
	
	@SuppressWarnings("unused")
	private BigDecimal futurePrincipalComponent = BigDecimal.ZERO;
	
	@SuppressWarnings("unused")
	private BigDecimal futureInterestComponent = BigDecimal.ZERO;
	
	@SuppressWarnings("unused")
	private BigDecimal intPaidByMfgrOrDealerUpfront = BigDecimal.ZERO;
	
	//Summary Reports List
	private List<SOASummaryReport> soaSummaryReports = new ArrayList<SOASummaryReport>();
	
	//Transaction Reports List
	private List<SOATransactionReport> transactionReports = new ArrayList<SOATransactionReport>();

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

	public BigDecimal getLinkedFinRef() {
		return this.linkedFinRef;
	}

	public void setLinkedFinRef(BigDecimal linkedFinRef) {

		if (linkedFinRef == null) {
			linkedFinRef = BigDecimal.ZERO;
		}
		
		this.linkedFinRef = linkedFinRef;
	}

	public BigDecimal getClosedlinkedFinRef() {
		return this.closedlinkedFinRef;
	}

	public void setClosedlinkedFinRef(BigDecimal closedlinkedFinRef) {

		if (closedlinkedFinRef == null) {
			closedlinkedFinRef = BigDecimal.ZERO;
		}
		
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

	public int getNOPaidInst() {
		return NOPaidInst;
	}

	public void setNOPaidInst(int nOPaidInst) {
		NOPaidInst = nOPaidInst;
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
}
