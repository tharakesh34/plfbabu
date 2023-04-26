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
 * * FileName : AccountingSetDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * * Modified
 * Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;

public interface AccountingSetDAO {

	AccountingSet getAccountingSetById(long id, String type);

	void update(AccountingSet accountingSet, String type);

	void delete(AccountingSet accountingSet, String type);

	long save(AccountingSet accountingSet, String type);

	AccountingSet getAccSetSysDflByEvent(String event, String setCode, String type);

	// Commitments
	Long getAccountingSetId(String eventCode, String accSetCode);

	Long getAccountingSetId(String eventCode);

	AccountingSet getAccountingSetbyEventCode(AccountingSet accountingset, String type);

	boolean isValidCategoryWiseEvents(String eventCode);

	List<AccountType> getAccountTypes();
}