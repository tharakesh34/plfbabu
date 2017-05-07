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
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
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
	private static NPABucketDAO nPABucketDAO;
	private static NPABucketConfigurationDAO	nPABucketConfigurationDAO;
	
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

	private static LoadingCache<Long, NPABucket> nPABucketCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<Long, NPABucket>() {
				@Override
				public NPABucket load(Long bucketID) throws Exception {
					return getNPABucketByID(bucketID);
				}
			});

	protected static NPABucket getNPABucketByID(long bucketID) {
		return getnPABucketDAO().getNPABucket(bucketID, TableType.MAIN_TAB.getSuffix());
	}

	private static LoadingCache<Long, NPABucketConfiguration> nPABucketConfigurationCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<Long, NPABucketConfiguration>() {
				@Override
				public NPABucketConfiguration load(Long configID) throws Exception {
					return getNPABucketConfigurationById(configID);
				}
			});

	protected static NPABucketConfiguration getNPABucketConfigurationById(long configID ) {
		return getnPABucketConfigurationDAO().getNPABucketConfiguration(configID, TableType.MAIN_TAB.getSuffix());
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
			logger.warn("Unable to load data from DPDBucket Configuration cache: ", e);
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


	/**
	 * It Fetches NPABucket Data from Cache
	 * @param long bucketID
	 * @return NPABucket
	 */
	public static NPABucket getNPABucket(long bucketID){
		NPABucket npaBucket=null;
		
		try {
			npaBucket =  nPABucketCache.get(bucketID);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from NPABucket cache: ", e);
			npaBucket =  getNPABucketByID(bucketID);
		}

		return npaBucket;
	}
	
	
	/**
	 * It Clear NPABucket  data from cache.
	 * @param bucketID
	 */
	public static void clearNPABucketCache(long bucketID) {
		try {
			nPABucketCache.invalidate(bucketID);
		} catch (Exception ex) {
			logger.warn("Error clearing data from NPABucket cache: ",ex);
		}
	}

	/**
	 * It Fetches DPDBucketConfiguration Data from Cache
	 * @param long configID
	 * @return DPDBucketConfiguration
	 */
	public static NPABucketConfiguration getNPABucketConfiguration(long configID){
		NPABucketConfiguration npaBucketConfiguration=null;
		
		try {
			npaBucketConfiguration =  nPABucketConfigurationCache.get(configID);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from NPABucket Configuration cache: ", e);
			npaBucketConfiguration =  getNPABucketConfigurationById(configID);
		}

		return npaBucketConfiguration;
	}

	/**
	 * It Clear NPABucket Configuration data from cache.
	 * @param configID
	 */
	public static void clearNPABucketConfigurationCache(long configID) {
		try {
			nPABucketConfigurationCache.invalidate(configID);
		} catch (Exception ex) {
			logger.warn("Error clearing data from NPABucket Configuration cache: ",ex);
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
	public static NPABucketDAO getnPABucketDAO() {
		return nPABucketDAO;
	}
	public void setnPABucketDAO(NPABucketDAO nPABucketDAO) {
		FinanceConfigCache.nPABucketDAO = nPABucketDAO;
	}
	public static NPABucketConfigurationDAO getnPABucketConfigurationDAO() {
		return nPABucketConfigurationDAO;
	}
	public void setnPABucketConfigurationDAO(
			NPABucketConfigurationDAO nPABucketConfigurationDAO) {
		FinanceConfigCache.nPABucketConfigurationDAO = nPABucketConfigurationDAO;
	}	

	
}
