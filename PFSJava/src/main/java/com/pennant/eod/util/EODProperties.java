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
import com.pennant.backend.dao.applicationmaster.TakafulProviderDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;

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
	private static Map<String, List<DPDBucketConfiguration>>	bucketConfigurations;
	private static Map<Long, String>							dPDBuckets;

	private FinanceTypeDAO										financeTypeDAO;
	private CurrencyDAO											currencyDAO;
	private SystemInternalAccountDefinitionDAO					definitionDAO;
	private RuleDAO												ruleDAO;
	private TransactionEntryDAO									transactionEntryDAO;
	private TakafulProviderDAO									takafulProviderDAO;
	private DPDBucketConfigurationDAO							dPDBucketConfigurationDAO;
	private DPDBucketDAO										dPDBucketDAO;

	public EODProperties() {
		super();
	}

	/**
	 * This method initialise all the static variable <br>
	 * 
	 */
	public void init() {
		List<FinanceType> finTypeList = getFinanceTypeDAO().getFinTypeDetailForBatch();
		List<ValueLabel> ruleList = getRuleDAO().getSubHeadAmountRule();
		List<Currency> ccyCodeList = getCurrencyDAO().getCurrencyList();
		List<ValueLabel> siaNumList = getDefinitionDAO().getEntrySIANumDetails();
		List<Long> accountSetIdList = getTransactionEntryDAO().getAccountSetIds();
		List<TakafulProvider> listProviders = getTakafulProviderDAO().getTakafulProviders();
		List<DPDBucketConfiguration> bucketConfigurationList = dPDBucketConfigurationDAO.getDPDBucketConfigurations();
		List<DPDBucket> dpdBucketList = dPDBucketDAO.getDPDBuckets();

		finananceTypesMap = new HashMap<String, FinanceType>(finTypeList.size());
		rulesMap = new HashMap<String, String>(ruleList.size());
		currencyMap = new HashMap<String, Currency>(ccyCodeList.size());
		internalAccMap = new HashMap<String, String>(siaNumList.size());
		takafulProviders = new HashMap<String, TakafulProvider>(siaNumList.size());
		transactionEntriesMap = new HashMap<Long, List<TransactionEntry>>(accountSetIdList.size());
		bucketConfigurations=new HashMap<String, List<DPDBucketConfiguration>>();
		dPDBuckets=new HashMap<Long, String>();
		
		for (FinanceType type : finTypeList) {
			finananceTypesMap.put(type.getFinType().trim(), type);
		}

		for (ValueLabel type : ruleList) {
			rulesMap.put(type.getLabel().trim(), type.getValue());
		}

		for (Currency currency : ccyCodeList) {
			currencyMap.put(currency.getCcyCode(), currency);
		}

		for (ValueLabel valueLabel : siaNumList) {
			internalAccMap.put(valueLabel.getLabel(), valueLabel.getValue());
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

		for (DPDBucketConfiguration bucketConfiguration : bucketConfigurationList) {
			String prductCode = bucketConfiguration.getProductCode();
			if (bucketConfigurations.containsKey(prductCode)) {
				bucketConfigurations.get(prductCode).add(bucketConfiguration);
			} else {
				List<DPDBucketConfiguration> list = new ArrayList<DPDBucketConfiguration>();
				list.add(bucketConfiguration);
				bucketConfigurations.put(prductCode, list);
			}
		}

		finTypeList = null;
		ruleList = null;
		ccyCodeList = null;
		siaNumList = null;
		accountSetIdList = null;
		listProviders = null;
		bucketConfigurationList = null;
		dPDBuckets = null;
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
		if (bucketConfigurations.containsKey(productCode)) {
			return bucketConfigurations.get(productCode);
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

	public void destroy() {
		finananceTypesMap = null;
		rulesMap = null;
		currencyMap = null;
		internalAccMap = null;
		transactionEntriesMap = null;
		takafulProviders = null;
		bucketConfigurations = null;
		dPDBuckets = null;
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

	public SystemInternalAccountDefinitionDAO getDefinitionDAO() {
		return definitionDAO;
	}

	public void setDefinitionDAO(SystemInternalAccountDefinitionDAO definitionDAO) {
		this.definitionDAO = definitionDAO;
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

}
