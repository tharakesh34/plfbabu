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
 * FileName    		:  FinanceDisbursementDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceDisbursement;

public interface FinanceDisbursementDAO {
	FinanceDisbursement getFinanceDisbursementById(String id,String type,boolean isWIF);
	void update(FinanceDisbursement financeDisbursement,String type,boolean isWIF);
	void deleteByFinReference(String id,String type,boolean isWIF, long logKey);
	String save(FinanceDisbursement financeDisbursement,String type,boolean isWIF);
	List<FinanceDisbursement> getFinanceDisbursementDetails(String id, String type,boolean isWIF);
	List<FinanceDisbursement> getFinanceDisbursementDetails(String id, String type,boolean isWIF, long logKey);
	void delete(FinanceDisbursement financeDisbursement,String type,boolean isWIF);
	void saveList(List<FinanceDisbursement> financeDisbursement, String type,boolean isWIF);
	void updateLinkedTranId(String finReference, long linkedTranId, String type);
	void updateBatchDisb(FinanceDisbursement disbursement, String type);
	List<FinanceDisbursement> getDisbursementToday(String finRefernce, Date disbDate);
}