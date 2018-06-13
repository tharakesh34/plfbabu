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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  PostingsDAO.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  07-02-2012    
 *                                                                  
 * Modified Date    :  07-02-2012    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-02-2012       PENNANT TECHONOLOGIES	                 0.1                            * 
 *                                                                                          * 
 * 13-06-2018       Siva					 0.2        Stage Accounting Modifications      * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinStageAccountingLog;


/**
 * DAO methods declaration for the <b>ReturnDataSet model</b> class.<br>
 * 
 */
public interface FinStageAccountingLogDAO {
	
	long getLinkedTranId(String finReference,String finevent, String roleCode);
	void saveStageAccountingLog(FinStageAccountingLog stageAccountingLog);
	void deleteByRefandRole(String finReference, String finEvent, String roleCode);
	List<Long> getLinkedTranIdList(String finReference, String finEvent);
	void update(String finReference, String finEvent,boolean processed);
	int getTranCountByReceiptNo(String receiptNo);
	void updateByReceiptNo(String receiptNo);
	List<Long> getTranIdListByReceipt(String receiptNo);
	void deleteByReceiptNo(String receiptNo);
	
}
