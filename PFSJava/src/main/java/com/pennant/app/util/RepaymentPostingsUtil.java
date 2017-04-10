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
 * FileName    		:  RepaymentPostingsUtil.java													*                           
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
import java.lang.reflect.InvocationTargetException;
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

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.exception.PFFInterfaceException;

public class RepaymentPostingsUtil implements Serializable {
	private static final long			serialVersionUID	= 4165353615228874397L;
	private static Logger				logger				= Logger.getLogger(RepaymentPostingsUtil.class);

	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private FinanceMainDAO				financeMainDAO;
	private FinRepayQueueDAO			finRepayQueueDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;
	private CustomerStatusCodeDAO		customerStatusCodeDAO;
	private FinStatusDetailDAO			finStatusDetailDAO;
	private OverDueRecoveryPostingsUtil	recoveryPostingsUtil;
	private SuspensePostingUtil			suspensePostingUtil;
	private PostingsPreparationUtil		postingsPreparationUtil;
	private FinanceProfitDetailDAO		profitDetailsDAO;
	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private CustomerDAO					customerDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;
	private FinODDetailsDAO				finODDetailsDAO;

	public RepaymentPostingsUtil() {
		super();
	}

	/**
	 * Method for Posting Repayments and Update Repayments related Tables in Manual Payment Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	public List<Object> postingsScreenRepayProcess(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
			List<FinRepayQueue> finRepayQueueList, Map<String, BigDecimal> totalsMap, boolean isRIAFinance,
			String eventCode, Map<String, FeeRule> feeRuleDetailMap, String finDivision) throws PFFInterfaceException,
			IllegalAccessException, InvocationTargetException {

		return screenRepayProcess(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList, totalsMap,
				isRIAFinance, eventCode, feeRuleDetailMap, finDivision);
	}

	/**
	 * Method for Process Updations After Repayments Process will Success
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param isPartialRepay
	 * @param isRIAFinance
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	public List<Object> UpdateScreenPaymentsProcess(FinanceMain financeMain,
			List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
			List<FinRepayQueue> finRepayQueueList, long linkedTranId, boolean isPartialRepay, boolean isRIAFinance,
			AEAmountCodes aeAmountCodes) throws PFFInterfaceException, IllegalAccessException,
			InvocationTargetException {

		return screenPaymentsUpdation(financeMain, scheduleDetails, financeProfitDetail, finRepayQueueList,
				linkedTranId, isPartialRepay, isRIAFinance, aeAmountCodes);
	}

	/**
	 * Method for Processing Updating Schedule Details
	 * 
	 * @param finRepayQueue
	 * @return
	 */
	public FinanceScheduleDetail updateSchdlDetail(FinRepayQueue finRepayQueue) {
		return scheduleUpdation(finRepayQueue);
	}

	

