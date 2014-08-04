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
 * FileName    		:  HomeLoanDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;

/**
 * Service declaration for methods that depends on <b>HomeLoanDetail</b>.<br>
 * 
 */
public interface HomeLoanDetailService {
	
	HomeLoanDetail getHomeLoanDetail();
	HomeLoanDetail getNewHomeLoanDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	HomeLoanDetail getHomeLoanDetailById(String id);
	HomeLoanDetail getApprovedHomeLoanDetailById(String id);
	HomeLoanDetail refresh(HomeLoanDetail homeLoanDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<LovFieldDetail> getHomeConstructionStage();
	
	public AuditDetail saveOrUpdate(HomeLoanDetail homeLoanDetail, String tableType, String auditTranType);
	public AuditDetail doApprove(HomeLoanDetail homeLoanDetail, String tableType, String auditTranType);
	public AuditDetail validate(HomeLoanDetail homeLoanDetail, String method, String auditTranType, String  usrLanguage);
	public AuditDetail delete(HomeLoanDetail homeLoanDetail, String tableType, String auditTranType);
	
	
}