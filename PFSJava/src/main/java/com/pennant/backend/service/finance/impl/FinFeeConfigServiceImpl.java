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

package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinFeeConfigDAO;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinFeeConfigService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinFeeConfigServiceImpl extends GenericService<FinFeeConfig> implements FinFeeConfigService {
	private static final Logger logger = LogManager.getLogger(FinFeeConfigServiceImpl.class);

	private FinFeeConfigDAO finFeeConfigDAO;

	@Override
	public void save(FinFeeConfig finFeeDetailConfig, TableType tableType) {
		logger.debug(Literal.ENTERING);
		finFeeConfigDAO.save(finFeeDetailConfig, tableType);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void saveList(List<FinFeeConfig> finFeeDetailConfig, String type) {
		logger.debug(Literal.ENTERING);
		finFeeConfigDAO.saveList(finFeeDetailConfig, type);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<FinFeeConfig> getFinFeeConfigList(String finReference, String eventCode, boolean origination,
			String type) {
		logger.debug(Literal.ENTERING);
		
		List<FinFeeConfig> list = finFeeConfigDAO.getFinFeeConfigList(finReference, eventCode, origination, type);

		logger.debug(Literal.LEAVING);
		return list;
	}

	public void setFinFeeConfigDAO(FinFeeConfigDAO finFeeConfigDAO) {
		this.finFeeConfigDAO = finFeeConfigDAO;
	}

}