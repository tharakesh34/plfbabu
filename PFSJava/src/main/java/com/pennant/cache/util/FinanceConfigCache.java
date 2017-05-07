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
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pff.core.TableType;

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
		return getFinanceTypeDAO().getFinanceTypeByID(finType,TableType.MAIN_TAB.getSuffix());
	}

	private static LoadingCache<Long, DPDBucket> dPDBucketCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<Long, DPDBucket>() {
				@Override
				public DPDBucket load(Long bucketID) throws Exception {
					return getDPDBucketByID(bucketID);
				}
			});

	protected static DPDBucket getDPDBucketByID(long bucketID) {
		return getdPDBucketDAO().getDPDBucket(bucketID, TableType.MAIN_TAB.getSuffix());
	}
	
	
	private static LoadingCache<Long, DPDBucketConfiguration> dPDBucketConfigurationCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<Long, DPDBucketConfiguration>() {
				@Override
				public DPDBucketConfiguration load(Long configID) throws Exception {
					return getDPDBucketConfigurationById(configID);
				}
			});

	protected static DPDBucketConfiguration getDPDBucketConfigurationById(long configID ) {
		return getdPDBucketConfigurationDAO().getDPDBucketConfiguration(configID, TableType.MAIN_TAB.getSuffix());
	}
		
	/**
	 * It Fetches FinanceType Data from Cache
	 * @param String finType
	 * @return FinanceType
	 */
	public static FinanceType getFinanceType(String finType){
		FinanceType financeType=null;
		try {
			financeType =  financeTypeCache.get(finType);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from FinanceType cache: ", e);
			financeType =  getFinanceTypeByID(finType);
		}
		return financeType;
	}
	
	/**
	 * It Clear FinanceType data from cache .
	 * @param String finType
	 */
	public static void clearFinanceTypeCache(String finType) {
		try {
			financeTypeCache.invalidate(finType);
		} catch (Exception ex) {
			logger.warn("Error clearing data from FinType cache: ",ex);
		}
	}

	/**
	 * It Fetches DPDBucket Data from Cache
	 * @param long bucketID
	 * @return DPDBucket
	 */
	public static DPDBucket getDPDBucket(long bucketID){
		DPDBucket dpdBucket=null;
		
		try {
			dpdBucket =  dPDBucketCache.get(bucketID);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from DPDBucket cache: ", e);
			dpdBucket =  getDPDBucketByID(bucketID);
		}

		return dpdBucket;
	}
	
	/**
	 * It Clear DPDBucket data from cache .
	 * @param long bucketID
	 */
	public static void clearDPDBucketCache(long bucketID) {
		try {
			dPDBucketCache.invalidate(bucketID);
		} catch (Exception ex) {
			logger.warn("Error clearing data from DPDBucket cache: ",ex);
		}
	}


	/**
	 * It Fetches DPDBucketConfiguration Data from Cache
	 * @param long configID
	 * @return DPDBucketConfiguration
	 */
	public static DPDBucketConfiguration getDPDBucketConfiguration(long configID){
		DPDBucketConfiguration dpdBucketConfiguration=null;
		
		try {
			dpdBucketConfiguration =  dPDBucketConfigurationCache.get(configID);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			dpdBucketConfiguration =  getDPDBucketConfigurationById(configID);
		}

		return dpdBucketConfiguration;
	}
	
	/**
	 * It Clear DPDBucket Configuration data from cache.
	 * @param long configID
	 */
	public static void clearDPDBucketConfigurationCache(long configID) {
		try {
			dPDBucketConfigurationCache.invalidate(configID);
		} catch (Exception ex) {
			logger.warn("Error clearing data from DPDBucket Configuration cache: ",ex);
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
