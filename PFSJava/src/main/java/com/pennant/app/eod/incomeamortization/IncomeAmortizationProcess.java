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
 * FileName    		:  IncomeAmortizationProcess.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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
package com.pennant.app.eod.incomeamortization;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;

public class IncomeAmortizationProcess {
	private static final Logger logger = Logger.getLogger(IncomeAmortizationProcess.class);

	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private ProjectedAmortizationService projectedAmortizationService;

	private IncomeAmortizationProcess() {
		super();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void projectedIncomeAmortization() throws Exception {
		logger.debug("Entering");

		FinEODEvent finEODEvent = new FinEODEvent();

		List<ProjectedAmortization> projAMZList = projectedAmortizationDAO.getActiveIncomeAMZDetails(true);
		finEODEvent.setProjectedAMZList(projAMZList);

		List<ProjectedAccrual> projAccrualList = projectedAmortizationDAO.getProjectedAccrualDetails();
		finEODEvent.setProjectedAccrualList(projAccrualList);

		// Income / Expense amortization calculations and save
		List<ProjectedAmortization> projIncomeAMZList = projectedAmortizationService.calculateMonthEndIncomeAmortizations(finEODEvent);
		projectedAmortizationDAO.saveBatchProjIncomeAMZ(projIncomeAMZList);

		// set amount values and update
		finEODEvent = projectedAmortizationService.updateProjectedAMZAmounts(finEODEvent, projIncomeAMZList);
		projectedAmortizationDAO.updateBatchIncomeAMZAmounts(finEODEvent.getProjectedAMZList());

		logger.debug("Leaving");
	}

	// getters / setters

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}
}
