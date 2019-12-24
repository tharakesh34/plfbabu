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
 * FileName    		:  ReasionCodeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennanttech.pff.core.TableType;

public interface ReasonCodeDAO extends BasicCrudDao<ReasonCode> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            id of the ReasionCode.
	 * @param tableType
	 *            The type of the table.
	 * @return ReasionCode
	 */
	ReasonCode getReasonCode(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            id of the ReasionCode.
	 * @param reasonTypeID
	 *            reasonTypeID of the ReasionCode.
	 * @param reasonCategoryID
	 *            reasonCategoryID of the ReasionCode.
	 * @param code
	 *            code of the ReasionCode.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long reasonTypeCode, long reasonCategoryCode, String code, TableType tableType);

	boolean isreasonCategoryIDExists(long rCategoryCode);

	boolean isreasonTypeIDExists(long rTypeCode);

	List<ReasonCode> getReasonDetails(String reasonTypeCode);

}