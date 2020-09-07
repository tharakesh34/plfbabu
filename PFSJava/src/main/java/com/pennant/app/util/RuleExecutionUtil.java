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
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.script.ScriptEngine;
import com.pennanttech.pennapps.service.GlobalVariableService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RuleExecutionUtil implements Serializable {
	private static final long serialVersionUID = -7634160175219913960L;
	private static final Logger logger = Logger.getLogger(RuleExecutionUtil.class);
	public static final Map<String, ScriptEngine> EOD_SCRIPT_ENGINE_MAP = new HashMap<>();

	private transient GlobalVariableService globalVariableService;

	/**
	 * default constructor.<br>
	 */
	public RuleExecutionUtil() {
		super();
	}

	public String getGlobalVariables(String templateStr, List<GlobalVariable> globalList) {
		StringWriter result = new StringWriter();
		Configuration cfg = new Configuration();
		LinkedHashMap<String, String> root = new LinkedHashMap<>();

		try {

			for (GlobalVariable globalVariable : globalList) {
				String str = (globalVariable.getName()).substring(2, (globalVariable.getName()).length() - 1);
				root.put(str, "(" + globalVariable.getValue() + ")");
			}

			Template t1 = new Template("RuleReplacement", new StringReader(templateStr), cfg);
			t1.process(root, result);

			Template t = new Template("RuleReplacement", new StringReader(result.getBuffer().toString()), cfg);
			result = new StringWriter();
			t.process(root, result);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return result.getBuffer().toString();
	}
	
	private ScriptEngine getScriptEngine() {
		String threadName = Thread.currentThread().getName();

		if (threadName.startsWith("PLF_EOD_THREAD_")) {
			return EOD_SCRIPT_ENGINE_MAP.computeIfAbsent(threadName, abc -> getScriptEngine(threadName));
		}

		return getScriptEngine(threadName);
	}

	private ScriptEngine getScriptEngine(String threadName) {
		return new ScriptEngine();
	}

	public Object executeRule(String rule, Map<String, Object> dataMap, String finccy, RuleReturnType returnType) {
		logger.debug(Literal.ENTERING);
		if (dataMap == null) {
			dataMap = new HashMap<String, Object>();
		}

		rule = replaceCurrencyCode(rule, finccy);
		rule = StringUtils.replace(rule, "{BLANK}", "");
		Object result = null;

		List<GlobalVariable> globalVariables = globalVariableService.getGlobalVariables();
		if (globalVariables != null && globalVariables.size() > 0) {
			rule = getGlobalVariables(rule, globalVariables);
		}

		try (ScriptEngine scriptEngine = getScriptEngine()) {
			switch (returnType) {
			case DECIMAL:
				result = scriptEngine.getResultAsBigDecimal(rule, dataMap);
				break;
			case OBJECT:
				if (returnType == RuleReturnType.OBJECT) {
					RuleResult ruleResult = new RuleResult();
					dataMap.put("result", ruleResult);
					scriptEngine.getResultAsObject(rule, dataMap);
					result=ruleResult;
				}else {
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
			logger.debug(Literal.EXCEPTION);
		}
		logger.debug(Literal.LEAVING);
		return result;
	}

	/**
	 * @param rule
	 * @param finccy
	 * @return
	 */
	public String replaceCurrencyCode(String rule, String finccy) {
		Pattern pattern = Pattern.compile(RuleConstants.RULEFIELD_CCY + "[A-Z]{3}[0-9]+");
		Matcher matcher = pattern.matcher(rule);
		String group = null;

		while (matcher.find()) {
			group = matcher.group(0);
			String fromCcy = group.replace(RuleConstants.RULEFIELD_CCY, "").substring(0, 3);
			long amount = Long.parseLong(group.replace(RuleConstants.RULEFIELD_CCY, "").replace(fromCcy, ""));
			BigDecimal ruleValue = new BigDecimal(amount * Math.pow(10, CurrencyUtil.getFormat(fromCcy)));
			String convRuleValue = CalculationUtil.convertedUnFormatAmount(fromCcy, finccy, ruleValue);
			rule = rule.replace(group, convRuleValue);
			matcher = pattern.matcher(rule);
		}

		return rule;
	}

	public void setExecutionMap(String field, List<Object> objects, Map<String, Object> map) {
		if (field != null) {
			Map<String, Object> objectsMap = getBeanMap(objects);
			Object value = fetchBeanValue(field, objectsMap.get(field.split("_")[0]));
			map.put(field, value);
		}
	}

	private Map<String, Object> getBeanMap(List<Object> objects) {
		Map<String, Object> objectsMap = new HashMap<String, Object>();
		if (objects != null) {
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
		}
		return objectsMap;
	}

	private Object fetchBeanValue(String field, Object object) {
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

	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	public BigDecimal getRuleResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
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

	public void setGlobalVariableService(GlobalVariableService globalVariableService) {
		this.globalVariableService = globalVariableService;
	}

}
