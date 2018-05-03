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
 * FileName    		:  AgreementFieldDetailsService.java                                                   * 	  
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
import com.pennant.backend.model.finance.AgreementFieldDetails;


/**
 * Service declaration for methods that depends on <b>BundledProductsDetail</b>.<br>
 * 
 */
public interface AgreementFieldsDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	AgreementFieldDetails getAgreementFieldDetailsById(String id,String type);
	AgreementFieldDetails getApprovedAgreementFieldDetailsById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	AuditDetail saveOrUpdate(AgreementFieldDetails agreementFieldDetails, String tableType, String auditTranType);
	AuditDetail doApprove(AgreementFieldDetails agreementFieldDetails, String tableType, String auditTranType);
	AuditDetail validate(AgreementFieldDetails agreementFieldDetails, String method, String auditTranType, String  usrLanguage);
	AuditDetail delete(AgreementFieldDetails agreementFieldDetails, String tableType, String auditTranType);
}