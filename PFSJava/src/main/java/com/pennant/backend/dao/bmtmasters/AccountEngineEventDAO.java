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
 * * FileName : AccountEngineEventDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * *
 * Modified Date : 27-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.bmtmasters;

import java.util.List;

import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.AccountingSet;

/**
 * DAO methods declaration for the <b>AccountEngineEvent model</b> class.<br>
 * 
 */
public interface AccountEngineEventDAO {

	AccountEngineEvent getAccountEngineEvent();

	AccountEngineEvent getNewAccountEngineEvent();

	AccountEngineEvent getAccountEngineEventById(String id, String type);

	void update(AccountEngineEvent accountEngineEvent, String type);

	void delete(AccountEngineEvent accountEngineEvent, String type);

	String save(AccountEngineEvent accountEngineEvent, String type);

	List<AccountEngineEvent> getAccountEngineEvents();

	List<AccountingSet> getAccountSetEvents();
}