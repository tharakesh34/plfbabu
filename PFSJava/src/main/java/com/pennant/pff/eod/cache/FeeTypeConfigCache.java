package com.pennant.pff.eod.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.finance.FeeType;

public class FeeTypeConfigCache {
	private static final Logger logger = LogManager.getLogger(FeeTypeConfigCache.class);
	private static FeeTypeDAO feeTypeDAO;

	private static LoadingCache<String, FeeType> feeTypeCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, FeeType>() {
				@Override
				public FeeType load(String acType) throws Exception {
					return getFeeTypeByCode(acType);
				}
			});

	private static FeeType getFeeTypeByCode(String code) {
		return feeTypeDAO.getTaxDetailByCode(code);
	}

	public static FeeType getCacheFeeTypeByCode(String code) {
		FeeType feeType = null;
		try {
			feeType = feeTypeCache.get(code);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Fee Type cache: ", e);
			feeType = getFeeType(code);
		}
		return feeType;
	}

	public static FeeType getFeeType(String code) {
		return getFeeTypeByCode(code);
	}

	public static void clearFeeTypeCache(String code) {
		try {
			feeTypeCache.invalidate(code);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Fee type cache: ", ex);
		}
	}

	public static void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		FeeTypeConfigCache.feeTypeDAO = feeTypeDAO;
	}

}
