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
 * * FileName : PaymentInstructionDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.feerefund;

import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennanttech.pff.core.TableType;

public interface FeeRefundInstructionDAO {

	long save(FeeRefundInstruction fri, TableType tableType);

	void update(FeeRefundInstruction fri, TableType tableType);

	void delete(FeeRefundInstruction fri, TableType tableType);

	FeeRefundInstruction getFeeRefundInstructionDetails(long headerID, String type);

	boolean isInstructionInProgress(long finID);
}