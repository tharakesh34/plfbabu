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
 * FileName    		:  AssetClassificationHeaderDAO.java                                                   * 	  
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
import com.pennanttech.pff.core.TableType;

public interface AssetClassificationHeaderDAO extends BasicCrudDao<AssetClassificationHeader> {

	/**
	 * Fetch the Record AssetClassificationHeader by key field
	 * 
	 * @param id
	 *            id of the AssetClassificationHeader.
	 * @param tableType
	 *            The type of the table.
	 * @return AssetClassificationHeader
	 */
	AssetClassificationHeader getAssetClassificationHeader(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id
	 *            id of the AssetClassificationHeader.
	 * @param code
	 *            code of the AssetClassificationHeader.
	 * @param stageOrder
	 *            stageOrder of the AssetClassificationHeader.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String code, int stageOrder, Long npaTemplateId, TableType tableType);

	void saveFinType(AssetClassificationDetail assetClassificationDetail, TableType tableType);

	void updateFinType(AssetClassificationDetail assetClassificationDetail, TableType tableType);

	void deleteFinType(AssetClassificationDetail assetClassificationDetail, TableType tableType);

	void deleteFinTypeList(long headerId, TableType tableType);

	List<AssetClassificationDetail> getAssetDetailList(long headerId, TableType tableType);

	AssetClassificationDetail getAssetClassificationDetail(long id, String type);

	boolean isStageOrderExists(int stageOrder, Long npaTemplateId, TableType type);

	boolean isAssetCodeExists(String code, TableType type);

	List<AssetClassificationDetail> getAssetClassificationDetails(String finType, TableType tableType);

	public int getCountByFinType(String id, TableType type);

	List<AssetClassificationHeader> getAssetClassificationHeaderByTemplate(long templateId, String tableType);
}