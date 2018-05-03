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
 *																							*
 * FileName    		:  BulkRateChangeProcess.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  06-11-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 * 06-11-2015       Satya	                 0.2          Added logging 		      		*
 *                                                         									*
 *                                                                         					*
 *                                                                                          *
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.app.bulkratechange;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;

public class BulkRateChangeProcess extends Thread {

	private static final Logger logger = Logger.getLogger(BulkRateChangeProcess.class);

	private BulkRateChangeProcessService bulkRateChangeProcessService;
	private BulkRateChangeHeader bulkRateChangeHeader = null;

	public AtomicLong success = null;
	public AtomicLong failure = null;
	public int finListSize = 0;

	public BulkRateChangeProcess(){

	}

	/**
	 * 
	 * @param bulkRateChangeHeader
	 * @param bulkRateChangeProcessService
	 * @param success
	 * @param failure
	 * @param finListSize
	 */
	public BulkRateChangeProcess(BulkRateChangeHeader bulkRateChangeHeader, BulkRateChangeProcessService bulkRateChangeProcessService, AtomicLong success, AtomicLong failure, int finListSize){
		this.bulkRateChangeProcessService = bulkRateChangeProcessService;
		this.bulkRateChangeHeader = bulkRateChangeHeader;
		this.finListSize = finListSize;
		this.success = success;
		this.failure = failure;
		//setName(threadName);
	}

	/**
	 * Override run() method and called from DialogCtrl
	 */
	public void run() {
		logger.debug("Entering");

		try {
			for (BulkRateChangeDetails bulkRateChangeDetail : bulkRateChangeHeader.getBulkRateChangeDetailsList()) {
				boolean rateChangeSuccess = bulkRateChangeProcessService.processBulkRateChangeDetail(bulkRateChangeHeader, bulkRateChangeDetail);
				if (rateChangeSuccess) {
					success.getAndIncrement();
				} else {
					failure.getAndIncrement();
				}
			}

			if (finListSize == (success.get() + failure.get())) {
				bulkRateChangeProcessService.doApproveBulkRateChangeHeader(bulkRateChangeHeader, success.get(), failure.get());
			}

		} catch (Exception e) {
			logger.error(e);
		} 

		logger.debug("Leaving");
	}
}