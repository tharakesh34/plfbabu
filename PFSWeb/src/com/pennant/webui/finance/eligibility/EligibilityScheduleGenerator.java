/*package com.pennant.webui.finance.eligibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.lmtmasters.EligibilityRules;
import com.pennant.backend.model.lmtmasters.LoanEligibility;
import com.pennant.backend.model.rmtmasters.FinanceEligibility;
import com.pennant.backend.service.lmtmasters.EligibilityRulesService;
import com.pennant.backend.service.rmtmasters.FinanceEligibilityService;
import com.pennant.webui.util.PTMessageUtils;

public class EligibilityScheduleGenerator {
	
	private final static Logger logger = Logger.getLogger(EligibilityScheduleGenerator.class);

	static List<LoanEligibility> loanEligibilities = new ArrayList<LoanEligibility>();
	private EligibilityRulesService eligibilityRulesService;
	private FinanceEligibilityService financeEligibilityService;
	
	//Getting FinanceTypes
	static Map<String,String> rulesMap = new HashMap<String, String>();
	static List<FinanceEligibility> financeEligibilities = new ArrayList<FinanceEligibility>();
	
	*//**
	 * Method of creating Schedule for Customer Finance Eligibility
	 * @param financeEligibilities
	 * @param rulesMap
	 * @param loanEligibility
	 * @return
	 * @throws InterruptedException
	 *//*
	public List<LoanEligibility> generateEligibilityList(boolean finTypeExists,LoanEligibility loanEligibility) throws InterruptedException{
		logger.debug("Entering");
		
		String ruleCode = "";
		
		if(finTypeExists){
			List<FinanceEligibility> eligibilities = getFinanceEligibilityService().getFinanceEligibilityByFinType(
					loanEligibility.getCustomerEligibilityCheck().getFinType());
			for (int i = 0; i < eligibilities.size(); i++) {
				FinanceEligibility eligibility = eligibilities.get(i);
				ruleCode = ruleCode + "'"+eligibility.getElgRuleCode().trim()+"',";
			}
			if(!ruleCode.equals("")){
				ruleCode = ruleCode.substring(0, ruleCode.length()-1);
			}
		}else{
			financeEligibilities = getFinanceEligibilityService().getFinanceEligibilities();
		}
		
		//Getting EligibilityRules
		List<EligibilityRules> eligibilityRulesList = getEligibilityRulesService().getEligibilityRuleList(ruleCode);
 		for (int i = 0; i < eligibilityRulesList.size(); i++) {
 			EligibilityRules eligibilityRule = eligibilityRulesList.get(i);
 			rulesMap.put(eligibilityRule.getElgRuleCode(), eligibilityRule.getElgRuleValue());
		}	
 		
		if(financeEligibilities.size() == 0 && 
				!(StringUtils.trimToEmpty(loanEligibility.getCustomerEligibilityCheck().getFinType()).equals(""))){
			
			List<String> ruleCodes = new ArrayList<String>(rulesMap.keySet());
			for (int i = 0; i < ruleCodes.size(); i++) {
				loanEligibility.setRuleCode(ruleCodes.get(i).toString());
				loanEligibilities.add(ruleCodeExecutionProcess(loanEligibility,
						rulesMap.get(ruleCodes.get(i)).toString()));
			}

		}else{
			for (int i = 0; i < financeEligibilities.size(); i++) {
				FinanceEligibility financeEligibility = (FinanceEligibility) financeEligibilities.get(i);
				loanEligibility.getCustomerEligibilityCheck().setFinType(financeEligibility.getFinType());
				loanEligibility.setRuleCode(financeEligibility.getElgRuleCode());
				loanEligibilities.add(ruleCodeExecutionProcess(loanEligibility, 
						rulesMap.get(financeEligibility.getElgRuleCode()).toString()));
			}
		}
		logger.debug("Leaving");
		return loanEligibilities;
	}

	*//**
	 * Method for Execution of Eligibility Formula and Return result
	 * @param loanEligibility
	 * @param ruleFormula
	 * @return
	 * @throws InterruptedException
	 *//*
	private LoanEligibility ruleCodeExecutionProcess(LoanEligibility loanEligibility,String ruleFormula) 
						throws InterruptedException{
		
		logger.debug("Entering");
		
		HashMap<String, Object> customerElgFields = new HashMap<String, Object>();
 		customerElgFields = loanEligibility.getCustomerEligibilityCheck().getDeclaredFieldValues();

 		List<String> fieldList = new ArrayList<String>(customerElgFields.keySet());

		try{
			// create script engine
			ScriptEngineManager factory = new ScriptEngineManager();
			// specify the engine language
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			// pass variables and values to the engine
			for (int i = 0; i < fieldList.size(); i++) {
				engine.put(fieldList.get(i), customerElgFields.get(fieldList.get(i)));
			}
			//Evaluation of data with existing Formula
			Object result = (Object)engine.eval("function rule(){"+ruleFormula+"}rule();");
			loanEligibility.setActualResult(result.toString());
			if(result.toString().equals("true")){
				loanEligibility.setStatus("Success");
			}else{
				loanEligibility.setStatus("failed");
				//TODO failed reason
			}
		} catch (Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
		}
		logger.debug("Leaving");
		return loanEligibility;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public EligibilityRulesService getEligibilityRulesService() {
		return eligibilityRulesService;
	}
	public void setEligibilityRulesService(
			EligibilityRulesService eligibilityRulesService) {
		this.eligibilityRulesService = eligibilityRulesService;
	}

	public FinanceEligibilityService getFinanceEligibilityService() {
		return financeEligibilityService;
	}
	public void setFinanceEligibilityService(
			FinanceEligibilityService financeEligibilityService) {
		this.financeEligibilityService = financeEligibilityService;
	}

	
	
	
	
}
*/