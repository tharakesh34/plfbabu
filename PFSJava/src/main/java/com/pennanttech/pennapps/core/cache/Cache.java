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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class Cache<K, V> {
	private static final Logger log = LogManager.getLogger(Cache.class);

	private static final String DEFAULT_ERROR_MESSAGE = "898: Unable to process the request due to issues with cache manager. Please try again later or contact the system administrator.";

	protected static final String NODE_COUNT = "NODE_COUNT";
	protected static final String CACHE_UPDATE_SLEEP = "CACHE_UPDATE_SLEEP";
	protected static final String CACHE_VERIFY_SLEEP = "CACHE_VERIFY_SLEEP";
	protected static final String CLUSTER_SIZE = "CLUSTER_SIZE";

	private org.infinispan.Cache<K, V> cache = null;
	private String name;

	public Cache(String name) {
		super();
		this.name = name;
		if (CacheManager.isEnabled()) {
			getCache();
		}
	}

	public void setEntity(K key, V value) {
		log.trace(Literal.ENTERING);

		if (CacheManager.isActivated()) {
			try {
				getCache().put(key, value);
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
				throw new AppException(DEFAULT_ERROR_MESSAGE, e);
			}
		}

		log.trace(Literal.LEAVING);
	}

	public V getEntity(K key) {
		log.trace(Literal.ENTERING);

		if (CacheManager.isActivated()) {
			try {
				return getCache().get(key);
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
				throw new AppException(DEFAULT_ERROR_MESSAGE, e);
			}
		}

		log.trace(Literal.LEAVING);
		return null;
	}

	public void invalidateEntity(K key) {
		log.trace(Literal.ENTERING);

		if (CacheManager.isActivated()) {
			try {
				getCache().remove(key);
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
				throw new AppException(DEFAULT_ERROR_MESSAGE, e);
			}
		}

		log.trace(Literal.LEAVING);
	}

	private org.infinispan.Cache<K, V> getCache() {
		if (cache != null) {
			return cache;
		}

		DefaultCacheManager cacheManager = CacheManager.getCacheManager();
		if (!CacheManager.isRegisterd(name)) {
			CacheManager.register(name);

			cache = cacheManager.getCache(name);
			log.info("Cache registerd from {} for the module {}", cacheManager.getNodeAddress(), name);
		} else if (cache == null) {
			log.info("Creating cache for the module {}", name);
			cache = cacheManager.getCache(name);
		}

		return cache;
	}

	@Listener
	public class MyListener {
		@CacheEntryCreated
		public void entryCreated(CacheEntryCreatedEvent<K, V> event) {
			// We are only interested in the post event
			if (!event.isPre()) {
				log.info("Entity with the key {} added into the cache for the module {}", event.getKey(), name);

			}
		}

		@CacheEntryRemoved
		public void entryRemoved(CacheEntryRemovedEvent<K, V> event) {
			if (!event.isPre()) {
				log.info("Entity with the key {} removed from the cache for the module {}", event.getKey(), name);
			}
		}
	}
}
