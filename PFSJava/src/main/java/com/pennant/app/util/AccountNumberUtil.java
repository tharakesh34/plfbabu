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
 * FileName    		:  AccountProcessUtil.java													*                           
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
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;

public class AccountNumberUtil implements Serializable {
    private static final long serialVersionUID = -1200799666995440280L;
	private Logger logger = Logger.getLogger(AccountNumberUtil.class);

	private RuleDAO ruleDAO;
	private  RuleExecutionUtil ruleExecutionUtil;
	private SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO;

	private int internalSysAccLength = SysParamUtil.getValueAsInt("SYSINT_ACCOUNT_LEN");
	
	public AccountNumberUtil() {
	    super();
    }

	/**
	 * Method To Fetch Basic Account Number Using TransactionEntry Details
	 * @param transactionEntry
	 * @param object
	 * @param ccy
	 */
	public String getBasicAccountNumber(TransactionEntry transactionEntry,SubHeadRule subHeadRule,String ccy){
		logger.debug("Entering");
		//Get Account Basic Number By Executing Rule
		String ruleAcNum = getAccountByRule(transactionEntry, subHeadRule, ccy);

		//Fetch Internal Account Using Account Type
		String internalSIANumber = getInternalAccount(transactionEntry.getAccountType());

		internalSIANumber =  mergeAccountNumber(internalSIANumber,ruleAcNum);
		logger.debug("Leaving");
		return internalSIANumber;
	}
	
	
	/**Merging Internal Account With Generated Rule Account
	 * @param internalSIANumber
	 * @param accBasicNumber
	 * @return
	 */
	private String mergeAccountNumber(String internalSIANumber,String ruleAcNum){
		logger.debug("Entering");
		String accNumber = "";
		int acBeginLength = internalSIANumber.length() - internalSysAccLength;
		
		accNumber = internalSIANumber.substring(acBeginLength,internalSIANumber.length()-ruleAcNum.length());
		
		accNumber = accNumber.concat(ruleAcNum);
		
		logger.debug("Leaving");
		return accNumber;
	}
	
	/**
	 * Get Account By Executing The Rule
	 * @param transactionEntry
	 * @param object
	 * @param ccy
	 * @return
	 */
	private String getAccountByRule(TransactionEntry transactionEntry,SubHeadRule subHeadRule,String ccy){
		logger.debug("Entering");
		
		String amountRule = getRuleDAO().getAmountRule(transactionEntry.getAccountSubHeadRule(), 
				RuleConstants.MODULE_SUBHEAD, RuleConstants.EVENT_SUBHEAD);
		
		HashMap<String, Object> fieldsAndValues = subHeadRule.getDeclaredFieldValues();
		
		String ruleAccount = (String) getRuleExecutionUtil().executeRule(amountRule, fieldsAndValues, ccy, RuleReturnType.STRING);
		
		logger.debug("Leaving");
		return ruleAccount;
	}
	
	/**Get System Internal Account Using Account Type
	 * @param accountType
	 * @return
	 */
	public String getInternalAccount(String accountType){
		logger.debug("Entering");
		String internalAccount = "";
		
		SystemInternalAccountDefinition sysInternalAccDef =  getInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(accountType, "");
		
		if(sysInternalAccDef != null){
			internalAccount = sysInternalAccDef.getSIANumber();
		}
		logger.debug("Leaving");
		return internalAccount;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setInternalAccountDefinitionDAO(
			SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO) {
		this.internalAccountDefinitionDAO = internalAccountDefinitionDAO;
	}
	public SystemInternalAccountDefinitionDAO getInternalAccountDefinitionDAO() {
		return internalAccountDefinitionDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	
}
