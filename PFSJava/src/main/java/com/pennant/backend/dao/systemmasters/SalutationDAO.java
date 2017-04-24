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
 * FileName    		:  SalutationDAO.java                                                   * 	  
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

package com.pennant.backend.dao.systemmasters;
import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declarations for the <b>Salutation model</b> class.<br>
 * 
 */
public interface SalutationDAO extends BasicCrudDao<Salutation> {

	Salutation getSalutationById(String id,String type);
	
	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param salutationCode
	 *            salutationCode of the salutation.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String salutationCode, TableType tableType);
	String getSystemDefaultCount(String salutationCode);
	void updateSytemDefaultByGender(String genderCode,boolean systemDefault);
	
}