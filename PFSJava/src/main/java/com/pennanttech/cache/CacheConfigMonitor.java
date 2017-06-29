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
 * FileName    		:  CacheConfigMonitor.java                             		    		* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2017    														*
 *                                                                  						*
 * Modified Date    :  27-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2017       Pennant	                 0.1                                            * 
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

package com.pennanttech.cache;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.log4j.Logger;
import com.pennant.backend.service.cacheadministration.CacheAdministrationService;



public class CacheConfigMonitor implements Runnable {
	private static final Logger logger = Logger.getLogger(CacheConfigMonitor.class);
	
	private long sleepTime = 0;
	private int nodeCount = 0;
	
	private CacheAdministrationService cacheAdministrationService;

	
	public CacheConfigMonitor(CacheAdministrationService cacheAdministrationService) {
		super();		
		this.cacheAdministrationService= cacheAdministrationService;
	}

	private void monitorConfig() {
		logger.debug("Entering ");
		try {
			
			//Fetch the Node count and Sleep Timings from 
			
			Map<String, Object> cacheParams =this.cacheAdministrationService.getCacheParameters();
			
			int count =		((BigDecimal) cacheParams.get("NODE_COUNT")).intValue();
			this.sleepTime =((BigDecimal) cacheParams.get("CACHE_UPDATE_SLEEP")).longValue();			
			GenericCacheManager.setCacheVerify(((BigDecimal) cacheParams.get("CACHE_VERIFY_SLEEP")).longValue());
						
						
			while (true) {
				if (nodeCount!= count){
					nodeCount= count;
					GenericCacheManager.setNodeSize(nodeCount);
				}
				Thread.sleep(sleepTime);

				cacheParams =this.cacheAdministrationService.getCacheParameters();
				count =  ((BigDecimal) cacheParams.get("NODE_COUNT")).intValue();
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving ");
	}

	@Override
	public void run() {
		monitorConfig();
	}

}
