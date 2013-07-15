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
 * 
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  FinanceProfitDetailFiller.java													*                           
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
import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;

public class FinanceProfitDetailFiller implements Serializable {

    private static final long serialVersionUID = 5554665802334950598L;
    private Logger logger = Logger.getLogger(AccountProcessUtil.class);

	// -------------------------------------------------------------------------------------------------
	// Process Schedule Details to fill Profit Details DATA
	// -------------------------------------------------------------------------------------------------

	public FinanceProfitDetail prepareFinPftDetails(AEAmountCodes aeAmountCodes,
			FinanceProfitDetail pftDetail, Date valueDate) {
		logger.debug("Entering");
		
		pftDetail.setLastMdfDate(valueDate);

		// TOTAL PROFIT FIELDS
		pftDetail.setTotalPftSchd(aeAmountCodes.getPft());
		pftDetail.setTotalPftCpz(aeAmountCodes.getCpzTot());
		pftDetail.setTotalPftPaid(aeAmountCodes.getPftAP());
		pftDetail.setTotalPftBal(aeAmountCodes.getPftAB());
		pftDetail.setTotalPftPaidInAdv(aeAmountCodes.getPftInAdv());

		// TOTAL PRINCIPAL FIELDS
		pftDetail.setTotalPriPaid(aeAmountCodes.getPriAP());
		pftDetail.setTotalPriBal(aeAmountCodes.getPriAB());

		// TILL DATE SCHEDULE PROFIT FIELDS
		pftDetail.setTdSchdPft(aeAmountCodes.getPftS());
		pftDetail.setTdPftCpz(aeAmountCodes.getCpzPrv());
		pftDetail.setTdSchdPftPaid(aeAmountCodes.getPftSP());
		pftDetail.setTdSchdPftBal(aeAmountCodes.getPftSB());
		
		// ACCRUAL FIELDS
		pftDetail.setTdPftAccrued(aeAmountCodes.getNAccrue());
		pftDetail.setTdPftAccrueSusp(aeAmountCodes.getAccrueS());
		pftDetail.setAcrTillNBD(aeAmountCodes.getNAccrue());
		pftDetail.setAcrTodayToNBD(aeAmountCodes.getDAccrue());

		// TILL DATE SCHEDULE PRINCIPAL FIELDS
		pftDetail.setTdSchdPri(aeAmountCodes.getPriS());
		pftDetail.setTdSchdPriPaid(aeAmountCodes.getPriSP());
		pftDetail.setTdSchdPriBal(aeAmountCodes.getPriSB());

		// AMORTIZATION FIELDS
		pftDetail.setTdPftAmortized(aeAmountCodes.getAmz());
		pftDetail.setTdPftAmortizedSusp(aeAmountCodes.getAmzS());
		pftDetail.setAmzTillNBD(aeAmountCodes.getnAmz());
		pftDetail.setAmzTodayToNBD(aeAmountCodes.getdAmz());

		logger.debug("Leaving");
		return pftDetail;
	}

}
