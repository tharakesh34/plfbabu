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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
		log.debug(Literal.ENTERING);
		if (CacheManager.isActivated()) {
			try {
				getCache().put(key, value);
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
				throw new AppException(DEFAULT_ERROR_MESSAGE, e);
			}
		}
		log.debug(Literal.LEAVING);
	}

	public V getEntity(K key) {
		log.debug(Literal.ENTERING);
		if (CacheManager.isActivated()) {
			try {
				return getCache().get(key);
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
				throw new AppException(DEFAULT_ERROR_MESSAGE, e);
			}
		}
		log.debug(Literal.LEAVING);
		return null;
	}

	public void invalidateEntity(K key) {
		log.debug(Literal.ENTERING);
		if (CacheManager.isActivated()) {
			try {
				getCache().remove(key);
			} catch (Exception e) {
				log.error(Literal.EXCEPTION, e);
				throw new AppException(DEFAULT_ERROR_MESSAGE, e);
			}
		}
		log.debug(Literal.LEAVING);
	}

	private org.infinispan.Cache<K, V> getCache() {
		log.debug(Literal.ENTERING);
		if (!CacheManager.isRegisterd(name)) {
			cache = CacheManager.getCacheManager().getCache(name);
			CacheManager.register(name);
			log.debug(String.format("Cache registerd from %s for the module %s",
					CacheManager.getCacheManager().getNodeAddress(), name));
		} else if (cache == null) {
			log.info(" Cache Null Creating Cache" + name);
			cache = CacheManager.getCacheManager().getCache(name);
		}
		log.debug(Literal.LEAVING);
		return cache;
	}

	@Listener
	public class MyListener {
		@CacheEntryCreated
		public void entryCreated(CacheEntryCreatedEvent<K, V> event) {
			// We are only interested in the post event
			if (!event.isPre()) {
				log.debug(String.format("Entity with the key %s added into the cache for the module %s",
						event.getKey().toString(), name));

			}
		}

		@CacheEntryRemoved
		public void entryRemoved(CacheEntryRemovedEvent<K, V> event) {
			if (!event.isPre()) {
				log.trace(String.format("Entity with the key %s removed from the cache for the module %s",
						event.getKey().toString(), name));
			}
		}
	}
}
