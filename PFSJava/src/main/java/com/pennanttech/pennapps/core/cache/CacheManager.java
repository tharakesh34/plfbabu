/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.core.cache;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.beans.factory.annotation.Value;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.FactoryException;
import com.pennanttech.pennapps.core.resource.Literal;

public class CacheManager {
	private static final Logger log = LogManager.getLogger(CacheNodeListener.class);
	
	private static Map<String, String> caches = new HashMap<String, String>();
	private static DefaultCacheManager cacheManager = null;
	
	private static boolean activated = false;
	private static boolean enabled = false;
	private static int nodes = 0;
	private static long sleepTime;
	
	private CacheAdmin cacheAdmin;
		
	@Value("${cache.enable}")
	private boolean cacheEnable;

	public CacheManager(CacheAdmin cacheAdmin) {
		super();
		this.cacheAdmin = cacheAdmin;
	}

	
	public void start() {
		log.debug(Literal.ENTERING);
		
		if (!cacheEnable) {
			log.warn("Cache not enabled, if you want to enable the cache please set the prperty cache.enable to true.");
			return;
		}
		
		new Thread(new CacheNodeListener(cacheAdmin)).start();
		
		String configPathName = App.getResourcePath(App.CONFIG, "cache", "cache-config.xml");
		if ("WEB".equals(App.TYPE.name())) {
			configPathName = App.getResourcePath(App.CONFIG, "cache", "cache-web-config.xml");
		} else if ("API".equals(App.TYPE.name())) {
			configPathName = App.getResourcePath(App.CONFIG, "cache", "cache-api-config.xml");
		} else {
			log.debug("Application Type should be either API or WEB to start the cache services");
			return;
		}
		
		File file = new File(configPathName);
		
		if (!file.exists()) {
			log.warn(String.format("Cache configuration file %s is not avaliable % s location", file.getName(), file.getParent()));
			return;
		}

		if (cacheManager == null) {
			try {
				cacheManager = new DefaultCacheManager(configPathName);
				enabled = true;
				new Thread(new CacheMonitor(cacheAdmin)).start();
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
			}
		} else {
			throw new FactoryException("CacheManager", new Exception("Cache manager already started"));
		}
		log.debug(Literal.LEAVING);
	}
	
	public void stop() {
		log.debug(Literal.ENTERING);
		if (enabled) {
			cacheManager.stop();
			enabled = false;
			caches.clear();
			cacheManager = null;
		}
		log.debug(Literal.LEAVING);
	}
	
	public static boolean isActivated() {
		return activated;
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

	public static void verifyCache() {
		log.debug(Literal.ENTERING);
		if (enabled) {
			if (cacheManager.getClusterSize() == nodes) {
				if (!activated) {
					Set<String> cacheSet = cacheManager.getCacheNames();

					for (String string : cacheSet) {
						Cache<Object, Object> cache = cacheManager.getCache(string);
						cache.clearAsync();
					}
					log.info(String.format("Cache activaded for in %d nodes", cacheManager.getClusterSize()));
				}
				activated = true;
			} else {
				if (enabled) {
					log.info(String.format("Cache deactivated %d/%d:", cacheManager.getClusterSize(), nodes));
				}
				activated = false;
			}

		} else {
			activated = false;
		}
		log.debug(Literal.LEAVING);
	}

	public static CacheStats getNodeDetails() {
		CacheStats stats = new CacheStats();
		stats.setAppNode(true);

		if (cacheManager != null) {
			stats.setClusterName(cacheManager.getClusterName());
			stats.setClusterNode(cacheManager.getNodeAddress());
			stats.setClusterIp(cacheManager.getPhysicalAddresses());
			stats.setClusterSize(cacheManager.getClusterSize());
			stats.setClusterMembers(cacheManager.getClusterMembers());
			
			for (String cacheName : cacheManager.getCacheNames()) {
				if (!"DefaultCache".equals(cacheName)) {
					stats.setCacheNames(cacheName);
				}
			}
			
			stats.setCacheNamesDet(String.join(",", stats.getCacheNames()));
			stats.setCacheCount(stats.getCacheNames().size());
			stats.setManagerCacheStatus(cacheManager.getCacheManagerStatus());
			stats.setActive(isActivated());
			stats.setEnabled(isEnabled());
			stats.setNodeCount(nodes);
			stats.setLastMntBy(1000);
			stats.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			
			log.debug(stats.toString());
		} else {
			stats.setManagerCacheStatus("STOPPED");
		}
		return stats;
	}


	public static int getNodes() {
		return nodes;
	}

	public static void setNodes(int nodes) {
		CacheManager.nodes = nodes;
	}

	public static long getSleepTime() {
		return sleepTime;
	}

	public static void setSleepTime(long sleepTime) {
		CacheManager.sleepTime = sleepTime;
	}

	public void setEnabled(boolean enabled) {
		CacheManager.enabled = enabled;
	}
	

}
