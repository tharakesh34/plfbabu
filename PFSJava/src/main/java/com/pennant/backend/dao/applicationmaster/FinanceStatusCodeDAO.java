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
 * FileName    		:  FinanceStatusCodeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-04-2017    														*
 *                                                                  						*
 * Modified Date    :  18-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-04-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.FinanceStatusCode;
import com.pennanttech.pff.core.TableType;

public interface FinanceStatusCodeDAO extends BasicCrudDao<FinanceStatusCode> {
	
	
	/**
	 * Fetch the Record FinanceStatusCode Details details by key field
	 * 
	 * @param statusID
	 *            statusID of the FinanceStatusCode.
	 * @param tableType
	 *            The type of the table.
	 * @return FinanceStatusCode
	 */
	FinanceStatusCode getFinanceStatusCode(long statusID,String type);
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param statusID
	 *            statusID of the FinanceStatusCode.
	 * @param statusCode
	 *            statusCode of the FinanceStatusCode.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long statusID, String statusCode, TableType tableType);	
	
}