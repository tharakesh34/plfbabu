/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  RuleExecutionUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.script.ScriptEngine;

public class RuleExecutionUtil implements Serializable {
	private static final long serialVersionUID = -7634160175219913960L;
	private static final Logger logger = LogManager.getLogger(RuleExecutionUtil.class);

	public static final Map<String, ScriptEngine> EOD_SCRIPT_ENGINE_MAP = new HashMap<>();
	private boolean splRule = false;

	private RuleExecutionUtil() {
		super();
	}

	public static Object executeRule(String rule, Map<String, Object> dataMap, String finccy,
			RuleReturnType returnType) {
		if (dataMap == null) {
			dataMap = new HashMap<String, Object>();
		}

		rule = replaceCurrencyCode(rule, finccy);
		rule = StringUtils.replace(rule, "{BLANK}", "");
		Object result = null;

		try (ScriptEngine scriptEngine = getScriptEngine()) {
			switch (returnType) {
			case DECIMAL:
				result = scriptEngine.getResultAsBigDecimal(rule, dataMap);
				break;
			case RATE:
				result = scriptEngine.getResultAsBigDecimal(rule, dataMap);
				break;
			case OBJECT:
				if (returnType == RuleReturnType.OBJECT) {
					RuleResult ruleResult = new RuleResult();
					dataMap.put("result", ruleResult);
					scriptEngine.getResultAsObject(rule, dataMap);
					result = ruleResult;
				} else {
					result = scriptEngine.getResultAsObject(rule, dataMap);
				}
				break;
			case STRING:
			case CALCSTRING:
				result = scriptEngine.getResultAsString(rule, dataMap);
				break;
			case INTEGER:
				result = scriptEngine.getResultAsInt(rule, dataMap);
				break;
			case BOOLEAN:
				result = scriptEngine.getResultAsBoolean(rule, dataMap);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		return result;
	}

	public static String replaceCurrencyCode(String rule, String finccy) {
		String rulefieldCcy = RuleConstants.RULEFIELD_CCY;

		Pattern pattern = Pattern.compile(rulefieldCcy + "[A-Z]{3}[0-9]+");
		Matcher matcher = pattern.matcher(rule);
		String group = null;

		while (matcher.find()) {
			group = matcher.group(0);
			String fromCcy = group.replace(rulefieldCcy, "").substring(0, 3);
			long amount = Long.parseLong(group.replace(rulefieldCcy, "").replace(fromCcy, ""));
			BigDecimal ruleValue = new BigDecimal(amount * Math.pow(10, CurrencyUtil.getFormat(fromCcy)));
			String convRuleValue = CalculationUtil.convertedUnFormatAmount(fromCcy, finccy, ruleValue);

			rule = rule.replace(group, convRuleValue);
			matcher = pattern.matcher(rule);
		}

		return rule;
	}

	public static void setExecutionMap(String field, List<Object> objects, Map<String, Object> map) {
		if (field != null) {
			Map<String, Object> objectsMap = getBeanMap(objects);
			Object value = fetchBeanValue(field, objectsMap.get(field.split("_")[0]));
			map.put(field, value);
		}
	}

	public static BigDecimal getRuleResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
		logger.debug(Literal.ENTERING);
		BigDecimal result = BigDecimal.ZERO;

		try {
			Object exereslut = executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.ENTERING);
		return result;
	}

	private static Map<String, Object> getBeanMap(List<Object> objects) {
		Map<String, Object> objectsMap = new HashMap<String, Object>();

		if (objects == null) {
			return objectsMap;
		}

		for (Object object : objects) {
			switch (object.getClass().getSimpleName()) {
			case RuleConstants.fm:
				objectsMap.put(RuleConstants.financeMain, object);
				break;
			case RuleConstants.ft:
				objectsMap.put(RuleConstants.financeType, object);
				break;
			case RuleConstants.cust:
				objectsMap.put(RuleConstants.customer, object);
				break;
			case RuleConstants.custEmp:
				objectsMap.put(RuleConstants.custEmployeeDetail, object);
				break;
			}
		}
		return objectsMap;
	}

	private static Object fetchBeanValue(String field, Object object) {
		if (field.split("_").length > 1) {
			String suffix = field.split("_")[1];
			String methodName = "get" + suffix.substring(0, 1).toUpperCase() + suffix.substring(1);
			try {
				return object.getClass().getMethod(methodName).invoke(object);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		return null;
	}

	private static ScriptEngine getScriptEngine() {
		String threadName = Thread.currentThread().getName();

		if (threadName.startsWith("PLF_EOD_THREAD_")) {
			return EOD_SCRIPT_ENGINE_MAP.computeIfAbsent(threadName, abc -> getScriptEngine(threadName, true));
		}

		return getScriptEngine(threadName);
	}

	private static ScriptEngine getScriptEngine(String threadName) {
		return new ScriptEngine();
	}

	private static ScriptEngine getScriptEngine(String threadName, boolean isEOD) {
		return new ScriptEngine(isEOD);
	}

	public boolean isSplRule() {
		return splRule;
	}

	public void setSplRule(boolean splRule) {
		this.splRule = splRule;
	}

}
