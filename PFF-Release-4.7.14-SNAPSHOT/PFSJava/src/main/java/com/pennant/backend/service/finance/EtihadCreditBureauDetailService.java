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
 * FileName    		:  EtihadCreditBureauDetailService.java                                                   * 	  
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

package com.pennant.backend.service.finance;



import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.EtihadCreditBureauDetail;

/**
 * Service declaration for methods that depends on <b>EtihadCreditBureauDetail</b>.<br>
 * 
 */
public interface EtihadCreditBureauDetailService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	EtihadCreditBureauDetail getEtihadCreditBureauDetailById(String id,String type);
	EtihadCreditBureauDetail getApprovedEtihadCreditBureauDetailById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	AuditDetail saveOrUpdate(EtihadCreditBureauDetail etihadCreditBureauDetail, String tableType, String auditTranType);
	AuditDetail doApprove(EtihadCreditBureauDetail etihadCreditBureauDetail, String tableType, String auditTranType);
	AuditDetail validate(EtihadCreditBureauDetail etihadCreditBureauDetail, String method, String auditTranType, String  usrLanguage);
	AuditDetail delete(EtihadCreditBureauDetail etihadCreditBureauDetail, String tableType, String auditTranType);
}