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
 * FileName    		:  FinTypePartnerBankService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;

public interface FinTypePartnerBankService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	FinTypePartnerBank getFinTypePartnerBank(String finType, long iD);
	FinTypePartnerBank getApprovedFinTypePartnerBank(String finType, long iD);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	List<AuditDetail> setFinTypePartnerBankDetailsAuditData(List<FinTypePartnerBank> finTypePartnerBankList, String auditTranType, String method);
	List<AuditDetail> processFinTypePartnerBankDetails(List<AuditDetail> auditDetails, String type);
	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);
	
	List<AuditDetail> delete(List<FinTypePartnerBank> finTypePartnerBankList,  String tableType, String auditTranType, String finType);
	
	List<FinTypePartnerBank> getFinTypePartnerBanksList(String finType, String type);
	
	int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID);
}