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
 *																							*
 * FileName    		:  RuleDAO.java															*                           
 *																							* 	
 * Author      		:  PENNANT TECHONOLOGIES              									*	
 *                                                                  						*
 * Creation Date    :  08-03-2011    														*
 *                                                                  						*
 * Modified Date    :  08-03-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-03-2011       PENNANT TECHONOLOGIES	 0.1                            				* 
 *                                                                                          * 
 *                                                                                          * 
 * 08-05-2019		Srinivasa Varma			 0.2		  Development Iteam 81              * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.dao.rulefactory;

import java.util.List;

import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;

/**
 * DAO methods declaration for the <b>Rule model</b> class.<br>
 * 
 */
public interface RuleDAO {

	Rule getRuleByID(String code,String module,String event,String type);
	void update(Rule rule,String type);
	void delete(Rule rule,String type);
	long save(Rule rule,String type);
	List<BMTRBFldDetails> getFieldList(String module, String event);
	List<BMTRBFldCriterias> getOperatorsList();
	List<RuleModule> getRuleModules(String module);
	List<Rule> getRuleByModuleAndEvent(String module, String event, String type);
	List<Rule> getRulesByGroupId(long groupId,String ruleModule, String ruleEvent, String type);
	List<Rule> getRulesByGroupIdList(long groupId, String categoryType, String type);
	List<NFScoreRuleDetail> getNFRulesByGroupId(long id, String categoryType, String type);
	List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String type);
	List<Rule> getRulesByFinScoreGroup(List<Long> groupIds, String categoryType, String type);
	List<NFScoreRuleDetail> getNFRulesByNFScoreGroup(List<Long> groupIds, String type);
	String getAmountRule(String id, String module, String event);
	List<Rule> getRuleDetails(List<String> ruleCodes, String module, String type);
	Rule getRuleById(String ruleCode, String module, String type);
	Rule getRuleByID(long ruleId, String type);
	List<Rule> getRuleDetailList(List<String> ruleCodeList, String ruleModule, String ruleEvent);
	
	List<String> getAEAmountCodesList(String event);
	List<Rule> getSubHeadRuleList(List<String> subHeadRuleList);
	//GST
	List<Rule> getGSTRuleDetails(String ruleModule, String type);
	
	//### 08-05-2018 Development Iteam 81 
	boolean isFieldAssignedToRule(String fieldName);

  }