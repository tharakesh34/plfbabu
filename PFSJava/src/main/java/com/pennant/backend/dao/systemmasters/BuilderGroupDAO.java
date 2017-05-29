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
 * FileName    		:  BuilderGroupDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-05-2017    														*
 *                                                                  						*
 * Modified Date    :  17-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennanttech.pff.core.TableType;

public interface BuilderGroupDAO extends BasicCrudDao<BuilderGroup> {
	
	
	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            id of the BuilderGroup.
	 * @param tableType
	 *            The type of the table.
	 * @return BuilderGroup
	 */
	BuilderGroup getBuilderGroup(long id,String type);
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            id of the BuilderGroup.
	 * @param name
	 *            name of the BuilderGroup.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String name, TableType tableType);	
	
}