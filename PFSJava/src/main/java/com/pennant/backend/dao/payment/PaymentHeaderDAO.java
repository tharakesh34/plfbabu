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
 * * FileName : PaymentHeaderDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennanttech.pff.core.TableType;

public interface PaymentHeaderDAO extends BasicCrudDao<PaymentHeader> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param paymentId paymentId of the PaymentHeader.
	 * @param tableType The type of the table.
	 * @return PaymentHeader
	 */
	PaymentHeader getPaymentHeader(long paymentId, String type);

	boolean isDuplicateKey(long paymentId, TableType tableType);

	FinanceMain getFinanceDetails(long finID);

	List<FinExcessAmount> getfinExcessAmount(long finID);

	List<ManualAdvise> getManualAdvise(long finID);

	List<ManualAdvise> getManualAdviseForEnquiry(long finID);

	long getNewPaymentHeaderId();

	BigDecimal getDueAgainstCustomer(long custId, String coreBankId);

	BigDecimal getDueAgainstLoan(long finId);

	Map<Long, BigDecimal> getAdvisesInProgess(long finId);

	Long getPaymentIdByFinId(long finID, long receiptId, String type);

	BigDecimal getInProgressExcessAmt(long finId, long receiptId);

}