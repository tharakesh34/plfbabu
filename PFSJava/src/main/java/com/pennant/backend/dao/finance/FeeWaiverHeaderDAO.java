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
 * * FileName : FeeWaiverHeaderDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified
 * Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FeeWaiverHeader;

public interface FeeWaiverHeaderDAO extends BasicCrudDao<FeeWaiverHeader> {

	FeeWaiverHeader getFeeWaiverHeaderByFinRef(long finID, String type);

	FeeWaiverHeader getFeeWaiverHeaderById(long waiverId, String type);

	Date getLastWaiverDate(long finID, Date appDate, Date receiptDate);

	boolean isFeeWaiverInProcess(long finID);

	List<FeeWaiverHeader> getFeeWaiverHeaderByFinReference(long finID, String type);

	List<FeeWaiverHeader> fetchPromisedFeeWaivers(Date promissedDate);

	void updateWaiverStatus(long waiverId, String status);

	Date getMaxFullFillDate(long finId);

}