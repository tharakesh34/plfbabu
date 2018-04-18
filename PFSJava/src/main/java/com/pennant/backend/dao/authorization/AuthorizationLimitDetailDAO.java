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
 * FileName    		:  AuthorizationLimitDetailDAO.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-04-2018    														*
 *                                                                  						*
 * Modified Date    :  06-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.authorization;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.authorization.AuthorizationLimitDetail;
import com.pennanttech.pff.core.TableType;

public interface AuthorizationLimitDetailDAO extends BasicCrudDao<AuthorizationLimitDetail> {
	
	/**
	 * Fetch the Record AuthorizationLimitDetail by key field
	 * 
	 * @param id
	 *            id of the AuthorizationLimitDetail.
	 * @param tableType
	 *            The type of the table.
	 * @return AuthorizationLimitDetail
	 */
	AuthorizationLimitDetail getAuthorizationLimitDetail(long id,String type);
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            id of the AuthorizationLimitDetail.
	 * @param authLimitId
	 *            authLimitId of the AuthorizationLimitDetail.
	 * @param code
	 *            code of the AuthorizationLimitDetail.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, long authLimitId, String code, TableType tableType);
	List<AuthorizationLimitDetail> getListByAuthLimitId(long authLimitId, String type);
	void deleteByAuthLimitId(long authLimitId, TableType tableType);	
	
}