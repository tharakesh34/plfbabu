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
 * FileName    		:  GenericCacheMonitor.java                            		    		* 	  
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


import org.apache.log4j.Logger;

import com.pennant.backend.service.cacheadministration.CacheAdministrationService;

public class GenericCacheMonitor implements Runnable {
	private static final Logger logger = Logger.getLogger(GenericCacheMonitor.class);
	private CacheAdministrationService cacheAdministrationService;

	public GenericCacheMonitor() {
		super();
	}	
	
	
	long sleepTime = 0;
	
	public GenericCacheMonitor(long sleepTime,CacheAdministrationService cacheAdministrationService) {
		super();
		this.sleepTime = sleepTime;
		this.cacheAdministrationService= cacheAdministrationService;
	}


	private void monitorManager() {
		logger.debug("Entering ");
		try {

			CacheStats stats = GenericCacheManager.getNodeDetails();

			// delete old status data from DB based on the clustered Name , Node
			try {
				this.cacheAdministrationService.delete(stats.getClusterName(), stats.getIpAddress(),
						stats.getClusterNode());
			} catch (Exception e) {
				logger.error("Error while deleting the existing cache details / No records to delete");
			}

			while (GenericCacheManager.isEnabled()) {
				GenericCacheManager.verifyCache();
				
				stats = GenericCacheManager.getNodeDetails();				

				// Insert / Update

				CacheStats existingCache = cacheAdministrationService.getCacheStats(stats.getClusterName(),
						stats.getClusterNode());

				if (existingCache != null) {
					cacheAdministrationService.update(stats);
				} else {
					cacheAdministrationService.insert(stats);
				}

				Thread.sleep(sleepTime);
				if(GenericCacheManager.getCacheManager()==null){
					return;
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving ");
	}

	
	@Override
	public void run() {
		monitorManager();
	}
	

}
