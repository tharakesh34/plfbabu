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
 * FileName    		:  NPABucketDAO.java                                                   * 	  
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
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennanttech.pff.core.TableType;

public interface NPABucketDAO extends BasicCrudDao<NPABucket> {
	
	
	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param bucketID
	 *            bucketID of the NPABucket.
	 * @param tableType
	 *            The type of the table.
	 * @return NPABucket
	 */
	NPABucket getNPABucket(long bucketID,String type);
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param bucketID
	 *            bucketID of the NPABucket.
	 * @param bucketCode
	 *            bucketCode of the NPABucket.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long bucketID, String bucketCode, TableType tableType);
	List<NPABucket> getNPABuckets();	
	
}