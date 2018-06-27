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
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.GlobalVariable;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RuleExecutionUtil implements Serializable {
	private static final long serialVersionUID = -7634160175219913960L;
	private static final Logger logger = Logger.getLogger(RuleExecutionUtil.class);

	private transient ScriptEngine scriptEngine;

	/**
	 * default constructor.<br>
	 */
	public RuleExecutionUtil() {
		super();
	}

	/**
	 * Method for replacement of GlobalVariables in Rule Execution
	 * 
	 * @param templateStr
	 * @param globalList
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getGlobalVariables(String templateStr, List<GlobalVariable> globalList) {
		logger.debug("Entering");

		StringWriter result = new StringWriter();
		Configuration cfg = null;
		LinkedHashMap root = null;
		Template t1 = null;
		Template t = null;
		try {

			cfg = new Configuration();
			// Create a data-model
			root = new LinkedHashMap();

			for (int i = 0; i < globalList.size(); i++) {
				GlobalVariable globalVariable = globalList.get(i);
				String str = (globalVariable.getName()).substring(2, (globalVariable.getName()).length() - 1);
				root.put(str, "(" + globalVariable.getValue() + ")");
			}
			// Prepare string template
			t1 = new Template("RuleReplacement", new StringReader(templateStr), cfg);

			// Process the output to StringWriter and convert that to String
			t1.process(root, result);

			// Load Data
			// Prepare string template
			t = new Template("RuleReplacement", new StringReader(result.getBuffer().toString()), cfg);
			result = new StringWriter();
			// Process the output to StringWriter and convert that to String
			t.process(root, result);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			root = null;
			t1 = null;
			t = null;
			cfg = null;
		}
		logger.debug("Leaving");
		return result.getBuffer().toString();
	}
	
	/**
	 * To Execute the Script Rule with object data
	 * 
	 * @param rule
	 * @param fieldsandvalues
	 * @param globalVariableList
	 * @param finccy
	 * @param ruleReturnDataType
	 * @return
	 * @throws Exception
	 */
	public Object executeRule(String rule, HashMap<String, Object> fieldsandvalues, String finccy, RuleReturnType returnType) {
		logger.debug("Entering");

		Bindings bindings = new SimpleBindings();
		
		if (fieldsandvalues != null && !fieldsandvalues.isEmpty()) {
			bindings.putAll(fieldsandvalues);
		}
		
		rule = replaceCurrencyCode(rule, finccy);
		rule = StringUtils.replace(rule, "{BLANK}", "");
		Object result = null;
		try {
			result = processEngineRule(rule, bindings, returnType);
		} catch (DatatypeConfigurationException e) {
			logger.error("Exception: ", e);
		}

		// fieldsandvalues.clear();
		logger.debug("Leaving");
		return result;
	}

	/**
	 * Process and execute the script by using ScriptEngine
	 * 
	 * @param rule
	 *            JavaScript which will be executed by Script Engine
	 * @param bindings
	 *            Contains data in the form of key and value pairs
	 * @param returnType
	 * @return
	 * @throws DatatypeConfigurationException 
	 */
	private Object processEngineRule(String rule, Bindings bindings, RuleReturnType returnType)
			throws DatatypeConfigurationException {
		logger.debug("Entering");

		List<GlobalVariable> globalVariables = SysParamUtil.getGlobaVariableList();
		if (globalVariables != null && globalVariables.size() > 0) {
			rule = getGlobalVariables(rule, globalVariables);
		}

		BigDecimal resultBigDecimal = BigDecimal.ZERO;
		Object result = null;
		String scriptRule = null;

		// get Script engine object
		ScriptEngine engine = this.scriptEngine;
		RuleResult ruleResult = null;
		
		if (returnType == RuleReturnType.OBJECT) {
			ruleResult = new RuleResult();
			bindings.put("result", ruleResult);
		}

		if(rule != null) {
			scriptRule = "var Result; function Pennant(){" + rule
					+ "}Pennant(); function pennantExec() { return Result; } pennantExec();";
			try {
				// pass script
				result = engine.eval(scriptRule, bindings);
			} catch (Exception exception) { // FIXME should be throw the Exception
				logger.error("Exception : ", exception);
			}
		}

		switch (returnType) {
		case DECIMAL:
			if (result == null) {
				result = BigDecimal.ZERO;
			} else if (result instanceof BigDecimal) {
				resultBigDecimal = (BigDecimal) result;
				resultBigDecimal = resultBigDecimal.setScale(2, RoundingMode.UP);
				result = resultBigDecimal;
			} else if (result instanceof Double) {
				if (((Double) result).isNaN()) {
					result = BigDecimal.ZERO;
				} else {
					resultBigDecimal = new BigDecimal(result.toString());
					resultBigDecimal = resultBigDecimal.setScale(2, RoundingMode.UP);
					result = resultBigDecimal;
				}
			} else if (result instanceof Integer || result instanceof Long) {
				resultBigDecimal = new BigDecimal(result.toString());
				resultBigDecimal = resultBigDecimal.setScale(2, RoundingMode.UP);
				result = resultBigDecimal;
			} else {
				throw new DatatypeConfigurationException(Labels.getLabel("RuleExecution_Decimal_Exception"));
			}

			break;
		case OBJECT:
			result = ruleResult;

			break;
		case STRING:
		case CALCSTRING:
			if (result == null) {
				result = "";
			} else if (!(result instanceof String)) {
				throw new DatatypeConfigurationException(Labels.getLabel("RuleExecution_String_Exception"));
			}

			break;
		case INTEGER:
			if (result == null) {
				result = Integer.valueOf(0);
			} else if (result instanceof Double) {
				if (((Double) result).isNaN()) {
					result = Integer.valueOf(0);
				} else {
					Double doubleValue = (Double) result;
					Integer integerValue = Integer.valueOf(doubleValue.intValue());
					result = integerValue;
				}
			} else if (result instanceof Integer) {
				result = Integer.valueOf(result.toString());
			} else {
				throw new DatatypeConfigurationException(Labels.getLabel("RuleExecution_Integer_Exception"));
			}

			break;
		case BOOLEAN:
			if (result == null) {
				result = false;
			} else if (result instanceof Double) {
				result = (Double) result == 0 ? false : true;
			} else if (result instanceof Integer) {
				result = (Integer) result == 0 ? false : true;
			} else {
				throw new DatatypeConfigurationException(Labels.getLabel("RuleExecution_Boolean_Exception"));
			}
			break;
		}

		logger.debug("Leaving");
		return result;
	}

	/**
	 * @param rule
	 * @param finccy
	 * @return
	 */
	public String replaceCurrencyCode(String rule, String finccy) {
		logger.debug(" Entering ");

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

		logger.debug(" Leaving ");
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
			}catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		return null;
	}

	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}
}
