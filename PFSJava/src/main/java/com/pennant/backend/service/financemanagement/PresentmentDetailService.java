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
 * * FileName : presentmentDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-05-2017 * *
 * Modified Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;

public interface PresentmentDetailService {

	PresentmentHeader getPresentmentHeader(long id);

	String savePresentmentDetails(PresentmentHeader presentmentHeader) throws Exception;

	List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type);

	void updatePresentmentIdAsZero(long presentmentId);

	long getSeqNumber(String tableNme);

	String getPaymenyMode(String presentmentRef);

	void processReceipts(PresentmentDetail presentmentDetail) throws Exception;

	void updatePresentmentIdAsZero(List<Long> presentmentIds);

	FinanceDetail getFinanceDetailsByRef(long finID);

	FinanceMain getDefualtPostingDetails(long finID, Date schDate);

	PresentmentDetail getPresentmentDetailByFinRefAndPresID(long finID, long presentmentId);

	void saveModifiedPresentments(List<Long> excludeList, List<Long> includeList, long presentmentId,
			long partnerBankId);

	boolean searchIncludeList(long presentmentId, int excludereason);

	List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude);

	List<PresentmentHeader> getPresenmentHeaderList(Date fromDate, Date toDate, int status);

	List<Long> getIncludeList(long id);

	List<Long> getExcludeList(long id);

	void updatePresentmentDetails(PresentmentHeader presentmentHeader);

	void processSuccessPresentments(long receiptId);

	void executeReceipts(PresentmentDetail presentmentDetail, boolean isFullEMIPresent, boolean isRealized)
			throws Exception;

	void updatePresentmentDetail(long id, String pexcSuccess, String utrNumber);

	void updatePresentmentDetail(long id, String status, Long linkedTranId, String utrNumber);

	void setProperties(PresentmentDetailExtract pde);
}