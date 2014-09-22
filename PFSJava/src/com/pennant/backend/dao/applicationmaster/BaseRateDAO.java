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
 * FileName    		:  BaseRateDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.applicationmaster.BaseRate;

/**
 * DAO methods declaration for the <b>BaseRate model</b> class.<br>
 */
public interface BaseRateDAO {

	BaseRate getBaseRate();
	BaseRate getNewBaseRate();
	BaseRate getBaseRateById(String bRType, Date bREffDate, String type);
	void update(BaseRate baseRate, String type);
	void delete(BaseRate baseRate, String type);
	void save(BaseRate baseRate, String type);
	void initialize(BaseRate baseRate);
	void refresh(BaseRate entity);
	BaseRate getBaseRateByType(final String bRType, Date bREffDate);
	boolean getBaseRateListById(String bRType, Date bREffDate, String type);
	List<BaseRate> getBSRListByMdfDate(Date bREffDate, String type);
	void deleteByEffDate(BaseRate baseRate, String type);
}