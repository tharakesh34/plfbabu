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
 * FileName    		:  SnapShotColumnDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-02-2018    														*
 *                                                                  						*
 * Modified Date    :  16-02-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-02-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.eodsnapshot;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.eodsnapshot.SnapShotColumn;

public interface SnapShotColumnDAO extends BasicCrudDao<SnapShotColumn> {
	
	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param id
	 *            id of the SnapShotColumn.
	 * @param columnName
	 *            columnName of the SnapShotColumn.
	 * @param tableType
	 *            The type of the table.
	 * @return SnapShotColumn
	 */
	SnapShotColumn getSnapShotColumn(long id,String columnName,String type);

	List<SnapShotColumn> getActiveTableColumns(long id);
	
}