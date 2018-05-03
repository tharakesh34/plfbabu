package com.pennant.cache.util;

import java.util.List;
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
	
	private static final Logger logger = Logger.getLogger(FinanceConfigCache.class);

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
	
	private static LoadingCache<String, DPDBucket> dPDBucketCodeCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, DPDBucket>() {
				@Override
				public DPDBucket load(String bucketCode) throws Exception {
					return getDPDBucketByCode(bucketCode);
				}
			});
	
	protected static DPDBucket getDPDBucketByCode(String bucketCode) {
		return getdPDBucketDAO().getDPDBucket(bucketCode, TableType.MAIN_TAB.getSuffix());
	}
	
	
	private static LoadingCache<String, List<DPDBucketConfiguration>> dPDBucketConfigurationCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, List<DPDBucketConfiguration>>() {
				@Override
				public List<DPDBucketConfiguration> load(String productCode) throws Exception {
					return getDPDBucketConfigurationById(productCode);
				}
			});

	protected static List<DPDBucketConfiguration> getDPDBucketConfigurationById(String productCode ) {
		return getdPDBucketConfigurationDAO().getDPDBucketConfigurations(productCode);
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

	private static LoadingCache<String, List<NPABucketConfiguration>> nPABucketConfigurationCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, List<NPABucketConfiguration>>() {
				@Override
				public List<NPABucketConfiguration> load(String productCode) throws Exception {
					return getNPABucketConfigurationByCode(productCode);
				}
			});

	protected static List<NPABucketConfiguration> getNPABucketConfigurationByCode(String productCode) {
		return getnPABucketConfigurationDAO().getNPABucketConfigByProducts(productCode);
	}

	/**
	 * It Fetches FinanceType Data from Cache
	 * @param String finType
	 * @return FinanceType
	 */
	public static FinanceType getFinanceType(String finType){
		return getFinanceTypeByID(finType);
	}
	
	/**
	 * It Fetches FinanceType Data from Cache
	 * @param String finType
	 * @return FinanceType
	 */
	public static FinanceType getCacheFinanceType(String finType){
		FinanceType financeType=null;
		try {
			financeType =  financeTypeCache.get(finType);
		} catch (Exception e) {
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
		return getDPDBucketByID(bucketID);
	}
	
	/**
	 * It Fetches DPDBucket Data from Cache
	 * @param long bucketID
	 * @return DPDBucket
	 */
	public static DPDBucket getCacheDPDBucket(long bucketID){
		DPDBucket dpdBucket=null;
		
		try {
			dpdBucket =  dPDBucketCache.get(bucketID);
		} catch (Exception e) {
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
	 * It Fetches DPDBucket Data from Cache
	 * @param long bucketID
	 * @return DPDBucket
	 */
	public static DPDBucket getDPDBucketCode(String bucketCode){
		return getDPDBucketByCode(bucketCode);
	}
	
	/**
	 * It Fetches DPDBucket Data from Cache
	 * @param long bucketID
	 * @return DPDBucket
	 */
	public static DPDBucket getCacheDPDBucketCode(String bucketCode){
		DPDBucket dpdBucket=null;
		
		try {
			dpdBucket =  dPDBucketCodeCache.get(bucketCode);
		} catch (Exception e) {
			logger.warn("Unable to load data from DPDBucket cache: ", e);
			dpdBucket =  getDPDBucketByCode(bucketCode);
		}
		
		return dpdBucket;
	}
	
	/**
	 * It Clear DPDBucket data from cache .
	 * @param long bucketID
	 */
	public static void clearDPDBucketCodeCache(String bucketCode) {
		try {
			dPDBucketCodeCache.invalidate(bucketCode);
		} catch (Exception ex) {
			logger.warn("Error clearing data from DPDBucket cache: ",ex);
		}
	}


	/**
	 * It Fetches DPDBucketConfiguration Data from Cache
	 * @param long configID
	 * @return DPDBucketConfiguration
	 */
	public static List<DPDBucketConfiguration> getDPDBucketConfiguration(String productCode){
		return  getDPDBucketConfigurationById(productCode);

	}
	/**
	 * It Fetches DPDBucketConfiguration Data from Cache
	 * @param long configID
	 * @return DPDBucketConfiguration
	 */
	public static List<DPDBucketConfiguration> getCacheDPDBucketConfiguration(String productCode){
		
		try {
			return  dPDBucketConfigurationCache.get(productCode);
		} catch (Exception e) {
			logger.warn("Unable to load data from DPDBucket Configuration cache: ", e);
			return  getDPDBucketConfigurationById(productCode);
		}
		
	}
	
	/**
	 * It Clear DPDBucket Configuration data from cache.
	 * @param long configID
	 */
	public static void clearDPDBucketConfigurationCache(String productCode) {
		try {
			dPDBucketConfigurationCache.invalidate(productCode);
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
//		NPABucket npaBucket=null;
//		
//		try {
//			npaBucket =  nPABucketCache.get(bucketID);
//		} catch (ExecutionException e) {
//			logger.warn("Unable to load data from NPABucket cache: ", e);
//			npaBucket =  getNPABucketByID(bucketID);
//		}

		return getNPABucketByID(bucketID);
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
	public static List<NPABucketConfiguration> getNPABucketConfiguration(String productCode) {
		return getNPABucketConfigurationByCode(productCode);
	}
	
	/**
	 * It Fetches DPDBucketConfiguration Data from Cache
	 * @param long configID
	 * @return DPDBucketConfiguration
	 */
	public static List<NPABucketConfiguration> getCacheNPABucketConfiguration(String productCode) {
		try {
			return nPABucketConfigurationCache.get(productCode);
		} catch (Exception e) {
			logger.warn("Unable to load data from NPABucket Configuration cache: ", e);
			return getNPABucketConfigurationByCode(productCode);
		}
	}

	/**
	 * It Clear NPABucket Configuration data from cache.
	 * @param configID
	 */
	public static void clearNPABucketConfigurationCache(String productCode) {
		try {
			nPABucketConfigurationCache.invalidate(productCode);
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
