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
 * * FileName : WIFFinanceMainDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * * Modified
 * Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinanceMain;

public interface WIFFinanceMainDAO {

	FinanceMain getWIFFinanceMainById(long finID, String type);

	void update(FinanceMain wIFFinanceMain, String type);

	void delete(FinanceMain wIFFinanceMain, String type);

	String save(FinanceMain wIFFinanceMain, String type);
}