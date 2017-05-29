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
 * FileName    		:  LovFieldDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>LovFieldDetail model</b> class.<br>
 * 
 */
public interface LovFieldDetailDAO extends BasicCrudDao<LovFieldDetail> {

	LovFieldDetail getLovFieldDetailById(String fieldCode, String fieldCodeValue, String type);

	int getSystemDefaultCount(String fieldCode, String fieldCodeValue);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param fieldCode
	 *            of LovFieldDetails
	 * @param fieldDetail
	 *            of LovFieldDetails
	 * @param tableType
	 *            of LovFieldDetails
	 * @return
	 */
	boolean isDuplicateKey(String fieldCode, String fieldDetail, TableType tableType);
}