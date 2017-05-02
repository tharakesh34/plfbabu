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
 * FileName    		:  ManualAdviseDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennanttech.pff.core.TableType;

public interface ManualAdviseDAO extends BasicCrudDao<ManualAdvise> {
	
	
	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise.
	 * @param tableType
	 *            The type of the table.
	 * @return ManualAdvise
	 */
	ManualAdvise getManualAdviseById(long adviseID,String type);

	// Receipts
	List<ManualAdvise> getManualAdviseByRef(String finReference, int adviseType, String type);
	void saveMovement(ManualAdviseMovements movement, String type);
	void updateAdvPayment(long adviseID, BigDecimal paidAmount, BigDecimal waivedAmount, TableType tableType);
	List<ManualAdviseMovements> getAdviseMovements(long id);
	ManualAdvise getManualAdviseByReceiptId(long receiptID, String string);
	List<ManualAdviseMovements> getAdviseMovementsByReceipt(long receiptID, String type);
	void deleteMovementsByReceiptID(long receiptID, String type);
	List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, String string);
	void updateMovementStatus(long receiptID, long receiptSeqID, String string, String string2);
	
}