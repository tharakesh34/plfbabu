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
 * FileName    		:  CountryDAO.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  08-03-2011    
 *                                                                  
 * Modified Date    :  08-03-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-03-2011       PENNANT TECHONOLOGIES	                 0.1                                         * 
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

	public Rule getRule();
	public Rule getNewRule();
	public Rule getRuleByID(String code,String module,String event,String type);
	public void update(Rule rule,String type);
	public void delete(Rule rule,String type);
	public long save(Rule rule,String type);
	public void initialize(Rule rule);
	public void refresh(Rule entity);
	public List<BMTRBFldDetails> getFieldList(String module, String event);
	public List<BMTRBFldCriterias> getOperatorsList();
	public List<RuleModule> getRuleModules(String module);
	public List<Rule> getRuleByModuleAndEvent(String module, String event, String type);
	
	public List<Rule> getRulesByGroupId(long groupId,String ruleModule, String ruleEvent, String type);
	public List<Rule> getRulesByGroupIdList(long groupId, String categoryType, String type);
	
	public List<NFScoreRuleDetail> getNFRulesByGroupId(long id, String categoryType, String type);
	public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId, String type);
	List<Rule> getRulesByFinScoreGroup(List<Long> groupIds, String categoryType, String type);
	List<NFScoreRuleDetail> getNFRulesByNFScoreGroup(List<Long> groupIds, String categoryType,
            String type);
	
  }