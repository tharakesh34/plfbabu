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
 * FileName    		:  ManualDeviationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2018    														*
 *                                                                  						*
 * Modified Date    :  03-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2018       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennanttech.pff.core.TableType;

public interface ManualDeviationDAO extends BasicCrudDao<ManualDeviation> {

	/**
	 * Fetch the Record ManualDeviation by key field
	 * 
	 * @param deviationID
	 *            deviationID of the ManualDeviation.
	 * @param tableType
	 *            The type of the table.
	 * @return ManualDeviation
	 */
	ManualDeviation getManualDeviation(long deviationID, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param deviationID
	 *            deviationID of the ManualDeviation.
	 * @param code
	 *            code of the ManualDeviation.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long deviationID, String code, TableType tableType);
	
	boolean isExistsFieldCodeID(long fieldCodeID, String type);
	
	long getDeviationIdByCode(String deviationCode);

	ManualDeviation getManualDeviationDesc(long deviationID);

}