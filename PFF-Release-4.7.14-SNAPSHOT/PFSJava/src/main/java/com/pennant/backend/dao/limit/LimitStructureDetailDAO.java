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
 * FileName    		:  LimitStructureDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.limit;

import java.util.List;

import com.pennant.backend.model.limit.LimitStructureDetail;

public interface LimitStructureDetailDAO {
	LimitStructureDetail getLimitStructureDetail();

	LimitStructureDetail getNewLimitStructureDetail();

	List<LimitStructureDetail> getLimitStructureDetailById(String id, String type);

	void update(LimitStructureDetail limitStructureDetail, String type);

	void delete(LimitStructureDetail limitStructureDetail, String type);

	long save(LimitStructureDetail limitStructureDetail, String type);
	
	void deleteByStructureCode(String code,String type);
	
	void deleteBySrtructureId(long id,String type);

	int validationCheck(String lmtGrp, String type);

	int limitItemCheck(String lmtGrp, String limitCategory, String type);

	LimitStructureDetail getLimitStructureDetail(long limitStructureId, String type);

	int getLimitStructureCountById(String structureCode, String type);

	List<LimitStructureDetail> getStructuredetailsByLimitGroup(String limitCategory, String code, boolean isLine,
			String type);

	void updateById(LimitStructureDetail limitStructureDetail, String type);

	LimitStructureDetail getStructureByLine(String limitStructureCode, String limitLine, boolean group);
}