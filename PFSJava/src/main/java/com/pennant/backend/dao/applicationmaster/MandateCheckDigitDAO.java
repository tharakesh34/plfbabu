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
 * FileName    		:  MandateCheckDigitDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-12-2017    														*
 *                                                                  						*
 * Modified Date    :  11-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-12-2017       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennanttech.pff.core.TableType;

public interface MandateCheckDigitDAO extends BasicCrudDao<MandateCheckDigit> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param checkDigitValue
	 *            checkDigitValue of the MandateCheckDigit.
	 * @param tableType
	 *            The type of the table.
	 * @return MandateCheckDigit
	 */
	MandateCheckDigit getMandateCheckDigit(int checkDigitValue, String type);
	
	boolean isDuplicateKey(long checkDigitValue, TableType tableType);
	
	int getCheckDigit(int checkDigitValue,String lookUpValue,String tableType);

}