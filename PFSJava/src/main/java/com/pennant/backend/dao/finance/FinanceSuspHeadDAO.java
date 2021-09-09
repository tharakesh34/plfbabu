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
 * * FileName : FinanceSuspHeadDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-02-2012 * * Modified
 * Date : 04-02-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-02-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;

public interface FinanceSuspHeadDAO {

	FinanceSuspHead getFinanceSuspHead();

	FinanceSuspHead getNewFinanceSuspHead();

	FinanceSuspHead getFinanceSuspHeadById(long finID, String type);

	long save(FinanceSuspHead fsh, String type);

	void update(FinanceSuspHead fsh, String type);

	long saveSuspenseDetails(FinanceSuspDetails fsd, String type);

	List<FinanceSuspDetails> getFinanceSuspDetailsListById(long finID);

	List<String> getSuspFinanceList();

	void delete(FinanceSuspHead fsh, String string);

	List<FinStatusDetail> getCustSuspDate(List<Long> custIdList);

	void updateSuspFlag(long finID);

	Date getFinSuspDate(long finID);
}
