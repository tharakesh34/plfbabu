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
package com.pennant.backend.dao.feerefund;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennanttech.pff.core.TableType;

public interface FeeRefundHeaderDAO {

	FinanceMain getFinanceDetails(long finID);

	List<ManualAdvise> getManualAdvise(long finID);

	FeeRefundHeader getFeeRefundHeader(long headerID, String type);

	long save(FeeRefundHeader frh, TableType tableType);

	int update(FeeRefundHeader frh, TableType tableType);

	void delete(FeeRefundHeader frh, TableType tableType);

	void updateApprovalStatus(long headerID, String isDownloaded);

	boolean isFileDownloaded(long id, String isDownloaded);

	BigDecimal getDueAgainstLoan(long finId);

	BigDecimal getDueAgainstCustomer(long custId, String custCoreBank);

}