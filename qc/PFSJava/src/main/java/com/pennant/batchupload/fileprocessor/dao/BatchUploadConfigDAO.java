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
package com.pennant.batchupload.fileprocessor.dao;

import java.util.List;

import com.pennant.batchupload.model.BatchUploadConfig;

/**
 * Data access layer for <code>BatchUploadConfig</code>
 * 
 * @param <T>
 */
public interface BatchUploadConfigDAO {
	List<BatchUploadConfig> getActiveConfiguration();
}
