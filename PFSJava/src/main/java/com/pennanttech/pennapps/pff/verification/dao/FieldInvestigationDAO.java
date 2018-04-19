/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
package com.pennanttech.pennapps.pff.verification.dao;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;

public interface FieldInvestigationDAO extends BasicCrudDao<FieldInvestigation> {

	/**
	 * Fetch the Record FieldInvestigation by key field
	 * 
	 * @param id
	 *            id of the FieldInvestigation.
	 * @param tableType
	 *            The type of the table.
	 * @return FieldInvestigation
	 */
	FieldInvestigation getFieldInvestigation(long id, String type);

	List<FieldInvestigation> getList(String keyReference);

	List<FieldInvestigation> getList(String[] cif);

}