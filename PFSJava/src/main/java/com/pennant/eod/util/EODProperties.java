package com.pennant.eod.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.DPDBucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.DPDBucketDAO;
import com.pennant.backend.dao.applicationmaster.NPABucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.NPABucketDAO;
import com.pennant.backend.dao.applicationmaster.TakafulProviderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.util.FinanceConstants;

/**
 * This class stores the following application master details into static variables <br>
 * 1. RMTFinanceTypes <br>
 * 2. Rules <br>
 * 3. RMTCurrencies <br>
 * 4. SystemInternalAccountDef <br>
 * 5. TransactionEntries <br>
 * 
 * While accessing any of the member in this class maker sure that init() method should be invoked first.<br>
 * While accessing the any column in the above masters please check whether the column is exist's in the corresponding
 * query or not
 * 
 */

public class EODProperties {

	private static Map<String, FinanceType>						finananceTypesMap;
	private static Map<String, String>							rulesMap;
	private static Map<String, Currency>						currencyMap;
	private static Map<String, String>							internalAccMap;
	private static Map<String, TakafulProvider>					takafulProviders;
	private static Map<Long, List<TransactionEntry>>			transactionEntriesMap;
	//DPD
	private static Map<String, List<DPDBucketConfiguration>>	dpdBucketConfigurations;
	private static Map<Long, String>							dPDBuckets;
	//NPA
	private static Map<String, List<NPABucketConfiguration>>	npaBucketConfigurations;
	private static Map<Long, String>							npaBuckets;

	private FinanceTypeDAO										financeTypeDAO;
	private CurrencyDAO											currencyDAO;
	private RuleDAO												ruleDAO;
	private TransactionEntryDAO									transactionEntryDAO;
	private TakafulProviderDAO									takafulProviderDAO;
	//DPD
	private DPDBucketConfigurationDAO							dPDBucketConfigurationDAO;
	private DPDBucketDAO										dPDBucketDAO;
	//NPA
	private NPABucketConfigurationDAO							nPABucketConfigurationDAO;
	private NPABucketDAO										nPABucketDAO;

	private FinTypeAccountingDAO								finTypeAccountingDAO;

	public EODProperties() {
		super();
	}

	/**
	 * This method initialise all the static variable <br>
	 * 
	 */
	public void init() {
		List<FinanceType> finTypeList = getFinanceTypeDAO().getFinTypeDetailForBatch();
		List<Currency> ccyCodeList = getCurrencyDAO().getCurrencyList();
		List<Long> accountSetIdList = getTransactionEntryDAO().getAccountSetIds();
		List<TakafulProvider> listProviders = getTakafulProviderDAO().getTakafulProviders();
		List<DPDBucketConfiguration> bucketConfigurationList = dPDBucketConfigurationDAO.getDPDBucketConfigurations();
		List<DPDBucket> dpdBucketList = dPDBucketDAO.getDPDBuckets();
		List<NPABucketConfiguration> npaBucketConfigurationsList = nPABucketConfigurationDAO
				.getNPABucketConfigurations();

		List<NPABucket> npaBucketList = nPABucketDAO.getNPABuckets();

		finananceTypesMap = new HashMap<String, FinanceType>(finTypeList.size());
		currencyMap = new HashMap<String, Currency>(ccyCodeList.size());
		transactionEntriesMap = new HashMap<Long, List<TransactionEntry>>(accountSetIdList.size());
		dpdBucketConfigurations = new HashMap<String, List<DPDBucketConfiguration>>();
		dPDBuckets = new HashMap<Long, String>();
		npaBucketConfigurations = new HashMap<String, List<NPABucketConfiguration>>();
		npaBuckets = new HashMap<Long, String>();

		for (FinanceType type : finTypeList) {
			List<FinTypeAccounting> list = finTypeAccountingDAO.getFinTypeAccountingByFinType(type.getFinType(), FinanceConstants.MODULEID_FINTYPE);
			type.setFinTypeAccountingList(list);
			finananceTypesMap.put(type.getFinType().trim(), type);
		}

		for (Currency currency : ccyCodeList) {
			currencyMap.put(currency.getCcyCode(), currency);
		}

		for (Long accountSetId : accountSetIdList) {
			transactionEntriesMap
					.put(accountSetId, getTransactionEntryDAO().getListTranEntryForBatch(accountSetId, ""));
		}

		for (TakafulProvider takafulProvider : listProviders) {
			takafulProviders.put(takafulProvider.getTakafulCode(), takafulProvider);
		}

		for (DPDBucket bucket : dpdBucketList) {
			dPDBuckets.put(bucket.getBucketID(), bucket.getBucketCode());
		}

		for (DPDBucketConfiguration dpdBucketConfiguration : bucketConfigurationList) {
			String prductCode = dpdBucketConfiguration.getProductCode();
			if (dpdBucketConfigurations.containsKey(prductCode)) {
				dpdBucketConfigurations.get(prductCode).add(dpdBucketConfiguration);
			} else {
				List<DPDBucketConfiguration> list = new ArrayList<DPDBucketConfiguration>();
				list.add(dpdBucketConfiguration);
				dpdBucketConfigurations.put(prductCode, list);
			}
		}

		for (NPABucketConfiguration npaBucketConfiguration : npaBucketConfigurationsList) {
			String prductCode = npaBucketConfiguration.getProductCode();
			if (npaBucketConfigurations.containsKey(prductCode)) {
				npaBucketConfigurations.get(prductCode).add(npaBucketConfiguration);
			} else {
				List<NPABucketConfiguration> list = new ArrayList<NPABucketConfiguration>();
				list.add(npaBucketConfiguration);
				npaBucketConfigurations.put(prductCode, list);
			}
		}

		for (NPABucket bucket : npaBucketList) {
			npaBuckets.put(bucket.getBucketID(), bucket.getBucketCode());
		}

		finTypeList = null;
		ccyCodeList = null;
		accountSetIdList = null;
		listProviders = null;
		bucketConfigurationList = null;
		dpdBucketList = null;
		npaBucketConfigurationsList = null;
		npaBucketList = null;
	}

