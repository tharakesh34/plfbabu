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
 * FileName    		:  CustomerLimitDetailsService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.limit;

import java.io.InputStream;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public interface LimitDetailService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	LimitHeader getApprovedCustomerLimits(long l);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader, boolean fromScreen);

	AuditHeader doReject(AuditHeader auditHeader);

	LimitHeader getCustomerLimits(long limitHeaderId);

	List<LimitReferenceMapping> getLimitReferences(LimitDetails customerLimitDetails);

	int validationCheck(String lmtGrp, String type);

	int limitItemCheck(String lmtItem, String limitCategory,String type );

	LimitHeader getLimitHeader();

	LimitHeader getNewLimitHeader();

	int limitStructureCheck(String structureCode, String type);

	List<LimitTransactionDetail> getLimitTranDetails(String code, String ref,long headerId);

	LimitHeader procExternalFinance(InputStream streamData, LoggedInUser user);
	
	LimitHeader getLimitHeaderByCustomer(long custId);
	
	LimitHeader getLimitHeaderByCustomerGroupCode(long custGrpId);
	
	LimitHeader getLimitHeaderById(long headerId);

	AuditDetail doValidations(AuditHeader auditHeader);
	
	int limitLineUtilizationCheck(String lmtGrp);

	int getLimitHeaderCountById(long headerId);

	List<String> getLinesForGroup(String groupCode);

	LimitHeader getCustomerLimitsById(long headerId);
	
	// Limit Rebuild Process
	List<Object> processCustomerRebuild(long custId);
	List<Object> processCustomerGroupRebuild(long groupId);
}