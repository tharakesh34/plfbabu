package com.pennant.cache.util;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
	private static final Logger logger = Logger
			.getLogger(AccountingConfigCache.class);

	private static LoadingCache<String, AccountType> accountTypeCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, AccountType>() {
				@Override
				public AccountType load(String acType) throws Exception {
					return getAccountTypeById(acType);
				}
			});

	private static LoadingCache<String, Long> finTypeAccountCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, Long>() {
				@Override
				public Long load(String accountSetKey) throws Exception {
					return getAccountSetID(accountSetKey);
				}
			});

	private static LoadingCache<Long, List<TransactionEntry>> transactionEntryCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<Long, List<TransactionEntry>>() {
				@Override
				public List<TransactionEntry> load(Long accountSetid)
						throws Exception {
					return getTransactionEntryForBatch(accountSetid);
				}
			});

	private static LoadingCache<String, Rule> ruleCache = CacheBuilder
			.newBuilder().expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String, Rule>() {
				@Override
				public Rule load(String ruleKey) throws Exception {
					return getRule(ruleKey);
				}
			});

	private static AccountType getAccountTypeById(String acType) {
		return getAccountTypeDAO().getAccountTypeById(acType, "");
	}

	private static long getAccountSetID(String accountSetKey) {
		String[] parmList = accountSetKey.split("@");
		return getFinTypeAccountingDAO().getAccountSetID(parmList[0],
				parmList[1], Integer.parseInt(parmList[2]));
	}

	private static List<TransactionEntry> getTransactionEntryForBatch(
			long accountSetid) {
		return getTransactionEntryDAO().getListTranEntryForBatch(
				accountSetid, "");
	}

	private static Rule getRule(String ruleKey) {
		String[] parmList = ruleKey.split("@");
		return getRuleDAO().getRuleByID(parmList[0], parmList[1], parmList[2],
				"");
	}

	/**
	 * @param String acType
	 * @return AccountType
	 */
	public static AccountType getAccountType(String acType) {
//		AccountType accountType;
//
//		try {
//			accountType = accountTypeCache.get(acType);
//		} catch (ExecutionException e) {
//			logger.warn("Unable to load data from  Finance Type Accounting cache: ",e);
//			accountType = getAccountTypeById(acType);
//		}
		return  getAccountTypeById(acType);
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
			logger.warn("Error clearing data from Account type cache: ",ex);
		}
	}
	
	/**
	 * @param finType
	 * @param event
	 * @param moduleId
	 * @return Long Account setID
	 */
	public static long getAccountSetID(String finType, String event,
			int moduleId) {
		return getFinTypeAccountingDAO().getAccountSetID(finType,
				event, moduleId);
	}
	
	/**
	 * @param finType
	 * @param event
	 * @param moduleId
	 * @return Long Account setID
	 */
	public static long getCacheAccountSetID(String finType, String event,
			int moduleId) {
		String accountSetKey = finType + "@" + event + "@" + moduleId;
		long accountSetID = Long.MIN_VALUE;
		try {
			accountSetID = finTypeAccountCache.get(accountSetKey);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from  Account Type cache: ",e);
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
	public static void clearAccountSetCache(String finType, String event,
			int moduleId) {
		String accountSetKey = finType + "@" + event + "@" + moduleId;
		try {
			finTypeAccountCache.invalidate(accountSetKey);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Accounting Set cache: ",ex);
		}
	}

	/**
	 * @param accountSetid
	 * @return List of TransactionEntry for the Account Set
	 */
	public static List<TransactionEntry> getTransactionEntry(long accountSetid) {
		return getTransactionEntryDAO()
				.getListTranEntryForBatch(accountSetid, "");
	}
	
	/**
	 * @param accountSetid
	 * @return List of TransactionEntry for the Account Set
	 */
	public static List<TransactionEntry> getCacheTransactionEntry(long accountSetid) {
		List<TransactionEntry> transactionEntries;
		try {
			transactionEntries = transactionEntryCache.get(accountSetid);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Transaction Entry cache: ", e);
			transactionEntries = getTransactionEntryDAO()
					.getListTranEntryForBatch(accountSetid, "");
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
			logger.warn("Error clearing data from Transaction Entry Cache: ",ex);
		}
	}

	/**
	 * @param ruleCode
	 * @param ruleModule
	 * @param ruleEvent
	 * @return Rule
	 */
	public static Rule getRule(String ruleCode, String ruleModule,
			String ruleEvent) {
		return getRuleDAO()
				.getRuleByID(ruleCode, ruleModule, ruleEvent, "");
	}
	/**
	 * @param ruleCode
	 * @param ruleModule
	 * @param ruleEvent
	 * @return Rule
	 */
	public static Rule getCacheRule(String ruleCode, String ruleModule,
			String ruleEvent) {
		String ruleKey = ruleCode + "@" + ruleModule + "@" + ruleEvent;
		Rule rule;
		try {
			rule = ruleCache.get(ruleKey);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			rule = getRuleDAO()
					.getRuleByID(ruleCode, ruleModule, ruleEvent, "");
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
	public static void clearRuleCache(String ruleCode, String ruleModule,
			String ruleEvent) {
		String ruleKey = ruleCode + "@" + ruleModule + "@" + ruleEvent;
		try {
			ruleCache.invalidate(ruleKey);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Rule cache: ",ex);
		}
		
	}

	public static AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

	public static void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		AccountingConfigCache.accountTypeDAO = accountTypeDAO;
	}

	/**
	 * @return the finTypeAccountingDAO
	 */
	public static FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	/**
	 * @param finTypeAccountingDAO
	 *            the finTypeAccountingDAO to set
	 */
	public void setFinTypeAccountingDAO(
			FinTypeAccountingDAO finTypeAccountingDAO) {
		AccountingConfigCache.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public static TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public static void setTransactionEntryDAO(
			TransactionEntryDAO transactionEntryDAO) {
		AccountingConfigCache.transactionEntryDAO = transactionEntryDAO;
	}

	public static RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public static void setRuleDAO(RuleDAO ruleDAO) {
		AccountingConfigCache.ruleDAO = ruleDAO;
	}

}
