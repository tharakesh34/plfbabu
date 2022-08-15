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
 * * FileName : TdsReceivablesTxnService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.tds.receivables;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennanttech.pff.core.TableType;

public interface TdsReceivablesTxnService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<TdsReceivablesTxn> getTdsReceivablesTxnsByTanId(long tanId, Date fromDate, Date toDate);

	Date getMaxFinancialDate(long TanId);

	int getPendingTransactions(long receivableId);

	List<TdsReceivablesTxn> getTdsReceivablesTxnByReceivableId(long receivableId);

	public void cancelReceivablesTxnByReceiptId(long receiptId);

	List<TdsReceivablesTxn> getTdsReceivablesTxnsByTxnId(long txnId, TableType type, String module);

	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId, TableType type, boolean showZeroBal);

	List<TdsReceivablesTxn> getTdsReceivablesTxnsByFinRef(String finReference, TableType type);

	public void deleteTxnByReceiptId(long receiptId);

	public long getPendingReceipt(long receiptID, TableType type);

}