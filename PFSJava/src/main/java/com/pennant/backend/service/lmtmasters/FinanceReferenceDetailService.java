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
 * FileName    		:  FinanceReferenceDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FinanceReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;

public interface FinanceReferenceDetailService {
	
	FinanceReferenceDetail getFinanceReferenceDetail();
	FinanceReferenceDetail getNewFinanceReferenceDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	FinanceReferenceDetail getFinanceReferenceDetailById(long id);
	FinanceReferenceDetail getApprovedFinanceReferenceDetailById(long id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	FinanceReference getFinanceReference(String finType, String finEvent, String moduleName);
	List<FinanceReferenceDetail> getFinRefDetByRoleAndFinType(final String financeType, String finEvent,
			String mandInputInStage, String type);	
	List<ValueLabel> getTemplateIdList(String financeType,String finEvent, String roleCode, List<String> lovCodeList);
	FinanceReferenceDetail getTemplateId(String financeType,String finEvent, String roleCode, String lovCodeList);
	List<Long> getRefIdListByFinType(final String financeType, String finEvent, String roleCode, String type);
	List<Long> getFinTypeAccounting(String fintype, List<String> events);
	List<FinTypeFees> getFinTypeFeesList(String finType, List<String> finEvents, String type, int moduleId);
	Map<String,String> getAccountingFeeCodes(List<Long> accountSetId);

	boolean resendNotification(String finType, String finEvent, String role, List<String> templateTyeList);

	List<ValueLabel> getTemplateIdList(String finType, String finEvent, String role, List<String> templateTyeList,
			List<FinanceReferenceDetail> finReferenceDetail);

	List<Long> getNotifications(String financeType, String finEvent, String roleCode, List<String> lovCodeList);
}