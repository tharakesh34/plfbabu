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
 * FileName    		:  FinanceMarginSlabDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-11-2011    														*
 *                                                                  						*
 * Modified Date    :  14-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-11-2011       Pennant~	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;
import java.util.List;

import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;

public interface FinanceMarginSlabDAO {

	public FinanceMarginSlab getFinanceMarginSlab();
	public FinanceMarginSlab getNewFinanceMarginSlab();
	public FinanceMarginSlab getFinanceMarginSlabById(String id,String type);
	public void update(FinanceMarginSlab financeMarginSlab,String type);
	public void delete(FinanceMarginSlab financeMarginSlab,String type);
	public String save(FinanceMarginSlab financeMarginSlab,String type);
	public void initialize(FinanceMarginSlab financeMarginSlab);
	public void refresh(FinanceMarginSlab entity);
	public List<FinanceMarginSlab> getFinanceMarginSlabByFinType(final String finType, String type);
	public void deleteAll(FinanceMarginSlab financeMarginSlab,String type);
}