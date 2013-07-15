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
 * FileName    		:  FinanceRepayPriorityDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance;
import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceRepayPriority;

public interface FinanceRepayPriorityDAO {

	public FinanceRepayPriority getFinanceRepayPriority();
	public FinanceRepayPriority getNewFinanceRepayPriority();
	public FinanceRepayPriority getFinanceRepayPriorityById(String id,String type);
	public void update(FinanceRepayPriority financeRepayPriority,String type);
	public void delete(FinanceRepayPriority financeRepayPriority,String type);
	public String save(FinanceRepayPriority financeRepayPriority,String type);
	public void initialize(FinanceRepayPriority financeRepayPriority);
	public void refresh(FinanceRepayPriority entity);
	public List<ValueLabel> getFinanceRepayPriorities(String type);
	public List<FinanceRepayPriority> getFinanceRpyPriorByPriority(int priority, String type);
}