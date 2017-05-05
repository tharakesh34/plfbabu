package com.pennant.cache.util;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.impl.TransactionEntryDAOImpl;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;

public class AccountingSetCache {

		private static FinTypeAccountingDAO finTypeAccountingDAO;
		private static TransactionEntryDAOImpl transactionEntryDAOImpl;
		private static RuleDAO ruleDAO ;
		private final static Logger logger = Logger.getLogger(AccountingSetCache.class);
		
		private static LoadingCache<String, Long> finTypeAccountCache = CacheBuilder.newBuilder().
				expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, Long>() {
					@Override
					public Long load(String accountSetKey) throws Exception {
						return getAccountSetID(accountSetKey);
					}
				});
		
		private static long getAccountSetID(String accountSetKey) {
			String[] parmList = accountSetKey.split("@");
			return getFinTypeAccountingDAO().getAccountSetID(parmList[0], parmList[1], Integer.parseInt(parmList[2]));
		}
		
	public static long getAccountSetID(String finType, String event, int moduleId){
		long accountSetID = Long.MIN_VALUE;
		String accountSetKey = finType+"@"+event+"@"+moduleId;
		
		try {
			accountSetID = finTypeAccountCache.get(accountSetKey);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from  Finance Type Accounting cache: ", e);
			accountSetID = getAccountSetID(accountSetKey);
		}
		return accountSetID;
	}

		/**
		 * It Clear  Finance Type Accounting data from cache .
		 * @param finType
		 * @param event
		 * @param moduleId
		 */
		public static void clearAccountSetCache(String finType, String event, int moduleId) {
			String accountSetKey = finType+"@"+event+"@"+moduleId;
			try {
				finTypeAccountCache.invalidate(accountSetKey);
			} catch (Exception ex) {
				logger.warn("Error clearing data from Finance Type Accounting cache: ", ex);
			}
		}
	
		
		/**
		 * @param accountSetid
		 * @return List of TransactionEntry for the Account Set
		 */
		public static List<TransactionEntry> getTransactionEntry(long accountSetid){
			return getTransactionEntryDAOImpl().getListTranEntryForBatch(accountSetid,"");
		}		
		
		/**
		 * It Clear  TransactionEntry data from cache .
		 * @param finType
		 * @param event
		 * @param moduleId
		 */
		public static void clearTransactionEntryCache(long accountSetid) {
			// TODO TO BE IMPLEMENT
		}


		/**
		 * @param ruleCode
		 * @param ruleModule
		 * @param ruleEvent
		 * @return Rule
		 */
		public static Rule getTransactionEntry(String ruleCode,String ruleModule,String ruleEvent){
			return getRuleDAO().getRuleByID(ruleCode, ruleModule, ruleEvent, "");
		}		
		
		/**
		 * It Clear  RuleCache cache .
		 * @param ruleCode
		 * @param ruleModule
		 * @param ruleEvent
		 */
		public static void clearRuleCache(String ruleCode,String ruleModule,String ruleEvent) {
			// TODO TO BE IMPLEMENT
		}


	/**
	 * @return the finTypeAccountingDAO
	 */
	public static FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	/**
	 * @param finTypeAccountingDAO the finTypeAccountingDAO to set
	 */
	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		AccountingSetCache.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public static TransactionEntryDAOImpl getTransactionEntryDAOImpl() {
		return transactionEntryDAOImpl;
	}

	public static void setTransactionEntryDAOImpl(
			TransactionEntryDAOImpl transactionEntryDAOImpl) {
		AccountingSetCache.transactionEntryDAOImpl = transactionEntryDAOImpl;
	}

	public static RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public static void setRuleDAO(RuleDAO ruleDAO) {
		AccountingSetCache.ruleDAO = ruleDAO;
	}
	


}