	/**
	 * Method for Posting Repayments and Update Repayments related Tables in Manual Payment Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	private List<Object> screenRepayProcess(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList,
			Map<String, BigDecimal> totalsMap, boolean isRIAFinance, String eventCode,
			Map<String, FeeRule> feeRuleDetailMap, String finDivison) throws PFFInterfaceException,
			IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		List<Object> actReturnList = null;

		Date dateValueDate = DateUtility.getValueDate();
		Date valueDate = dateValueDate;

		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP))) {
			actReturnList = doOverduePostings(Long.MIN_VALUE, finRepayQueueList, dateValueDate, financeMain,
					isRIAFinance, finDivison);
			if (actReturnList != null) {
				return actReturnList;
			}
		}

		// Schedule Principal and Profit payments
		BigDecimal totRpyAmt = totalsMap.get("totRpyTot");
		if (totRpyAmt.compareTo(BigDecimal.ZERO) > 0) {
			actReturnList = doProfitPrincipalPostings(totalsMap, valueDate, dateValueDate, financeMain,
					scheduleDetails, financeProfitDetail, isRIAFinance, eventCode, feeRuleDetailMap);
		} else {
			if (actReturnList == null) {
				actReturnList = new ArrayList<Object>();
			}
			actReturnList.clear();
			actReturnList.add(true);// Postings Success
			actReturnList.add(Long.MIN_VALUE);// Linked Transaction ID
			actReturnList.add(false); // Partial Repay
			actReturnList.add(null);// AE Amounts Object
			actReturnList.add(null);// Finance Account
		}

		if ((Boolean) actReturnList.get(0)) {
			if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC))
					|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))
					|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS))
					|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {
				List<Object> returnList = doOverduePostings(Long.MIN_VALUE, finRepayQueueList, dateValueDate,
						financeMain, isRIAFinance, finDivison);
				if (returnList != null) {
					return returnList;
				}
			}
		}

		// Movements Accounting Process execution
		// (PD to Normal) or (Suspense to PD) or (Suspense to Normal)

		// Previous Details
		String execEventCode = null;
		boolean proceedFurther = true;
		if ((Boolean) actReturnList.get(0) && financeProfitDetail.getoDDays() > 0) {

			if (financeProfitDetail.isPftInSusp()) {

				// Check Manual Suspense
				FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(
						financeMain.getFinReference(), "");
				if (suspHead.isManualSusp()) {
					execEventCode = null;
					proceedFurther = false;
				}

				// Fetch Current Details
				if (proceedFurther) {

					// Get Current Over Due Details Days Count after Payment Process
					int curMaxODDays = getFinODDetailsDAO().getFinODDays(financeMain.getFinReference(), "");

					// Status of Suspense from CustStatusCodes based on OD Days when OD Days > 0 
					boolean curFinIsSusp = false;
					if (curMaxODDays > 0) {
						curFinIsSusp = getCustomerStatusCodeDAO().getFinanceSuspendStatus(curMaxODDays);
					}

					// If Finance Still in Suspense case, no need to do Any Accounting further.
					if (!curFinIsSusp) {
						if (curMaxODDays > 0) {
							execEventCode = AccountEventConstants.ACCEVENT_PIS_PD;
						} else {
							execEventCode = AccountEventConstants.ACCEVENT_PIS_NORM;
						}
					}
				}
			} else {

				// Get Current Over Due Details Days Count after Payment Process
				int curMaxODDays = getFinODDetailsDAO().getFinODDays(financeMain.getFinReference(), "");

				if (curMaxODDays == 0) {
					execEventCode = AccountEventConstants.ACCEVENT_PD_NORM;
				}
			}

			// Do Accounting based on Accounting Event selected from above process check
			if (StringUtils.isNotEmpty(execEventCode)) {

				AEAmountCodes amountCodes = (AEAmountCodes) actReturnList.get(4);
				long linkedtranId = (long) actReturnList.get(1);

				// Set O/S balances for Principal & profits in Amount Codes Data--TODO

				DataSet dataSet = AEAmounts.createDataSet(financeMain, execEventCode, valueDate, valueDate);
				dataSet.setNewRecord(false);

				// Reset AEAmount Code Details Bean and send for Accounting Execution.
				getPostingsPreparationUtil().processPostingDetailsWithFee(dataSet, amountCodes, false, isRIAFinance,
						"Y", valueDate, true, linkedtranId, feeRuleDetailMap);

			}
		}

		logger.debug("Leaving");
		return actReturnList;
	}

	private List<Object> doOverduePostings(long linkedTranId, List<FinRepayQueue> finRepayQueueList,
			Date dateValueDate, FinanceMain financeMain, boolean isRIAFinance, String finDivison)
			throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		for (FinRepayQueue repayQueue : finRepayQueueList) {
			if (repayQueue.getRpyDate().compareTo(dateValueDate) < 0
					&& (repayQueue.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0 || repayQueue.getWaivedAmount()
							.compareTo(BigDecimal.ZERO) > 0)) {

				//Check Repayment Amount is Fully Paid or not
				boolean fullyPaidSchd = false;
				if ((repayQueue.getSchdPftBal().add(repayQueue.getSchdPriBal())).compareTo(BigDecimal.ZERO) == 0) {
					fullyPaidSchd = true;
				}

				List<Object> returnList = getRecoveryPostingsUtil().recoveryPayment(financeMain, dateValueDate,
						repayQueue.getRpyDate(), repayQueue.getFinRpyFor(), dateValueDate,
						repayQueue.getPenaltyPayNow(), BigDecimal.ZERO, repayQueue.getWaivedAmount(),
						repayQueue.getChargeType(), isRIAFinance, linkedTranId, finDivison, fullyPaidSchd);

				if (!(Boolean) returnList.get(0)) {
					List<Object> actReturnList = new ArrayList<Object>();
					actReturnList.add(returnList.get(0));
					actReturnList.add(returnList.get(2));
					returnList = null;
					return actReturnList;
				}
			} else {
				//Only in case of Profit & Principal Amount greater than ZERO , Penalty Pay Now is ZERO
				//Update RcdCanDel = 0 flag on Overdue Recovery details for IPC or PIC
				if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC) || ImplementationConstants.REPAY_HIERARCHY_METHOD
						.equals(RepayConstants.REPAY_HIERARCHY_FPIC))
						|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS))
						|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {
					if (repayQueue.getRpyDate().compareTo(dateValueDate) < 0
							&& (repayQueue.getSchdPftPayNow().add(repayQueue.getSchdPriPayNow()))
									.compareTo(BigDecimal.ZERO) > 0
							&& repayQueue.getPenaltyPayNow().compareTo(BigDecimal.ZERO) == 0) {
						getRecoveryDAO().updateRcdCanDel(repayQueue.getFinReference(), repayQueue.getRpyDate());
					}
				}
			}
		}
		logger.debug("Leaving");
		return null;
	}

	private List<Object> doProfitPrincipalPostings(Map<String, BigDecimal> totalsMap, Date valueDate,
			Date dateValueDate, FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, boolean isRIAFinance, String eventCode,
			Map<String, FeeRule> feeRuleDetailMap) throws PFFInterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");
		List<Object> actReturnList = new ArrayList<Object>();
		boolean isPartialRepay = false;
		//Partial Repay Check
		if (totalsMap.get("totRpyPft").compareTo(BigDecimal.ZERO) > 0) {
			isPartialRepay = true;
		}

		//Remove Below line for Single Transaction Posting Entry
		long linkedTranId = Long.MIN_VALUE;

		//Method for Postings Process
		List<Object> resultList = postingEntryProcess(valueDate, dateValueDate, valueDate, false, financeMain,
				scheduleDetails, financeProfitDetail, totalsMap, isRIAFinance, linkedTranId, eventCode,
				feeRuleDetailMap);

		boolean isPostingSuccess = (Boolean) resultList.get(0);
		linkedTranId = (Long) resultList.get(1);
		
		// Temporary Fix , Once Accounting Configuration done, should be removed FIXME
		if(linkedTranId == Long.MIN_VALUE || linkedTranId == 0){
			linkedTranId = DateUtility.getSysDate().getTime();
		}

		if (!isPostingSuccess) {
			actReturnList.add(resultList.get(0));
			actReturnList.add(resultList.get(3));

			logger.debug("Leaving");
			resultList = null;
			return actReturnList;
		}

		actReturnList.add(isPostingSuccess);
		actReturnList.add(linkedTranId);
		actReturnList.add(isPartialRepay);
		actReturnList.add(resultList.get(5)); // Amount Codes
		actReturnList.add(resultList.get(4)); // Finance Account if Exists

		logger.debug("Leaving");
		return actReturnList;
	}

	/**
	 * Method for Process Updations After Repayments Process will Success
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param finRepayQueueList
	 * @param linkedTranId
	 * @param isPartialRepay
	 * @param isRIAFinance
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	private List<Object> screenPaymentsUpdation(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, List<FinRepayQueue> finRepayQueueList, long linkedTranId,
			boolean isPartialRepay, boolean isRIAFinance, AEAmountCodes aeAmountCodes) throws PFFInterfaceException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> actReturnList = new ArrayList<Object>();
		Date dateValueDate = DateUtility.getValueDate();

		Map<String, FinanceScheduleDetail> scheduleMap = new HashMap<String, FinanceScheduleDetail>();
		for (FinanceScheduleDetail detail : scheduleDetails) {
			scheduleMap.put(detail.getSchDate().toString(), detail);
		}

		// AE Amounts Object Check
		BigDecimal rpyTotal = BigDecimal.ZERO;
		BigDecimal rpyPri = BigDecimal.ZERO;
		BigDecimal rpyPft = BigDecimal.ZERO;
		if (aeAmountCodes != null) {
			rpyTotal = aeAmountCodes.getRpTot();
			rpyPri = aeAmountCodes.getRpPri();
			rpyPft = aeAmountCodes.getRpPft();
		}

		//Database Updations for Finance RepayQueue Details List
		boolean isPenaltyAvail = false;
		for (FinRepayQueue repayQueue : finRepayQueueList) {

			if (rpyTotal.compareTo(BigDecimal.ZERO) > 0) {

				FinanceScheduleDetail scheduleDetail = null;
				if (scheduleMap.containsKey(DateUtility.formatDate(repayQueue.getRpyDate(),
						PennantConstants.DBDateFormat))) {
					scheduleDetail = scheduleMap.get(DateUtility.formatDate(repayQueue.getRpyDate(),
							PennantConstants.DBDateFormat));
				}

				List<Object> resultList = paymentProcessExecution(financeMain, scheduleDetail, repayQueue,
						dateValueDate, linkedTranId, false, isPartialRepay, financeProfitDetail, isRIAFinance, rpyTotal);

				if (!(Boolean) resultList.get(0)) {
					actReturnList.add(resultList.get(0));
					actReturnList.add(resultList.get(2));

					logger.debug("Leaving");
					return actReturnList;
				}

				scheduleMap.remove(scheduleDetail.getSchDate().toString());
				scheduleMap.put(scheduleDetail.getSchDate().toString(), (FinanceScheduleDetail) resultList.get(3));
			}

			if (!isPenaltyAvail && (repayQueue.getPenaltyBal().compareTo(BigDecimal.ZERO) > 0)) {
				isPenaltyAvail = true;
			}
		}

		String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
		boolean isStsChanged = false;
		if (!StringUtils.equals(financeMain.getFinStatus(), curFinStatus)) {
			isStsChanged = true;
		}

		//Finance Status Details insertion, if status modified then change to High Risk Level
		if (isStsChanged) {
			FinStatusDetail statusDetail = new FinStatusDetail();
			statusDetail.setFinReference(financeMain.getFinReference());
			statusDetail.setValueDate(dateValueDate);
			statusDetail.setCustId(financeMain.getCustID());
			statusDetail.setFinStatus(curFinStatus);

			getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
		}

		//Reset Finance Schedule Details
		scheduleDetails = new ArrayList<FinanceScheduleDetail>(scheduleMap.values());
		scheduleDetails = sortSchdDetails(scheduleDetails);

		// Finance Main Details Update
		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(rpyPri));
		financeMain.setFinStatus(curFinStatus);
		financeMain.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);
		BigDecimal totalFinAmt = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
		
		if(ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)) {
			if(totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0){
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)) {
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {
				financeMain.setFinIsActive(false);
				financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC)) {

			//Check Penalty Amount & Repayment's Principal Amount
			if (!isPenaltyAvail
					&& totalFinAmt.subtract(financeMain.getFinRepaymentAmount()).compareTo(BigDecimal.ZERO) <= 0) {

				//Check Total Finance profit Amount
				boolean pftFullyPaid = true;
				for (int i = 1; i < scheduleDetails.size(); i++) {
					FinanceScheduleDetail curSchd = scheduleDetails.get(i);
					if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
						pftFullyPaid = false;
						break;
					}
				}

				if (pftFullyPaid) {
					financeMain.setFinIsActive(false);
					financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_MATURED);
				}
			}
		}

		//Finance Profit Details Updation
		financeProfitDetail = AEAmounts.calProfitDetails(financeMain, scheduleDetails, financeProfitDetail,
				dateValueDate);
		financeProfitDetail.setFinStatus(financeMain.getFinStatus());
		financeProfitDetail.setFinStsReason(financeMain.getFinStsReason());
		financeProfitDetail.setFinIsActive(financeMain.isFinIsActive());
		financeProfitDetail.setClosingStatus(financeMain.getClosingStatus());
		financeProfitDetail.setLatestRpyDate(dateValueDate);
		financeProfitDetail.setLatestRpyPri(rpyPri);
		financeProfitDetail.setLatestRpyPft(rpyPft);

		String curFinWorstStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), false);
		financeProfitDetail.setFinWorstStatus(curFinWorstStatus);
		getProfitDetailsDAO().update(financeProfitDetail, true);

		//Customer Status & Status Change Date(Suspense From Date) Updation
		String custSts = getCustomerDAO().getCustWorstSts(financeMain.getCustID());
		List<Long> custIdList = new ArrayList<Long>(1);
		custIdList.add(financeMain.getCustID());
		List<FinStatusDetail> suspDateSts = getFinanceSuspHeadDAO().getCustSuspDate(custIdList);

		Date suspFromdate = null;
		if (suspDateSts != null && !suspDateSts.isEmpty()) {
			suspFromdate = suspDateSts.get(0).getValueDate();
		}

		FinStatusDetail statusDetail = new FinStatusDetail();
		List<FinStatusDetail> custStatuses = new ArrayList<FinStatusDetail>(1);
		statusDetail.setCustId(financeMain.getCustID());
		statusDetail.setFinStatus(custSts);
		statusDetail.setValueDate(suspFromdate);
		custStatuses.add(statusDetail);

		getFinStatusDetailDAO().updateCustStatuses(custStatuses);

		statusDetail = null;
		custStatuses = null;
		suspDateSts = null;
		custIdList = null;

		actReturnList.add(true);
		actReturnList.add(linkedTranId);
		actReturnList.add(null);
		actReturnList.add(financeMain);
		actReturnList.add(scheduleDetails);

		logger.debug("Leaving");
		return actReturnList;
	}

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
	 * Method for Posting Process execution in Single Entry Event for Total Repayment Amount
	 * 
	 * @param valueDate
	 * @param dateValueDate
	 * @param dateSchdDate
	 * @param isEODProcess
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param rpyTotal
	 * @param rpyPri
	 * @param rpyPft
	 * @param rpyRefund
	 * @param isRIAFinance
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<Object> postingEntryProcess(Date valueDate, Date dateValueDate, Date dateSchdDate,
			boolean isEODProcess, FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail financeProfitDetail, Map<String, BigDecimal> repayDetailMap, boolean isRIAFinance,
			long linkedTranId, String eventCode, Map<String, FeeRule> feeRuleDetailMap) throws PFFInterfaceException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// DataSet Creation
		DataSet dataSet = AEAmounts.createDataSet(financeMain, eventCode, dateValueDate, dateSchdDate);
		dataSet.setNewRecord(false);

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which
		// Repayments are processing
		AEAmountCodes amountCodes = AEAmounts.procAEAmounts(financeMain, scheduleDetails, financeProfitDetail,
				valueDate);

		//Set Repay Amount Codes
		amountCodes.setRpTot(repayDetailMap.get("totRpyTot"));
		amountCodes.setRpPft(repayDetailMap.get("totRpyPft"));
		amountCodes.setRpPri(repayDetailMap.get("totRpyPri"));
		amountCodes.setRefund(repayDetailMap.get("totRefund"));
		amountCodes.setInsRefund(repayDetailMap.get("INSREFUND"));

		// Fee Details
		amountCodes.setInsPay(repayDetailMap.get("insPay"));
		amountCodes.setSuplRentPay(repayDetailMap.get("suplRentPay"));
		amountCodes.setIncrCostPay(repayDetailMap.get("incrCostPay"));
		amountCodes.setSchFeePay(repayDetailMap.get("schFeePay"));

		// Setting Accounting Finance Status for Selection of Account
		if (!isEODProcess) {

			boolean isRpyAdv = false;
			boolean isRpyPD = false;
			boolean isRpySusp = false;

			// Check Details from the Finance Profit Details parameters
			if (financeProfitDetail.isPftInSusp()) {
				isRpySusp = true;
			} else {
				if (financeProfitDetail.getoDDays() > 0) {
					isRpyPD = true;
				} else {
					// Check with in Grace Period
					if (financeMain.isAllowGrcPeriod() && valueDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
						if (valueDate.compareTo(financeMain.getNextGrcPftDate()) < 0) {
							isRpyAdv = true;
						}
					} else {
						// Check with in Repay Period
						if (valueDate.compareTo(financeMain.getNextRepayDate()) < 0
								|| valueDate.compareTo(financeMain.getNextRepayPftDate()) < 0) {
							isRpyAdv = true;
						}
					}
				}
			}

			amountCodes.setRepayInAdv(isRpyAdv);
			amountCodes.setRepayInPD(isRpyPD);
			amountCodes.setRepayInSusp(isRpySusp);
		}

		Date dateAppDate = DateUtility.getAppDate();
		List<Object> resultList = getPostingsPreparationUtil().processPostingDetailsWithFee(dataSet, amountCodes,
				isEODProcess, isRIAFinance, "Y", dateAppDate, true, linkedTranId, feeRuleDetailMap);

		resultList.add(amountCodes);

		logger.debug("Leaving");
		return resultList;
	}

	/**
	 * Database Updations related Repayments Schedule Details
	 * 
	 * @param isPostingSuccess
	 * @param financeMain
	 * @param finRepayQueue
	 * @param dateValueDate
	 * @param linkedTranId
	 * @param isEODProcess
	 * @param isPartialRepay
	 * @param financeProfitDetail
	 * @param isRIAFinance
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	public List<Object> paymentProcessExecution(FinanceMain financeMain, FinanceScheduleDetail scheduleDetail,
			FinRepayQueue finRepayQueue, Date dateValueDate, long linkedTranId, boolean isEODProcess,
			boolean isPartialRepay, FinanceProfitDetail financeProfitDetail, boolean isRIAFinance,
			BigDecimal totalRpyAmt) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");

		boolean isDueSuspNow = false;
		boolean suspPostingsSuccess = true;
		String errorCode = null;

		//Schedule Updation depends on Finance Repay Queue Details
		if (scheduleDetail == null) {
			scheduleDetail = updateSchdlDetail(finRepayQueue);
		} else {
			scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue);
		}

		// Finance Repayments Details
		FinanceRepayments repayment = prepareRepayDetailsData(finRepayQueue, dateValueDate, linkedTranId, totalRpyAmt);
		getFinanceRepaymentsDAO().save(repayment, "");

		// Finance Repay Queue Data Updation
		finRepayQueue = prepareQueueData(finRepayQueue);

		//Check for Schedule is Completely paid or not
		boolean isCompletlyPaid = false;
		if (scheduleDetail.isSchPftPaid() && scheduleDetail.isSchPriPaid()) {
			isCompletlyPaid = true;
			isPartialRepay = false;
		}

		boolean isLatePay = false;
		if (isEODProcess) {
			if ((finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0)
					|| (finRepayQueue.getRpyDate().compareTo(dateValueDate) == 0 && !isCompletlyPaid)) {
				isLatePay = true;
			}
		} else {
			if (finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0) {
				isLatePay = true;
			}
		}

		if (isLatePay) {

			//Overdue Details preparation
			getRecoveryPostingsUtil().recoveryCalculation(finRepayQueue, financeMain.getProfitDaysBasis(),
					dateValueDate, isEODProcess, false);

			//SUSPENSE
			if (isEODProcess) {

				//Suspense Details Preparation
				List<Object> returnList = getSuspensePostingUtil().suspensePreparation(financeMain, finRepayQueue,
						dateValueDate, isRIAFinance, false);
				suspPostingsSuccess = (Boolean) returnList.get(0);
				isDueSuspNow = (Boolean) returnList.get(1);
				errorCode = (String) returnList.get(2);
			}

			//SUSPENSE RELEASE
			if (!isDueSuspNow && (isCompletlyPaid || isPartialRepay)) {
				getSuspensePostingUtil().suspReleasePreparation(financeMain, finRepayQueue.getSchdPftPayNow(),
						finRepayQueue, dateValueDate, isEODProcess, isRIAFinance);
			}
		}

		List<Object> returnList = new ArrayList<Object>(4);
		returnList.add(suspPostingsSuccess);
		returnList.add(isDueSuspNow);
		returnList.add(errorCode);
		returnList.add(scheduleDetail);

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for updating Schedule Details
	 * 
	 * @param finRepayQueue
	 * @return
	 */
	private FinanceScheduleDetail scheduleUpdation(FinRepayQueue finRepayQueue) {
		logger.debug("Entering");

		// Finance Schedule Details Update
		FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(
				finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(), "", false);

		scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue);
		getFinanceScheduleDetailDAO().updateForRpy(scheduleDetail, finRepayQueue.getFinRpyFor());

