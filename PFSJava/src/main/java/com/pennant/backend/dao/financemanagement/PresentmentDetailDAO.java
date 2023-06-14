/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PresentmentDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-05-2017 * * Modified
 * Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.financemanagement;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.financemanagement.impl.PresentmentDetailExtractService;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public interface PresentmentDetailDAO {

	PresentmentHeader getPresentmentHeader(long id, String type);

	long getSeqNumber(String tableName);

	long savePresentmentHeader(PresentmentHeader presentmentHeader);

	List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type);

	int updatePresentmentDetials(long presentmentId, List<Long> list, int mnualExclude);

	void updatePresentmentHeader(long presentmentId, int pexcBatchCreated, Long partnerBankId);

	void updatePresentmentIdAsZero(long presentmentId);

	void deletePresentmentHeader(long id);

	void deletePresentmentDetails(long presentmentId);

	PresentmentDetail getPresentmentDetail(String presentmentRef, String type);

	PresentmentDetail getRePresentmentDetails(String presentmentRef);

	void updateReceptId(PresentmentDetail pd);

	List<PresentmentDetail> getPresentmenToPost(Customer customer, Date schData);

	List<PresentmentDetail> getPresentmentDetail(long presentmentId, boolean includeData);

	int getAssignedPartnerBankCount(long partnerBankId, String type);

	String getPaymenyMode(String presentmentRef);

	void updatePresentmentIdAsZero(List<Long> presentmentIds);

	List<PresentmentDetail> getPresentmensByExcludereason(long presentmentId, int excludeReason);

	PresentmentDetail getPresentmentDetailByFinRefAndPresID(long finID, long presentmentId, String string);

	boolean searchIncludeList(long presentmentId, int excludereason);

	List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude);

	void updateStatusAgainstReseipId(String status, long receiptID);

	List<PresentmentHeader> getPresentmentHeaderList(Date fromDate, Date toDate, int status);

	List<Long> getIncludeList(long id);

	List<Long> getExcludeList(long id);

	boolean isPresentmentInProcess(long finID);

	Long getApprovedPresentmentCount(long presentmentId);

	Presentment getPresentmentByBatchId(String batchId, String type);

	long saveList(List<PresentmentDetail> presentments);

	void extactPDCPresentments(PresentmentHeader ph, PresentmentDetailExtractService service);

	int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments);

	void extactPDCRePresentments(PresentmentHeader ph, PresentmentDetailExtractService service);

	void extactPresentments(PresentmentHeader ph, PresentmentDetailExtractService presentmentDetailExtractService);

	void extactRePresentments(PresentmentHeader ph, PresentmentDetailExtractService presentmentDetailExtractService);

	List<PresentmentDetail> getIncludePresentments(List<Long> headerIdList);

	FinanceMain getDefualtPostingDetails(long finID, Date schDates);

	void updatePresentmentDetail(long id, String status, String urtNumber);

	void updatePresentmentDetail(long id, String status, Long linkedTranId, String urtNumber);

	void updatePresentmentDetail(PresentmentDetail pd);

	int updateChequeStatus(long chequeDetailsId, String chequestatus);

	long logHeader(String fileName, String entityCode, String event, int totalRecords);

	void updateHeader(long headerId, long deExecutionId, int totalRecords, int successRecords, int failedRecords,
			String status, String remarks);

	int logRespDetails(long importHeaderId, long headerId);

	int getMinIDByHeaderID(long headerId);

	int getMaxIDByHeaderID(long headerId);

	int updateThreadID(long headerId, long from, long to, int i);

	List<Integer> getThreads(long headerId);

	List<PresentmentDetail> getPresentmentDetails(long headerId, int threadId);

	void logRespDetailError(long headerId, long detailId, String errorCode, String errorDesc);

	void updateDataEngineLog(long id, String presentmentRef, String errorCode, String errorDesc);

	List<DataEngineLog> getDEExceptions(long id);

	int logRespDetailsLog(long headerId);

	List<Long> getPresentmentHeaderIdsByHeaderId(long headerId);

	List<String> getStatusListByHeader(Long id);

	void updateHeaderCounts(Long id, int successCount, int failedCount);

	void updateHeaderStatus(Long id, int status);

	void truncate(String tableName);

	List<String> getUnProcessedPrentmntRef(long headerId);

	List<PresentmentDetail> getPresentmentStatusByFinRef(long finID);

	void deleteByHeaderId(long headerId);

	void logRequest(long headerId, Presentment presentment);

	PresentmentDetail getPresentmentByRef(String batchId);

	PresentmentDetail getPresentmentById(long presentmentId);

	boolean isFileProcessed(String fileName);

	String getPresentmentType(long id);

	List<Long> getManualExcludeList(long id);

	int getApprovedPresentmentCount(String finReference);

	void updateReceptIdAndAmounts(PresentmentDetail pd);

	void updateProgess(long importHeaderId, int i);

	PresentmentDetail getRePresentmentDetail(String finReference, Date SchDate);

	String getBackOfficeNameByBranchCode(String branchCode);

	Long getLatestMandateId(long finID);

	List<PresentmentDetail> getBouncedPresenments(long finID);
}