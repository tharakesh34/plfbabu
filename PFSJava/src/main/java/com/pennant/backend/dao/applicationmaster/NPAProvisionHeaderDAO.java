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
 * FileName    		:  NPAProvisionHeaderDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-05-2020    														*
 *                                                                  						*
 * Modified Date    :  04-05-2020    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-05-2020       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennanttech.pff.core.TableType;

public interface NPAProvisionHeaderDAO extends BasicCrudDao<NPAProvisionHeader> {

	/**
	 * Fetch the Record NPAProvisionHeader by key field
	 * 
	 * @param id
	 *            id of the NPAProvisionHeader.
	 * @param tableType
	 *            The type of the table.
	 * @return NPAProvisionHeader
	 */
	NPAProvisionHeader getNPAProvisionHeader(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            id of the NPAProvisionHeader.
	 * @param entity
	 *            entity of the NPAProvisionHeader.
	 * @param finType
	 *            finType of the NPAProvisionHeader.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String entity, String finType, TableType tableType);

	List<AssetClassificationDetail> getAssetHeaderIdList(String finType, TableType type);

	AssetClassificationHeader getAssetClassificationCodesList(long listHeaderId, TableType aview);

	boolean getIsFinTypeExists(String finType, TableType type);

	NPAProvisionHeader getNPAProvisionByFintype(String finType, TableType tableType);

}