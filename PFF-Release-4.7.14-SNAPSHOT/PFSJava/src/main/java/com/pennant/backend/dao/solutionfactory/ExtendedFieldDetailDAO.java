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
 * FileName    		:  ExtendedFieldDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.solutionfactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;

public interface ExtendedFieldDetailDAO {

	ExtendedFieldDetail getExtendedFieldDetailById(long id, String name, int extendedType, String type);

	void update(ExtendedFieldDetail extendedFieldDetail, String type);

	void delete(ExtendedFieldDetail extendedFieldDetail, String type);

	long save(ExtendedFieldDetail extendedFieldDetail, String type);

	List<ExtendedFieldDetail> getExtendedFieldDetailById(long id, String type);

	List<ExtendedFieldDetail> getExtendedFieldNameById(long id, String type);

	void deleteByExtendedFields(final long id, String type);

	void alter(ExtendedFieldDetail extendedFieldDetail, String type, boolean dropCol, boolean reCreateCol, boolean isAudit);

	@Deprecated
	Map<String, Object> retrive(String tableName, String id, String type);

	Map<String, Object> retrive(String tableName, String primaryKeyColumn, Serializable id, String type);

	@Deprecated
	void saveAdditional(final String id, HashMap<String, Object> mappedValues, String type, String tableName);

	void saveAdditional(String primaryKeyColumn, final Serializable id, HashMap<String, Object> mappedValues,
			String type, String tableName);

	@Deprecated
	void updateAdditional(HashMap<String, ?> mappedValues, final String id, String type, String tableName);

	void updateAdditional(String primaryKeyColumn, final Serializable id, HashMap<String, Object> mappedValues,
			String type, String tableName);

	@Deprecated
	void deleteAdditional(final String id, String type, String tableName);

	void deleteAdditional(String primaryKeyColumn, final Serializable id, String type, String tableName);

	List<ExtendedFieldDetail> getExtendedFieldDetailBySubModule(String subModule, String type);

	@Deprecated
	boolean isExist(String tableName, String id, String type);

	boolean isExist(String tableName, String primaryKeyColumn, Serializable id, String type);

	void revertColumn(ExtendedFieldDetail efd);

	List<ExtendedFieldDetail> getExtendedFieldDetailById(long moduleId, int extendedType, String type);
}