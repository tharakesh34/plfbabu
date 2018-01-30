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

package com.pennanttech.pennapps.core.cache;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class CacheMonitor implements Runnable {
	private static final Logger log = LogManager.getLogger(CacheMonitor.class);
	
	private CacheAdmin cacheAdmin;
			
	public CacheMonitor(CacheAdmin cacheAdmin) {
		super();
		this.cacheAdmin = cacheAdmin;
	}


	@Override
	public void run() {
		log.debug(Literal.ENTERING);

		CacheStats stats = CacheManager.getNodeDetails();

		// delete old status data from DB based on the clustered Name , Node
		try {
			log.debug(String.format("Deleting the old status of %s Cluster, %s  IP, %s Node", stats.getClusterName(),
					stats.getClusterIp(), stats.getClusterNode()));
			this.cacheAdmin.delete(stats.getClusterName(), stats.getClusterIp(), stats.getClusterNode());
		} catch (Exception e) {
			log.error("Error while deleting the existing cache details / No records to delete");
		}

		while (CacheManager.isEnabled()) {
			CacheManager.verifyCache();
			stats = CacheManager.getNodeDetails();
			CacheStats existingCache = cacheAdmin.getCacheStats(stats.getClusterName(), stats.getClusterNode());

			if (existingCache != null) {
				cacheAdmin.update(stats);
			} else {
				cacheAdmin.insert(stats);
			}

			try {
				Thread.sleep(CacheManager.getSleepTime());
			} catch (InterruptedException e) {
				log.error(Literal.EXCEPTION, e);
			}

			if (CacheManager.getCacheManager() == null) {
				return;
			}
		}

	}
}
