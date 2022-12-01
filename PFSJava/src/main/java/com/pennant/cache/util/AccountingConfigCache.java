package com.pennant.cache.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;

/**
 * @author pasvarma
 *
 */
public class AccountingConfigCache {

	private static AccountTypeDAO accountTypeDAO;

	private static FinTypeAccountingDAO finTypeAccountingDAO;
	private static TransactionEntryDAO transactionEntryDAO;
	private static RuleDAO ruleDAO;
	private static AccountMappingDAO accountMappingDAO;

	private static final Logger logger = LogManager.getLogger(AccountingConfigCache.class);

	private static LoadingCache<String, String> accountMappingCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
				@Override
				public String load(String account) throws Exception {
					return getAccountMappingByAccount(account);
				}
			});

	private static LoadingCache<String, AccountType> accountTypeCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, AccountType>() {
				@Override
				public AccountType load(String acType) throws Exception {
					return getAccountTypeById(acType);
				}
			});

	private static LoadingCache<String, Long> finTypeAccountCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, Long>() {
				@Override
				public Long load(String accountSetKey) throws Exception {
					return getAccountSetID(accountSetKey);
				}
			});

	private static LoadingCache<Long, List<TransactionEntry>> transactionEntryCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<Long, List<TransactionEntry>>() {
				@Override
				public List<TransactionEntry> load(Long accountSetid) throws Exception {
					return getTransactionEntryForBatch(accountSetid);
				}
			});

	private static LoadingCache<String, Rule> ruleCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, Rule>() {
				@Override
				public Rule load(String ruleKey) throws Exception {
					return getRule(ruleKey);
				}
			});

	private static String getAccountMappingByAccount(String account) {
		return accountMappingDAO.getAccountMappingByAccount(account);
	}

	private static AccountType getAccountTypeById(String acType) {
		return accountTypeDAO.getAccountTypeById(acType, "");
	}

	private static Long getAccountSetID(String accountSetKey) {
		String[] parmList = accountSetKey.split("@");
		return finTypeAccountingDAO.getAccountSetID(parmList[0], parmList[1], Integer.parseInt(parmList[2]));
	}

	private static List<TransactionEntry> getTransactionEntryForBatch(long accountSetid) {
		return transactionEntryDAO.getListTranEntryForBatch(accountSetid, "");
	}

	private static Rule getRule(String ruleKey) {
		String[] parmList = ruleKey.split("@");
		return ruleDAO.getRuleByID(parmList[0], parmList[1], parmList[2], "");
	}

	/**
	 * @param String acType
	 * @return AccountType
	 */
	public static AccountType getAccountType(String acType) {
		// AccountType accountType;
		//
		// try {
		// accountType = accountTypeCache.get(acType);
		// } catch (ExecutionException e) {
		// logger.warn("Unable to load data from Finance Type Accounting cache: ",e);
		// accountType = getAccountTypeById(acType);
		// }
		return getAccountTypeById(acType);
	}

	/**
	 * It Clear Account Type Accounting data from cache .
	 * 
	 * @param acType
	 * 
	 */

	public static void clearAccountTypeCache(String acType) {
		try {
			finTypeAccountCache.invalidate(acType);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Account type cache: ", ex);
		}
	}

	/**
	 * @param finType
	 * @param event
	 * @param moduleId
	 * @return Long Account setID
	 */
	public static Long getAccountSetID(String finType, String event, int moduleId) {
		return finTypeAccountingDAO.getAccountSetID(finType, event, moduleId);
	}

	/**
	 * @param finType
	 * @param event
	 * @param moduleId
	 * @return Long Account setID
	 */
	public static Long getCacheAccountSetID(String finType, String event, int moduleId) {
		String accountSetKey = finType + "@" + event + "@" + moduleId;
		Long accountSetID = Long.MIN_VALUE;
		try {
			accountSetID = finTypeAccountCache.get(accountSetKey);
		} catch (Exception e) {
			logger.warn("Unable to load data from  Account Type cache: ", e);
			accountSetID = getAccountSetID(accountSetKey);
		}
		return accountSetID;
	}

	/**
	 * It Clear Finance Type Accounting data from cache .
	 * 
	 * @param finType
	 * @param event
	 * @param moduleId
	 */
	public static void clearAccountSetCache(String finType, String event, int moduleId) {
		String accountSetKey = finType + "@" + event + "@" + moduleId;
		try {
			finTypeAccountCache.invalidate(accountSetKey);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Accounting Set cache: ", ex);
		}
	}

	/**
	 * @param accountSetid
	 * @return List of TransactionEntry for the Account Set
	 */
	public static List<TransactionEntry> getTransactionEntry(long accountSetid) {
		return transactionEntryDAO.getListTranEntryForBatch(accountSetid, "");
	}

	/**
	 * @param accountSetid
	 * @return List of TransactionEntry for the Account Set
	 */
	public static List<TransactionEntry> getCacheTransactionEntry(long accountSetid) {
		List<TransactionEntry> transactionEntries;
		try {
			transactionEntries = transactionEntryCache.get(accountSetid);
		} catch (Exception e) {
			logger.warn("Unable to load data from Transaction Entry cache: ", e);
			transactionEntries = transactionEntryDAO.getListTranEntryForBatch(accountSetid, "");
		}
		return transactionEntries;
	}

	/**
	 * It Clear TransactionEntry data from cache .
	 * 
	 * @param accountSetid
	 */
	public static void clearTransactionEntryCache(long accountSetid) {
		try {
			transactionEntryCache.invalidate(accountSetid);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Transaction Entry Cache: ", ex);
		}
	}

	/**
	 * @param ruleCode
	 * @param ruleModule
	 * @param ruleEvent
	 * @return Rule
	 */
	public static Rule getRule(String ruleCode, String ruleModule, String ruleEvent) {
		return ruleDAO.getRuleByID(ruleCode, ruleModule, ruleEvent, "");
	}

	/**
	 * @param ruleCode
	 * @param ruleModule
	 * @param ruleEvent
	 * @return Rule
	 */
	public static Rule getCacheRule(String ruleCode, String ruleModule, String ruleEvent) {
		String ruleKey = ruleCode + "@" + ruleModule + "@" + ruleEvent;
		Rule rule;
		try {
			rule = ruleCache.get(ruleKey);
		} catch (Exception e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			rule = ruleDAO.getRuleByID(ruleCode, ruleModule, ruleEvent, "");
		}
		return rule;
	}

	/**
	 * It Clear Rule From Cache .
	 * 
	 * @param ruleCode
	 * @param ruleModule
	 * @param ruleEvent
	 */
	public static void clearRuleCache(String ruleCode, String ruleModule, String ruleEvent) {
		String ruleKey = ruleCode + "@" + ruleModule + "@" + ruleEvent;
		try {
			ruleCache.invalidate(ruleKey);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Rule cache: ", ex);
		}

	}

	public static void clearAccountMappingCache(String account) {
		try {
			accountMappingCache.invalidate(account);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Account type cache: ", ex);
		}
	}

	public static String getCacheAccountMapping(String account) {
		try {
			return accountMappingCache.get(account);
		} catch (Exception e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			return accountMappingDAO.getAccountMappingByAccount(account);
		}
	}

	public static String getAccountMapping(String account) {
		return accountMappingDAO.getAccountMappingByAccount(account);
	}

	public static void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		AccountingConfigCache.accountTypeDAO = accountTypeDAO;
	}

	/**
	 * @param finTypeAccountingDAO the finTypeAccountingDAO to set
	 */
	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		AccountingConfigCache.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public static void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		AccountingConfigCache.transactionEntryDAO = transactionEntryDAO;
	}

	public static void setRuleDAO(RuleDAO ruleDAO) {
		AccountingConfigCache.ruleDAO = ruleDAO;
	}

	public static void setAccountMappingDAO(AccountMappingDAO accountMappingDAO) {
		AccountingConfigCache.accountMappingDAO = accountMappingDAO;
	}

}
