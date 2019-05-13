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
 * FileName    		:  ManagerChequeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;

import com.pennant.backend.model.financemanagement.ManagerCheque;

public interface ManagerChequeDAO {
	ManagerCheque getManagerCheque();

	ManagerCheque getNewManagerCheque();

	ManagerCheque getManagerChequeById(long id, String type);

	ManagerCheque getReprintManagerChequeById(long id, String type);

	void update(ManagerCheque managerCheque, String type);

	void delete(ManagerCheque managerCheque, String type);

	long save(ManagerCheque managerCheque, String type);

	BigDecimal getTotalChqAmtByFinReference(String finReference, String type);

	int getMgrChqCountByChqPurposeCode(String chqPurposeCode, String type);

	int getMgrChqCountByChqNoAndAccount(long chequeID, String chqNo, String nostroAccount, String type);

	long getNextId();
}