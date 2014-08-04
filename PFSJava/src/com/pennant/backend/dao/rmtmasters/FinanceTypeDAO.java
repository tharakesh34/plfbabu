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
 * FileName    		:  FinanceTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.rmtmasters.FinanceType;

/**
 * DAO methods declaration for the <b>FinanceType model</b> class.<br>
 * 
 */
public interface FinanceTypeDAO {

	public FinanceType getFinanceType();
	public FinanceType getNewFinanceType();
	public FinanceType getFinanceTypeByID(String id, String type);
	public FinanceType getFinanceTypeByFinType(String finType);
	public void update(FinanceType financeType, String type);
	public void delete(FinanceType financeType, String type);
	public String save(FinanceType financeType, String type);
	public void initialize(FinanceType financeType);
	public void refresh(FinanceType entity);
	public FinanceType getCommodityFinanceType();
	public FinanceType getNewCommodityFinanceType();
	public boolean checkRIAFinance(String finType);
	public List<FinanceType> getFinTypeDetailForBatch();

}