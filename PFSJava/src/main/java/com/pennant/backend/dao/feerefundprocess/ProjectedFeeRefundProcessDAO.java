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
 * * FileName : ProjectedAmortizationDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-01-2018 * *
 * Modified Date : 23-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-01-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.feerefundprocess;

import java.util.List;

import com.pennant.app.feerefundqueue.FeeRefundProcessQueuing;

public interface ProjectedFeeRefundProcessDAO {

	int prepareFeeRefundProcessQueue(long id);

	void insertLogTableAndTruncate();

	long getCountByProgress();

	int updateThreadIDByRowNumber(long rowNum, int threadId);

	void updateFailedQueue(FeeRefundProcessQueuing auQueuing);

	int[] prepareFeeRefundProcessQueue(List<FeeRefundProcessQueuing> refundQueuings);

	long getCountByFinReference();

	List<Long> getThreads();

	void updateStatusQueue(long id, int progress);
}
