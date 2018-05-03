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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  RuleService.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.service.rulefactory;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;

/**
 * Service declaration for methods that depends on <b>Rule</b>.<br>
 */
public interface RuleService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	Rule getRuleById(String code,String module,String event);
	Rule getApprovedRuleById(String code,String module,String event);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<BMTRBFldDetails> getFieldList(String module,String event);
	List<BMTRBFldCriterias> getOperatorsList();
	List<RuleModule> getRuleModules(String module);
	
	List<Rule> getRulesByGroupId(long groupId, String ruleModule, String ruleEvent);
	List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId);
	String getAmountRule(String id, String module, String event);
	List<Rule> getRuleDetails(List<String> ruleCodes, String module);
	boolean validationCheck(String ruleEvent,String ruleCode);
	List<Rule> getRuleDetailList(List<String> ruleCodeList, String ruleModule, String ruleEvent);
	List<String> getAEAmountCodesList(String event);
	Rule getRuleById(long ruleID, String type);
	Rule getApprovedRule(String ruleCode, String ruleModule, String ruleEvent);
	
	List<Rule> getGSTRuleDetails(String ruleModule, String type);
}