		logger.debug("Leaving");
		return scheduleDetail;
	}

	/**
	 * Method for Upadte Data for Finance schedule Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	private FinanceScheduleDetail updateScheduleDetailsData(FinanceScheduleDetail schedule, FinRepayQueue finRepayQueue) {
		logger.debug("Entering");

		schedule.setFinReference(finRepayQueue.getFinReference());
		schedule.setSchDate(finRepayQueue.getRpyDate());

		// Fee Details paid Amounts updation
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().add(finRepayQueue.getSchdFeePayNow()));
		schedule.setSchdInsPaid(schedule.getSchdInsPaid().add(finRepayQueue.getSchdInsPayNow()));
		schedule.setSuplRentPaid(schedule.getSuplRentPaid().add(finRepayQueue.getSchdSuplRentPayNow()));
		schedule.setIncrCostPaid(schedule.getIncrCostPaid().add(finRepayQueue.getSchdIncrCostPayNow()));

		schedule.setSchdPftPaid(schedule.getSchdPftPaid().add(finRepayQueue.getSchdPftPayNow()));
		schedule.setSchdPriPaid(schedule.getSchdPriPaid().add(finRepayQueue.getSchdPriPayNow()));

		// Finance Schedule Profit Balance Check
		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)) {
			if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setSchPftPaid(true);

				// Finance Schedule Principal Balance Check
				if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
					schedule.setSchPriPaid(true);
				} else {
					schedule.setSchPriPaid(false);
				}
			} else {
				schedule.setSchPftPaid(false);
			}
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS)) {
			if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setSchPriPaid(true);

				// Finance Schedule Principal Balance Check
				if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
					schedule.setSchPftPaid(true);
				} else {
					schedule.setSchPftPaid(false);
				}
			} else {
				schedule.setSchPriPaid(false);
			}
		}

		logger.debug("Leaving");
		return schedule;
	}

	/**
	 * Method for Preparing Data for Finance Repay Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	public FinanceRepayments prepareRepayDetailsData(FinRepayQueue queue, Date valueDate, long linkedTranId,
			BigDecimal totalRpyAmt) {
		logger.debug("Entering");

		FinanceRepayments repayment = new FinanceRepayments();
		Date curAppDate = DateUtility.getAppDate();

		repayment.setFinReference(queue.getFinReference());
		repayment.setFinSchdDate(queue.getRpyDate());
		repayment.setFinRpyFor(queue.getFinRpyFor());
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(totalRpyAmt);
		repayment.setFinPostDate(curAppDate);
		repayment.setFinValueDate(valueDate);
		repayment.setFinBranch(queue.getBranch());
		repayment.setFinType(queue.getFinType());
		repayment.setFinCustID(queue.getCustomerID());
		repayment.setFinSchdPftPaid(queue.getSchdPftPayNow());
		repayment.setFinSchdPriPaid(queue.getSchdPriPayNow());
		repayment.setFinTotSchdPaid(queue.getSchdPftPayNow().add(queue.getSchdPriPayNow()));
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(queue.getWaivedAmount());
		repayment.setFinRefund(queue.getRefundAmount());

		//Fee Details
		repayment.setSchdFeePaid(queue.getSchdFeePayNow());
		repayment.setSchdInsPaid(queue.getSchdInsPayNow());
		repayment.setSchdSuplRentPaid(queue.getSchdSuplRentPayNow());
		repayment.setSchdIncrCostPaid(queue.getSchdIncrCostPayNow());

		logger.debug("Leaving");
		return repayment;
	}

	/**
	 * Method for Updating the Finance RepayQueue Data
	 * 
	 * @param repayQueue
	 * @param repayAmtBal
	 * @return
	 */
	private FinRepayQueue prepareQueueData(FinRepayQueue repayQueue) {
		logger.debug("Entering");
		repayQueue.setSchdPftPaid(repayQueue.getSchdPftPaid().add(repayQueue.getSchdPftPayNow()));
		repayQueue.setSchdPriPaid(repayQueue.getSchdPriPaid().add(repayQueue.getSchdPriPayNow()));
		repayQueue.setSchdPftBal(repayQueue.getSchdPftBal().subtract(repayQueue.getSchdPftPayNow()));
		repayQueue.setSchdPriBal(repayQueue.getSchdPriBal().subtract(repayQueue.getSchdPriPayNow()));

		// Modified Conditions for Balances Paid or not
		if (repayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) == 0) {

			repayQueue.setSchdIsPftPaid(true);
			if (repayQueue.getSchdPriBal().compareTo(BigDecimal.ZERO) == 0) {
				repayQueue.setSchdIsPriPaid(true);
			}
		}

		logger.debug("Leaving");
		return repayQueue;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

}
