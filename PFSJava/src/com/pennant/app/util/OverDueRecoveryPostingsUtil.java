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
 * FileName    		:  OverDueRecoveryPostingsUtil.java													*                           
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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class OverDueRecoveryPostingsUtil implements Serializable {

    private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = Logger.getLogger(OverDueRecoveryPostingsUtil.class);

	private FinanceMainDAO financeMainDAO;
	private AccountInterfaceService accountInterfaceService;
	private PostingsPreparationUtil postingsPreparationUtil;

	private AEAmountCodes amountCodes = null;
	private BigDecimal zeroValue = new BigDecimal(0);

	/**
	 * Default constructor
	 */
	public OverDueRecoveryPostingsUtil() {
		super();
	}

	/**
	 * Method for Posting OverDue Recoveries .
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public List<Object> oDRPostingProcess(OverdueChargeRecovery recovery, Date dateValueDate, 
			boolean isRIAFinance) throws AccountNotFoundException,
	        IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");

		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(
		        "PHASE").toString());
		boolean isEODProcess = true;
		if (!phase.equals("EOD")) {
			isEODProcess = false;
		}

		BigDecimal pendingPenalty = recovery.getFinODCPenalty().subtract(recovery.getFinODCPaid());
		BigDecimal pendingWaiver = recovery.getFinODCWaived().subtract(
		        recovery.getFinODCWaiverPaid());
		
		//Get Finance Details From DB
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForDataSet(recovery.getFinReference());
		boolean isPayNow = false;

		IAccounts iAccount = new IAccounts();
		iAccount.setAccountId(financeMain.getRepayAccountId());

		// Check Available Funding Account Balance
		iAccount = getAccountInterfaceService().fetchAccountAvailableBal(iAccount, !isEODProcess);

		BigDecimal penaltyPaid = zeroValue;
		BigDecimal waiverPaid = zeroValue;

		// Set Requested Repayment Amount as RepayAmount Balance
		if (iAccount.getAcAvailableBal().compareTo(pendingPenalty) >= 0) {
			penaltyPaid = pendingPenalty;
			waiverPaid = pendingWaiver;
			isPayNow = true;
		} else {
			if (recovery.isFinODCSweep() && iAccount.getAcAvailableBal().intValue() > 0) {
				penaltyPaid = iAccount.getAcAvailableBal();
				isPayNow = true;
			}
		}

		boolean isPostingSuccess = false;
		if (isPayNow) {

			AEAmounts aeAmounts = new AEAmounts();

			// DataSet Creation
			DataSet dataSet = aeAmounts.createDataSet(financeMain, "LATEPAY", dateValueDate,recovery.getFinSchdDate());
			dataSet.setNewRecord(false);

			// AmountCodes Preparation
			// EOD Repayments should pass the value date as schedule for which
			// repayment is processing
			amountCodes = new AEAmountCodes();
			amountCodes.setFinReference(financeMain.getFinReference());
			amountCodes.setODCPLShare(recovery.getFinODCPLShare());
			amountCodes.setPENALTY(penaltyPaid);
			amountCodes.setWAIVER(waiverPaid);

			// Accounting Set Execution to get Posting Details List
			Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());
			isPostingSuccess = (Boolean) getPostingsPreparationUtil().processPostingDetails(dataSet,amountCodes, true, 
					isRIAFinance, "Y", dateAppDate, null,false).get(0);
		}

		List<Object> returnList = new ArrayList<Object>(3);
		BigDecimal penaltyPosted = new BigDecimal(-1);
		BigDecimal waiverPosted = new BigDecimal(0);
		
		if (isPostingSuccess) {
			penaltyPosted = penaltyPaid;
			waiverPosted = waiverPaid;
		}
		returnList.add(penaltyPosted);
		returnList.add(waiverPosted);
		
		logger.debug("Leaving");
		return returnList;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}
	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
    	return postingsPreparationUtil;
    }
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
    	this.postingsPreparationUtil = postingsPreparationUtil;
    }

}
