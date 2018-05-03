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
 * FileName    		:  NPABucketConfigurationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennanttech.pff.core.TableType;

public interface NPABucketConfigurationDAO extends BasicCrudDao<NPABucketConfiguration> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param configID
	 *            configID of the NPABucketConfiguration.
	 * @param tableType
	 *            The type of the table.
	 * @return NPABucketConfiguration
	 */
	NPABucketConfiguration getNPABucketConfiguration(long configID, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param configID
	 *            configID of the NPABucketConfiguration.
	 * @param productCode
	 *            productCode of the NPABucketConfiguration.
	 * @param bucketID
	 *            bucketID of the NPABucketConfiguration.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long configID, String productCode, long bucketID, TableType tableType);

	public int getByProductCode(String producCode, int dueDys, String type);

	List<NPABucketConfiguration> getNPABucketConfigurations();

	int getNPABucketConfigurationById(long bucketID, String type);

	List<NPABucketConfiguration> getNPABucketConfigByProducts(String productCode);

}