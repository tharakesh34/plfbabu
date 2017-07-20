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
 * FileName    		:  FinanceWorkFlowDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters;
import java.util.List;

import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;

public interface FinanceWorkFlowDAO {

	FinanceWorkFlow getFinanceWorkFlowById(String finType, String finEvent, String moduleName, String type);
	void update(FinanceWorkFlow financeWorkFlow,String type);
	void delete(FinanceWorkFlow financeWorkFlow,String type);
	String save(FinanceWorkFlow financeWorkFlow,String type);
	List<FinanceWorkFlow> getFinanceWorkFlowListById(String finType,String moduleName, String type);
	void saveList(List<FinanceWorkFlow> financeWorkFlowList, String type);
	String getFinanceWorkFlowType(String finType, String finEvent,	String moduleName, String type);
	List<String> getFinanceWorkFlowRoles(String module,String finEvent);
	boolean isWorkflowExists(String finType, String moduleName);
	int getVASProductCode(String finType, String type);
	
}