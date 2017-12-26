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
package com.pennant.batchupload.fileprocessor.service;

import java.util.List;

import com.pennant.batchupload.model.BatchUploadConfig;

/**
 * Service declaration for methods that depends on <b>BatchUploadConfig</b>.<br>
 * 
 */
public interface BatchUploadConfigService {
	List<BatchUploadConfig> getActiveConfiguration(); 
}