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
 * FileName    		:  CovenantTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2019    														*
 *                                                                  						*
 * Modified Date    :  06-02-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2019       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.finance.covenant;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennanttech.pff.core.TableType;

public interface CovenantTypeDAO extends BasicCrudDao<CovenantType> {

	/**
	 * Fetch the Record CovenantType by key field
	 * 
	 * @param id
	 *            id of the CovenantType.
	 * @param tableType
	 *            The type of the table.
	 * @return CovenantType
	 */
	CovenantType getCovenantType(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param covenantType
	 *            covenantType of the Covenant Type.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(CovenantType covenantType, TableType tableType);

	CovenantType getCovenantTypeId(String covenanttype, String category, String type);

}