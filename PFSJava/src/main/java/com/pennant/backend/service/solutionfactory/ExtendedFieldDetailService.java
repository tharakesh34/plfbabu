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
 * FileName    		:  ExtendedFieldDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.solutionfactory;

import java.util.List;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;

public interface ExtendedFieldDetailService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ExtendedFieldDetail getExtendedFieldDetailById(long id, String fieldName);

	ExtendedFieldDetail getApprovedExtendedFieldDetailById(long id, String fieldName);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	ExtendedFieldHeader getExtendedFieldHeaderById(ExtendedFieldHeader extendedFieldHeader);

	void revertColumn(ExtendedFieldDetail efd);
	
	List<ErrorDetails> doValidations(ExtendedFieldHeader extendedFieldHeader);
	
	ExtendedFieldHeader getExtendedFieldHeaderByModuleName(String moduleName,String subModuleName,String type);
	
	List<ExtendedFieldDetail> getExtendedFieldDetailByModuleID(long id, String type);
}