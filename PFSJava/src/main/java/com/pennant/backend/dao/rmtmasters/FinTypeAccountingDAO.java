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
 * * FileName : FinTypeAccountingDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified
 * Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;

/**
 * DAO methods declaration for the <b>FinTypeAccounting model</b> class.<br>
 * 
 */
public interface FinTypeAccountingDAO {

	FinTypeAccounting getFinTypeAccounting();

	FinTypeAccounting getNewFinTypeAccounting();

	FinTypeAccounting getFinTypeAccountingByID(FinTypeAccounting finTypeAccounting, String type);

	List<FinTypeAccounting> getFinTypeAccountingListByID(final String id, int moduleId, String type);

	void update(FinTypeAccounting finTypeAccounting, String type);

	String save(FinTypeAccounting finTypeAccounting, String type);

	void delete(FinTypeAccounting finTypeAccounting, String type);

	void deleteByFinType(String finType, int moduleId, String type);

	Long getAccountSetID(String finType, String event, int moduleId);

	List<String> getFinTypeAccounting(String event, Long accountSetId, int moduleId);

	List<Long> getFinTypeAccounting(String fintype, List<String> events, int moduleId);

	List<FinTypeAccounting> getFinTypeAccountingByFinType(String finType, int moduleId);

	int getAccountingSetIdCount(long accountSetId, String type);

	FinTypeAccounting getFinTypeAccountingByRef(FinTypeAccounting finTypeAccounting, String type);

	List<AccountEngineEvent> getAccountEngineEvents(String categoryCode);
}