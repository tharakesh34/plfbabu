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
 * FileName    		:  PresentmentDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.financemanagement;

import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.impl.PresentmentDetailExtractService;
import com.pennanttech.model.presentment.Presentment;

public interface PresentmentDetailDAO extends BasicCrudDao<PresentmentHeader> {

	PresentmentHeader getPresentmentHeader(long id, String type);

	long getSeqNumber(String tableName);

	long savePresentmentHeader(PresentmentHeader presentmentHeader);

	List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type);

	int updatePresentmentDetials(long presentmentId, List<Long> list, int mnualExclude);

	void updatePresentmentHeader(long presentmentId, int pexcBatchCreated, long partnerBankId);

	void updatePresentmentIdAsZero(long presentmentId);

	void deletePresentmentHeader(long id);

	void deletePresentmentDetails(long presentmentId);

	PresentmentDetail getPresentmentDetail(String presentmentRef, String type);

	void updateReceptId(long id, long receiptID);

	List<PresentmentDetail> getPresentmenToPost(long custId, Date schData);

	List<PresentmentDetail> getPresentmentDetail(long presentmentId, boolean includeData);

	void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc);

	void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc);

	int getAssignedPartnerBankCount(long partnerBankId, String type);

	String getPaymenyMode(String presentmentRef);

	void updatePresentmentIdAsZero(List<Long> presentmentIds);

	List<PresentmentDetail> getPresentmensByExcludereason(long presentmentId, int excludeReason);

	PresentmentDetail getPresentmentDetailByFinRefAndPresID(String finReference, long presentmentId, String string);

	boolean searchIncludeList(long presentmentId, int excludereason);

	List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude);

	void updateStatusAgainstReseipId(String status, long receiptID);

	List<PresentmentHeader> getPresentmentHeaderList(Date fromDate, Date toDate, int status);

	List<Long> getIncludeList(long id);

	List<Long> getExcludeList(long id);

	boolean isPresentmentInProcess(String finReference);

	String getPresentmentReference(long presentmentid, String finreference);

	Long getApprovedPresentmentCount(long presentmentId);

	Presentment getPresentmentByBatchId(String batchId, String type);

	long saveList(List<PresentmentDetail> presentments);

	void extactPDCPresentments(PresentmentHeader ph, PresentmentDetailExtractService service);

	int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments);

	void extactPDCRePresentments(PresentmentHeader ph, PresentmentDetailExtractService service);

	void extactPresentments(PresentmentHeader ph, PresentmentDetailExtractService presentmentDetailExtractService);

	void extactRePresentments(PresentmentHeader ph, PresentmentDetailExtractService presentmentDetailExtractService);

	List<PresentmentDetail> getIncludePresentments(List<Long> headerIdList);

	FinanceMain getDefualtPostingDetails(String finReference, Date schDates);

	String getPresementStatus(String presentmentRef);

	PresentmentDetail getPresentmentDetail(String batchId);

	void updatePresentmentDetail(String presentmentRef, String status);

	void updatePresentmentDetail(String presentmentRef, String status, Long linkedTranId);

	long getPresentmentId(String presentmentRef);

}