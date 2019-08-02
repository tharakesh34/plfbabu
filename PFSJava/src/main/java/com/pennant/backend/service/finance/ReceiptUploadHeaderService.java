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
 * FileName    		:  UploadHeaderService.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
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

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;

public interface ReceiptUploadHeaderService {

	ReceiptUploadHeader getUploadHeaderById(long id, boolean getSucessRecords);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	boolean isFileNameExist(String value);

	void updateStatus(ReceiptUploadDetail receiptUploadDetail);

	void uploadHeaderStatusCnt(long receiptUploadId, int sucessCount, int failedCount);

	boolean isFileDownloaded(long id, int receiptDownloaded);

	void updateUploadProgress(long id, int receiptDownloaded);

	void updateRejectStatusById(String id, String errorMsg);

	List<ManualAdvise> getManualAdviseByRef(String reference, String referenceCode, String type);

	long saveReceiptResponseFileHeader(String procName);

	List<ReceiptUploadDetail> getReceiptResponseDetails();

	List<UploadAlloctionDetail> getReceiptResponseAllocationDetails(String rootId);

	void updateReceiptResponseFileHeader(long batchId, int recordCount, int sCount, int fCount, String remarks);

	void updateReceiptResponseDetails(ReceiptUploadDetail receiptresponseDetail, long jobid);

	void updatePickBatchId(long jobid);

	String getLoanReferenc(String reference, String value);

	int getFinanceCountById(String reference, String string, boolean b);

	boolean isFinReferenceExitsWithEntity(String reference, String string, String validatedValue);

	boolean isReceiptDetailsExits(String reference, String paytypeCheque, String chequeNo, String favourNumber,
			String type);

	boolean isFinReferenceExists(String reference, String type, boolean b);

	boolean isChequeExist(String reference, String paytypeCheque, String chequeNo, String favourNumber, String type);

	boolean isOnlineExist(String reference, String subReceiptMode, String tranRef, String type);

}