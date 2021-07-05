package com.pennant.pff.eod.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.rulefactory.Rule;

public class RuleConfigCache {
	private static final Logger logger = LogManager.getLogger(RuleConfigCache.class);
	private static RuleDAO ruleDAO;

	public static String getCacheRuleCode(String id, String module, String event) {
		String sqlRule = null;
		String code = id + "#" + module + "#" + event;
		try {
			sqlRule = sqlRuleCache.get(code);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			sqlRule = getRuleByCode(code);
		}
		return sqlRule;
	}

	public static Rule getCacheRule(long id) {
		Rule rule = null;
		try {
			rule = ruleObjCache.get(id);
		} catch (ExecutionException e) {
			logger.warn("Unable to load data from Rule cache: ", e);
			rule = getRuleById(id);
		}
		return rule;
	}

	private static LoadingCache<String, String> sqlRuleCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
				@Override
				public String load(String id) throws Exception {
					return getRuleByCode(id);
				}
			});

	private static String getRuleByCode(String id) {
		String[] split = id.split("#");
		return ruleDAO.getAmountRule(split[0], split[1], split[2]);
	}

	private static LoadingCache<Long, Rule> ruleObjCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<Long, Rule>() {
				@Override
				public Rule load(Long id) throws Exception {
					return getRuleById(id);
				}
			});

	private static Rule getRuleById(Long id) {
		return ruleDAO.getRuleByID(id, "");
	}

	public static void clearSqlRuleCache(String ruleCode, String ruleModule, String ruleEvent) {
		String code = ruleCode + "#" + ruleModule + "#" + ruleEvent;
		try {
			sqlRuleCache.invalidate(code);
		} catch (Exception ex) {
			logger.warn("Error clearing data from Rule cache: ", ex);
		}
	}

	public static void invalidateAll() {
		try {
			sqlRuleCache.invalidateAll();
			sqlRuleCache.invalidateAll();
		} catch (Exception ex) {
			logger.warn("Error clearing data from Rule cache: ", ex);
		}
	}

	public static void setRuleDAO(RuleDAO ruleDAO) {
		RuleConfigCache.ruleDAO = ruleDAO;
	}

}
