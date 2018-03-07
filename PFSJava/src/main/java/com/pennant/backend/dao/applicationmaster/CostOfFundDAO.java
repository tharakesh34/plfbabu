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
 * FileName    		:  CostOfFundDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster;
import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>CostOfFund model</b> class.<br>
 */
public interface CostOfFundDAO extends BasicCrudDao<CostOfFund> {

	CostOfFund getCostOfFundById(String cofCode, String currency, Date cofEffDate, String type);
	
	/**
	 *  Checks whether another record exists with the key attributes in the specified table type.
	 *  
	 * @param bRType
	 *            bRType of the baseRate
	 * @param bREffDate
	 *            bREffDate of the baseRate
	 * @param currency
	 *            currency of the baseRate
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String cofCode,Date cofEffDate, String currency, TableType tableType);
	CostOfFund getCostOfFundByType(final String cofCode, String currency, Date cofEffDate);
	boolean getCostOfFundListById(String cofCode, String currency, Date cofEffDate, String type);
	List<CostOfFund> getBSRListByMdfDate(Date cofEffDate, String type);
	void deleteByEffDate(CostOfFund cofCode, String type);
	int getCostOfFundCountById(String cofCode, String currency, String type);
	List<CostOfFund> getCostOfFundHistByType(String cofCode, String currency, Date cofEffDate);
}