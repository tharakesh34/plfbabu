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
 * FileName    		:  FutureInstallmentHolds.java													*                           
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
package com.pennant.backend.endofday.tasklet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.util.PennantConstants;

public class FutureInstallmentHolds implements Tasklet {
	
	private Logger logger = Logger.getLogger(FutureInstallmentHolds.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private AccountInterfaceService accountInterfaceService;
	
	private Date dateValueDate = null;
	private Date dateNextBusinessDate = null;
	
	public FutureInstallmentHolds() {
		
	}
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getAppValueDate();
		dateNextBusinessDate = SysParamUtil.getValueAsDate("APP_NEXT_BUS_DATE");
		
		logger.debug("START: Future installments Account Holds for Value Date: "+ DateUtility.addDays(dateValueDate,-1));
		
		try {
			
			// If NBD is holiday then loop continues, else end process.
			if (dateValueDate.compareTo(dateNextBusinessDate) == 0) {	
				Date futureDate = DateUtility.addDays(
						BusinessCalendar.getWorkingBussinessDate(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY),
								"N", dateNextBusinessDate).getTime(), -1);
				
				//Adding Holdings To Accounts After Repayments Process
				List<AccountHoldStatus> accountsList = getFinanceScheduleDetailDAO().getFutureInstAmtByRepayAc(DateUtility.addDays(dateValueDate,-1), futureDate);
				List<AccountHoldStatus> returnAcList = null;
				if(!accountsList.isEmpty()){

					logger.debug("START: Adding Future Account Holds for Value Date: "+ DateUtility.addDays(dateValueDate,-1));

					returnAcList = new ArrayList<AccountHoldStatus>();

					//Sending 2000 Records At a time to Process for Holding
					while (!accountsList.isEmpty()) {

						List<AccountHoldStatus> subAcList = null;
						if(accountsList.size() > 2000){
							subAcList = accountsList.subList(0, 2000);
						}else{
							subAcList = accountsList;
						}
						returnAcList.addAll(getAccountInterfaceService().addAccountHolds(subAcList,DateUtility.addDays(dateValueDate,-1),PennantConstants.HOLDTYPE_FUTURE));

						if(accountsList.size() > 2000){
							accountsList.subList(0, 2000).clear();
						}else{
							accountsList.clear();
						}
					}

					//Save Returned Account List For Report Purpose
					getFinODDetailsDAO().saveHoldAccountStatus(returnAcList);

					logger.debug("END: Adding Future Account Holds for Value Date: "+ DateUtility.addDays(dateValueDate,-1));
				}
			}
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}  finally {
			
		}

		logger.debug("END: Future installments Account Holds for Value Date: " + DateUtility.addDays(dateValueDate,-1));
		return RepeatStatus.FINISHED;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	
	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}
	
	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

}
