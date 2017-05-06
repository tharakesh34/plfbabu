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
 * FileName    		:  ReceiptCalculator.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.RepayConstants;
import com.rits.cloning.Cloner;

public class ReceiptCalculator implements Serializable {

	private static final long					serialVersionUID	= 8062681791631293126L;
	private static Logger						logger				= Logger.getLogger(ReceiptCalculator.class);

	Date										curBussniessDate	= DateUtility.getAppDate();
	private FinODDetailsDAO						finODDetailsDAO;
	private ManualAdviseDAO						manualAdviseDAO;

	// Default Constructor
	public ReceiptCalculator() {
		super();
	}

	/*
	 * ___________________________________________________________________________________________
	 * 
	 * Main Methods 
	 * ___________________________________________________________________________________________
	 */

	public FinReceiptData initiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, String receiptPurpose) {
		return procInitiateReceipt(receiptData, scheduleData, receiptPurpose);
	}

	/** To Calculate the Amounts for given schedule */
	public FinReceiptData procInitiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, String receiptPurpose) {
		logger.debug("Entering");

		// Initialize Repay
		if ("I".equals(receiptData.getBuildProcess())) {
			receiptData = initializeReceipt(receiptData, scheduleData, receiptPurpose);
		}

		// Recalculate Repay
		if ("R".equals(receiptData.getBuildProcess())) {
			receiptData = recalReceipt(receiptData, scheduleData, receiptPurpose);
		}
		
		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for Initialize the data from Finance Details to Receipt Data to render Summary
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData initializeReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, String receiptPurpose) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		receiptData.setFinReference(financeMain.getFinReference());
		receiptData.setRepayMain(null);

		RepayMain repayMain = new RepayMain();
		repayMain.setFinReference(financeMain.getFinReference());
		repayMain.setFinCcy(financeMain.getFinCcy());
		repayMain.setProfitDaysBais(financeMain.getProfitDaysBasis());
		repayMain.setFinType(financeMain.getFinType());
		repayMain.setLovDescFinTypeName(financeMain.getLovDescFinTypeName());
		repayMain.setFinBranch(financeMain.getFinBranch());
		repayMain.setLovDescFinBranchName(financeMain.getLovDescFinBranchName());
		repayMain.setCustID(financeMain.getCustID());
		repayMain.setLovDescCustCIF(financeMain.getLovDescCustCIF());
		repayMain.setLovDescSalutationName(financeMain.getLovDescSalutationName());
		repayMain.setLovDescCustFName(financeMain.getLovDescCustFName());
		repayMain.setLovDescCustLName(financeMain.getLovDescCustLName());
		repayMain.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
		repayMain.setDateStart(financeMain.getFinStartDate());
		repayMain.setDateMatuirty(financeMain.getMaturityDate());
		repayMain.setAccrued(receiptData.getAccruedTillLBD());
		repayMain.setRepayAccountId(financeMain.getRepayAccountId());
		repayMain.setFinAccount(financeMain.getFinAccount());
		repayMain.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		repayMain.setPendindODCharges(receiptData.getPendingODC());
		repayMain.setEarlyPayEffectOn(financeMain.getLovDescFinScheduleOn());

		repayMain.setFinAmount(BigDecimal.ZERO);
		repayMain.setCurFinAmount(BigDecimal.ZERO);
		repayMain.setProfit(BigDecimal.ZERO);
		repayMain.setProfitBalance(BigDecimal.ZERO);
		repayMain.setPrincipal(BigDecimal.ZERO);
		repayMain.setPrincipalBalance(BigDecimal.ZERO);
		repayMain.setTotalCapitalize(BigDecimal.ZERO);
		repayMain.setCapitalizeBalance(BigDecimal.ZERO);
		repayMain.setOverduePrincipal(BigDecimal.ZERO);
		repayMain.setOverdueProfit(BigDecimal.ZERO);
		repayMain.setDateLastFullyPaid(financeMain.getFinStartDate());
		repayMain.setDateNextPaymentDue(financeMain.getMaturityDate());
		repayMain.setDownpayment(BigDecimal.ZERO);
		repayMain.setPrincipalPayNow(BigDecimal.ZERO);
		repayMain.setProfitPayNow(BigDecimal.ZERO);
		repayMain.setRefundNow(BigDecimal.ZERO);

		repayMain.setRepayAmountNow(BigDecimal.ZERO);
		repayMain.setRepayAmountExcess(BigDecimal.ZERO);
		receiptData.setRepayMain(repayMain);

		receiptData = calSummaryDetail(receiptData, scheduleData, receiptPurpose);
		
		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData recalReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, String receiptPurpose) {
		logger.debug("Entering");

		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();

		// Rendering 
		if(receiptDetailList == null || receiptDetailList.isEmpty()){
			return null;//TODO: Add Error to display
		}
		
		// Fetch total Advise details
		Map<Long, ManualAdvise> adviseMap = new HashMap<Long, ManualAdvise>();
		List<ManualAdvise> advises = getManualAdviseDAO().getManualAdviseByRef(financeMain.getFinReference(), 
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
		if (advises != null && !advises.isEmpty()) {
			for (int i = 0; i < advises.size(); i++) {
				if (adviseMap.containsKey(advises.get(i).getAdviseID())) {
					adviseMap.remove(advises.get(i).getAdviseID());
				}
				adviseMap.put(advises.get(i).getAdviseID(), advises.get(i));
			}
		}

		// Fetch total overdue details
		Map<Date, FinODDetails> overdueMap = new HashMap<Date, FinODDetails>();
		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		if (overdueList != null && !overdueList.isEmpty()) {
			for (int m = 0; m < overdueList.size(); m++) {
				if (overdueMap.containsKey(overdueList.get(m).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(m).getFinODSchdDate());
				}
				overdueMap.put(overdueList.get(m).getFinODSchdDate(), overdueList.get(m));
			}
		}
		
		// Allocation Details Map Preparation based on key Details
		Map<String, BigDecimal> paidAllocationMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> waivedAllocationMap = new HashMap<String, BigDecimal>();
		BigDecimal totalWaivedAmt = BigDecimal.ZERO;
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		if (allocationList != null && !allocationList.isEmpty()) {
			for (int a = 0; a < allocationList.size(); a++) {
				ReceiptAllocationDetail allocate = allocationList.get(a);
				if (allocate.getAllocationTo() == 0 || allocate.getAllocationTo() == Long.MIN_VALUE) {
					paidAllocationMap.put(allocate.getAllocationType(), allocate.getPaidAmount().add(allocate.getWaivedAmount()));
					waivedAllocationMap.put(allocate.getAllocationType(), allocate.getWaivedAmount());
				}else{
					paidAllocationMap.put(allocate.getAllocationType()+"_"+allocate.getAllocationTo(), allocate.getPaidAmount().add(allocate.getWaivedAmount()));
					
					BigDecimal waivedAmount = BigDecimal.ZERO;
					if(waivedAllocationMap.containsKey(allocate.getAllocationType())){
						waivedAmount = waivedAllocationMap.get(allocate.getAllocationType());
					}
					waivedAllocationMap.put(allocate.getAllocationType(), waivedAmount.add(allocate.getWaivedAmount()));
				}
				totalWaivedAmt = totalWaivedAmt.add(allocate.getWaivedAmount());
			}
		}
		
		List<FinRepayHeader> repayHeaderList = null;
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);

		// Start Receipt Details Rendering Process using allocation Details
		for (int i = 0; i < receiptDetailList.size(); i++) {
			
			FinReceiptDetail receiptDetail = receiptDetailList.get(i);
			
			// Internal temporary Record can't be processed for Calculation
			if(receiptDetail.isDelRecord()){
				continue;
			}
			
			BigDecimal totalReceiptAmt = receiptDetail.getAmount();
			repayHeaderList = new ArrayList<>();

			// If no balance for repayment then return with out calculation
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			List<RepayScheduleDetail> pastdueRpySchdList = new ArrayList<>();
			List<RepayScheduleDetail> partialRpySchdList = new ArrayList<>();
			String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();
			char[] rpyOrder = repayHierarchy.replace("CS", "C").toCharArray();
			int lastRenderSeq = 0;
			BigDecimal totPriPaidNow = BigDecimal.ZERO;
			BigDecimal totPftPaidNow = BigDecimal.ZERO;
			BigDecimal totLPftPaidNow = BigDecimal.ZERO;
			BigDecimal totFeePaidNow = BigDecimal.ZERO;
			BigDecimal totInsPaidNow = BigDecimal.ZERO;
			BigDecimal totPenaltyPaidNow = BigDecimal.ZERO;
			
			boolean isPartialPayNow = false; 
			BigDecimal partialSettleAmount = BigDecimal.ZERO;

			// Load Pending Schedules until balance available for payment
			for (int s = 1; s < tempScheduleDetails.size(); s++) {
				FinanceScheduleDetail curSchd = tempScheduleDetails.get(s);
				Date schdDate = curSchd.getSchDate();
				RepayScheduleDetail rsd = null;

				// Skip if repayment date after Current Business date
				if (schdDate.compareTo(curBussniessDate) > 0 && 
						!StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
					break;
				}
				
				// Find out early payment/ partial Settlement schedule term and amount
				if ((schdDate.compareTo(curBussniessDate) == 0 && 
						StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) ||
						(schdDate.compareTo(curBussniessDate) >= 0 && 
						StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE))) {
					
					// Manual Advises 
					if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

						if(!adviseMap.isEmpty()){
							List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
							Collections.sort(adviseIdList);
							for (int a = 0; a < adviseIdList.size(); a++) {

								ManualAdvise advise = adviseMap.get(adviseIdList.get(a));
								if(advise != null){
									
									if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID())){
										BigDecimal advAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID());
										if(advAllocateBal.compareTo(BigDecimal.ZERO) > 0){
											BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
											if(balAdvise.compareTo(BigDecimal.ZERO) > 0){
												if(totalReceiptAmt.compareTo(advAllocateBal) >= 0 && advAllocateBal.compareTo(balAdvise) < 0){
													balAdvise = advAllocateBal;
												}else if(totalReceiptAmt.compareTo(advAllocateBal) < 0 && balAdvise.compareTo(totalReceiptAmt) > 0){
													balAdvise = totalReceiptAmt;
												}

												// Reset Total Receipt Amount
												totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
												paidAllocationMap.put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), advAllocateBal.subtract(balAdvise));
												
												// Save Movements for Manual Advise
												ManualAdviseMovements movement = new ManualAdviseMovements();
												movement.setAdviseID(advise.getAdviseID());
												movement.setMovementDate(DateUtility.getAppDate());
												movement.setMovementAmount(balAdvise);
												movement.setPaidAmount(balAdvise);
												movement.setWaivedAmount(BigDecimal.ZERO);
												receiptDetail.getAdvMovements().add(movement);
											}
										}
									}
								}
							}
						}
					}
					
					// Only For Partial Settlement
					if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
						isPartialPayNow = true;
						partialSettleAmount = totalReceiptAmt;
					}
				}

				for (int j = 0; j < rpyOrder.length; j++) {

					char repayTo = rpyOrder[j];
					if(repayTo == RepayConstants.REPAY_PRINCIPAL){
						
						if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_PRI)){
							BigDecimal priAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_PRI);
							if(priAllocateBal.compareTo(BigDecimal.ZERO) > 0){
								BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
								if(balPri.compareTo(BigDecimal.ZERO) > 0){
									if(totalReceiptAmt.compareTo(priAllocateBal) >= 0 && priAllocateBal.compareTo(balPri) < 0){
										balPri = priAllocateBal;
									}else if(totalReceiptAmt.compareTo(priAllocateBal) < 0 && balPri.compareTo(totalReceiptAmt) > 0){
										balPri = totalReceiptAmt;
									}
									rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPri);

									// Reset Total Receipt Amount
									totalReceiptAmt = totalReceiptAmt.subtract(balPri);
									if(!isPartialPayNow){
										totPriPaidNow = totPriPaidNow.add(balPri);
									}
									paidAllocationMap.put(RepayConstants.ALLOCATION_PRI, priAllocateBal.subtract(balPri));

									// Update Schedule to avoid on Next loop Payment
									curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().add(balPri));
								}
							}
						}
					}else if(repayTo == RepayConstants.REPAY_PROFIT){

						String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
						char[] pftPayOrder = profit.toCharArray();
						for (char pftPayTo : pftPayOrder) {
							if(pftPayTo == RepayConstants.REPAY_PROFIT){

								if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)){
									BigDecimal pftAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_PFT);
									if(pftAllocateBal.compareTo(BigDecimal.ZERO) > 0){
										BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
										if(balPft.compareTo(BigDecimal.ZERO) > 0){
											if(totalReceiptAmt.compareTo(pftAllocateBal) >= 0 && pftAllocateBal.compareTo(balPft) < 0){
												balPft = pftAllocateBal;
											}else if(totalReceiptAmt.compareTo(pftAllocateBal) < 0 && balPft.compareTo(totalReceiptAmt) > 0){
												balPft = totalReceiptAmt;
											}
											rsd = prepareRpyRecord(curSchd, rsd, pftPayTo, balPft);

											// Reset Total Receipt Amount
											totalReceiptAmt = totalReceiptAmt.subtract(balPft);
											totPftPaidNow = totPftPaidNow.add(balPft);
											paidAllocationMap.put(RepayConstants.ALLOCATION_PFT, pftAllocateBal.subtract(balPft));

											// Update Schedule to avoid on Next loop Payment
											curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().add(balPft));
										}
									}
								}

							}else if(pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT){
								
								if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_LPFT)){
									BigDecimal latePftAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_LPFT);
									if(latePftAllocateBal.compareTo(BigDecimal.ZERO) > 0){
										FinODDetails overdue = overdueMap.get(schdDate);
										if(overdue != null){
											BigDecimal balLatePft = overdue.getLPIBal();
											if(balLatePft.compareTo(BigDecimal.ZERO) > 0){
												if(totalReceiptAmt.compareTo(latePftAllocateBal) >= 0 && latePftAllocateBal.compareTo(balLatePft) < 0){
													balLatePft = latePftAllocateBal;
												}else if(totalReceiptAmt.compareTo(latePftAllocateBal) < 0 && balLatePft.compareTo(totalReceiptAmt) > 0){
													balLatePft = totalReceiptAmt;
												}
												rsd = prepareRpyRecord(curSchd, rsd, pftPayTo, balLatePft);

												// Reset Total Receipt Amount
												totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
												totLPftPaidNow = totLPftPaidNow.add(balLatePft);
												paidAllocationMap.put(RepayConstants.ALLOCATION_LPFT, latePftAllocateBal.subtract(balLatePft));

												// Update Schedule to avoid on Next loop Payment
												overdue.setLPIBal(overdue.getLPIBal().subtract(balLatePft));
											}
										}
									}
								}
							}
						}

					}else if(repayTo == RepayConstants.REPAY_PENALTY){
						
						if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_ODC)){
							BigDecimal penaltyAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_ODC);
							if(penaltyAllocateBal.compareTo(BigDecimal.ZERO) > 0){
								FinODDetails overdue = overdueMap.get(schdDate);
								if(overdue != null){
									BigDecimal balPenalty = overdue.getTotPenaltyBal();
									if(balPenalty.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(penaltyAllocateBal) >= 0 && penaltyAllocateBal.compareTo(balPenalty) < 0){
											balPenalty = penaltyAllocateBal;
										}else if(totalReceiptAmt.compareTo(penaltyAllocateBal) < 0 && balPenalty.compareTo(totalReceiptAmt) > 0){
											balPenalty = totalReceiptAmt;
										}
										rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPenalty);

										// Reset Total Receipt Amount
										totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
										totPenaltyPaidNow = totPenaltyPaidNow.add(balPenalty);

										paidAllocationMap.put(RepayConstants.ALLOCATION_ODC, penaltyAllocateBal.subtract(balPenalty));

										// Update Schedule to avoid on Next loop Payment
										overdue.setTotPenaltyBal(overdue.getTotPenaltyBal().subtract(balPenalty));
									}
								}
							}
						}

					}else if(repayTo == RepayConstants.REPAY_OTHERS){

						// If Schedule has Unpaid Fee Amount
						if(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()).compareTo(BigDecimal.ZERO) > 0){

							// Fee Detail Collection
							for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
								FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
								List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();

								// No more Receipt amount left for next schedules
								if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
									break;
								}

								if(feeSchdList == null || feeSchdList.isEmpty()){
									continue;
								}

								// If Schedule Fee terms are less than actual calculated Sequence
								if(feeSchdList.size() < lastRenderSeq){
									continue;
								}

								// Calculate and set Fee Amount based on Fee ID
								for (int l = lastRenderSeq; l < feeSchdList.size(); l++) {
									if(feeSchdList.get(l).getSchDate().compareTo(schdDate) == 0){
										
										if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
											BigDecimal feeAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
											if(feeAllocateBal.compareTo(BigDecimal.ZERO) > 0){
												BigDecimal balFee = feeSchdList.get(l).getSchAmount().subtract(feeSchdList.get(l).getPaidAmount());
												if(balFee.compareTo(BigDecimal.ZERO) > 0){
													if(totalReceiptAmt.compareTo(feeAllocateBal) >= 0 && feeAllocateBal.compareTo(balFee) < 0){
														balFee = feeAllocateBal;
													}else if(totalReceiptAmt.compareTo(feeAllocateBal) < 0 && balFee.compareTo(totalReceiptAmt) > 0){
														balFee = totalReceiptAmt;
													}
													rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_FEE, balFee);

													// Reset Total Receipt Amount
													totalReceiptAmt = totalReceiptAmt.subtract(balFee);
													totFeePaidNow = totFeePaidNow.add(balFee);

													paidAllocationMap.put(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID(), feeAllocateBal.subtract(balFee));

													// Update Schedule to avoid on Next loop Payment
													feeSchdList.get(l).setPaidAmount(feeSchdList.get(l).getPaidAmount().add(balFee));
												}
											}
										}
										if(lastRenderSeq == 0){
											lastRenderSeq = l;
										}
										break;
									}
								}
							}
						}

						// If Schedule has Unpaid Fee Amount
						if((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0){

							// Insurance Details Collection
							for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
								FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
								List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();

								// No more Receipt amount left for next schedules
								if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
									break;
								}

								if(insSchdList == null || insSchdList.isEmpty()){
									continue;
								}

								// Calculate and set Fee Amount based on Fee ID
								for (int l = 0; l < insSchdList.size(); l++) {
									if(insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0){

										if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId())){
											BigDecimal insAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId());
											if(insAllocateBal.compareTo(BigDecimal.ZERO) > 0){
												BigDecimal balIns = insSchdList.get(l).getAmount().subtract(insSchdList.get(l).getInsurancePaid());
												if(balIns.compareTo(BigDecimal.ZERO) > 0){
													if(totalReceiptAmt.compareTo(insAllocateBal) >= 0 && insAllocateBal.compareTo(balIns) < 0){
														balIns = insAllocateBal;
													}else if(totalReceiptAmt.compareTo(insAllocateBal) < 0 && balIns.compareTo(totalReceiptAmt) > 0){
														balIns = totalReceiptAmt;
													}
													rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_INS, balIns);

													// Reset Total Receipt Amount
													totalReceiptAmt = totalReceiptAmt.subtract(balIns);
													totInsPaidNow = totInsPaidNow.add(balIns);

													paidAllocationMap.put(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId(), insAllocateBal.subtract(balIns));

													// Update Schedule to avoid on Next loop Payment
													insSchdList.get(l).setInsurancePaid(insSchdList.get(l).getInsurancePaid().add(balIns));
												}
											}
										}
										break;
									}
								}
							}
						}
					}

					// No more Receipt amount left for next schedules
					if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
						break;
					}
				}
				
				// Add Repay Schedule detail List
				if(rsd != null){
					if(isPartialPayNow){
						partialRpySchdList.add(rsd);
					}else{
						pastdueRpySchdList.add(rsd);
					}
				}
				
				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}

				// Sequence Order Increment to reduce loops on Fee setting
				lastRenderSeq++;
			}
			
			// Manual Advises 
			if (!isPartialPayNow && totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

				if(!adviseMap.isEmpty()){
					List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
					Collections.sort(adviseIdList);
					for (int a = 0; a < adviseIdList.size(); a++) {

						ManualAdvise advise = adviseMap.get(adviseIdList.get(a));
						if(advise != null){
							
							if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID())){
								BigDecimal insAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID());
								if(insAllocateBal.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
									if(balAdvise.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(insAllocateBal) >= 0 && insAllocateBal.compareTo(balAdvise) < 0){
											balAdvise = insAllocateBal;
										}else if(totalReceiptAmt.compareTo(insAllocateBal) < 0 && balAdvise.compareTo(totalReceiptAmt) > 0){
											balAdvise = totalReceiptAmt;
										}

										// Reset Total Receipt Amount
										totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
										paidAllocationMap.put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), insAllocateBal.subtract(balAdvise));
										
										// Save Movements for Manual Advise
										ManualAdviseMovements movement = new ManualAdviseMovements();
										movement.setAdviseID(advise.getAdviseID());
										movement.setMovementDate(DateUtility.getAppDate());
										movement.setMovementAmount(balAdvise);
										movement.setPaidAmount(balAdvise);
										movement.setWaivedAmount(BigDecimal.ZERO);
										receiptDetail.getAdvMovements().add(movement);
									}
								}
							}
						}
					}
				}
			}
			
			FinRepayHeader repayHeader = null;
			if(receiptDetail.getAmount().compareTo(totalReceiptAmt) > 0 && 
					receiptDetail.getAmount().compareTo(partialSettleAmount) > 0){
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(DateUtility.getAppDate());
				repayHeader.setRepayAmount(receiptDetail.getAmount().subtract(totalReceiptAmt).subtract(partialSettleAmount));
				if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
				}else{
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_SCHDRPY);
				}
				repayHeader.setPriAmount(totPriPaidNow);
				repayHeader.setPftAmount(totPftPaidNow);
				repayHeader.setLatePftAmount(totLPftPaidNow);
				repayHeader.setTotalPenalty(totPenaltyPaidNow);
				repayHeader.setTotalIns(totInsPaidNow);
				repayHeader.setTotalSchdFee(totFeePaidNow);
				repayHeader.setTotalWaiver(totalWaivedAmt);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(pastdueRpySchdList);
				repayHeaderList.add(repayHeader);
			}
			
			if(totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0 && 
					(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) || 
							StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE))){
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(DateUtility.getAppDate());
				repayHeader.setRepayAmount(totalReceiptAmt);
				repayHeader.setFinEvent(receiptData.getReceiptHeader().getExcessAdjustTo());
				repayHeader.setPriAmount(BigDecimal.ZERO);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);
				repayHeader.setTotalWaiver(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(null);
				repayHeaderList.add(repayHeader);
			}
			
			// Prepare Remaining Balance Amount as Partial Settlement , If selected Receipt Purpose is same
			if (partialSettleAmount.compareTo(BigDecimal.ZERO) > 0 && 
					StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {

				// Prepare Repay Header for Partial Settlement 
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(DateUtility.getAppDate());
				repayHeader.setRepayAmount(partialSettleAmount);
				repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYRPY);
				repayHeader.setPriAmount(partialSettleAmount);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(partialRpySchdList);
				repayHeaderList.add(repayHeader);
			}
			
			// Adding Repay Headers to Receipt Details
			receiptDetail.setRepayHeaders(repayHeaderList);
			
		}
		
		overdueList = null;
		overdueMap = null;
		paidAllocationMap = null;
		
		// If Total Waived amount is Zero, no need to Waived amount settings
		if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
			waivedAllocationMap = null;

			logger.debug("Leaving");
			return receiptData;
		}
		
		// Waived Details Updation on Schedules
		int size = receiptDetailList.size();
		for (int i = size - 1; i >= 0; i--) {
			FinReceiptDetail rd = receiptDetailList.get(i);
			
			int rhSize = rd.getRepayHeaders().size();
			for (int j = rhSize - 1; j >= 0; j--) {
				
				FinRepayHeader rh = rd.getRepayHeaders().get(j);
				int rsSize = rh.getRepayScheduleDetails().size();
				if(rsSize > 0){
					for (int k = rsSize - 1; k >= 0; k--) {

						RepayScheduleDetail rsd = rh.getRepayScheduleDetails().get(k);

						// Principal Amount
						if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PRI)){
							BigDecimal waivedNow = rsd.getPrincipalSchdPayNow();
							BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_PRI);
							if(waivedNow.compareTo(balWaived) > 0){
								waivedNow = balWaived;
							}
							rsd.setPriSchdWaivedNow(waivedNow);
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
							waivedAllocationMap.put(RepayConstants.ALLOCATION_PRI, balWaived.subtract(waivedNow));
						}

						// Profit Amount
						if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)){
							BigDecimal waivedNow = rsd.getProfitSchdPayNow();
							BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_PFT);
							if(waivedNow.compareTo(balWaived) > 0){
								waivedNow = balWaived;
							}
							rsd.setPftSchdWaivedNow(waivedNow);
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
							waivedAllocationMap.put(RepayConstants.ALLOCATION_PFT, balWaived.subtract(waivedNow));
						}

						// Late Payment Profit Amount
						if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_LPFT)){
							BigDecimal waivedNow = rsd.getLatePftSchdPayNow();
							BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_LPFT);
							if(waivedNow.compareTo(balWaived) > 0){
								waivedNow = balWaived;
							}
							rsd.setLatePftSchdWaivedNow(waivedNow);
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
							waivedAllocationMap.put(RepayConstants.ALLOCATION_LPFT, balWaived.subtract(waivedNow));
						}

						// Penalty Amount
						if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_ODC)){
							BigDecimal waivedNow = rsd.getPenaltyPayNow();
							BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_ODC);
							if(waivedNow.compareTo(balWaived) > 0){
								waivedNow = balWaived;
							}
							rsd.setWaivedAmt(waivedNow);
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
							waivedAllocationMap.put(RepayConstants.ALLOCATION_ODC, balWaived.subtract(waivedNow));
						}

						// Schedule Fee Amount
						if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_FEE)){
							BigDecimal waivedNow = rsd.getSchdFeePayNow();
							BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_FEE);
							if(waivedNow.compareTo(balWaived) > 0){
								waivedNow = balWaived;
							}
							rsd.setSchdFeeWaivedNow(waivedNow);
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
							waivedAllocationMap.put(RepayConstants.ALLOCATION_FEE, balWaived.subtract(waivedNow));
						}

						// Insurance Amount
						if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_INS)){
							BigDecimal waivedNow = rsd.getSchdInsPayNow();
							BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_INS);
							if(waivedNow.compareTo(balWaived) > 0){
								waivedNow = balWaived;
							}
							rsd.setSchdInsWaivedNow(waivedNow);
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
							waivedAllocationMap.put(RepayConstants.ALLOCATION_INS, balWaived.subtract(waivedNow));
						}
						
						// If No Outstanding Waiver amount
						if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
							break;
						}
					}
				}
				
				// If No Outstanding Waiver amount
				if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
					break;
				}
			}
			
			// Schedule Fee Amount
			if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_MANADV)){
				for (ManualAdviseMovements advMovement : rd.getAdvMovements()) {
					BigDecimal waivedNow = advMovement.getPaidAmount();
					BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_MANADV);
					if(waivedNow.compareTo(balWaived) > 0){
						waivedNow = balWaived;
					}
					advMovement.setWaivedAmount(waivedNow);
					totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
					waivedAllocationMap.put(RepayConstants.ALLOCATION_MANADV, balWaived.subtract(waivedNow));
				}
			}
			
			// If No Outstanding Waiver amount
			if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
				break;
			}
		}

		waivedAllocationMap = null;
		
		logger.debug("Leaving");
		return receiptData;
	}
	
	/**
	 * Method for Sorting Schedule Details
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}
	
	/**
	 * Method for Preparation of Repayment Schedule Details
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo, BigDecimal balPayNow){
		
		if(rsd == null){
			rsd = new RepayScheduleDetail();
			rsd.setFinReference(curSchd.getFinReference());
			rsd.setSchDate(curSchd.getSchDate());
			rsd.setDefSchdDate(curSchd.getSchDate());

			rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			
			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

			rsd.setSchdIns(curSchd.getInsSchd());
			rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
			rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));
			
			rsd.setSchdSuplRent(curSchd.getSuplRent());
			rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
			rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));
			
			rsd.setSchdIncrCost(curSchd.getIncrCost());
			rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
			rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));
			
			rsd.setSchdFee(curSchd.getFeeSchd());
			rsd.setSchdFeePaid(curSchd.getSchdFeePaid());
			rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));
			
			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), curBussniessDate));
			rsd.setDaysEarly(0);
		}
		
		// Principal Payment 
		if(rpyTo == RepayConstants.REPAY_PRINCIPAL){
			rsd.setPrincipalSchdPayNow(balPayNow);
		}
		
		// Profit Payment 
		if(rpyTo == RepayConstants.REPAY_PROFIT){
			rsd.setProfitSchdPayNow(balPayNow);
		}
		
		// Late Payment Profit Payment 
		if(rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT){
			rsd.setLatePftSchdPayNow(balPayNow);
		}
		
		// Fee Detail Payment 
		if(rpyTo == RepayConstants.REPAY_FEE){
			rsd.setSchdFeePayNow(balPayNow);
			
		}
		
		// Insurance Detail Payment 
		if(rpyTo == RepayConstants.REPAY_INS){
			rsd.setSchdInsPayNow(balPayNow);
		}
		
		// Penalty Charge Detail Payment 
		if(rpyTo == RepayConstants.REPAY_PENALTY){
			rsd.setPenaltyPayNow(balPayNow);
		}
		
		return rsd;
		
	}
	
	/**
	 * Method for Processing Schedule Data and Receipts to Prepare Allocation Details
	 * @param receiptData
	 * @param aFinScheduleData
	 */
	public Map<String, BigDecimal> recalAutoAllocation(FinScheduleData scheduleData, BigDecimal totalReceiptAmt, String receiptPurpose) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();

		// If no balance for repayment then return with out calculation
		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		
		// Fetch total Advise details
		Map<Long, ManualAdvise> adviseMap = new HashMap<Long, ManualAdvise>();
		List<ManualAdvise> advises = getManualAdviseDAO().getManualAdviseByRef(financeMain.getFinReference(), 
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
		if (advises != null && !advises.isEmpty()) {
			for (int i = 0; i < advises.size(); i++) {
				if (adviseMap.containsKey(advises.get(i).getAdviseID())) {
					adviseMap.remove(advises.get(i).getAdviseID());
				}
				adviseMap.put(advises.get(i).getAdviseID(), advises.get(i));
			}
		}
		
		// Fetch manual Advise Details details
		Map<Date, FinODDetails> overdueMap = new HashMap<Date, FinODDetails>();
		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		if (overdueList != null && !overdueList.isEmpty()) {
			for (int i = 0; i < overdueList.size(); i++) {
				if (overdueMap.containsKey(overdueList.get(i).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(i).getFinODSchdDate());
				}
				overdueMap.put(overdueList.get(i).getFinODSchdDate(), overdueList.get(i));
			}
		}
		
		String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();
		boolean seperatePenalties = false;
		if(repayHierarchy.contains("CS")){
			seperatePenalties = true;
		}
		char[] rpyOrder = repayHierarchy.replace("CS", "").toCharArray();
		Map<String, BigDecimal> allocatePaidMap = new HashMap<>();
		int lastRenderSeq = 0;
		
		// Load Pending Schedules until balance available for payment
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			// Skip if repayment date after Current Business date
			if (schdDate.compareTo(curBussniessDate) > 0 && 
					!StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				break;
			}

			for (int j = 0; j < rpyOrder.length; j++) {
				
				char repayTo = rpyOrder[j];
				if(repayTo == RepayConstants.REPAY_PRINCIPAL){
					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
					if(balPri.compareTo(BigDecimal.ZERO) > 0){
						if(totalReceiptAmt.compareTo(balPri) > 0){
							totalReceiptAmt = totalReceiptAmt.subtract(balPri);
						}else{
							balPri = totalReceiptAmt;
							totalReceiptAmt = BigDecimal.ZERO;
						}

						BigDecimal totPriPayNow = BigDecimal.ZERO;
						if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PRI)){
							totPriPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PRI);
							allocatePaidMap.remove(RepayConstants.ALLOCATION_PRI);
						}
						allocatePaidMap.put(RepayConstants.ALLOCATION_PRI, totPriPayNow.add(balPri));
					}
				}else if(repayTo == RepayConstants.REPAY_PROFIT){
					
					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if(pftPayTo == RepayConstants.REPAY_PROFIT){
							
							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if(balPft.compareTo(BigDecimal.ZERO) > 0){
								if(totalReceiptAmt.compareTo(balPft) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(balPft);
								}else{
									balPft = totalReceiptAmt;
									totalReceiptAmt = BigDecimal.ZERO;
								}

								BigDecimal totPftPayNow = BigDecimal.ZERO;
								if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PFT)){
									totPftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PFT);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_PFT);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_PFT, totPftPayNow.add(balPft));
							}
							
						}else if(pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT){
							
							FinODDetails overdue = overdueMap.get(schdDate);
							if(overdue != null){
								BigDecimal balLatePft = overdue.getLPIBal();
								if(balLatePft.compareTo(BigDecimal.ZERO) > 0){
									if(totalReceiptAmt.compareTo(balLatePft) > 0){
										totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
									}else{
										totalReceiptAmt = BigDecimal.ZERO;
									}

									BigDecimal totLatePftPayNow = BigDecimal.ZERO;
									if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_LPFT)){
										totLatePftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_LPFT);
										allocatePaidMap.remove(RepayConstants.ALLOCATION_LPFT);
									}
									allocatePaidMap.put(RepayConstants.ALLOCATION_LPFT, totLatePftPayNow.add(balLatePft));
								}
							}
						}
					}
					
				}else if(repayTo == RepayConstants.REPAY_PENALTY){
					if(!seperatePenalties){
						FinODDetails overdue = overdueMap.get(schdDate);
						if(overdue != null){
							BigDecimal balPenalty = overdue.getTotPenaltyBal();
							if(balPenalty.compareTo(BigDecimal.ZERO) > 0){
								if(totalReceiptAmt.compareTo(balPenalty) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
								}else{
									balPenalty = totalReceiptAmt;
									totalReceiptAmt = BigDecimal.ZERO;
								}

								BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
								if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_ODC)){
									totPenaltyPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_ODC);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_ODC);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_ODC, totPenaltyPayNow.add(balPenalty));
							}
						}
					}
					
				}else if(repayTo == RepayConstants.REPAY_OTHERS){
					
					// If Schedule has Unpaid Fee Amount
					if((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0){
						
						// Fee Detail Collection
						for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
							FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
							List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();
							
							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
								break;
							}
							
							if(feeSchdList == null || feeSchdList.isEmpty()){
								continue;
							}

							// If Schedule Fee terms are less than actual calculated Sequence
							if(feeSchdList.size() < lastRenderSeq){
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = lastRenderSeq; l < feeSchdList.size(); l++) {
								if(feeSchdList.get(l).getSchDate().compareTo(schdDate) == 0){

									BigDecimal balFee = feeSchdList.get(l).getSchAmount().subtract(feeSchdList.get(l).getPaidAmount());
									if(balFee.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(balFee) > 0){
											totalReceiptAmt = totalReceiptAmt.subtract(balFee);
										}else{
											balFee = totalReceiptAmt;
											totalReceiptAmt = BigDecimal.ZERO;
										}

										BigDecimal totFeePayNow = BigDecimal.ZERO;
										if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
											totFeePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
											allocatePaidMap.remove(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID(), totFeePayNow.add(balFee));
									}
									if(lastRenderSeq == 0){
										lastRenderSeq = l;
									}
									break;
								}
							}
						}
					}
					
					// If Schedule has Unpaid Fee Amount
					if((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0){

						// Insurance Details Collection
						for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
							FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
							List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();
							
							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
								break;
							}
							
							if(insSchdList == null || insSchdList.isEmpty()){
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = 0; l < insSchdList.size(); l++) {
								if(insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0){

									BigDecimal balIns = insSchdList.get(l).getAmount().subtract(insSchdList.get(l).getInsurancePaid());
									if(balIns.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(balIns) > 0){
											totalReceiptAmt = totalReceiptAmt.subtract(balIns);
										}else{
											balIns = totalReceiptAmt;
											totalReceiptAmt = BigDecimal.ZERO;
										}

										BigDecimal totInsPayNow = BigDecimal.ZERO;
										if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId())){
											totInsPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId());
											allocatePaidMap.remove(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId());
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId(), totInsPayNow.add(balIns));
									}
									break;
								}
							}
						}
					}
				}
				
				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// No more Receipt amount left for next schedules
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
			
			// Sequence Order Increment to reduce loops on Fee setting
			lastRenderSeq++;
		}

		//Set Penalty Payments for Prepared Past due Schedule Details
		if (seperatePenalties && totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
			
			if(!overdueMap.isEmpty()){
				List<Date> odDateList = new ArrayList<Date>(overdueMap.keySet());
				Collections.sort(odDateList);
				for (int i = 0; i < odDateList.size(); i++) {

					FinODDetails overdue = overdueMap.get(odDateList.get(i));
					if(overdue != null){
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if(balPenalty.compareTo(BigDecimal.ZERO) > 0){
							if(totalReceiptAmt.compareTo(balPenalty) > 0){
								totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
							}else{
								balPenalty = totalReceiptAmt;
								totalReceiptAmt = BigDecimal.ZERO;
							}

							BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
							if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_ODC)){
								totPenaltyPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_ODC);
								allocatePaidMap.remove(RepayConstants.ALLOCATION_ODC);
							}
							allocatePaidMap.put(RepayConstants.ALLOCATION_ODC, totPenaltyPayNow.add(balPenalty));
						}
					}
				}
			}
		}
		
		// Manual Advises
		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

			if(!adviseMap.isEmpty()){
				List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
				Collections.sort(adviseIdList);
				for (int i = 0; i < adviseIdList.size(); i++) {

					ManualAdvise advise = adviseMap.get(adviseIdList.get(i));
					if(advise != null){
						BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
						if(balAdvise.compareTo(BigDecimal.ZERO) > 0){
							if(totalReceiptAmt.compareTo(balAdvise) > 0){
								totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
							}else{
								balAdvise = totalReceiptAmt;
								totalReceiptAmt = BigDecimal.ZERO;
							}

							BigDecimal totAdvisePayNow = BigDecimal.ZERO;
							if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID())){
								totAdvisePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID());
								allocatePaidMap.remove(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID());
							}
							allocatePaidMap.put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), totAdvisePayNow.add(balAdvise));
						}
					}
				}
			}
		}
		
		logger.debug("Leaving");
		return allocatePaidMap;
	}

	/**
	 * Method for calculating Schedule Total and Unpaid amounts based on Schedule Details
	 */
	private FinReceiptData calSummaryDetail(FinReceiptData receiptData, FinScheduleData finScheduleData, String receiptPurpose) {
		logger.debug("Entering");

		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		Boolean isNextDueSet = false;
		boolean isSkipLastDateSet = false;

		RepayMain repayMain = receiptData.getRepayMain();
		List<FinanceScheduleDetail> scheduleDetails = finScheduleData.getFinanceScheduleDetails();
		
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);

		boolean setEarlyPayRecord = false;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;
		for (int i = 0; i < tempScheduleDetails.size(); i++) {
			curSchd = tempScheduleDetails.get(i);
			if(i != 0){
				prvSchd = tempScheduleDetails.get(i - 1);
			}
			Date schdDate = curSchd.getSchDate();

			// Finance amount and current finance amount
			repayMain.setFinAmount(repayMain.getFinAmount().add(curSchd.getDisbAmount())
					.add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd.getFeeChargeAmt()));

			repayMain.setCurFinAmount(repayMain.getFinAmount().subtract(curSchd.getSchdPriPaid())
					.subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDownPaymentAmount()));

			if (schdDate.compareTo(curBussniessDate) < 0) {
				repayMain.setCurFinAmount(repayMain.getCurFinAmount().subtract(curSchd.getCpzAmount()));
			}else{
				
				if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
					if (DateUtility.getDaysBetween(curBussniessDate, schdDate) == 0) {
						repayMain.setEarlyPayOnSchDate(curSchd.getSchDate());
						repayMain.setEarlyRepayNewSchd(null);
						setEarlyPayRecord = true;
					} else {
						if (!setEarlyPayRecord) {

							setEarlyPayRecord = true;
							if ("NONSCH".equals(SysParamUtil.getValueAsString("EARLYPAY_TERM_INS"))) {
								repayMain.setEarlyPayOnSchDate(curBussniessDate);
								repayMain.setEarlyPayNextSchDate(curSchd.getSchDate());
							} else {
								repayMain.setEarlyPayOnSchDate(curSchd.getSchDate());
							}

							FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail(repayMain.getFinReference());
							newSchdlEP.setDefSchdDate(repayMain.getEarlyPayOnSchDate());
							newSchdlEP.setSchDate(repayMain.getEarlyPayOnSchDate());
							newSchdlEP.setSchSeq(1);
							newSchdlEP.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
							newSchdlEP.setRepayOnSchDate(true);
							newSchdlEP.setPftOnSchDate(true);
							newSchdlEP.setSchdMethod(prvSchd.getSchdMethod());
							newSchdlEP.setBaseRate(prvSchd.getBaseRate());
							newSchdlEP.setSplRate(prvSchd.getSplRate());
							newSchdlEP.setMrgRate(prvSchd.getMrgRate());
							newSchdlEP.setActRate(prvSchd.getActRate());
							newSchdlEP.setCalculatedRate(prvSchd.getCalculatedRate());
							newSchdlEP.setPftDaysBasis(prvSchd.getPftDaysBasis());
							newSchdlEP.setEarlyPaidBal(prvSchd.getEarlyPaidBal());
							repayMain.setEarlyRepayNewSchd(newSchdlEP);
						}
					}
				}
			}

			// Profit scheduled and Paid
			repayMain.setProfit(repayMain.getProfit().add(curSchd.getProfitSchd()));
			pftPaid = pftPaid.add(curSchd.getSchdPftPaid());

			// Principal scheduled and Paid
			repayMain.setPrincipal(repayMain.getPrincipal().add(curSchd.getPrincipalSchd()));
			priPaid = priPaid.add(curSchd.getSchdPriPaid());

			// Capitalization and Capitalized till now
			repayMain.setTotalCapitalize(repayMain.getTotalCapitalize().add(curSchd.getCpzAmount()));

			//Total Fee Amount
			repayMain.setTotalFeeAmt(repayMain.getTotalFeeAmt().add(
					curSchd.getFeeSchd() == null ? BigDecimal.ZERO : curSchd.getFeeSchd()));

			// Overdue Principal and Profit
			if (DateUtility.compare(schdDate, curBussniessDate) < 0) {
				cpzTillNow = cpzTillNow.add(curSchd.getCpzAmount());
				repayMain.setOverduePrincipal(repayMain.getOverduePrincipal().add(
						curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())));

				repayMain.setOverdueProfit(repayMain.getOverdueProfit().add(
						curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())));

			}

			// Down Payment
			repayMain.setDownpayment(repayMain.getDownpayment().add(curSchd.getDownPaymentAmount()));

			// REPAY SCHEDULE RECORD
			if (curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				BigDecimal balance = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
				balance = balance.add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPftPaid());

				if (balance.compareTo(BigDecimal.ZERO) > 0) {
					isSkipLastDateSet = true;
				}

				// Set the last and next scheduled repayments to deferred dates (Agreed with customer)
				if (balance.compareTo(BigDecimal.ZERO) == 0 && !isSkipLastDateSet) {
					repayMain.setDateLastFullyPaid(curSchd.getSchDate());
				}

				if (balance.compareTo(BigDecimal.ZERO) > 0 && !isNextDueSet) {
					repayMain.setDateNextPaymentDue(curSchd.getSchDate());
					isNextDueSet = true;
				}
			}
		}
		
		repayMain.setProfitBalance(repayMain.getProfit().subtract(pftPaid));
		repayMain.setPrincipalBalance(repayMain.getPrincipal().subtract(priPaid));
		
		// Allocation Map and Description Details Preparation
		if(receiptData.getAllocationMap() == null){
			receiptData.setAllocationMap(new HashMap<String, BigDecimal>());
		}
		
		// Applicable only when Fees, Insurances & Manual Advises to maintain descriptions in screen level
		if(receiptData.getAllocationDescMap() == null){
			receiptData.setAllocationDescMap(new HashMap<String, String>());
		}

		// Principal Amount
		if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, repayMain.getPrincipalBalance());
		}else{
			if(repayMain.getOverduePrincipal().compareTo(BigDecimal.ZERO) > 0){
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, repayMain.getOverduePrincipal());
			}
		}
		
		// Profit Amount
		if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PFT, repayMain.getProfitBalance());
		}else{
			if(repayMain.getOverdueProfit().compareTo(BigDecimal.ZERO) > 0){
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PFT, repayMain.getOverdueProfit());
			}
		}
		
		// Fetch Late Pay Profit Details
		BigDecimal latePayPftBal = getFinODDetailsDAO().getTotalODPftBal(repayMain.getFinReference());
		if(latePayPftBal.compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_LPFT, latePayPftBal);
		}
		
		// Fetch Sum of Overdue Charges
		BigDecimal penaltyBal = getFinODDetailsDAO().getTotalPenaltyBal(repayMain.getFinReference());
		if(penaltyBal.compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_ODC, penaltyBal);
		}
		
		// Fee Details
		if(finScheduleData.getFinFeeDetailList() != null && 
				!finScheduleData.getFinFeeDetailList().isEmpty()){
			
			for (int i = 0; i < finScheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = finScheduleData.getFinFeeDetailList().get(i);

				if(StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT) ||
						StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) ||
						StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)){
					
					// Calculate Overdue Fee Schedule Amount
					List<FinFeeScheduleDetail> feeSchdList = feeDetail.getFinFeeScheduleDetailList();
					BigDecimal pastFeeAmount = BigDecimal.ZERO;
					for (int j = 0; j < feeSchdList.size(); j++) {
						
						FinFeeScheduleDetail feeSchd = feeSchdList.get(j);
						if (DateUtility.compare(feeSchd.getSchDate(), curBussniessDate) < 0 || 
								StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
							pastFeeAmount = pastFeeAmount.add(feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()));
						}else{
							break;
						}
					}
					
					// Adding Fee Details to Map
					if(pastFeeAmount.compareTo(BigDecimal.ZERO) > 0){
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_FEE+"_"+feeDetail.getFeeID(), pastFeeAmount);
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_FEE+"_"+feeDetail.getFeeID(), feeDetail.getFeeTypeDesc());
					}
				}
			}
		}
		
		// Insurance Details
		if(finScheduleData.getFinInsuranceList() != null && 
				!finScheduleData.getFinInsuranceList().isEmpty()){
			
			for (int i = 0; i < finScheduleData.getFinInsuranceList().size(); i++) {
				FinInsurances finInsurance = finScheduleData.getFinInsuranceList().get(i);

				if(StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)){
					
					// Calculate Overdue Fee Schedule Amount
					List<FinSchFrqInsurance> insSchdList = finInsurance.getFinSchFrqInsurances();
					BigDecimal pastInsAmount = BigDecimal.ZERO;
					for (int j = 0; j < insSchdList.size(); j++) {
						
						FinSchFrqInsurance insSchd = insSchdList.get(j);
						if (DateUtility.compare(insSchd.getInsSchDate(), curBussniessDate) < 0 ||
								StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
							pastInsAmount = pastInsAmount.add(insSchd.getAmount().subtract(insSchd.getInsurancePaid()));
						}else{
							break;
						}
					}
					
					// Adding Fee Details to Map
					if(pastInsAmount.compareTo(BigDecimal.ZERO) > 0){
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_INS+"_"+finInsurance.getInsId(), pastInsAmount);
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_INS+"_"+finInsurance.getInsId(), 
								finInsurance.getInsuranceTypeDesc()+"-"+finInsurance.getInsReference());
					}
				}
			}
		}
		
		// Manual Advises 
		List<ManualAdvise> adviseList = getManualAdviseDAO().getManualAdviseByRef(repayMain.getFinReference(), 
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
		if(adviseList != null && !adviseList.isEmpty()){
			for (ManualAdvise advise : adviseList) {
				BigDecimal adviseBal = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
				// Adding Advise Details to Map
				if(adviseBal.compareTo(BigDecimal.ZERO) > 0){
					receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), adviseBal);
					receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), advise.getFeeTypeDesc());
				}
			}
		}
		
		logger.debug("Leaving");
		return receiptData;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

}
