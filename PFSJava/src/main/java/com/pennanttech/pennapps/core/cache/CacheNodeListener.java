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

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class CacheNodeListener implements Runnable {
	private static final Logger log = LogManager.getLogger(CacheNodeListener.class);

	private int nodes = 0;
	private CacheAdmin cacheAdmin;

	public CacheNodeListener(CacheAdmin cacheAdmin) {
		this.cacheAdmin = cacheAdmin;
	}

	@Override
	public void run() {
		log.debug(Literal.ENTERING);
		Map<String, Object> parameters = this.cacheAdmin.getParameters();

		int nodeCount = 0;
		long nodeListenerSleepTime = 3000;
		long monitorSleepTime = 3000;

		if (parameters != null && parameters.isEmpty()) {
			nodeCount = ((Integer) parameters.get("NODE_COUNT")).intValue();
			nodeListenerSleepTime = ((Long) parameters.get("CACHE_UPDATE_SLEEP")).longValue();
			monitorSleepTime = ((Long) parameters.get("CACHE_VERIFY_SLEEP")).longValue();
		}

		CacheManager.setSleepTime(monitorSleepTime);

		while (true) {
			if (nodes != nodeCount) {
				nodes = nodeCount;
				CacheManager.setNodes(nodes);
			}

			try {
				Thread.sleep(nodeListenerSleepTime);
			} catch (InterruptedException e) {
				log.error(Literal.EXCEPTION, e);
			}

			parameters = this.cacheAdmin.getParameters();
			if (parameters != null && !parameters.isEmpty()) {
				nodeCount = ((Integer) parameters.get("NODE_COUNT")).intValue();
				nodeListenerSleepTime = ((Long) parameters.get("CACHE_UPDATE_SLEEP")).longValue();
			}
		}
	}
}
