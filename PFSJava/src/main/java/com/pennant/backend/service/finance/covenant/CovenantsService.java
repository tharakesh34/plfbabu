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
 * FileName    		:  FinCovenantTypeService.java                                                   * 	  
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

package com.pennant.backend.service.finance.covenant;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public interface CovenantsService {
	List<Covenant> getCovenants(String finreference, String module, TableType tableType);

	List<AuditDetail> saveOrUpdate(List<Covenant> covenants, TableType tableType, String auditTranType);

	List<AuditDetail> doApprove(List<Covenant> covenants, TableType tableType, String auditTranType, int docSize);

	List<AuditDetail> delete(List<Covenant> covenants, TableType tableType, String auditTranType);

	List<AuditDetail> validate(List<Covenant> covenants, long workflowId, String method, String auditTranType,
			String usrLanguage);

	FinanceDetail getFinanceDetailById(String id, String type, String userRole, String moduleDefiner,
			String eventCodeRef);

	List<AuditDetail> validateOTC(FinanceDetail financeDetail);

	List<AuditDetail> processCovenants(List<Covenant> covenants, TableType tableType, String auditTranType,
			boolean isApproveRcd);

	List<AuditDetail> validateCovenant(List<AuditDetail> auditDetails, String usrLanguage, String method);

	List<AuditDetail> doProcess(List<Covenant> covenants, TableType tableType, String auditTranType,
			boolean isApproveRcd, int docSize);

	List<ErrorDetail> validatePDDDocuments(String finReference, List<ErrorDetail> errorDetails);

	void deleteDocumentByDocumentId(Long documentId, String tableType);
}