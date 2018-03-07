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
 * FileName    		:  IRRFeeTypeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennanttech.pff.core.TableType;

public interface IRRFeeTypeDAO extends BasicCrudDao<IRRFeeType> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param iRRID
	 *            iRRID of the IRRFeeType.
	 * @param tableType
	 *            The type of the table.
	 * @return IRRFeeType
	 */
	IRRFeeType getIRRFeeType(long iRRID, String type);

	String save(IRRFeeType irrFeeType, TableType tableType);

	void update(IRRFeeType irrFeeType, TableType tableType);

	void delete(IRRFeeType irrFeeType, TableType tableType);

	void deleteList(IRRFeeType irrFeeType, TableType type);

	List<IRRFeeType> getIRRFeeTypeList(long iRRID, String type);

}