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
 * FileName    		:  CashDenominationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2018    														*
 *                                                                  						*
 * Modified Date    :  11-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.finance.CashDenomination;
import com.pennanttech.pff.core.TableType;

public interface CashDenominationDAO {
	
	List<CashDenomination> getCashDenominationList(long id, String type);
	boolean isDuplicateKey(long processId, TableType tableType);
	void update(CashDenomination cashDenomination, String type);
	String save(CashDenomination cashDenomination, String type);
	void delete(CashDenomination cashDenomination, String type);
	void deleteByMovementId(long movementId, String tableType);
	
}