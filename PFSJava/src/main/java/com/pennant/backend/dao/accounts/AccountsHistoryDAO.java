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
 * * FileName : AccountsDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-01-2012 * * Modified Date :
 * 02-01-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-01-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.accounts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.accounts.AccountHistoryDetail;
import com.pennant.backend.model.accounts.AccountsHistory;

public interface AccountsHistoryDAO {
	boolean saveOrUpdate(AccountsHistory accountsHist);

	BigDecimal getClosingBalance(String accountId, Date postDate);

	BigDecimal getPrvClosingBalance(String accountId, Date postDate);

	void save(List<AccountHistoryDetail> accountHist);

	void update(List<AccountHistoryDetail> accountHist);

	void updateCurrAccHstyDetails(List<AccountHistoryDetail> accountHist);
}