	/**
	 * Method for Fetching Finance type Object
	 * 
	 * @param finType
	 * @return
	 */
	public static FinanceType getFinanceType(String finType) {
		return finananceTypesMap.get(finType);
	}

	/**
	 * Method for Fetch SubHead Rule Code By SubHead Code In Transaction Entry
	 * 
	 * @param ruleCode
	 * @return
	 */
	public static String getSubHeadRule(String ruleCode) {
		return rulesMap.get(ruleCode);
	}

	/**
	 * Method for Fetching Currency Number by Currency Code
	 * 
	 * @param ccyCode
	 * @return
	 */
	public static String getCcyNumber(String ccyCode) {
		return currencyMap.get(ccyCode).getCcyNumber();

	}

	/**
	 * Method for Fetching Currency format by Currency Code
	 * 
	 * @param ccyCode
	 * @return
	 */
	public static int getCcyEditField(String ccyCode) {
		return currencyMap.get(ccyCode).getCcyEditField();

	}

	/**
	 * Method for Fetching SIA Number Number by SIA Code
	 * 
	 * @param ccyCode
	 * @return
	 */
	public static String getSIANumber(String sIACode) {
		return internalAccMap.get(sIACode);
	}

	/**
	 * Method for Fetching TakafulProvider by Code
	 * 
	 * @param code
	 * @return
	 */
	public static TakafulProvider getTakafulProvider(String code) {
		return takafulProviders.get(code);
	}

	/**
	 * Method for Fetching TransactionEntry by accountSetId
	 * 
	 * @param accountSetId
	 * @return List<TransactionEntry>
	 */
	public static List<TransactionEntry> getTransactionEntryList(Long accountSetId) {

		if (transactionEntriesMap.containsKey(accountSetId)) {
			return transactionEntriesMap.get(accountSetId);
		} else {
			return new ArrayList<TransactionEntry>();
		}
	}

	public static List<DPDBucketConfiguration> getBucketConfigurations(String productCode) {
		if (dpdBucketConfigurations.containsKey(productCode)) {
			return dpdBucketConfigurations.get(productCode);
		} else {
			return new ArrayList<DPDBucketConfiguration>();
		}
	}

	public static String getBucket(long bucketID) {
		return dPDBuckets.get(bucketID);
	}

	public static Long getBucketID(String bucketCode) {
		for (Entry<Long, String> entry : dPDBuckets.entrySet()) {
			if (StringUtils.equals(entry.getValue(), bucketCode)) {
				return entry.getKey();
			}

		}
		return new Long(0);
	}

	public static List<NPABucketConfiguration> getNPABucketConfigurations(String productCode) {
		if (npaBucketConfigurations.containsKey(productCode)) {
			return npaBucketConfigurations.get(productCode);
		} else {
			return new ArrayList<NPABucketConfiguration>();
		}
	}

	public static String getNPABucket(long bucketID) {
		return npaBuckets.get(bucketID);
	}

	public void destroy() {
		finananceTypesMap = null;
		rulesMap = null;
		currencyMap = null;
		internalAccMap = null;
		transactionEntriesMap = null;
		takafulProviders = null;
		dpdBucketConfigurations = null;
		dPDBuckets = null;
		npaBucketConfigurations = null;
		npaBuckets = null;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public TakafulProviderDAO getTakafulProviderDAO() {
		return takafulProviderDAO;
	}

	public void setTakafulProviderDAO(TakafulProviderDAO takafulProviderDAO) {
		this.takafulProviderDAO = takafulProviderDAO;
	}

	public void setDPDBucketConfigurationDAO(DPDBucketConfigurationDAO dPDBucketConfigurationDAO) {
		this.dPDBucketConfigurationDAO = dPDBucketConfigurationDAO;
	}

	public void setdPDBucketDAO(DPDBucketDAO dPDBucketDAO) {
		this.dPDBucketDAO = dPDBucketDAO;
	}

	public void setNPABucketConfigurationDAO(NPABucketConfigurationDAO nPABucketConfigurationDAO) {
		this.nPABucketConfigurationDAO = nPABucketConfigurationDAO;
	}

	public void setNPABucketDAO(NPABucketDAO nPABucketDAO) {
		this.nPABucketDAO = nPABucketDAO;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

}
