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
package com.pennant.batchupload.fileprocessor.service.impl;

import java.util.List;

import com.pennant.batchupload.fileprocessor.dao.BatchUploadConfigDAO;
import com.pennant.batchupload.fileprocessor.service.BatchUploadConfigService;
import com.pennant.batchupload.model.BatchUploadConfig;

/**
 * Service implementation for methods that depends on <b>BatchUploadConfig</b>.<br>
 * 
 */
public class BatchUploadConfigServiceImpl implements BatchUploadConfigService {

	private BatchUploadConfigDAO batchUploadConfigDAO;

	@Override
	public List<BatchUploadConfig> getActiveConfiguration() {
		return batchUploadConfigDAO.getActiveConfiguration();
	}
	
	public BatchUploadConfigDAO getBatchUploadConfigDAO() {
		return batchUploadConfigDAO;
	}

	public void setBatchUploadConfigDAO(BatchUploadConfigDAO batchUploadConfigDAO) {
		this.batchUploadConfigDAO = batchUploadConfigDAO;
	}
	
}