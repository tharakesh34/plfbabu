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
 * FileName    		:  LegalDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.legal;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennanttech.pff.core.TableType;

public interface LegalDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	LegalDetail getLegalDetail(long legalId);

	LegalDetail getApprovedLegalDetail(long legalId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	void saveLegalDetails(FinanceDetail financeDetail, Object apiHeader);

	AuditHeader isLegalApproved(AuditHeader auditHeader);

	/**
	 * Checks whether all Legal Details Completed with Legal Decision as “Positive” for the Loan.
	 * 
	 * @param auditHeader
	 *            The audit header.
	 * @return True if all Legal Details Completed with Legal Decision as “Positive” for the Loan. Otherwise False.
	 */
	AuditHeader isLegalCompletedAsPositive(AuditHeader auditHeader);

	List<LegalDetail> getApprovedLegalDetail(FinanceMain aFinanceMain) throws Exception;

	LegalDetail formatLegalDetails(LegalDetail legalDetail) throws Exception;

	void saveDocumentDetails(DocumentDetails details);

	List<DocumentDetails> getDocumentDetails(String reference, String module);

	DocumentDetails getDocDetailByDocId(long docId, String string, boolean readAttachment);

	void saveDocumentDetails(List<DocumentDetails> documentsList);

	List<LegalDetail> getLegalDetailByFinreference(String finReference);

	List<AuditDetail> validateDetailsFromLoan(FinanceDetail financeDetail, String auditTranType, String method);

	List<AuditDetail> processLegalDetails(AuditHeader aAuditHeader, String method);

	void deleteList(String finReference, TableType tempTab);

}