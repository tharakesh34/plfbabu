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
 * FileName    		:  EducationalLoanService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.EducationalLoan;

/**
 * Service declaration for methods that depends on <b>EducationalLoan</b>.<br>
 * 
 */
public interface EducationalLoanService {
	
	EducationalLoan getEducationalLoan();
	EducationalLoan getNewEducationalLoan();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	EducationalLoan getEducationalLoanById(String id);
	EducationalLoan getApprovedEducationalLoanById(String id);
	EducationalLoan refresh(EducationalLoan educationalLoan);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	public EducationalLoan getEducationalLoanById(String finReference, String tableType);
	public void setEducationalLoanDetails(FinanceDetail financeDetail, String tableType);
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType, String auditTranType);
	public List<AuditDetail> doApprove(FinanceDetail financeDetail, String recordType, String auditTranType);
	public List<AuditDetail> delete(FinanceDetail financeDetail, String tableType, String auditTranType);
	public List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method);	
	public List<AuditDetail> validate(FinanceDetail financeDetail, String method,String  usrLanguage);
	
}