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
 * FileName    		:  AccrualProcess.java													*                           
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
package com.pennant.app.eod.accrual;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.eod.service.AmortizationService;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.PennantConstants;

public class AccrualProcess extends Thread {
	private static final Logger logger = Logger.getLogger(AccrualProcess.class);

	private static AccrualProcess me = null;
	private  ExecutionStatus calculation = new ExecutionStatus();
	private  ExecutionStatus posting = new ExecutionStatus();
	
	public static String ACC_RUNNING = "";

	private Date valueDate  = null;
	private String branch 	= null;
	private AmortizationService amortizationService;

	private AccrualProcess(){
		super();
	}

	private AccrualProcess(AmortizationService amortizationService, Date valueDate, String branch){
		super();
		this.amortizationService = amortizationService;
		this.valueDate = valueDate;
		this.branch = branch;
	}

	public static AccrualProcess getInstance(AmortizationService amortizationService, Date valueDate, String branch) {
		if(StringUtils.isEmpty(ACC_RUNNING) || me == null) {
			me = new AccrualProcess(amortizationService, valueDate, branch);
		}
		return me;
	}
	
	public static AccrualProcess getInstance() {
		if(me == null) {
			me = new AccrualProcess();
		}
		return me;
	}

	public void run() {
		try {
			ACC_RUNNING = "STARTED";
			calculation.setExecutionName(PennantConstants.EOD_ACCRUAL_CALC);
			posting.setExecutionName(PennantConstants.EOD_ACCRUAL_POSTING);
			
			calculation.setStartTime(new Date(System.currentTimeMillis()));
			this.calculation.setStatus("EXECUTING");
			try{
				amortizationService.doAccrualCalculation(calculation, valueDate);
				calculation.setEndTime(new Date(System.currentTimeMillis()));
				this.calculation.setStatus("COMPLETED");
			}catch (Exception e) {
				logger.error("Exception: ", e);
				this.calculation.setStatus("FAILED");
				ACC_RUNNING = "FAILED";
				return;
			} finally {
				calculation.setEndTime(new Date(System.currentTimeMillis()));
			}
			
			
			posting.setStartTime(new Date(System.currentTimeMillis()));
			this.posting.setStatus("EXECUTING");
			try {
				amortizationService.doAccrualPosting(posting, valueDate, this.branch);
				posting.setEndTime(new Date(System.currentTimeMillis()));
				this.posting.setStatus("COMPLETED");
			} catch (Exception e) {
				logger.error("Exception: ", e);
				this.posting.setStatus("FAILED");
				ACC_RUNNING = "FAILED";
				return;
			} finally {
				posting.setEndTime(new Date(System.currentTimeMillis()));
			}
			
			ACC_RUNNING = "COMPLETED";
		} catch (Exception e) {
			logger.error("Exception: ", e);
			ACC_RUNNING = "FAILED";
		} finally {
		}

	}

	public ExecutionStatus getCalculation() {
    	return calculation;
    }

	public void setCalculation(ExecutionStatus calculation) {
    	this.calculation = calculation;
    }

	public ExecutionStatus getPosting() {
    	return posting;
    }

	public void setPosting(ExecutionStatus posting) {
    	this.posting = posting;
    }

	
}
