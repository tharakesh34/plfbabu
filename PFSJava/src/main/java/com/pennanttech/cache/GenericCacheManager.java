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
 * FileName    		:  GenericCacheManager.java                             		    	* 	  
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.service.cacheadministration.CacheAdministrationService;
import com.pennanttech.pff.core.AppException;
import com.pennanttech.pff.core.ErrorCode;

public class GenericCacheManager {
	private static final Logger logger = Logger.getLogger(GenericCacheManager.class);
	private static DefaultCacheManager cacheManager = null;
	private static boolean enabled = false;
	private static Map<String, String> caches = new HashMap<String, String>();
	private static String applicationType;
	private static boolean activated = false;
	private static long cacheVerify = 0;

	private static long cacheUpdate = 0;
	private static int nodeCount = 0;
	@Autowired
	private static CacheAdministrationService cacheAdministrationService;

	public GenericCacheManager(String type, CacheAdministrationService cacheAdministrationService) {
		super();
		GenericCacheManager.applicationType = type;
		GenericCacheManager.cacheAdministrationService = cacheAdministrationService;
		Thread nodeCountThread = new Thread(new CacheConfigMonitor(cacheAdministrationService));
		nodeCountThread.start();
	}

	public static boolean isActivated() {
		return activated;
	}

	public static void init() {
		startManager();
	}

	public static void startManager() {
		logger.debug("Entering");
		String configPathName = "";

		if ("WEB".equals(applicationType)) {
			configPathName = System.getenv("APP_ROOT_PATH") + "/PFF/BASE/config/" + "infini-WebConfig.xml";
		} else if ("API".equals(applicationType)) {
			configPathName = System.getenv("APP_ROOT_PATH") + "/PFF/BASE/config/" + "infini-ApiConfig.xml";
		} else {
			logger.debug("Application Type should be either API or WEB to start the cache services");
			return;
		}

		if (StringUtils.trimToNull(configPathName) == null) {
			logger.debug("Configuration file for either WEB or API is not Avaliable ");
			return;
		}

		if (cacheManager == null) {
			try {

				cacheManager = new DefaultCacheManager(configPathName);

				enabled = true;
				Thread thread = new Thread(
						new GenericCacheMonitor(GenericCacheManager.cacheVerify, cacheAdministrationService));
				thread.start();

			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			throw new AppException(ErrorCode.PPS_902.getMessage());
		}
		logger.debug("Leaving");
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static DefaultCacheManager getCacheManager() {
		return cacheManager;
	}

	public static void register(String name) {
		caches.put(name, name);
	}

	public static boolean isRegisterd(String name) {

		if (caches.get(name) != null) {
			return true;
		}
		return false;
	}

	public static void stopManager() {
		if (enabled) {
			cacheManager.stop();
			enabled = false;
			caches.clear();
			cacheManager = null;
		}
	}

	public static void setNodeSize(int newSize) {
		nodeCount = newSize;
	}

	public static void verifyCache() {
		logger.debug("Entering");
		if (enabled) {
			if (cacheManager.getClusterSize() == nodeCount) {
				if (!activated) {
					Set<String> cacheSet = cacheManager.getCacheNames();

					for (String string : cacheSet) {
						@SuppressWarnings("rawtypes")
						Cache cache = cacheManager.getCache(string);
						cache.clearAsync();
					}
					logger.info("Cache Activated");
				}
				activated = true;
			} else {
				activated = false;
				logger.info("Cache De-Activated Active Nodes :" + cacheManager.getClusterSize());

			}

		} else {
			activated = false;
		}
		logger.debug("Leaving");
	}

	public static long getCacheVerify() {
		return cacheVerify;
	}

	public static void setCacheVerify(long cacheVerify) {
		GenericCacheManager.cacheVerify = cacheVerify;
	}

	public static long getCacheUpdate() {
		return cacheUpdate;
	}

	public static void setCacheUpdate(long cacheUpdate) {
		GenericCacheManager.cacheUpdate = cacheUpdate;
	}

	public static CacheStats getNodeDetails() {
		CacheStats stats = new CacheStats();
		stats.setAppNode(true);

		if (cacheManager != null) {
			stats.setClusterName(cacheManager.getClusterName());
			stats.setClusterNode(cacheManager.getNodeAddress());
			stats.setIpAddress(cacheManager.getPhysicalAddresses());
			stats.setClusterSize(cacheManager.getClusterSize());
			stats.setClusterMembers(cacheManager.getClusterMembers());
			stats.setCacheNames(new ArrayList<>(cacheManager.getCacheNames()));
			String citiesCommaSeparated = String.join(",", stats.getCacheNames());
			stats.setCacheNamesDet(citiesCommaSeparated);
			stats.setCacheCount(stats.getCacheNames().size() - 1);
			stats.setManagerStatus(cacheManager.getCacheManagerStatus());
			stats.setActive(GenericCacheManager.isActivated());
			stats.setEnabled(GenericCacheManager.isEnabled());
			stats.setNodeCount(GenericCacheManager.nodeCount);
			stats.setLastMntBy(1000);
			stats.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		} else {
			stats.setManagerStatus("STOPPED");
		}

		return stats;

	}

}
