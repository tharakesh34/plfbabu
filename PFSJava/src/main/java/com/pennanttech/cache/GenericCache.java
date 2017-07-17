package com.pennanttech.cache;


import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.core.ErrorCode;

public class GenericCache<K, V> {
	private static final Logger logger = Logger.getLogger(GenericCache.class);
	private Cache<K, V> cache = null;
	private String name;

	public GenericCache(String name) {
		super();
		this.name = name;
		if (GenericCacheManager.isEnabled()) {
			getCache();
		}
	}

	private Cache<K, V> getCache(){
		logger.debug("Entering");
		if (!GenericCacheManager.isRegisterd(name)) {
			cache = GenericCacheManager.getCacheManager().getCache(name);
			GenericCacheManager.register(name);
			logger.debug(GenericCacheManager.getCacheManager().getNodeAddress() +" Cache Registerd" + name);
		} else if(cache==null){
			logger.info(" Cache Null Creating Cache" + name);
			cache = GenericCacheManager.getCacheManager().getCache(name);
		}
		logger.debug("Leaving");
		return cache;
	}
	
	public void setEntry(K key, V value) {
		logger.debug("Entering");
		if (GenericCacheManager.isActivated()) {
			// cache.put(key, value);
			try {
				getCache().put(key, value);
				logger.debug("Adding Cache Entry  for " +name + " :"  + key);

			} catch (Exception e) {
				logger.error(e);
				throw new AppException(ErrorCode.PPS_901.getMessage(),e);
			}

		}
		logger.debug("Leaving");
	}

	public V getEntry(K key) {
		logger.debug("Entering");
		if (GenericCacheManager.isActivated()) {
			try {
				return getCache().get(key);
			} catch (Exception e) {
				logger.error(e);
				throw new AppException(ErrorCode.PPS_901.getMessage(),e);
			}
	}
		logger.debug("Leaving");
		return null;
	}

	public void invalidateEntry(K key) {
		logger.debug("Entering");
		if (GenericCacheManager.isActivated()) {
			try {
				getCache().remove(key);
				logger.debug("Clearing Cache Entry  for " +name + " :"  + key);
			} catch (Exception e) {
				logger.error(e);
				throw new AppException(ErrorCode.PPS_901.getMessage(),e);
			}
		}
		logger.debug("Leaving");
	}

	@Listener
	public class MyListener {

		@CacheEntryCreated
		public void entryCreated(CacheEntryCreatedEvent<K, V> event) {
			// We are only interested in the post event
			if (!event.isPre())
				logger.trace("Added to Cache " + name + " " + event.getKey());
		}

		@CacheEntryRemoved
		public void entryRemoved(CacheEntryRemovedEvent<K, V> event) {
			if (!event.isPre())
				logger.trace("Removed From Cache " + name + " " + event.getKey());
		}
	}
}
