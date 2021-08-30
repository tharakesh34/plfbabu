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
 * * FileName : FinCollateralsDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * * Modified
 * Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.LinkedFinances;

public interface LinkedFinancesDAO {

	List<LinkedFinances> getLinkedFinancesByFinRef(long finID, String type);

	LinkedFinances getLinkedFinancesByLinkRef(String linkedReference, long finID, String type);

	void update(LinkedFinances linkedFinances, String type);

	void updateList(List<LinkedFinances> linFinList, String type);

	void deleteByLinkedReference(String linkedFinReference, long finID, String type);

	void delete(long finID, String type);

	long save(LinkedFinances linkedFinances, String type);

	void saveList(List<LinkedFinances> linFinList, String type);

	List<LinkedFinances> getFinIsLinkedActive(String finReference);

	List<LinkedFinances> getLinkedFinancesByFin(String ref, String type);

	List<String> getFinReferences(String reference);

}