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
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;

public interface FinanceWorkFlowDAO {

	public FinanceWorkFlow getFinanceWorkFlow();
	public FinanceWorkFlow getNewFinanceWorkFlow();
	public FinanceWorkFlow getFinanceWorkFlowById(String id,String type);
	public void update(FinanceWorkFlow financeWorkFlow,String type);
	public void delete(FinanceWorkFlow financeWorkFlow,String type);
	public String save(FinanceWorkFlow financeWorkFlow,String type);
	public void initialize(FinanceWorkFlow financeWorkFlow);
	public void refresh(FinanceWorkFlow entity);
}