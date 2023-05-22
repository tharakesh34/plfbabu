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
 * * FileName : FinFeeReceiptDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 1-06-2017 * * Modified Date
 * : 1-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 1-06-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinFeeReceipt;

public interface FinFeeReceiptDAO {

	FinFeeReceipt getFinFeeReceiptById(FinFeeReceipt finFeeReceipt, String type);

	void update(FinFeeReceipt finFeeReceipt, String type);

	void delete(FinFeeReceipt finFeeReceipt, String type);

	long save(FinFeeReceipt finFeeReceipt, String type);

	List<FinFeeReceipt> getFinFeeReceiptByFinRef(List<Long> feeIds, String type);

	boolean isFinFeeReceiptAllocated(long receiptID, String type);

	List<FinFeeReceipt> getFinFeeReceiptByFeeId(long feeId, String type);

	BigDecimal getUpfrontFee(long feeId, String tableType);

	List<Map<String, Object>> getFeeDetails(String finReference);

	List<FinFeeReceipt> getFinFeeReceiptByReceiptId(long receiptID, String type);

	void deleteFinFeeReceiptByReceiptId(long receiptID, String suffix);

	List<FinFeeReceipt> getFinFeeReceiptByFeeType(String finrReference, String feeType);
}