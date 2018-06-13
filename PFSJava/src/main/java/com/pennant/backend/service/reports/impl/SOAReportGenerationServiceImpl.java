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
 *
 * FileName    		: SOAReportGenerationServiceImpl.java							        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
 * 24-05-2018          Srikanth                  0.2           Merge the Code From Bajaj To Core                                                                                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.reports.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.ComparisonChain;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.dataengine.model.EventProperties;
public class SOAReportGenerationServiceImpl extends GenericService<StatementOfAccount> implements SOAReportGenerationService{
	private static Logger logger = Logger.getLogger(SOAReportGenerationServiceImpl .class);
	
	private static final String inclusive = "*";
	private static final String exclusive = "^";
	
	private SOAReportGenerationDAO soaReportGenerationDAO;

	public SOAReportGenerationServiceImpl() {
		super();
	}

	
	private FinanceMain getFinanceMain(String finReference) {
		return this.soaReportGenerationDAO.getFinanceMain(finReference);
	}
	
	private List<FinanceScheduleDetail> getFinScheduleDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinScheduleDetails(finReference);
	}
	
	
	private List<FinAdvancePayments> getFinAdvancePayments(String finReference) {
		return this.soaReportGenerationDAO.getFinAdvancePayments(finReference);
	}
	
	private List<PaymentInstruction> getPaymentInstructions(String finReference) {
		return this.soaReportGenerationDAO.getPaymentInstructions(finReference);
	}
	
	private List<FinODDetails> getFinODDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinODDetails(finReference);
	}
	
	private List<ManualAdvise> getManualAdvise(String finReference) {
		return this.soaReportGenerationDAO.getManualAdvise(finReference);
	}
	
	private List<ManualAdviseMovements> getManualAdviseMovements(String finReference) {
		return this.soaReportGenerationDAO.getManualAdviseMovements(finReference);
	}
	
	private List<ReceiptAllocationDetail> getReceiptAllocationDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getReceiptAllocationDetailsList(finReference);
	}
	
	private List<FinReceiptHeader> getFinReceiptHeaders(String finReference) {
		return this.soaReportGenerationDAO.getFinReceiptHeaders(finReference);
	}
	
	private List<FinReceiptDetail> getFinReceiptDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinReceiptDetails(finReference);
	}
	
	
	private StatementOfAccount getSOALoanDetails(String finReference) {
		return this.soaReportGenerationDAO.getSOALoanDetails(finReference);
	}
	
	private FinanceProfitDetail getFinanceProfitDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinanceProfitDetails(finReference);
	}

	private int getFinanceProfitDetailActiveCount(long finProfitDetailActiveCount, boolean active) {
		return this.soaReportGenerationDAO.getFinanceProfitDetailActiveCount(finProfitDetailActiveCount, active);
	}
	
	private StatementOfAccount getSOACustomerDetails(long custId) {
		return this.soaReportGenerationDAO.getSOACustomerDetails(custId);
	}
	
	private StatementOfAccount getSOAProductDetails(String finBranch, String finType) {
		return this.soaReportGenerationDAO.getSOAProductDetails(finBranch, finType);
	}
	
	private List<FinExcessAmount> getFinExcessAmountsList(String finReference) {
		return this.soaReportGenerationDAO.getFinExcessAmountsList(finReference);
	}
	
	private List<FinRepayHeader> getFinRepayHeadersList(String finReference) {
		return this.soaReportGenerationDAO.getFinRepayHeadersList(finReference);
	}
	
	private List<FinFeeDetail> getFinFeedetails(String finReference) {
		return this.soaReportGenerationDAO.getFinFeedetails(finReference);
	}
	
	private Date getMaxSchDate(String finReference) {
		return this.soaReportGenerationDAO.getMaxSchDate(finReference);
	}

	private List<PresentmentDetail> getPresentmentDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getPresentmentDetailsList(finReference);
	}
	
	private List<RepayScheduleDetail> getRepayScheduleDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getRepayScheduleDetailsList(finReference);
	}
	
	private List<VASRecording> getVASRecordingsList(String finReference) {
		return this.soaReportGenerationDAO.getVASRecordingsList(finReference);
	}
	
	private List<FinFeeScheduleDetail> getFinFeeScheduleDetailsList(String finReference) {
		return this.soaReportGenerationDAO.getFinFeeScheduleDetailsList(finReference);
	}

	public void setSoaReportGenerationDAO(SOAReportGenerationDAO soaReportGenerationDAO) {
		this.soaReportGenerationDAO = soaReportGenerationDAO;
	}

	@Override
	public StatementOfAccount getStatmentofAccountDetails(String finReference, Date startDate, Date endDate) {
		logger.debug("Entering");
		//get the Loan Basic Details
		StatementOfAccount statementOfAccount = getSOALoanDetails(finReference);
		
		//get the FinProfitDeatails
		FinanceProfitDetail financeProfitDetail = getFinanceProfitDetails(finReference);
		//get the finance basic details
		FinanceMain finMain = getFinanceMain(finReference);
		int ccyEditField = statementOfAccount.getCcyEditField();
		if (financeProfitDetail != null) {
			
			long custId = financeProfitDetail.getCustId();
			int activeCount = getFinanceProfitDetailActiveCount(custId, true);
			int closeCount = getFinanceProfitDetailActiveCount(custId, false);
			
			statementOfAccount.setCustID(custId);
			statementOfAccount.setActiveCnt(activeCount);
			statementOfAccount.setCloseCnt(closeCount);
			statementOfAccount.setTot(activeCount + closeCount);
			statementOfAccount.setFinStartDate(financeProfitDetail.getFinStartDate());
			statementOfAccount.setLinkedFinRef(financeProfitDetail.getLinkedFinRef());
			statementOfAccount.setClosedlinkedFinRef(financeProfitDetail.getClosedlinkedFinRef());
			//BFSD Related
			statementOfAccount.setFinPurpose(financeProfitDetail.getFinPurpose());
			statementOfAccount.setCurrentDate(DateUtility.getAppDate());
			statementOfAccount.setMaturityDate(financeProfitDetail.getMaturityDate());
			statementOfAccount.setNOPaidInst(financeProfitDetail.getNOPaidInst());
			statementOfAccount.setTotalPriPaid(PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriPaid(),ccyEditField));
			statementOfAccount.setTotalPftPaid(PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPftPaid(),ccyEditField));
			BigDecimal paidTotal = financeProfitDetail.getTotalPriPaid().add(financeProfitDetail.getTotalPftPaid());
			statementOfAccount.setPaidTotal(PennantApplicationUtil.formateAmount(paidTotal,ccyEditField));
			statementOfAccount.setTotalPriBal(PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPriBal(),ccyEditField));
			statementOfAccount.setTotalPftBal(PennantApplicationUtil.formateAmount(financeProfitDetail.getTotalPftBal(),ccyEditField));
			BigDecimal totalOutStanding = financeProfitDetail.getTotalPriBal().add(financeProfitDetail.getTotalPftBal());
			statementOfAccount.setTotalOutStanding(PennantApplicationUtil.formateAmount(totalOutStanding,ccyEditField));
			int futureInst=financeProfitDetail.getNOInst()-financeProfitDetail.getNOPaidInst();
			statementOfAccount.setNoOfOutStandInst(futureInst);
			statementOfAccount.setFinCurrAssetValue(PennantApplicationUtil.formateAmount(finMain.getFinCurrAssetValue(),ccyEditField));
			//get the Customer Details
			StatementOfAccount statementOfAccountCustDetails = getSOACustomerDetails(custId);
			
			if (statementOfAccountCustDetails != null) {
				statementOfAccount.setCustShrtName(statementOfAccountCustDetails.getCustShrtName());
				statementOfAccount.setCustCIF(statementOfAccountCustDetails.getCustCIF());
				statementOfAccount.setCustAddrHNbr(statementOfAccountCustDetails.getCustAddrHNbr());
				statementOfAccount.setCustFlatNbr(statementOfAccountCustDetails.getCustFlatNbr());
				statementOfAccount.setCustAddrStreet(statementOfAccountCustDetails.getCustAddrStreet());
				statementOfAccount.setCustPOBox(statementOfAccountCustDetails.getCustPOBox());
				statementOfAccount.setCustAddrCity(statementOfAccountCustDetails.getCustAddrCity());
				statementOfAccount.setCustAddrProvince(statementOfAccountCustDetails.getCustAddrProvince());
				statementOfAccount.setCustAddrCountry(statementOfAccountCustDetails.getCustAddrCountry());
				statementOfAccount.setPhoneCountryCode(statementOfAccountCustDetails.getPhoneCountryCode());
				statementOfAccount.setPhoneAreaCode(statementOfAccountCustDetails.getPhoneAreaCode());
				statementOfAccount.setPhoneNumber(statementOfAccountCustDetails.getPhoneNumber());
				statementOfAccount.setCustEMail(statementOfAccountCustDetails.getCustEMail());
				statementOfAccount.setCustAddrLine1(statementOfAccountCustDetails.getCustAddrLine1());
				statementOfAccount.setCustAddrLine2(statementOfAccountCustDetails.getCustAddrLine2());
				statementOfAccount.setCustAddrZIP(statementOfAccountCustDetails.getCustAddrZIP());
			}
			
			//to get the FinType and FinBranch
			StatementOfAccount statementOfAccountProductDetails = getSOAProductDetails(financeProfitDetail.getFinBranch(), financeProfitDetail.getFinType());
			
			if (statementOfAccountProductDetails != null) {
				statementOfAccount.setFinType(statementOfAccountProductDetails.getFinType());
				statementOfAccount.setFinBranch(statementOfAccountProductDetails.getFinBranch());
			}
		}
		
		

		BigDecimal ccyMinorCcyUnits = statementOfAccount.getCcyMinorCcyUnits();
		if(startDate==null){
			startDate=financeProfitDetail.getFinStartDate();
		}
		if(endDate == null){
			endDate = DateUtility.getAppDate();
		} else { //endDate should be grater than app date then set to the Application date
			if(DateUtility.compare(endDate, DateUtility.getAppDate())>0){
				endDate = DateUtility.getAppDate();
			}
		}
		statementOfAccount.setStartDate(startDate);
		statementOfAccount.setEndDate(endDate);

		//Formatting the amounts
		 statementOfAccount.setLoanAmount(PennantApplicationUtil.formateAmount(statementOfAccount.getLoanAmount(), ccyEditField));
 		statementOfAccount.setPreferredCardLimit(PennantApplicationUtil.formateAmount(statementOfAccount.getPreferredCardLimit(), ccyEditField));
		statementOfAccount.setChargeCollCust(PennantApplicationUtil.formateAmount(statementOfAccount.getChargeCollCust(), ccyEditField));
		statementOfAccount.setUpfrontIntCust(PennantApplicationUtil.formateAmount(statementOfAccount.getUpfrontIntCust(), ccyEditField));
		statementOfAccount.setLinkedFinRef(PennantApplicationUtil.formateAmount(statementOfAccount.getLinkedFinRef(), ccyEditField));
		statementOfAccount.setClosedlinkedFinRef(PennantApplicationUtil.formateAmount(statementOfAccount.getClosedlinkedFinRef(), ccyEditField));
		
		statementOfAccount.setEmiReceivedPri(PennantApplicationUtil.formateAmount(statementOfAccount.getEmiReceivedPri(), ccyEditField));
		statementOfAccount.setEmiReceivedPft(PennantApplicationUtil.formateAmount(statementOfAccount.getEmiReceivedPft(), ccyEditField));
		
		statementOfAccount.setPrevInstAmtPri(PennantApplicationUtil.formateAmount(statementOfAccount.getPrevInstAmtPri(), ccyEditField));
		statementOfAccount.setPrevInstAmtPft(PennantApplicationUtil.formateAmount(statementOfAccount.getPrevInstAmtPft(), ccyEditField));
		
		statementOfAccount.setFuturePri1(PennantApplicationUtil.formateAmount(statementOfAccount.getFuturePri1(), ccyEditField));
		statementOfAccount.setFuturePri2(PennantApplicationUtil.formateAmount(statementOfAccount.getFuturePri2(), ccyEditField));
		
		statementOfAccount.setFutureRpyPft1(PennantApplicationUtil.formateAmount(statementOfAccount.getFutureRpyPft1(), ccyEditField));
		statementOfAccount.setFutureRpyPft2(PennantApplicationUtil.formateAmount(statementOfAccount.getFutureRpyPft2(), ccyEditField));
		if(statementOfAccount.isFinIsActive()){
			statementOfAccount.setLatestRpyDate(statementOfAccount.getEndInstallmentDate());
		}
	
		//get the Summary Details
		List<SOASummaryReport> soaSummaryDetailsList = getSOASummaryDetails(finReference,finMain,financeProfitDetail);
		
		for (SOASummaryReport summary : soaSummaryDetailsList) {
			summary.setFinReference(finReference);
			summary.setCcyEditField(ccyEditField);
			summary.setCcyMinorCcyUnits(ccyMinorCcyUnits);
			summary.setAppDate(DateUtility.getAppDate());
		}
		
		//get the Transaction Details
		List<SOATransactionReport> soaTransactionReportsList = getTransactionDetails(finReference, statementOfAccount,finMain);
		
		List<SOATransactionReport> finalSOATransactionReports = new ArrayList<SOATransactionReport>();
		
		//Transaction Details Filtering
		for (SOATransactionReport soaTransactionReport : soaTransactionReportsList) {
			
			if (DateUtility.compare(soaTransactionReport.getTransactionDate(), startDate) >= 0
					&& DateUtility.compare(soaTransactionReport.getTransactionDate(), endDate) <= 0) {
				
				soaTransactionReport.setFinReference(finReference);
				soaTransactionReport.setCcyEditField(statementOfAccount.getCcyEditField());
				soaTransactionReport.setFromDate(startDate);
				soaTransactionReport.setToDate(endDate);
				soaTransactionReport.setCcyMinorCcyUnits(ccyMinorCcyUnits);
				
				soaTransactionReport.setDebitAmount(PennantApplicationUtil.formateAmount(soaTransactionReport.getDebitAmount(), ccyEditField));
				soaTransactionReport.setCreditAmount(PennantApplicationUtil.formateAmount(soaTransactionReport.getCreditAmount(), ccyEditField));
				
				finalSOATransactionReports.add(soaTransactionReport);
			} 
		}
		//Get the Selected Loan Types are Adding ValueDate and balance for the SOA Report.
		List<String> soaFinTypes = getSOAFinTypes();
		if (soaFinTypes != null && soaFinTypes.contains(finMain.getFinType())) {
			Collections.sort(finalSOATransactionReports, new Comparator<SOATransactionReport>() {
				public int compare(SOATransactionReport o1, SOATransactionReport o2) {

					return ComparisonChain.start().compare(o1.getValueDate(), o2.getValueDate())
							.compare(o1.getPriority(), o2.getPriority()).result();
				}
			});

		} else {

			Collections.sort(finalSOATransactionReports, new Comparator<SOATransactionReport>() {
				public int compare(SOATransactionReport o1, SOATransactionReport o2) {

					return ComparisonChain.start().compare(o1.getTransactionDate(), o2.getTransactionDate())
							.compare(o1.getPriority(), o2.getPriority()).result();
				}
			});
		}
		
		BigDecimal balanceAmt = BigDecimal.ZERO;
		for (SOATransactionReport soaTransactionReport : finalSOATransactionReports) {
			if(BigDecimal.ZERO.compareTo(soaTransactionReport.getDebitAmount()) != 0 ){
				balanceAmt = balanceAmt.add(soaTransactionReport.getDebitAmount());
			}else if(BigDecimal.ZERO.compareTo(soaTransactionReport.getCreditAmount()) != 0 ){
				balanceAmt = balanceAmt.subtract(soaTransactionReport.getCreditAmount());
			}
			soaTransactionReport.setBalanceAmount(balanceAmt);
		}
		//send the toDate and from Date for Report
		if(finalSOATransactionReports.isEmpty()){
			SOATransactionReport sOATransactionReport= new SOATransactionReport();
			sOATransactionReport.setFromDate(startDate);
			sOATransactionReport.setToDate(endDate);
			finalSOATransactionReports.add(sOATransactionReport);
		}

		//Summary Reports List
		statementOfAccount.setSoaSummaryReports(soaSummaryDetailsList);
		
		//Transaction Reports List
		statementOfAccount.setTransactionReports(finalSOATransactionReports);
		
		logger.debug("Leaving");
		return statementOfAccount;
	}
	/**
	 * get the Report Summary Details
	 * 
	 */
	private List<SOASummaryReport> getSOASummaryDetails(String finReference,FinanceMain finMain,FinanceProfitDetail financeProfitDetail) {
		logger.debug("Enetring");
		
		SOASummaryReport soaSummaryReport = null;
		List<SOASummaryReport> soaSummaryReportsList = new ArrayList<SOASummaryReport>();
		
		//FinanceMain finMain = getFinanceMain(finReference);
		
		if (finMain != null) {
			
			List<FinanceScheduleDetail> finSchdDetList = getFinScheduleDetails(finReference);
			//FinanceProfitDetail financeProfitDetail = getFinanceProfitDetails(finReference);

			BigDecimal due = BigDecimal.ZERO;
			BigDecimal receipt = BigDecimal.ZERO;
			BigDecimal overDue = BigDecimal.ZERO;

			BigDecimal totalProfitSchd = BigDecimal.ZERO;
			BigDecimal totalPrincipalSchd = BigDecimal.ZERO;
			BigDecimal totalFeeschd = BigDecimal.ZERO;
			
			BigDecimal totalSchdPriPaid = BigDecimal.ZERO;
			BigDecimal totalSchdPftPaid = BigDecimal.ZERO;
			BigDecimal totalSchdfeepaid = BigDecimal.ZERO;

			if (finSchdDetList != null && !finSchdDetList.isEmpty()) {
				
				for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
					
					if ((DateUtility.compare(finSchdDetail.getSchDate(), DateUtility.getAppDate()) <= 0)) {
						
						if (finSchdDetail.getProfitSchd() != null) {
							totalProfitSchd = totalProfitSchd.add(finSchdDetail.getProfitSchd());
						}
						
						if (finSchdDetail.getPrincipalSchd() != null) {
							totalPrincipalSchd = totalPrincipalSchd.add(finSchdDetail.getPrincipalSchd());
						}
						
						if (finSchdDetail.getFeeSchd() != null) {
							totalFeeschd = totalFeeschd.add(finSchdDetail.getFeeSchd());
						}
						
						if (finSchdDetail.getSchdPriPaid() != null) {
							totalSchdPriPaid = totalSchdPriPaid.add(finSchdDetail.getSchdPriPaid());
						}
						
						if (finSchdDetail.getSchdPftPaid() != null) {
							totalSchdPftPaid = totalSchdPftPaid.add(finSchdDetail.getSchdPftPaid());
						}
						
						if (finSchdDetail.getSchdFeePaid() != null) {
							totalSchdfeepaid = totalSchdfeepaid.add(finSchdDetail.getSchdFeePaid());
						}
					}
				}
				
				due = totalProfitSchd.add(totalPrincipalSchd).add(totalFeeschd);
				receipt = totalSchdPriPaid.add(totalSchdPftPaid).add(totalSchdfeepaid);
				
				overDue = due.subtract(receipt);
				
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Instalment Amount");
				soaSummaryReport.setDue(due);
				soaSummaryReport.setReceipt(receipt);
				soaSummaryReport.setOverDue(overDue);
				
				soaSummaryReportsList.add(soaSummaryReport);
				
				due = totalPrincipalSchd;
				receipt = totalSchdPriPaid;
				
				overDue = due.subtract(receipt);
				
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Principal Component");
				soaSummaryReport.setDue(due);
				soaSummaryReport.setReceipt(receipt);
				soaSummaryReport.setOverDue(overDue);
				
				soaSummaryReportsList.add(soaSummaryReport);
				
				due = totalProfitSchd;
				receipt = totalSchdPftPaid;
				
				overDue = due.subtract(receipt);
				
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Interest Component");
				soaSummaryReport.setDue(due);
				soaSummaryReport.setReceipt(receipt);
				soaSummaryReport.setOverDue(overDue);
				
				soaSummaryReportsList.add(soaSummaryReport);
			}
			
			if (financeProfitDetail != null) {
				
				List<FinODDetails> finODDetailsList = getFinODDetails(finReference);
				List<ManualAdvise>  manualAdviseList = getManualAdvise(finReference);
				List<FinExcessAmount>  finExcessAmountsList = getFinExcessAmountsList(finReference);
				
				// FinODDetails
				if (finODDetailsList != null && !finODDetailsList.isEmpty()) {
					
					BigDecimal totPenaltyAmt = BigDecimal.ZERO;
					BigDecimal totwaived = BigDecimal.ZERO;
					BigDecimal totPenaltyPaid = BigDecimal.ZERO;
					
					for (FinODDetails finODDetails : finODDetailsList) {
						
						if (finODDetails.getTotPenaltyAmt() != null) {
							totPenaltyAmt = totPenaltyAmt.add(finODDetails.getTotPenaltyAmt());
						}
						
						if (finODDetails.getTotWaived() != null) {
							totwaived = totwaived.add(finODDetails.getTotWaived());
						}
						
						if (finODDetails.getTotPenaltyPaid() != null) {
							totPenaltyPaid = totPenaltyPaid.add(finODDetails.getTotPenaltyPaid());
						}
					}
					
					due = totPenaltyAmt.subtract(totwaived);
					receipt = totPenaltyPaid;
					
					overDue = due.subtract(receipt);
					
					soaSummaryReport = new SOASummaryReport();
					soaSummaryReport.setComponent("Late Payment Penalty");
					soaSummaryReport.setDue(due);
					soaSummaryReport.setReceipt(receipt);
					soaSummaryReport.setOverDue(overDue);
					
					soaSummaryReportsList.add(soaSummaryReport);
				}
				
				BigDecimal bounceDue = BigDecimal.ZERO;
				BigDecimal bounceRecipt = BigDecimal.ZERO;
				
				BigDecimal otherReceivableDue = BigDecimal.ZERO;
				BigDecimal otherReceivableReceipt = BigDecimal.ZERO;
				
				BigDecimal otherPayableDue = BigDecimal.ZERO;
				
				//Manual Advise
				if (manualAdviseList != null && !manualAdviseList.isEmpty())  {
					
					BigDecimal adviseBalanceAmt = BigDecimal.ZERO;
					BigDecimal bounceZeroAdviseAmount = BigDecimal.ZERO;
					BigDecimal bounceGreaterZeroAdviseAmount = BigDecimal.ZERO;
					BigDecimal bounceZeroPaidAmount = BigDecimal.ZERO;
					BigDecimal bounceGreaterZeroPaidAmount = BigDecimal.ZERO;
					
					for (ManualAdvise manualAdvise : manualAdviseList) {
						if (manualAdvise.getAdviseType() == 2 && manualAdvise.getBalanceAmt() != null) {
							adviseBalanceAmt = adviseBalanceAmt.add(manualAdvise.getBalanceAmt());
						}  
						
						if (manualAdvise.getAdviseType() == 1 && manualAdvise.getBounceID() == 0) {
							
							if (manualAdvise.getAdviseAmount() != null) {
								bounceZeroAdviseAmount = bounceZeroAdviseAmount.add(manualAdvise.getAdviseAmount()).subtract(manualAdvise.getWaivedAmount());
							}
							
							if (manualAdvise.getPaidAmount() != null) {
								bounceZeroPaidAmount = bounceZeroPaidAmount.add(manualAdvise.getPaidAmount());
							}
						} 
						
						if (manualAdvise.getBounceID() > 0) {
							
							if (manualAdvise.getAdviseAmount() != null) {
								bounceGreaterZeroAdviseAmount = bounceGreaterZeroAdviseAmount.add(manualAdvise.getAdviseAmount()).subtract(manualAdvise.getWaivedAmount());
							}
							
							if (manualAdvise.getPaidAmount() != null) {
								bounceGreaterZeroPaidAmount = bounceGreaterZeroPaidAmount.add(manualAdvise.getPaidAmount());
							}
						}
					}
					
					bounceDue = bounceGreaterZeroAdviseAmount;
					bounceRecipt = bounceGreaterZeroPaidAmount;
					
					
					otherReceivableDue = bounceZeroAdviseAmount;
					otherReceivableReceipt = bounceZeroPaidAmount;
					
					otherPayableDue = adviseBalanceAmt;
				} 
				
				overDue = bounceDue.subtract(bounceRecipt);
				
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Bounce Charges");
				soaSummaryReport.setDue(bounceDue);
				soaSummaryReport.setReceipt(bounceRecipt);
				soaSummaryReport.setOverDue(overDue);
				
				soaSummaryReportsList.add(soaSummaryReport);
				
				overDue = otherReceivableDue.subtract(otherReceivableReceipt);
				
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Other Receivables");
				soaSummaryReport.setDue(otherReceivableDue);
				soaSummaryReport.setReceipt(otherReceivableReceipt);
				soaSummaryReport.setOverDue(overDue);
				
				soaSummaryReportsList.add(soaSummaryReport);
				
				receipt = BigDecimal.ZERO;
				overDue = BigDecimal.ZERO;
				
				soaSummaryReport = new SOASummaryReport();
				soaSummaryReport.setComponent("Other Payables");
				soaSummaryReport.setDue(otherPayableDue);
				soaSummaryReport.setReceipt(receipt);
				soaSummaryReport.setOverDue(overDue);
				
				soaSummaryReportsList.add(soaSummaryReport);
				
				//FinExcess Amount
				if (finExcessAmountsList != null && !finExcessAmountsList.isEmpty()) {
					
					BigDecimal balanceAmt = BigDecimal.ZERO;
					
					for (FinExcessAmount finExcessAmount : finExcessAmountsList) {
						if (finExcessAmount.getBalanceAmt() != null) {
							balanceAmt = balanceAmt.add(finExcessAmount.getBalanceAmt());
						}
					}
					
					due = balanceAmt;
					receipt = BigDecimal.ZERO;
					overDue = BigDecimal.ZERO;
					
					soaSummaryReport = new SOASummaryReport();
					soaSummaryReport.setComponent("Unadjusted Amount");
					soaSummaryReport.setDue(due);
					soaSummaryReport.setReceipt(receipt);
					soaSummaryReport.setOverDue(overDue);
					
					soaSummaryReportsList.add(soaSummaryReport);
				} else {
					
					soaSummaryReport = new SOASummaryReport();
					soaSummaryReport.setComponent("Unadjusted Amount");
					soaSummaryReport.setDue(BigDecimal.ZERO);
					soaSummaryReport.setReceipt(BigDecimal.ZERO);
					soaSummaryReport.setOverDue(BigDecimal.ZERO);
					
					soaSummaryReportsList.add(soaSummaryReport);
				}
			}
		}
		logger.debug("Leaving");
		return soaSummaryReportsList;
	}
	
	/**
	 * to get the Report Transaction Details
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public List<SOATransactionReport> getTransactionDetails(String finReference, StatementOfAccount statementOfAccount,FinanceMain finMain) {
		
		logger.debug("Enetring");
		
		//Fin Schedule details
		String finSchedulePayable = "Amount Financed - Payable ";//Add Disbursement  1

		String brokenPeriodEvent = "Broken Period Interest Receivable- Due "; //6
		String foreclosureAmount = "Foreclosure Amount "; //19
		String dueForInstallment = "Due for Installment "; //3
		String partPrepayment = "Part Prepayment Amount "; //5

		//Fin Advance Payments
		String advancePayment = "Amount Paid Vide "; //Amount Paid Vide  2

		//Payment Instructions
		String paymentInstrucionEvent = "Amount Paid Vide ";//"STLMNT"; 7

		//FinODDetails
		String penality = "Penalty Due Created for Past due till date "; //16

		//Manual Advise Movement List
		String manualAdviseMovementEvent = "Waived Amount "; //13

		//Manual Advise
		String manualAdvFeeType = "- Payable"; //12
		String manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; //10
		String manualAdvPrentmentIn = "Bounce - Due for Installment: "; //11

		//Receipt Header
		String receiptHeaderEventExcess = "Payment Recieved vide ";
		String receiptHeaderTdsAdjust = "TDS Adjustment"; //17
		String receiptHeaderPaymentBouncedFor = "Payment Bounced For "; //9
		String receiptHeaderTdsAdjustReversal = "TDS Adjustment Reversal "; //18

		//FinFeeDetails
		String finFeeDetailOrgination = "- Due "; //14
		String finFeeDetailNotInDISBorPOSP = "- Due "; //15
		String finFeeDetailEvent = "- Due "; //4
		String finRef = "";//"(" + finReference + ")";
		String receiptHeaderPftWaived = "Interest from customer Waived Off ";
		String receiptHeaderPriWaived = "Principal from customer Waived Off ";
		String receiptHeaderPenaltyWaived = "Penalty from customer Waived Off ";
		
		
		SOATransactionReport soaTransactionReport = null;
		List<SOATransactionReport> soaTransactionReports = new ArrayList<SOATransactionReport>();
		
		//FinanceMain finMain = getFinanceMain(finReference);
		
		if (finMain != null) {
			
			//Finance Schedule Details
			List<FinanceScheduleDetail>  finSchdDetList = getFinScheduleDetails(finReference);
			Date maxSchDate = getMaxSchDate(finReference);
			
			//Finance Advance Payment Details
			List<FinAdvancePayments>  finAdvancePaymentsList = getFinAdvancePayments(finReference);
			
			//Payment Instruction Details
			List<PaymentInstruction>  paymentInstructionsList = getPaymentInstructions(finReference);
			
			//FinODDetails
			List<FinODDetails>  finODDetailsList = getFinODDetails(finReference);
			
			//Manual Advise Movements List
			List<ManualAdviseMovements>  manualAdviseMovementsList = getManualAdviseMovements(finReference);
			
			//Manual Advise List
			List<ManualAdvise>  manualAdviseList = getManualAdvise(finReference);
			
			//PresentmentDetails
			List<PresentmentDetail> PresentmentDetailsList = getPresentmentDetailsList(finReference);
			
			//Fin Receipt Header
			List<FinReceiptHeader>  finReceiptHeadersList = getFinReceiptHeaders(finReference);
			
			//Fin Fee Details
			List<FinFeeDetail> finFeedetailsList = getFinFeedetails(finReference);
						
			//Finance Schedule Details
			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
				
				if (StringUtils.isBlank(finMain.getClosingStatus())
						|| !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C")){
					
					//Add disbursement
					if (finSchdDetail.isDisbOnSchDate()) {
						
						BigDecimal transactionAmount = BigDecimal.ZERO;
						
						if (finSchdDetail.getDisbAmount() != null) {
							transactionAmount = finSchdDetail.getDisbAmount();
						}
						
						if (DateUtility.compare(finSchdDetail.getSchDate(), finMain.getFinStartDate()) == 0) {
							transactionAmount = transactionAmount.add(finMain.getFeeChargeAmt());
						}
						
						soaTransactionReport = new SOATransactionReport();
						soaTransactionReport.setEvent(finSchedulePayable+finRef);
						soaTransactionReport.setTransactionDate(finMain.getFinApprovedDate());
						soaTransactionReport.setValueDate(finMain.getFinStartDate());
						soaTransactionReport.setCreditAmount(transactionAmount);
						soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
						soaTransactionReport.setPriority(1);
						
						soaTransactionReports.add(soaTransactionReport);
					}
					
					//Broken Period Interest Receivable- Due
					if (StringUtils.equalsIgnoreCase("B", finSchdDetail.getBpiOrHoliday()) 
							&& finSchdDetail.getRepayAmount() != null
							&& finSchdDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						soaTransactionReport = new SOATransactionReport();
						soaTransactionReport.setEvent(brokenPeriodEvent+finRef);
						soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTransactionReport.setValueDate(finSchdDetail.getSchDate());
						soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
						soaTransactionReport.setDebitAmount(finSchdDetail.getRepayAmount());
						soaTransactionReport.setPriority(8);

						soaTransactionReports.add(soaTransactionReport);
					}
				}
				
				//fore closure Amount 
				if (maxSchDate != null && DateUtility.compare(maxSchDate, finSchdDetail.getSchDate()) == 0) {
					soaTransactionReport = new SOATransactionReport();
					soaTransactionReport.setEvent(foreclosureAmount+finRef);
					soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
					soaTransactionReport.setValueDate(finSchdDetail.getSchDate());
					soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
					soaTransactionReport.setDebitAmount(finSchdDetail.getRepayAmount().subtract(finSchdDetail.getPartialPaidAmt()));
					soaTransactionReport.setPriority(23);
					soaTransactionReports.add(soaTransactionReport);
				}
				
				if ((DateUtility.compare(finSchdDetail.getSchDate(), DateUtility.getAppDate()) <= 0)) {

					// Partial Prepayment Amount
					if (finSchdDetail.getPartialPaidAmt() != null
							&& finSchdDetail.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) {

						soaTransactionReport = new SOATransactionReport();
						soaTransactionReport.setEvent(partPrepayment+finRef);
						soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTransactionReport.setValueDate(finSchdDetail.getSchDate());
						soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
						soaTransactionReport.setDebitAmount(finSchdDetail.getPartialPaidAmt());
						soaTransactionReport.setPriority(7);
						soaTransactionReports.add(soaTransactionReport);
					}
					
					//Due for Installment 
					if ((StringUtils.isBlank(finMain.getClosingStatus()) || !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C"))
							&& (!finSchdDetail.isDisbOnSchDate() && DateUtility.compare(maxSchDate, finSchdDetail.getSchDate()) != 0)
							&& (StringUtils.isBlank(finSchdDetail.getBpiOrHoliday()) 
									&& !StringUtils.equalsIgnoreCase(finSchdDetail.getBpiOrHoliday(), "H")
									&& !StringUtils.equalsIgnoreCase(finSchdDetail.getBpiOrHoliday(), "B"))) {
						
						BigDecimal transactionAmt = BigDecimal.ZERO;
						
						if (finSchdDetail.getRepayAmount() != null) {
							transactionAmt = finSchdDetail.getRepayAmount();
						}
						
						if (finSchdDetail.getPartialPaidAmt() != null) {
							transactionAmt = transactionAmt.subtract(finSchdDetail.getPartialPaidAmt());
						}

						soaTransactionReport = new SOATransactionReport();
						soaTransactionReport.setEvent(dueForInstallment+finSchdDetail.getInstNumber()+finRef);
						soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTransactionReport.setValueDate(finSchdDetail.getSchDate());
						soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
						soaTransactionReport.setDebitAmount(transactionAmt);
						soaTransactionReport.setPriority(3);
						if (soaTransactionReport.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
							soaTransactionReports.add(soaTransactionReport);
						}
					}
				}
			}
			
			//fin Advance Payments List 
			if (finAdvancePaymentsList != null && !finAdvancePaymentsList.isEmpty()) {
				for (FinAdvancePayments finAdvancePayments : finAdvancePaymentsList) {
					advancePayment = "Amount Paid Vide "; 
					soaTransactionReport = new SOATransactionReport();
					if(StringUtils.isNotBlank(finAdvancePayments.getPaymentType())){
					advancePayment = advancePayment.concat(finAdvancePayments.getPaymentType()+":");
					}
					if(StringUtils.equals(finAdvancePayments.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE) ||
							StringUtils.equals(finAdvancePayments.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)){
						if ( StringUtils.isNotBlank(finAdvancePayments.getLlReferenceNo())){
							advancePayment = advancePayment.concat(finAdvancePayments.getLlReferenceNo());
						}
						soaTransactionReport.setValueDate(finAdvancePayments.getValueDate());
					} else {
						if ( StringUtils.isNotBlank(finAdvancePayments.getTransactionRef())) {
							advancePayment = advancePayment.concat(finAdvancePayments.getTransactionRef());
						}
						soaTransactionReport.setValueDate(finAdvancePayments.getLlDate());
					}
					soaTransactionReport.setEvent(advancePayment+finRef);
					soaTransactionReport.setTransactionDate(finAdvancePayments.getLlDate());
					soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
					soaTransactionReport.setDebitAmount(finAdvancePayments.getAmtToBeReleased());
					soaTransactionReport.setPriority(2);

					soaTransactionReports.add(soaTransactionReport);
				}
			}
			
			//paymentInstructionsList 
			for (PaymentInstruction paymentInstruction : paymentInstructionsList) {
				soaTransactionReport = new SOATransactionReport();
				paymentInstrucionEvent = "Amount Paid Vide ";
				if (StringUtils.isNotBlank(paymentInstruction.getPaymentType())) {
					paymentInstrucionEvent = paymentInstrucionEvent.concat(paymentInstruction.getPaymentType() + ":");
				}
				if(StringUtils.equals(paymentInstruction.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE) ||
						StringUtils.equals(paymentInstruction.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)){
					if (StringUtils.isNotBlank(paymentInstruction.getFavourNumber())) {
						paymentInstrucionEvent = paymentInstrucionEvent.concat(paymentInstruction.getFavourNumber());
					}
					soaTransactionReport.setValueDate(paymentInstruction.getValueDate());
				} else {
					if (StringUtils.isNotBlank(paymentInstruction.getTransactionRef())) {
						paymentInstrucionEvent = paymentInstrucionEvent.concat(paymentInstruction.getTransactionRef());
					}
					soaTransactionReport.setValueDate(paymentInstruction.getPostDate());
				}
				soaTransactionReport.setEvent(paymentInstrucionEvent+finRef);
				soaTransactionReport.setTransactionDate(paymentInstruction.getPostDate());
				soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
				soaTransactionReport.setDebitAmount(paymentInstruction.getPaymentAmount());
				soaTransactionReport.setPriority(9);

				soaTransactionReports.add(soaTransactionReport);
			}
			
			// FinODDetails 
			if (finODDetailsList != null && !finODDetailsList.isEmpty()
					&& (StringUtils.isBlank(finMain.getClosingStatus())
							|| !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C"))) {

				for (FinODDetails finODDetails : finODDetailsList) {
					soaTransactionReport = new SOATransactionReport();
					soaTransactionReport.setEvent(penality+DateUtility.formateDate(finODDetails.getFinODTillDate(), ""));
					soaTransactionReport.setTransactionDate(finODDetails.getFinODSchdDate());
					soaTransactionReport.setValueDate(finODDetails.getFinODSchdDate());
					soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
					soaTransactionReport.setDebitAmount(finODDetails.getTotPenaltyAmt());
					soaTransactionReport.setPriority(19);
					soaTransactionReports.add(soaTransactionReport);
				}
			}
			
			//Manual Advise Movement List 
			if (manualAdviseMovementsList != null && !manualAdviseMovementsList.isEmpty()) {
				for (ManualAdviseMovements manualAdviseMovements : manualAdviseMovementsList) {
					soaTransactionReport = new SOATransactionReport();
					manualAdviseMovementEvent = "Waived Amount ";
					if (StringUtils.isNotBlank(manualAdviseMovements.getFeeTypeDesc())) {
						manualAdviseMovementEvent = manualAdviseMovementEvent.concat(manualAdviseMovements.getFeeTypeDesc());
					} else {
						manualAdviseMovementEvent = manualAdviseMovementEvent.concat("Bounce");
					}
					soaTransactionReport.setEvent(manualAdviseMovementEvent.concat(finRef));
					soaTransactionReport.setTransactionDate(manualAdviseMovements.getMovementDate());
					soaTransactionReport.setValueDate(manualAdviseMovements.getValueDate());
					soaTransactionReport.setCreditAmount(manualAdviseMovements.getWaivedAmount());
					soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
					soaTransactionReport.setPriority(15);

					soaTransactionReports.add(soaTransactionReport);
				}
			}
			
			List<Long> presentmentReceiptIds = new ArrayList<Long>();
			
			for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {
				if (!presentmentReceiptIds.contains(presentmentDetail.getReceiptID())) {
					presentmentReceiptIds.add(presentmentDetail.getReceiptID());
				}
			}
			
			//Manual Advise 
			for (ManualAdvise manualAdvise : manualAdviseList) {
				manualAdvFeeType = "- Payable"; //12
				manualAdvPrentmentNotIn = "FeeDesc or Bounce - Due "; //10
				manualAdvPrentmentIn = "Bounce - Due for Installment: "; //11
				 
				if ((manualAdvise.getFeeTypeID() != 0 && manualAdvise.getFeeTypeID() != Long.MIN_VALUE)
						&& StringUtils.isNotBlank(manualAdvise.getFeeTypeDesc()) && manualAdvise.getAdviseType() == 2
						&& manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {
					soaTransactionReport = new SOATransactionReport();
					soaTransactionReport.setEvent(manualAdvise.getFeeTypeDesc()+manualAdvFeeType+finRef);
					soaTransactionReport.setTransactionDate(manualAdvise.getPostDate());
					soaTransactionReport.setValueDate(manualAdvise.getValueDate());
					soaTransactionReport.setCreditAmount(manualAdvise.getAdviseAmount());
					soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
					soaTransactionReport.setPriority(14);


					soaTransactionReports.add(soaTransactionReport);
				}
				//Bounce/Fee - Due
				if (manualAdvise.getAdviseType() != 2 
						&& manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {
					
					if (!presentmentReceiptIds.contains(manualAdvise.getReceiptID())) {

						soaTransactionReport = new SOATransactionReport();
						if(manualAdvise.getFeeTypeID()>0){
							
							manualAdvPrentmentNotIn= manualAdvise.getFeeTypeDesc()+" - Due";
							
							if(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(manualAdvise.getTaxComponent())){
								manualAdvPrentmentNotIn= manualAdvPrentmentNotIn + inclusive;
							}else if(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())){
								manualAdvPrentmentNotIn= manualAdvPrentmentNotIn + exclusive;
							}
						} else {
							manualAdvPrentmentNotIn = "Bounce - Due";
						}
						soaTransactionReport.setEvent(manualAdvPrentmentNotIn+finRef);
						soaTransactionReport.setTransactionDate(manualAdvise.getPostDate());
						soaTransactionReport.setValueDate(manualAdvise.getValueDate());
						soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
						soaTransactionReport.setDebitAmount(manualAdvise.getAdviseAmount());
						soaTransactionReport.setPriority(12);

						
						soaTransactionReports.add(soaTransactionReport);
					} else {
						//Bounce created for particular on Installment 
						if (manualAdvise.getFeeTypeID() == 0) {
							
							for (PresentmentDetail presentmentDetail : PresentmentDetailsList) {
								
								if (manualAdvise.getReceiptID() == presentmentDetail.getReceiptID()) {
									
									for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
										
										if (DateUtility.compare(presentmentDetail.getSchDate(), finSchdDetail.getSchDate()) == 0) {
											
											soaTransactionReport = new SOATransactionReport();
											if (finSchdDetail.getInstNumber() > 0) {
												manualAdvPrentmentIn= manualAdvPrentmentIn.concat(String.valueOf(finSchdDetail.getInstNumber()));
											}
											manualAdvPrentmentIn= manualAdvPrentmentIn.concat(finRef);
											soaTransactionReport.setEvent(manualAdvPrentmentIn);
											soaTransactionReport.setTransactionDate(manualAdvise.getPostDate());
											soaTransactionReport.setValueDate(manualAdvise.getValueDate());
											soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
											soaTransactionReport.setDebitAmount(manualAdvise.getAdviseAmount());
											soaTransactionReport.setPriority(13);

											soaTransactionReports.add(soaTransactionReport);
										}
									}
								}
							}
						}
					}
				}
			}
			
			if (finReceiptHeadersList != null && !finReceiptHeadersList.isEmpty()) {
				
				//FinReceiptDetails List
				List<FinReceiptDetail>  finReceiptDetailsList = getFinReceiptDetails(finReference);
				
				//FinRepayHeaders List
				List<FinRepayHeader> finRepayHeadersList = getFinRepayHeadersList(finReference);
				
				//FinRepayscheduledetails List
				List<RepayScheduleDetail> finRepayScheduleDetailsList = getRepayScheduleDetailsList(finReference);
				
				//FinReceipt Allocation Details
				List<ReceiptAllocationDetail> finReceiptAllocationDetailsList = getReceiptAllocationDetailsList(finReference);
				
				for (FinReceiptHeader finReceiptHeader : finReceiptHeadersList) {
					
					for (FinReceiptDetail finReceiptDetail : finReceiptDetailsList) {
						
						if (finReceiptDetail.getReceiptID()  == finReceiptHeader.getReceiptID()) {
							receiptHeaderEventExcess = "Payment Recieved vide ";
							if (StringUtils.isBlank(finReceiptHeader.getReceiptModeStatus())
									|| !StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), "C")) {
								if (!StringUtils.equals("PAYABLE", finReceiptDetail.getPaymentType())) {
									soaTransactionReport = new SOATransactionReport();
									soaTransactionReport.setTransactionDate(finReceiptHeader.getReceiptDate());
									if (!(StringUtils.equals("EXCESS", finReceiptDetail.getPaymentType())
											|| StringUtils.equals("CASH", finReceiptDetail.getPaymentType()))) {
										if (StringUtils.isNotBlank(finReceiptDetail.getPaymentType())) {
											receiptHeaderEventExcess = receiptHeaderEventExcess.concat(finReceiptDetail.getPaymentType()+"No.:");
										}
										if(StringUtils.isNotBlank(finReceiptDetail.getTransactionRef())){
											receiptHeaderEventExcess= receiptHeaderEventExcess.concat(finReceiptDetail.getTransactionRef());
										}
										if(StringUtils.isNotBlank(finReceiptDetail.getFavourNumber())){
											receiptHeaderEventExcess= receiptHeaderEventExcess.concat(finReceiptDetail.getFavourNumber());
										}
										receiptHeaderEventExcess = receiptHeaderEventExcess.concat(" "+finRef);
										
									} else if (StringUtils.equals("EXCESS", finReceiptDetail.getPaymentType())) {
										receiptHeaderEventExcess =  "Amount Adjusted " + finRef;
									} else if(StringUtils.equals("CASH", finReceiptDetail.getPaymentType())){
										receiptHeaderEventExcess =  "Cash received Vide Receipt No";
										if(StringUtils.isNotBlank(finReceiptDetail.getPaymentRef())){
											receiptHeaderEventExcess = receiptHeaderEventExcess.concat(finReceiptDetail.getPaymentRef() + finRef);
										}
									}
									soaTransactionReport.setValueDate(finReceiptDetail.getReceivedDate());
									soaTransactionReport.setEvent(receiptHeaderEventExcess);
									soaTransactionReport.setCreditAmount(finReceiptDetail.getAmount());
									
									if (StringUtils.equals(finReceiptDetail.getPaymentType(), "EXCESS")) {
										soaTransactionReport.setDebitAmount(finReceiptDetail.getAmount());
									} else {
										soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
									}
									soaTransactionReport.setPriority(10);
									
									soaTransactionReports.add(soaTransactionReport);
								}
								
								//Receipt Allocation Details  
								for (ReceiptAllocationDetail finReceiptAllocationDetail : finReceiptAllocationDetailsList) {
									
									if (finReceiptHeader.getReceiptID() == finReceiptAllocationDetail.getReceiptID()
											&& StringUtils.equalsIgnoreCase("TDS", finReceiptAllocationDetail.getAllocationType())) {
										
										soaTransactionReport = new SOATransactionReport();
										soaTransactionReport.setEvent(receiptHeaderTdsAdjust+finRef);
										soaTransactionReport.setTransactionDate(finReceiptHeader.getReceiptDate());
										soaTransactionReport.setValueDate(finReceiptHeader.getReceiptDate());
										soaTransactionReport.setCreditAmount(finReceiptAllocationDetail.getPaidAmount());
										soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
										soaTransactionReport.setPriority(21);
										soaTransactionReports.add(soaTransactionReport);
									}
								}
							}
							
							//Receipt Header with Manual Advise 
							if (StringUtils.equals(finReceiptHeader.getReceiptMode(), finReceiptDetail.getPaymentType())) {
								
								for (ManualAdvise manualAdvise : manualAdviseList) {
									receiptHeaderPaymentBouncedFor = "Payment Bounced For "; //9
									if (finReceiptHeader.getReceiptID()  == manualAdvise.getReceiptID()) {
										
										if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), "B")
												&& manualAdvise.getAdviseType() == 1
												&& manualAdvise.getBounceID() > 0) {
											
											soaTransactionReport = new SOATransactionReport();
											if (!(StringUtils.equals("EXCESS", finReceiptDetail.getPaymentType())
													|| StringUtils.equals("CASH", finReceiptDetail.getPaymentType()))) {
												receiptHeaderPaymentBouncedFor= receiptHeaderPaymentBouncedFor.concat(finReceiptDetail.getPaymentType()+"No.:" );
												if(StringUtils.isNotBlank(finReceiptDetail.getFavourNumber())){
													receiptHeaderPaymentBouncedFor=receiptHeaderPaymentBouncedFor.concat(finReceiptDetail.getFavourNumber());
												}
												receiptHeaderPaymentBouncedFor= receiptHeaderPaymentBouncedFor + finRef;
											} else if (StringUtils.equals("CASH", finReceiptDetail.getPaymentType())) {
												receiptHeaderPaymentBouncedFor = "Cash Bounced For Receipt No.";
												if(StringUtils.isNotBlank(finReceiptDetail.getPaymentRef())){
												receiptHeaderPaymentBouncedFor= receiptHeaderPaymentBouncedFor.concat(finReceiptDetail.getPaymentRef());
												}
												receiptHeaderPaymentBouncedFor =receiptHeaderPaymentBouncedFor.concat(finRef);
											}
											soaTransactionReport.setEvent(receiptHeaderPaymentBouncedFor);
											soaTransactionReport.setTransactionDate(finReceiptHeader.getReceiptDate());
											soaTransactionReport.setValueDate(finReceiptHeader.getBounceDate());
											soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
											soaTransactionReport.setDebitAmount(finReceiptDetail.getAmount());
											soaTransactionReport.setPriority(11);

											soaTransactionReports.add(soaTransactionReport);
										}
									}
								}
							}
							
							if (StringUtils.equalsIgnoreCase(finReceiptDetail.getStatus(), "B")) {
								
								if (finRepayHeadersList != null && !finRepayHeadersList.isEmpty()) {
									
									for (FinRepayHeader finRepayHeader: finRepayHeadersList) {
										
										if (finReceiptDetail.getReceiptSeqID() == finRepayHeader.getReceiptSeqID()) {
											
											BigDecimal totalTdsSchdPayNow = BigDecimal.ZERO;
											for (RepayScheduleDetail finRepayScheduleDetail : finRepayScheduleDetailsList) {
												
												if (finRepayScheduleDetail.getRepayID() == finRepayHeader.getRepayID()) {
													
													if(finRepayScheduleDetail.getTdsSchdPayNow() != null
															&& finRepayScheduleDetail.getTdsSchdPayNow().compareTo(BigDecimal.ZERO) > 0){
														totalTdsSchdPayNow = totalTdsSchdPayNow.add(finRepayScheduleDetail.getTdsSchdPayNow());
														
													}
													//Interest from customer Waived Off
													if (finRepayScheduleDetail.getPftSchdWaivedNow() != null
															&& finRepayScheduleDetail.getPftSchdWaivedNow().compareTo(BigDecimal.ZERO) > 0) {
														soaTransactionReport = new SOATransactionReport();
														soaTransactionReport.setEvent(receiptHeaderPftWaived + finRef);
														soaTransactionReport.setTransactionDate(finReceiptDetail.getReceivedDate());
														soaTransactionReport.setValueDate(finReceiptDetail.getValueDate());
														soaTransactionReport.setCreditAmount(finRepayScheduleDetail.getPftSchdWaivedNow());
														soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
														soaTransactionReport.setPriority(4);
														soaTransactionReports.add(soaTransactionReport);

													}
													//Principal from customer Waived Off
													if(finRepayScheduleDetail.getPrincipalSchdPayNow() != null
															&& finRepayScheduleDetail.getPrincipalSchdPayNow().compareTo(BigDecimal.ZERO) > 0){
														soaTransactionReport = new SOATransactionReport();
														soaTransactionReport.setEvent(receiptHeaderPriWaived+finRef);
														soaTransactionReport.setTransactionDate(finReceiptDetail.getReceivedDate());
														soaTransactionReport.setValueDate(finReceiptDetail.getValueDate());
														soaTransactionReport.setCreditAmount(finRepayScheduleDetail.getPrincipalSchdPayNow());
														soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
														soaTransactionReport.setPriority(5);
														soaTransactionReports.add(soaTransactionReport);
														
													}
													//Penalty from customer Waived Off
													if ((StringUtils.isBlank(finMain.getClosingStatus())
															|| !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C")) &&
															finRepayScheduleDetail.getWaivedAmt() != null
															&& finRepayScheduleDetail.getWaivedAmt().compareTo(BigDecimal.ZERO) > 0) {
														soaTransactionReport = new SOATransactionReport();
														soaTransactionReport.setEvent(receiptHeaderPenaltyWaived + finRef);
														soaTransactionReport.setTransactionDate(finReceiptHeader.getReceiptDate());
														soaTransactionReport.setValueDate(finReceiptDetail.getReceivedDate());
														soaTransactionReport.setCreditAmount(finRepayScheduleDetail.getWaivedAmt());
														soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
														soaTransactionReport.setPriority(20);
														soaTransactionReports.add(soaTransactionReport);
													}
												}
											}
											//TDS Adjustment Reversal 
											if (totalTdsSchdPayNow.compareTo(BigDecimal.ZERO) > 0) {

												soaTransactionReport = new SOATransactionReport();
												soaTransactionReport.setEvent(receiptHeaderTdsAdjustReversal+finRef);
												soaTransactionReport.setTransactionDate(finReceiptHeader.getReceiptDate());
												soaTransactionReport.setValueDate(finReceiptHeader.getBounceDate());
												soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
												soaTransactionReport.setDebitAmount(totalTdsSchdPayNow);
												soaTransactionReport.setPriority(22);
												soaTransactionReports.add(soaTransactionReport);
											}
											
										}
										
									}
								}
							}
							
						}
					}
				}
			}
			
			if (StringUtils.isBlank(finMain.getClosingStatus())
					|| !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C")) {

				//Fin Fee Details List
				if (finFeedetailsList != null && !finFeedetailsList.isEmpty()) {

					// VAS Recordings
					List<VASRecording> VASRecordingsList = getVASRecordingsList(finReference);
					
					// Fin fee schedule details
					List<FinFeeScheduleDetail> finFeeScheduleDetailsList = getFinFeeScheduleDetailsList(finReference);
					
					for (FinFeeDetail finFeeDetail : finFeedetailsList) {
						finFeeDetailOrgination = "- Due ";
						finFeeDetailNotInDISBorPOSP = "- Due "; //15
						String vasProduct=null;
						for(VASRecording vASRecording:VASRecordingsList){
							if(StringUtils.equals(finReference, vASRecording.getPrimaryLinkRef()) && 
									StringUtils.equals(finFeeDetail.getVasReference(), vASRecording.getVasReference())){
								vasProduct=vASRecording.getProductDesc();
							}
						}
						BigDecimal debitAmount = BigDecimal.ZERO;
						BigDecimal waivedAmount = BigDecimal.ZERO;
						//Fee/Vas - Due 
						if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(finFeeDetail.getFeeScheduleMethod())
								|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(finFeeDetail.getFeeScheduleMethod())) {

							if (finFeeDetail.isOriginationFee()) {
								
								if (finFeeDetail.getRemainingFee() != null) {
									debitAmount = finFeeDetail.getRemainingFee();
								}

								if (finFeeDetail.getPaidAmount() != null) {
									debitAmount = debitAmount.add(finFeeDetail.getPaidAmount());
								}

								if (debitAmount.compareTo(BigDecimal.ZERO) > 0) {

									soaTransactionReport = new SOATransactionReport();
									if(StringUtils.isNotBlank(finFeeDetail.getFeeTypeDesc())){
										
										finFeeDetailOrgination  = finFeeDetail.getFeeTypeDesc()+" - Due";
										if(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())){
											finFeeDetailOrgination = finFeeDetailOrgination + inclusive;
										}else if(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())){
											finFeeDetailOrgination = finFeeDetailOrgination + exclusive;
										}
									} else {
										finFeeDetailOrgination=vasProduct+" - Due";
									}
									soaTransactionReport.setEvent(finFeeDetailOrgination+finRef);
									soaTransactionReport.setTransactionDate(finMain.getFinApprovedDate());
									soaTransactionReport.setValueDate(finMain.getFinStartDate());
									soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
									soaTransactionReport.setDebitAmount(debitAmount);
									soaTransactionReport.setPriority(16);

									soaTransactionReports.add(soaTransactionReport);
								}
							}
						} else {
							if (finFeeDetail.getPaidAmount() != null && finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
								soaTransactionReport = new SOATransactionReport();
								if(StringUtils.isNotBlank(finFeeDetail.getFeeTypeDesc())){
									finFeeDetailNotInDISBorPOSP  = finFeeDetail.getFeeTypeDesc();
								} else {
									finFeeDetailNotInDISBorPOSP=vasProduct;
								}
								soaTransactionReport.setEvent(finFeeDetailNotInDISBorPOSP+"- Due"+finRef);
								soaTransactionReport.setTransactionDate(finFeeDetail.getPostDate());
								soaTransactionReport.setValueDate(finFeeDetail.getPostDate());
								soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
								soaTransactionReport.setDebitAmount(finFeeDetail.getPaidAmount());
								soaTransactionReport.setPriority(17);
								soaTransactionReports.add(soaTransactionReport);
							}
						}
						//Waived amount for Fee/Vas 
						/*if (finFeeDetail.getWaivedAmount() != null ) {
							waivedAmount = waivedAmount.add(finFeeDetail.getWaivedAmount());
						}

						if (waivedAmount.compareTo(BigDecimal.ZERO) > 0 ) {

							soaTransactionReport = new SOATransactionReport();
							if (StringUtils.isNotBlank(finFeeDetail.getFeeTypeDesc())) {
								finFeeDetailOrgination = finFeeDetail.getFeeTypeDesc();
							} else {
								finFeeDetailOrgination = vasProduct;
							}
							waivedAmountForFee = Labels.getLabel("label_waivedAmountForFee");
							soaTransactionReport.setEvent(waivedAmountForFee +" " +finFeeDetailOrgination +" "+finRef);
							soaTransactionReport.setTransactionDate(finFeeDetail.getPostDate());
							soaTransactionReport.setValueDate(finMain.getFinStartDate());
							soaTransactionReport.setCreditAmount(waivedAmount);
							soaTransactionReport.setDebitAmount(BigDecimal.ZERO);
							soaTransactionReport.setPriority(18);

							soaTransactionReports.add(soaTransactionReport);
						}*/
					}
					
					//Fin Fee Schedule Details List 
					if (finFeeScheduleDetailsList != null && !finFeeScheduleDetailsList.isEmpty()) {
						
						for (FinFeeScheduleDetail finFeeScheduleDetail : finFeeScheduleDetailsList) {
							
							soaTransactionReport = new SOATransactionReport();
							soaTransactionReport.setEvent(finFeeScheduleDetail.getFeeTypeDesc()+finFeeDetailEvent+finRef);
							soaTransactionReport.setTransactionDate(finFeeScheduleDetail.getSchDate());
							soaTransactionReport.setValueDate(finFeeScheduleDetail.getSchDate());
							soaTransactionReport.setCreditAmount(BigDecimal.ZERO);
							soaTransactionReport.setDebitAmount(finFeeScheduleDetail.getSchAmount());
							soaTransactionReport.setPriority(6);
							
							soaTransactionReports.add(soaTransactionReport);
						}
					}
				}

			}
		}
		logger.debug("Leaving");
		return soaTransactionReports;
	}
	
	@Override
	public EventProperties getEventPropertiesList(String configName) {
		return this.soaReportGenerationDAO.getEventPropertiesList(configName);
	}
	
	@Override
	public List<String> getSOAFinTypes() {
		
		return soaReportGenerationDAO.getSOAFinTypes();
	}
}