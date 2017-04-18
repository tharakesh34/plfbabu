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
 * FileName    		:  SplRateDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.applicationmaster.SplRate;

/**
 * DAO methods declaration for the <b>SplRate model</b> class.<br>
 */
public interface SplRateDAO {
	SplRate getSplRateById(String id, Date date, String type);

	void update(SplRate splRate, String type);

	void delete(SplRate splRate, String type);

	void save(SplRate splRate, String type);

	SplRate getSplRateByID(final String id, Date date);

	List<SplRate> getSRListByMdfDate(Date date, String type);

	boolean getSplRateListById(String sRType, Date sREffDate, String type);

	void deleteByEffDate(SplRate splRate, String type);

	int getSpecialRateCountById(String repaySpecialRate, String type);

	List<SplRate> getSplRateHistByType(String sRType, Date sREffDate);
}