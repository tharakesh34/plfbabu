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
 * FileName    		:  FinAdvancePaymentsService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;

public interface FinAdvancePaymentsService {
	List<FinAdvancePayments> getFinAdvancePaymentsById(String id,String type);
	List<AuditDetail> saveOrUpdate(List<FinAdvancePayments> finAdvancePaymentDetails, String tableType, String auditTranType);
	List<AuditDetail> doApprove(List<FinAdvancePayments> finAdvancePaymentDetails, String tableType, String auditTranType);
	List<AuditDetail> delete(List<FinAdvancePayments> finAdvancePaymentDetails, String tableType, String auditTranType);
	List<AuditDetail> validate(List<FinAdvancePayments> finAdvancePaymentDetails, long workflowId, String method, String auditTranType, String  usrLanguage);
	void processDisbursments(FinanceDetail financeDetail);
	List<AuditDetail> processQuickDisbursment(FinanceDetail financeDetail, String tableType, String auditTranType);
	void doCancel(FinanceDetail financeDetail);
	
}