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
 * FileName    		:  BaseRateDAO.java                                                   * 	  
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
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>BaseRate model</b> class.<br>
 */
public interface BaseRateDAO extends BasicCrudDao<BaseRate> {

	BaseRate getBaseRateById(String bRType, String currency, Date bREffDate, String type);
	
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
	boolean isDuplicateKey(String bRType,Date bREffDate, String currency, TableType tableType);
	BaseRate getBaseRateByType(final String bRType, String currency, Date bREffDate);
	boolean getBaseRateListById(String bRType, String currency, Date bREffDate, String type);
	List<BaseRate> getBSRListByMdfDate(Date bREffDate, String type);
	void deleteByEffDate(BaseRate baseRate, String type);
	int getBaseRateCountById(String bRType, String currency, String type);
	List<BaseRate> getBaseRateHistByType(String bRType, String currency, Date bREffDate);
}