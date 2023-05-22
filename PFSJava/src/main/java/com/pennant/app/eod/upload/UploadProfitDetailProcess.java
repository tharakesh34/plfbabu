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
 *
 * FileName : UploadProfitDetailProcess.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.eod.upload;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.eod.service.UploadFinPftDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class UploadProfitDetailProcess extends Thread {
	private static final Logger logger = LogManager.getLogger(UploadProfitDetailProcess.class);

	private static UploadProfitDetailProcess me = null;
	private DataEngineStatus status = new DataEngineStatus();

	public static String RUNNING = "";

	private UploadFinPftDetailService uploadFinPftDetailService;

	private UploadProfitDetailProcess() {
	    super();
	}

	private UploadProfitDetailProcess(UploadFinPftDetailService uploadFinPftDetailService) {
		this.uploadFinPftDetailService = uploadFinPftDetailService;
	}

	public static UploadProfitDetailProcess getInstance(UploadFinPftDetailService uploadFinPftDetailService) {
		if (StringUtils.isEmpty(RUNNING) || me == null) {
			me = new UploadProfitDetailProcess(uploadFinPftDetailService);
		}
		return me;
	}

	public static UploadProfitDetailProcess getInstance() {
		if (me == null) {
			me = new UploadProfitDetailProcess();
		}
		return me;
	}

	public void run() {
		try {
			RUNNING = "STARTED";
			status.setName(PennantConstants.EOD_PFT_DTL_UPLOAD);

			status.setStartTime(new Date(System.currentTimeMillis()));
			this.status.setStatus("EXECUTING");
			try {
				uploadFinPftDetailService.doUploadPftDetails(status);
				status.setEndTime(new Date(System.currentTimeMillis()));
				this.status.setStatus("COMPLETED");
			} catch (Exception e) {
				logger.error("Exception: ", e);
				this.status.setStatus("FAILED");
				RUNNING = "FAILED";
				return;
			} finally {
				status.setEndTime(new Date(System.currentTimeMillis()));
			}
			RUNNING = "COMPLETED";
		} catch (Exception e) {
			logger.error("Exception: ", e);
			RUNNING = "FAILED";
		} finally {
		}

	}

	public DataEngineStatus getStatus() {
		return status;
	}

	public void setStatus(DataEngineStatus status) {
		this.status = status;
	}
}
