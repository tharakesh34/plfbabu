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
 * FileName    		:  SnapShotConfigurationDAO.java                                                   * 	  
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

import java.sql.Timestamp;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.eodsnapshot.SnapShotConfiguration;
import com.pennanttech.pff.core.TableType;

public interface SnapShotConfigurationDAO extends BasicCrudDao<SnapShotConfiguration> {
	
	/**
	 * Fetch the Record SnapShotConfigurationdetails by key field
	 * 
	 * @param id
	 *            id of the SnapShotConfiguration.
	 * @param tableType
	 *            The type of the table.
	 * @return SnapShotConfiguration
	 */
	SnapShotConfiguration getSnapShotConfiguration(long id,String type);
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            id of the SnapShotConfiguration.
	 * @param fromTable
	 *            fromTable of the SnapShotConfiguration.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String fromTable, TableType tableType);	
	
	/**
	 * Fetch All Record SnapShotConfiguration Details 
	 * 
	 * @param id
	 *            id of the SnapShotConfiguration.
	 * @param tableType
	 *            The type of the table.
	 * @return SnapShotConfiguration
	 */
	List<SnapShotConfiguration> getActiveConfigurationList();
	
	/**
	 * Update Last run time Stamp for the SnapShotConfiguration Details 
	 * 
	 * @param id
	 *            id of the SnapShotConfiguration.
	 * @param lastRunDate
	 *            lastRunDate for the Configuration.
	 *            
	 */
	void updateLastRunDate(long id,Timestamp lastRunDate);
}