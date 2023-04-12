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
 * * FileName : ManualAdviseService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified
 * Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.app.core.CustEODEvent;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface ManualAdviseService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ManualAdvise getManualAdviseById(long adviseID);

	ManualAdvise getApprovedManualAdvise(long adviseID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<ManualAdviseMovements> getAdivseMovements(long id);

	FinanceMain getFinanceDetails(long finID);

	String getTaxComponent(Long adviseID, String type);

	List<ReturnDataSet> getAccountingSetEntries(ManualAdvise manualAdvise);

	long getNewAdviseID();

	BigDecimal getBalanceAmt(long finID, Date valueDate);

	void cancelFutureDatedAdvises(CustEODEvent custEODEvent);

	void prepareManualAdvisePostings(CustEODEvent custEODEvent) throws Exception;

	int getFutureDatedAdvises(long finID);

	int cancelFutureDatedAdvises();

	void cancelManualAdvises(FinanceMain fm);

	BigDecimal getEligibleAmount(ManualAdvise ma, FeeType feeType);

	boolean isDuplicatePayble(long finID, long feeTypeId, String linkTo);

	boolean isPaybleExist(long finID, long feeTypeID, String linkTo);

	boolean isManualAdviseExist(long finID);

	boolean isunAdjustablePayables(long finID);

	boolean isAdviseUploadExist(long finID);

	BigDecimal getRefundedAmount(long finID, long feeTypeID);

	public BigDecimal getRefundedAmt(long finID, long receivableID, long receivableFeeTypeID);

	void updateAdviseStatusForFinCancel(String finReference);
}