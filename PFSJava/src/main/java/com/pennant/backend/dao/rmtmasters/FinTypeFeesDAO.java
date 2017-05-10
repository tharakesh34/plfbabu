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
 * FileName    		:  FinTypeFeesDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;


import java.util.List;

import com.pennant.backend.model.rmtmasters.FinTypeFees;

/**
 * DAO methods declaration for the <b>FinTypeFees model</b> class.<br>
 * 
 */
public interface FinTypeFeesDAO {

	FinTypeFees getFinTypeFees();
	FinTypeFees getNewFinTypeFees();
	FinTypeFees getFinTypeFeesByID(FinTypeFees finTypeFees, String type); 
	List<FinTypeFees> getFinTypeFeesListByID(final String id, int moduleId, String type); 
	List<FinTypeFees> getFinTypeFeesList(String finType, String finEvent, String type, boolean origination, int moduleId);
	List<FinTypeFees> getFinTypeFeesList(String finType, List<String> finEvents, String type, int moduleId);
	List<FinTypeFees> getFinTypeFeeCodes(String finType, int moduleId); 
	void update(FinTypeFees finTypeFees, String type);
	String save(FinTypeFees finTypeFees, String type);
	void delete(FinTypeFees finTypeFees, String type);
	void deleteByFinType(String finType, String type, int moduleId); 
	void refresh(FinTypeFees entity);
	List<FinTypeFees> getFinTypeFeesList(String finEvent,List<String> finTypes, int moduleId);
	int getFinTypeFeesByRuleCode(String ruleCode, String type);

}