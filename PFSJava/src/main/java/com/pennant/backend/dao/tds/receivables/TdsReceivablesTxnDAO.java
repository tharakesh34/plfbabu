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
 * * FileName : TDSReceivablesTxnDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * * Modified
 * Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.tds.receivables;

import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennanttech.pff.core.TableType;

public interface TdsReceivablesTxnDAO extends BasicCrudDao<TdsReceivablesTxn> {

	/**
	 * Fetch the Record TDSReceivablesTxn by key field
	 * 
	 * @param iD        iD of the TDSReceivablesTxn.
	 * @param tableType The type of the table.
	 * @return TDSReceivablesTxn
	 */

	List<TdsReceivablesTxn> getTdsReceivablesTxnsByTxnId(long txnId, TableType type, String module);

	long getAdjustmentTxnSeq();

	List<TdsReceivablesTxn> getTdsReceivablesTxnsByTanId(long tanId, Date fromDate, Date toDate);

	List<TdsReceivablesTxn> getTdsReceivablesTxnByReceivableId(long receivableID, TableType type);

	Date getMinRcptFinancialDate(long tanId);

	int isDuplicateTransaction(long tanId, Date fromDate, Date toDate);

	int getPendingTransactions(long receivableId);

	void updateReceivablesTxnStatus(long Id, TdsReceivablesTxnStatus Status);

	List<TdsReceivablesTxn> getTdsReceivablesTxnByReceiptId(long receiptId, TableType type);

	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId, TableType type, boolean showZeroBal);

	List<TdsReceivablesTxn> getTdsReceiptTxnsByFinRef(String finReference, TableType type);

	List<TdsReceivablesTxn> getTdsJvPostingsTxnsByFinRef(String finReference, TableType type);

	long getPendingReceipt(long receiptId, TableType type);

	public void deleteTxnByReceiptId(long receiptId);

	List<TdsReceivablesTxn> getTdsReceivablesPostTxnsByTanId(long tanId, Date fromDate, Date toDate);

	List<TdsReceivablesTxn> getTdsReceivablesPostTxnsByTxnId(long txnId, TableType type, String module);

	Date getMinPostFinancialDate(long tanId);

}