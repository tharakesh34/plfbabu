package com.pennant.eod.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ValueLabel;
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

	private static Map<String, FinanceType> finananceTypesMap;
	private static Map<String, String> rulesMap;
	private static Map<String, String> currencyMap;
	private static Map<String, String> internalAccMap;	
	private static Map<Long, List<TransactionEntry>> transactionEntriesMap;	
	
	private FinanceTypeDAO financeTypeDAO;
	private CurrencyDAO currencyDAO;
	private SystemInternalAccountDefinitionDAO definitionDAO;
	private RuleDAO ruleDAO;
	private TransactionEntryDAO transactionEntryDAO;
	
	/**
	 * This method initialise all the static variable  <br>
	 * 
	 */
	public void init() {
		List<FinanceType> finTypeList = getFinanceTypeDAO().getFinTypeDetailForBatch();
		List<ValueLabel> ruleList = getRuleDAO().getSubHeadAmountRule();
		List<ValueLabel> ccyCodeList = getCurrencyDAO().getCcyCodesByFinRef();
		List<ValueLabel> siaNumList = getDefinitionDAO().getEntrySIANumDetails();
		List<Long> accountSetIdList = getTransactionEntryDAO().getAccountSetIds();

		finananceTypesMap = new HashMap<String, FinanceType>(finTypeList.size());
		rulesMap = new HashMap<String, String>(ruleList.size());
		currencyMap = new HashMap<String, String>(ccyCodeList.size());
		internalAccMap = new HashMap<String, String>(siaNumList.size());
		transactionEntriesMap = new HashMap<Long,  List<TransactionEntry>>(accountSetIdList.size());

		for (FinanceType type : finTypeList) {
			finananceTypesMap.put(type.getFinType().trim(), type);
		}

		for (ValueLabel type : ruleList) {
			rulesMap.put(type.getLabel().trim(), type.getValue());
		}

		for (ValueLabel valueLabel : ccyCodeList) {
			currencyMap.put(valueLabel.getLabel(), valueLabel.getValue());
		}


		for (ValueLabel valueLabel : siaNumList) {
			internalAccMap.put(valueLabel.getLabel(), valueLabel.getValue());
		}
		
		for(Long accountSetId : accountSetIdList) {
			transactionEntriesMap.put(accountSetId, getTransactionEntryDAO().getListTranEntryForBatch(accountSetId, ""));
		}

		finTypeList = null;	
		ruleList = null;
		ccyCodeList = null;
		siaNumList = null;
		accountSetIdList = null;
	}

	/**
	 * Method for Fetching Finance type Object
	 * @param finType
	 * @return
	 */
	public static FinanceType getFinanceType(String finType) {
		return finananceTypesMap.get(finType);		
	}

	/**
	 * Method for Fetch SubHead Rule Code By SubHead Code In Transaction Entry
	 * @param ruleCode
	 * @return
	 */
	public static String getSubHeadRule(String ruleCode) {		
		return rulesMap.get(ruleCode);
	}

	/**
	 * Method for Fetching Currency Number by Currency Code
	 * @param ccyCode
	 * @return
	 */
	public static String getCcyNumber(String ccyCode) {
		return currencyMap.get(ccyCode);
		
	}

	/**
	 * Method for Fetching SIA Number Number by SIA Code
	 * @param ccyCode
	 * @return
	 */
	public static String getSIANumber(String SIACode) {
		return internalAccMap.get(SIACode);
	}
	
	/**
	 * Method for Fetching TransactionEntry by accountSetId
	 * @param accountSetId
	 * @return List<TransactionEntry>
	 */
	public static List<TransactionEntry> getTransactionEntryList(Long accountSetId) {
		
		if(transactionEntriesMap.containsKey(accountSetId)) {
			return transactionEntriesMap.get(accountSetId);
		} else {
			return new ArrayList<TransactionEntry>();
		}
	}
	
	public void destroy() {
		finananceTypesMap = null;
		rulesMap = null;
		currencyMap = null;
		internalAccMap = null;
		transactionEntriesMap = null;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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


	
}
