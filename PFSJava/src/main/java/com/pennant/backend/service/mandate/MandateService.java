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
 * FileName    		:  MandateService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.mandate;

import java.util.List;

import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface MandateService {
	
	Mandate getMandate();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	Mandate getMandateById(long id);
	Mandate getMandateStatusUpdateById(long id,String status);
	Mandate getApprovedMandateById(long id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	void processDownload(Mandate mandate);
	void processFileUpload(Mandate mandate, String status, String reason, long fileID);
	void processStatusUpdate(MandateStatusUpdate mandateStatusUpdate);
	long processStatusSave(MandateStatusUpdate mandateStatusUpdate);
	List<FinanceEnquiry> getMandateFinanceDetailById(long mandateID);
	int getFileCount(String fileName);
	List<Mandate>getApprovedMandatesByCustomerId(long custID);
	void getDocumentImage(Mandate mandate);
	byte[] getDocumentManImage(String mandateRef);
	MandateCheckDigit getLookUpValueByCheckDigit(int rem);
	List<ErrorDetail>  doValidations(Mandate mandate);
	int getSecondaryMandateCount(long mandateID);
}