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
 * FileName    		:  FinTypeFeesService.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  3-04-2017    														*
 *                                                                  						*
 * Modified Date    :  3-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.model.rmtmasters.FinTypeFees;

/**
 * Service Declaration for methods that depends on <b>FinanceType</b>.<br>
 * 
 */
public interface FinTypeFeesService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	List<FinTypeFees> getFinTypeFeesById(String finType, int moduleId);
	List<FinTypeFees> getApprovedFinTypeFeesById(String id, int moduleId);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<AuditDetail> setFinTypeFeesAuditData(List<FinTypeFees> finTypeFeesList, String auditTranType, String method);
	List<AuditDetail> processFinTypeFeesDetails(List<AuditDetail> auditDetails, String type);
	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);
	
	List<AuditDetail> delete(List<FinTypeFees> finTypeFeesList,  String tableType, String auditTranType, String finType, int moduleId);
}