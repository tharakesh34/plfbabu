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
 * FileName    		:  DepositChequesDAO.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-07-2018    														*
 *                                                                  						*
 * Modified Date    :  18-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-07-2018       PENNANT	                 0.1                                            * 
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

import com.pennant.backend.model.finance.DepositCheques;
import com.pennanttech.pff.core.TableType;

public interface DepositChequesDAO {

	List<DepositCheques> getDepositChequesList(long id, String type);
	boolean isDuplicateKey(long processId, TableType tableType);
	void update(DepositCheques depositCheques, String type);
	String save(DepositCheques depositCheques, String type);
	void delete(DepositCheques depositCheques, String type);
	void deleteByMovementId(long movementId, String tableType);
	List<DepositCheques> getDepositChequesList(String branchCode);
	DepositCheques getDepositChequeByReceiptID(long receiptID);
	void reverseChequeStatus(long movementId, long receiptID, long linkedTranId);
}