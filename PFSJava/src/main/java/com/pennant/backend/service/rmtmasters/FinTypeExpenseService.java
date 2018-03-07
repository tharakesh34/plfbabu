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
 * FileName    		:  FinTypeExpenseService.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  				   														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;

/**
 * Service Declaration for methods that depends on <b>FinTypeExpense</b>.<br>
 * 
 */
public interface FinTypeExpenseService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<FinTypeExpense> getFinTypeExpenseById(String finType);

	List<FinTypeExpense> getApprovedFinTypeExpenseById(String finType);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> setFinTypeExpenseAuditData(List<FinTypeExpense> finTypeExpenseList, String auditTranType,
			String method);

	List<AuditDetail> processFinTypeExpenseDetails(List<AuditDetail> auditDetails, String type);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);

	List<AuditDetail> delete(List<FinTypeExpense> finTypeExpenseList, String tableType, String auditTranType,
			String finType);
	
	FinTypeExpense getFinExpensesByFinType(String finType, long expenseTypeId);
}