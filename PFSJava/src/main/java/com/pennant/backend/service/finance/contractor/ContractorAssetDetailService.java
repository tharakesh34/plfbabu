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
 * FileName    		:  ContractorAssetDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.contractor;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;

public interface ContractorAssetDetailService {
	
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	ContractorAssetDetail getContractorAssetDetailById(String finReference, long contractorId);
	ContractorAssetDetail getApprovedContractorAssetDetailById(String finReference, long contractorId);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<ContractorAssetDetail> getContractorAssetDetailList(String finReference, String tableType);
	void setContractorAssetDetails(FinanceDetail financeDetail, String tableType);
	List<AuditDetail> getAuditDetail(List<ContractorAssetDetail> contractorAssetDetails, FinanceMain financeMain, String auditTranType, String method);	
	List<AuditDetail> validate(FinanceDetail financeDetail, String method,String  usrLanguage);
	List<AuditDetail> saveOrUpdate(String finReference, List<ContractorAssetDetail> contractorAssetDetails, String tableType, String auditTranType);
	List<AuditDetail> doApprove(List<ContractorAssetDetail> contractorAssetDetails, String tableType, String auditTranType);
	List<AuditDetail> delete(List<ContractorAssetDetail> contractorAssetDetails, String tableType, String auditTranType);
}