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
 * * FileName : ManualAdviseDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified Date
 * : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennanttech.pff.core.TableType;

public interface ManualAdviseDAO extends BasicCrudDao<ManualAdvise> {

	ManualAdvise getManualAdviseById(long adviseID, String type);

	List<ManualAdvise> getManualAdviseByRef(long finID, int adviseType, String type);

	void saveMovement(ManualAdviseMovements movement, String type);

	void updateAdvPayment(ManualAdvise advise, TableType tableType);

	List<ManualAdviseMovements> getAdviseMovements(long id);

	ManualAdvise getManualAdviseByReceiptId(long receiptID, String string);

	List<ManualAdviseMovements> getAdviseMovementsByReceipt(long receiptID, String type);

	void deleteMovementsByReceiptID(long receiptID, String type);

	List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, int adviseType,
			String string);

	void updateMovementStatus(long receiptID, long receiptSeqID, String string, String string2);

	List<ManualAdviseReserve> getPayableReserveList(long receiptSeqID);

	ManualAdviseReserve getPayableReserve(long receiptSeqID, long payAgainstID);

	void savePayableReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt);

	void updatePayableReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve);

	void deletePayableReserve(long receiptID, long payAgainstID);

	void updatePayableReserve(long payAgainstID, BigDecimal reserveAmt);

	void updateUtilise(long adviseID, BigDecimal amount, boolean noManualReserve);

	void updateUtiliseOnly(long adviseID, BigDecimal amount);

	void reverseUtilise(long adviseID, BigDecimal amount);

	Date getPresentmentBounceDueDate(long receiptId);

	List<Long> getBounceAdvisesListByRef(long finID, int adviseType, String type);

	void deleteByAdviseId(ManualAdvise manualAdvise, TableType tableType);

	FinanceMain getFinanceDetails(long finID);

	BigDecimal getBalanceAmt(long finID);

	String getTaxComponent(long adviseID, String type);

	List<ManualAdvise> getManualAdvise(long finID);

	List<ManualAdvise> getManualAdvisesByFinRef(long finID, String type);

	List<ManualAdviseMovements> getDMAdviseMovementsByFinRef(long finID, String type);

	// ### Ticket id :124998
	List<ManualAdvise> getManualAdviseByRef(long finID, String feeTypeCode, String type);

	// Refund Uploads
	List<ManualAdvise> getManualAdviseByRefAndFeeCode(long finID, int adviseType, String feeTypeCode);

	List<ManualAdvise> getManualAdviseByRefAndFeeId(int adviseType, long feeTypeId);

	void updatePaidAmountOnly(long adviseID, BigDecimal amount);

	List<ManualAdvise> getManualAdviseByRef(long finID, int adviseType, String type, Date valuDate);

	Date getManualAdviseDate(long finID, Date valueDate, String string, int manualAdviseReceivable);

	List<ManualAdviseMovements> getInProcManualAdvMovmnts(List<Long> receiptList);

	List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, String string);

	List<ManualAdvise> getPreviousAdvPayments(long finID);

	void saveDueTaxDetail(AdviseDueTaxDetail dueTaxDetail);

	boolean isAdviseDueCreated(long adviseID);

	long getNewAdviseID();

	Long getDebitInvoiceID(long adviseID);

	ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, String type);

	ManualAdviseMovements getAdvMovByReceiptSeq(long receiptID, long receiptSeqID, long adviseId, String type);

	void updatePayableReserveAmount(long payAgainstID, BigDecimal reserveAmt);

	BigDecimal getReceivableAmt(long finID, boolean isBounce);

}