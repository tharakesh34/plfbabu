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
 * FileName    		:  CheckListDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.applicationmaster.CheckListDetail;

public interface CheckListDetailDAO {

	CheckListDetail getCheckListDetail();
	CheckListDetail getNewCheckListDetail();
	CheckListDetail getCheckListDetailById(long id, String type);
	void update(CheckListDetail checkListDetail, String type);
	void delete(CheckListDetail checkListDetail, String type);
	void delete(long checkListId, String type);
	long save(CheckListDetail checkListDetail, String type);
	List<CheckListDetail> getCheckListDetailByChkList(final long checkListId, String type);
	List<CheckListDetail> getCheckListDetailByChkList(final Map<String, Set<Long>> checkListIdMap, String type);
	CheckListDetail getCheckListDetailByDocType(String docType, String finType);
}