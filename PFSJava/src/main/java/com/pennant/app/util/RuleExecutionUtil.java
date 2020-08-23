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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.service.GlobalVariableService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RuleExecutionUtil implements Serializable {
	private static final long serialVersionUID = -7634160175219913960L;
	private static final Logger logger = Logger.getLogger(RuleExecutionUtil.class);

	private transient ScriptEngine scriptEngine;
	private transient GlobalVariableService globalVariableService;
	private boolean splRule = false;

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
			logger.error(Literal.EXCEPTION, e);
		} finally {
			root = null;
			t1 = null;
			t = null;
			cfg = null;
		}
		return result.getBuffer().toString();
	}

	public Object executeRule(String rule, Map<String, Object> fieldsandvalues, String finccy,
			RuleReturnType returnType) {
		return executeRule(rule, fieldsandvalues, finccy, returnType, false);
	}

	public Object executeRule(String rule, Map<String, Object> fieldsandvalues, String finccy,
			RuleReturnType returnType, boolean splRule) {

		Bindings bindings = new SimpleBindings();

		if (fieldsandvalues != null && !fieldsandvalues.isEmpty()) {
			bindings.putAll(fieldsandvalues);
		}

		rule = replaceCurrencyCode(rule, finccy);
		rule = StringUtils.replace(rule, "{BLANK}", "");
		Object result = null;

		List<GlobalVariable> globalVariables = globalVariableService.getGlobalVariables();
		if (globalVariables != null && globalVariables.size() > 0) {
			rule = getGlobalVariables(rule, globalVariables);
		}

		String scriptRule = getExecutableRule(rule);

		try {

			if (splRule) {
				setFields(fieldsandvalues);
				result = processSPLEngineRule(scriptRule.toString(), null, returnType);
			}

			result = processEngineRule(scriptRule.toString(), null, fieldsandvalues, returnType);
		} catch (DatatypeConfigurationException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug("Leaving");

		return result;
	}

	public String getExecutableRule(String rule) {
		StringBuilder scriptRule = new StringBuilder();
		scriptRule.append("var Result; function Pennant(){");
		scriptRule.append(rule);
		scriptRule.append("}Pennant(); function pennantExec() { return Result; } pennantExec();");
		return scriptRule.toString();
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
	private Object processEngineRule(String rule, CompiledScript compiledScript, Map<String, Object> fieldsandvalues,
			RuleReturnType returnType) throws DatatypeConfigurationException {

		BigDecimal resultBigDecimal = BigDecimal.ZERO;
		Object result = null;

		RuleResult ruleResult = null;
		Bindings bindings = null;

		if (returnType == RuleReturnType.OBJECT) {
			ruleResult = new RuleResult();
			if (rule != null) {
				bindings = this.scriptEngine.createBindings();
			} else if (compiledScript != null) {
				bindings = compiledScript.getEngine().createBindings();
			}
			bindings.put("result", ruleResult);
		} else {
			bindings = this.scriptEngine.createBindings();
			bindings.put("result", ruleResult);
		}

		try {
			if (rule != null) {
				// pass script

				if (fieldsandvalues != null && !fieldsandvalues.isEmpty()) {
					bindings.putAll(fieldsandvalues);
				}
				if (returnType == RuleReturnType.OBJECT) {
					ruleResult = new RuleResult();
					bindings.put("result", ruleResult);
				}
				result = this.scriptEngine.eval(rule.toString(), bindings);
			} else if (compiledScript != null) {

				if (fieldsandvalues != null && !fieldsandvalues.isEmpty()) {
					bindings.putAll(fieldsandvalues);
				}

				if (returnType == RuleReturnType.OBJECT) {
					ruleResult = new RuleResult();
					bindings.put("result", ruleResult);
				}

				result = compiledScript.eval(bindings);
			}

		} catch (Exception e) { // FIXME should be throw the Exception
			logger.error(Literal.EXCEPTION, e);
		}

		switch (returnType) {
		case DECIMAL:
			if (result == null) {
				result = BigDecimal.ZERO;
			} else if (result instanceof BigDecimal) {
				resultBigDecimal = (BigDecimal) result;
				resultBigDecimal = resultBigDecimal.setScale(0, RoundingMode.HALF_DOWN);
				result = resultBigDecimal;
			} else if (result instanceof Double) {
				if (((Double) result).isNaN()) {
					result = BigDecimal.ZERO;
				} else {
					resultBigDecimal = new BigDecimal(result.toString());
					resultBigDecimal = resultBigDecimal.setScale(2, RoundingMode.HALF_DOWN);
					result = resultBigDecimal;
				}
			} else if (result instanceof Integer || result instanceof Long) {
				resultBigDecimal = new BigDecimal(result.toString());
				resultBigDecimal = resultBigDecimal.setScale(0, RoundingMode.HALF_DOWN);
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

	private void setFields(Map<String, Object> fieldsandvalues) {
		List<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
		for (int i = 0; i < keyset.size(); i++) {

			Object var = fieldsandvalues.get(keyset.get(i));
			if (var instanceof String) {
				var = var.toString().trim();
			}

			scriptEngine.put(keyset.get(i), var);
		}
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

	private Object processSPLEngineRule(String rule, Bindings bindings, RuleReturnType returnType)
			throws DatatypeConfigurationException {

		List<GlobalVariable> globalVariables = SysParamUtil.getGlobaVariableList();
		if (globalVariables != null && globalVariables.size() > 0) {
			rule = getGlobalVariables(rule, globalVariables);
		}

		BigDecimal resultBigDecimal = BigDecimal.ZERO;
		String result = null;
		try {
			String scriptRule = "function Pennant(){" + rule + "}Pennant();";

			if (scriptEngine.eval(scriptRule) == null) {
				scriptEngine.put("Result", null);
			}
			if (scriptEngine.eval(scriptRule) != null) {
				resultBigDecimal = new BigDecimal(scriptEngine.eval(rule).toString());
				resultBigDecimal = resultBigDecimal.setScale(0, RoundingMode.HALF_UP);
				result = resultBigDecimal.toString();
			} else {
				if (scriptEngine.get("Result") != null) {
					result = scriptEngine.get("Result").toString();
					try {
						if ("D".equals(returnType.value())) {
							resultBigDecimal = new BigDecimal(result);
							resultBigDecimal = resultBigDecimal.setScale(2, RoundingMode.UP);
							result = resultBigDecimal.toString();
						} else if ("S".equals(returnType.value())) {
							result = result.trim().toString();
						} else if ("C".equals(returnType.value())) {
							result = result.trim().toString();
						} else {
							resultBigDecimal = new BigDecimal(result);
							resultBigDecimal = resultBigDecimal.setScale(0, RoundingMode.FLOOR);
							result = resultBigDecimal.toString();
						}
					} catch (Exception e) {
						//do Nothing-- if return type is not a decimal
						resultBigDecimal = new BigDecimal(scriptEngine.get("Result").toString());
						resultBigDecimal = resultBigDecimal.setScale(0, RoundingMode.HALF_UP);
						result = resultBigDecimal.toString();
					}
				}
			}
		} catch (ScriptException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		logger.debug("Leaving");
		return result;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public GlobalVariableService getGlobalVariableService() {
		return globalVariableService;
	}

	public void setGlobalVariableService(GlobalVariableService globalVariableService) {
		this.globalVariableService = globalVariableService;
	}

	public boolean isSplRule() {
		return splRule;
	}

	public void setSplRule(boolean splRule) {
		this.splRule = splRule;
	}

}
