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
 * FileName    		:  FinSuspHoldDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement;

import com.pennant.backend.model.financemanagement.FinSuspHold;

/**
 * DAO methods declaration for the <b>FinSuspHold model</b> class.<br>
 * 
 */
public interface FinSuspHoldDAO {

	FinSuspHold getFinSuspHold();
	FinSuspHold getNewFinSuspHold();
	FinSuspHold getFinSuspHoldById(long id, String type);
	void update(FinSuspHold finSuspHold, String type);
	void delete(FinSuspHold finSuspHold, String type);
	long save(FinSuspHold finSuspHold, String type);
	FinSuspHold getFinSuspHoldByDetails(FinSuspHold finSuspHold, String type);
	boolean holdSuspense(String product, String finType, String finReference, long custID);
	
}