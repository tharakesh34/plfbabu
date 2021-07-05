package com.pennant.pff.eod.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.model.applicationmaster.BounceReason;

public class BounceConfigCache {
	private static final Logger logger = LogManager.getLogger(BounceConfigCache.class);
	private static BounceReasonDAO bounceReasonDAO;

	public static BounceReason getCacheBounceReason(String bounceCode) {
		BounceReason bounceReason = null;
		try {
			bounceReason = bounceCache.get(bounceCode);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Bounce cache: ", e);
			bounceReason = getBounceReason(bounceCode);
		}
		return bounceReason;
	}

	private static LoadingCache<String, BounceReason> bounceCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, BounceReason>() {
				@Override
				public BounceReason load(String bounceCode) throws Exception {
					return getBounceReason(bounceCode);
				}
			});

	private static BounceReason getBounceReason(String bounceCode) {
		return bounceReasonDAO.getBounceReasonByReturnCode(bounceCode, "");
	}

	public static void invalidateAll() {
		try {
			bounceCache.invalidateAll();
		} catch (Exception ex) {
			logger.warn("Error clearing data from Bounce cache: ", ex);
		}
	}

	public static void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		BounceConfigCache.bounceReasonDAO = bounceReasonDAO;
	}

}
