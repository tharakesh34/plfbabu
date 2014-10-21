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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIAFB;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSetFiller;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.PennantRuleConstants;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RuleExecutionUtil implements Serializable {
	
    private static final long serialVersionUID = -7634160175219913960L;
	private Logger logger = Logger.getLogger(RuleExecutionUtil.class);
	
	public RuleExecutionUtil() {
	    super();
    }

	/**
	 * Method for replacement of GlobalVariables in Rule Execution
	 * @param templateStr
	 * @param globalList
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getGlobalVariables(String templateStr,List<GlobalVariable> globalList) {
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
			
			for(int i=0; i<globalList.size();i++){
				GlobalVariable globalVariable = globalList.get(i);
				String str = (globalVariable.getVarName()).substring(2,  (globalVariable.getVarName()).length()-1);
				root.put(str, "("+globalVariable.getVarValue()+")");
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
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
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
	 * @param object
	 * @return Object
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings("unchecked")
    public Object executeRule(String rule, Object object,List<GlobalVariable> globalVariableList, String finccy){
		logger.debug("Entering");
		
		HashMap<String, Object> fieldsandvalues = new HashMap<String, Object>();
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		
		String returnType = "D";
		
		if (object instanceof CustomerScoringCheck) {
			CustomerScoringCheck custcheck = (CustomerScoringCheck) object;
			fieldsandvalues = custcheck.getDeclaredFieldValues();
		} else if (object instanceof CustomerEligibilityCheck) {
			CustomerEligibilityCheck custelg = (CustomerEligibilityCheck) object;
			fieldsandvalues = custelg.getDeclaredFieldValues();
		}else if (object instanceof SubHeadRule) {
			SubHeadRule subHeadRule = (SubHeadRule) object;
			fieldsandvalues = subHeadRule.getDeclaredFieldValues();
			returnType = "S";
		} else if (object instanceof DataSetFiller) {
			DataSetFiller dataSetFiller = (DataSetFiller) object;
			fieldsandvalues = dataSetFiller.getDeclaredFieldValues();
		} else if (object instanceof AEAmountCodesRIA) {
			AEAmountCodesRIA codesRIA = (AEAmountCodesRIA) object;
			fieldsandvalues = codesRIA.getDeclaredFieldValues();
		} else if (object instanceof AEAmountCodesRIAFB) {
			AEAmountCodesRIAFB codesRIAFB = (AEAmountCodesRIAFB) object;
			fieldsandvalues = codesRIAFB.getDeclaredFieldValues();
		}else if (object instanceof AECommitment) {
			 AECommitment codesRIAFB = (AECommitment) object;
			fieldsandvalues = codesRIAFB.getDeclaredFieldValues();
		}else{
			try{
				fieldsandvalues = (HashMap<String, Object>) object.getClass().getMethod("getDeclaredFieldValues").invoke(object);
				returnType = "S";
			}catch(Exception e){
				logger.debug(e);
			}

		}
		
		ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
		for (int i = 0; i < keyset.size(); i++) {
			
			Object var=fieldsandvalues.get(keyset.get(i));
			if (var instanceof String) {
				var=var.toString().trim();
			}
			
			engine.put(keyset.get(i),var );
		}
		
		//Currency Conversions if Courrency Constants Exists in Rule 
		String[] ccyConstantsList = rule.split("[^"+PennantRuleConstants.RULEFIELD_CCY+"0-9]+");
		if(ccyConstantsList != null && ccyConstantsList.length > 0){
			for (String ruleField : ccyConstantsList) {
				if(ruleField.startsWith(PennantRuleConstants.RULEFIELD_CCY)){
					BigDecimal ruleValue = new BigDecimal(Integer.parseInt(ruleField.replace(
							PennantRuleConstants.RULEFIELD_CCY, ""))*PennantRuleConstants.RULEFIELD_CCY_AMT);
					String convRuleValue = CalculationUtil.convertedUnFormatAmount(null, finccy, ruleValue);
					rule = rule.replace(ruleField, convRuleValue);
				}
            }
		}
		
		Object result = processEngineRule(rule, engine, globalVariableList, returnType);
		
		fieldsandvalues.clear();
		logger.debug("Leaving");
		return result;
	}
	
	/**
	 * Method for Execution of Rule with using Data Filled ScriptEngine
	 * @param sqlRule
	 * @param engine
	 * @param globalVariableList
	 * @return Object
	 */
	public Object processEngineRule(String sqlRule, ScriptEngine engine,List<GlobalVariable> globalVariableList, String returnType) {
		logger.debug("Entering");
		
		if(globalVariableList!=null && globalVariableList.size() > 0) {
			sqlRule = getGlobalVariables(sqlRule,globalVariableList);
		}
		
		BigDecimal tempResult= BigDecimal.ZERO;		
		String result =null;
		
		try {// pass script
			String scriptRule = "function Pennant(){" + sqlRule + "}Pennant();";
			
			if (engine.eval(scriptRule) == null) {
				engine.put("Result", null);
			}
			if (engine.eval(scriptRule)!=null) {
				tempResult=new BigDecimal(engine.eval(sqlRule).toString());
				tempResult = tempResult.setScale(0,RoundingMode.HALF_UP);
				result = tempResult.toString();
			}else{
				if(engine.get("Result")!=null){
					result=engine.get("Result").toString();
					try {
						if("D".equals(returnType)){
							tempResult=new BigDecimal(result);
							tempResult = tempResult.setScale(2,RoundingMode.UP);
							result = tempResult.toString();
						}else if("S".equals(returnType)){
							result = result.trim().toString();
						}else{
							tempResult=new BigDecimal(result);
							tempResult = tempResult.setScale(0,RoundingMode.FLOOR);
							result = tempResult.toString();
						}
					} catch (Exception e) {
						//do Nothing-- if return type is not a decimal
						tempResult = new BigDecimal(engine.get("Result").toString());
						tempResult = tempResult.setScale(0,RoundingMode.HALF_UP);
						result = tempResult.toString();
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
	
	/**
	 * Execute Rule by using rule engine
	 * @param rule
	 * @param customerEligibilityCheck
	 * @return
	 */
	public String executeRule(String rule, CustomerEligibilityCheck customerEligibilityCheck, String finccy){
		Object ruleResult = null;
		try {
			ruleResult = executeRule(rule, customerEligibilityCheck, SystemParameterDetails.getGlobaVariableList(), finccy);
		} catch (Exception e) {
			ruleResult = "E";
		}
		return ruleResult == null ? "" : ruleResult.toString();
	}

	
}
