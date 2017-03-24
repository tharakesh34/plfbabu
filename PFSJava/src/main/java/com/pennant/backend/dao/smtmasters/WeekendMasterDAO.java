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
 * FileName    		:  WeekendMasterDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.smtmasters;
import com.pennant.backend.model.smtmasters.WeekendMaster;

/**
 * DAO methods declaration for the <b>WeekendMaster model</b> class.<br>
 * 
 */
public interface WeekendMasterDAO {

	WeekendMaster getWeekendMasterByID(String id,String type);
	void update(WeekendMaster weekendMaster,String type);
	void delete(WeekendMaster weekendMaster,String type);
	String save(WeekendMaster weekendMaster,String type);
	//ErrorDetails getErrorDetail (String errorId,String errorLanguage,String[] parameters);
	WeekendMaster getWeekendMasterByCode(String weekendCode);
}