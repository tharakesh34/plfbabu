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
 * FileName    		:  CalendarUpdation.java													*                           
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

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.Interface.service.CalendarInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennanttech.pennapps.core.InterfaceException;

public class CalendarUpdation implements Tasklet {

	private Logger logger = Logger.getLogger(CalendarUpdation.class);

	private AccountsDAO accountsDAO;
	private CalendarInterfaceService calendarInterfaceService;
	boolean COREBANK_CALENDAR_REQ = false;


	public CalendarUpdation() {
		super();
	}
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		// Date Parameter List
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: CalendarUpdation for Value Date: " + valueDate);
		try {
			// Accounts Accrual Balance reset to Zero
			getAccountsDAO().updateAccrualBalance(); 

			//Calendar Updation from Core Bank only When Flag becomes TRUE
			if(COREBANK_CALENDAR_REQ){
				getCalendarInterfaceService().calendarUpdate();
			}else{
				//BETTER TO MAKE SURE NEXT BUSINESS DATE AND COONFIRM WITH EOD USER
			}

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} 

		logger.debug("COMPLETE: CalendarUpdation for Value Date: "+ valueDate);
		
		return RepeatStatus.FINISHED;
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCalendarInterfaceService(CalendarInterfaceService calendarInterfaceService) {
		this.calendarInterfaceService = calendarInterfaceService;
	}
	public CalendarInterfaceService getCalendarInterfaceService() {
		return calendarInterfaceService;
	}

	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}
	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}


}
