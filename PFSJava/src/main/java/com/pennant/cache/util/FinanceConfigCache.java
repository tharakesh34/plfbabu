package com.pennant.cache.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.applicationmaster.DPDBucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.DPDBucketDAO;
import com.pennant.backend.dao.applicationmaster.NPABucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.NPABucketDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.rmtmasters.FinanceType;

/**
 * @author Pennant
 *
 */

public class FinanceConfigCache {
	
	private final static Logger logger = Logger.getLogger(FinanceConfigCache.class);

	private static FinanceTypeDAO financeTypeDAO;
	private static DPDBucketDAO dPDBucketDAO;
	private static DPDBucketConfigurationDAO	dPDBucketConfigurationDAO;
	private NPABucketDAO nPABucketDAO;
	private NPABucketConfigurationDAO	nPABucketConfigurationDAO;
	
	private static LoadingCache<String, FinanceType> financeTypeCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, FinanceType>() {
				@Override
				public FinanceType load(String finType) throws Exception {
					return getFinanceTypeByID(finType);
				}
			});


	protected static FinanceType getFinanceTypeByID(String finType) {
		return getFinanceTypeDAO().getFinanceTypeByID(finType,"");
	}
	
	
	
	/**
	 * It Fetches FinanceType Data from Cache
	 * @param finType
	 * @return FinanceType
	 */
	public static FinanceType getFinanceType(String finType){
		FinanceType financeType;
		try {
			financeType =  financeTypeCache.get(finType);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			financeType =  getFinanceTypeByID(finType);
		}
		return financeType;
	}
	
	/**
	 * It Clear FinanceType data from cache .
	 * @param finType
	 */
	public static void clearFinanceTypeCache(String finType) {
		try {
			financeTypeCache.invalidate(finType);
		} catch (Exception ex) {
			logger.warn("Error clearing data from FinType cache: ",ex);
		}
	}
	
	
	public static FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public static void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		FinanceConfigCache.financeTypeDAO = financeTypeDAO;
	}
	public static DPDBucketDAO getdPDBucketDAO() {
		return dPDBucketDAO;
	}
	public static void setdPDBucketDAO(DPDBucketDAO dPDBucketDAO) {
		FinanceConfigCache.dPDBucketDAO = dPDBucketDAO;
	}
	public static DPDBucketConfigurationDAO getdPDBucketConfigurationDAO() {
		return dPDBucketConfigurationDAO;
	}
	public static void setdPDBucketConfigurationDAO(
			DPDBucketConfigurationDAO dPDBucketConfigurationDAO) {
		FinanceConfigCache.dPDBucketConfigurationDAO = dPDBucketConfigurationDAO;
	}
	public NPABucketDAO getnPABucketDAO() {
		return nPABucketDAO;
	}
	public void setnPABucketDAO(NPABucketDAO nPABucketDAO) {
		this.nPABucketDAO = nPABucketDAO;
	}
	public NPABucketConfigurationDAO getnPABucketConfigurationDAO() {
		return nPABucketConfigurationDAO;
	}
	public void setnPABucketConfigurationDAO(
			NPABucketConfigurationDAO nPABucketConfigurationDAO) {
		this.nPABucketConfigurationDAO = nPABucketConfigurationDAO;
	}	

	
}
