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

    public FinanceProfitDetailFiller() {
    	super();
    }
    
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
		pftDetail.setTdPftAccrued(aeAmountCodes.getNAccrue() );
		pftDetail.setTdPftAccrueSusp(aeAmountCodes.getAccrueS().compareTo(aeAmountCodes.getNAccrue()) > 0 ? aeAmountCodes.getNAccrue() :  aeAmountCodes.getAccrueS());
		pftDetail.setAcrTillNBD(aeAmountCodes.getNAccrue());
		pftDetail.setAcrTodayToNBD(aeAmountCodes.getDAccrue());

		// TILL DATE SCHEDULE PRINCIPAL FIELDS
		pftDetail.setTdSchdPri(aeAmountCodes.getPriS());
		pftDetail.setTdSchdPriPaid(aeAmountCodes.getPriSP());
		pftDetail.setTdSchdPriBal(aeAmountCodes.getPriSB());

		// AMORTIZATION FIELDS
		pftDetail.setTdPftAmortized(aeAmountCodes.getAmz());
		pftDetail.setTdPftAmortizedSusp(aeAmountCodes.getAmzS().compareTo(aeAmountCodes.getAmz()) > 0 ? aeAmountCodes.getAmz() :  aeAmountCodes.getAmzS());
		pftDetail.setAmzTillNBD(aeAmountCodes.getnAmz());
		pftDetail.setAmzTodayToNBD(aeAmountCodes.getdAmz());
		
		//Paid Details 
		pftDetail.setFullPaidDate(aeAmountCodes.getFullyPaidDate());
		pftDetail.setCurReducingRate(aeAmountCodes.getCurReducingRate());
		pftDetail.setCurFlatRate(aeAmountCodes.getCurFlatRate());
		pftDetail.setTotalpriSchd(aeAmountCodes.getPri());
		pftDetail.setEarlyPaidAmt(aeAmountCodes.getPriAP().add(aeAmountCodes.getPftAP()).
					subtract(aeAmountCodes.getPriSP()).subtract(aeAmountCodes.getPftSP()));
		
		//Overdue and penalty details
		pftDetail.setODPrincipal(aeAmountCodes.getPriOD());
		pftDetail.setODProfit(aeAmountCodes.getPftOD());
		pftDetail.setPenaltyPaid(aeAmountCodes.getPenaltyPaid());
		pftDetail.setPenaltyDue(aeAmountCodes.getPenaltyDue());
		pftDetail.setPenaltyWaived(aeAmountCodes.getPenaltyWaived());
		
		//Next Schedule details
		pftDetail.setNSchdDate(aeAmountCodes.getNextRepayPftDate());
		pftDetail.setNSchdPri(aeAmountCodes.getNextSchdPri());
		pftDetail.setNSchdPft(aeAmountCodes.getNextSchdPft());
		pftDetail.setNSchdPriDue(aeAmountCodes.getNextSchdPriBal());
		pftDetail.setNSchdPftDue(aeAmountCodes.getNextSchdPftBal());
		
		//Profit Details
		pftDetail.setFinReference(aeAmountCodes.getFinReference());
		pftDetail.setAccruePft(aeAmountCodes.getAccrue());
		pftDetail.setEarnedPft(aeAmountCodes.getPftAP().add(aeAmountCodes.getAccrue()));
		pftDetail.setUnearned(aeAmountCodes.getPft().subtract(aeAmountCodes.getPftAP().add(aeAmountCodes.getAccrue())));
		pftDetail.setPftInSusp(aeAmountCodes.isPftInSusp());
		pftDetail.setSuspPft(aeAmountCodes.getAccrueS());
		
		pftDetail.setLastRpySchDate(aeAmountCodes.getLastRpySchDate());
		pftDetail.setNextRpySchDate(aeAmountCodes.getNextRpySchDate());
		pftDetail.setLastRpySchPri(aeAmountCodes.getLastRpySchPri());
		pftDetail.setLastRpySchPft(aeAmountCodes.getLastRpySchPft());
		pftDetail.setLatestWriteOffDate(aeAmountCodes.getLatestWriteOffDate());
		pftDetail.setTotalWriteoff(aeAmountCodes.getTotalWriteoff());
		
		//Installment Details
		pftDetail.setNOInst(aeAmountCodes.getTtlTerms());
		pftDetail.setNOPaidInst(aeAmountCodes.getPaidInst());
		pftDetail.setNOODInst(aeAmountCodes.getODInst());

		//Repayment Details 
		pftDetail.setNORepayments(aeAmountCodes.getTtlTerms());
		pftDetail.setFirstRepayAmt(aeAmountCodes.getFirstRepayAmt());
		pftDetail.setLastRepayAmt(aeAmountCodes.getLastRepayAmt());
		
		//Depreciation Fields
		pftDetail.setAccumulatedDepPri(aeAmountCodes.getAccumulatedDepPri());
		pftDetail.setDepreciatePri(aeAmountCodes.getDepreciatePri());
		
		//Overdue Details
		pftDetail.setoDDays(aeAmountCodes.getODDays());
		if(pftDetail.getFirstODDate() == null){
			pftDetail.setFirstODDate(aeAmountCodes.getFirstODDate());
		}
		
		if(aeAmountCodes.getLastODDate() != null){
			pftDetail.setLastODDate(aeAmountCodes.getLastODDate());
		}
		
		//CRB Field Details
		pftDetail.setCRBODDays(aeAmountCodes.getCRBODDays());
		pftDetail.setCRBODInst(aeAmountCodes.getCRBODInst());
		pftDetail.setCRBODPrincipal(aeAmountCodes.getCRBPriOD());
		pftDetail.setCRBODProfit(aeAmountCodes.getCRBPftOD());
		if(pftDetail.getCRBFirstODDate() == null){
			pftDetail.setCRBFirstODDate(aeAmountCodes.getCRBFirstODDate());
		}
		
		if(aeAmountCodes.getCRBLastODDate() != null){
			pftDetail.setCRBLastODDate(aeAmountCodes.getCRBLastODDate());
		}

		logger.debug("Leaving");
		return pftDetail;
	}

}
