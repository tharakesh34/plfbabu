package com.pennant.cache.util;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;

public class AccountingSetCache {

	private static FinTypeAccountingDAO finTypeAccountingDAO;
	private static TransactionEntryDAO transactionEntryDAO;
	private static RuleDAO ruleDAO;
	private final static Logger logger = Logger
			.getLogger(AccountingSetCache.class);

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

	public static long getAccountSetID(String finType, String event,
			int moduleId) {
		long accountSetID = Long.MIN_VALUE;
		String accountSetKey = finType + "@" + event + "@" + moduleId;
		try {
			accountSetID = finTypeAccountCache.get(accountSetKey);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from  Finance Type Accounting cache: ",e);
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
			logger.warn("Error clearing data from Finance Type Accounting cache: ",ex);
		}
	}

	/**
	 * @param accountSetid
	 * @return List of TransactionEntry for the Account Set
	 */
	public static List<TransactionEntry> getTransactionEntry(long accountSetid) {
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
		Rule rule;
		String ruleKey = ruleCode + "@" + ruleModule + "@" + ruleEvent;
		try {
			rule = ruleCache.get(ruleKey);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Rule  cache: ", e);
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
		AccountingSetCache.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public static TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public static void setTransactionEntryDAO(
			TransactionEntryDAO transactionEntryDAO) {
		AccountingSetCache.transactionEntryDAO = transactionEntryDAO;
	}

	public static RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public static void setRuleDAO(RuleDAO ruleDAO) {
		AccountingSetCache.ruleDAO = ruleDAO;
	}

}
