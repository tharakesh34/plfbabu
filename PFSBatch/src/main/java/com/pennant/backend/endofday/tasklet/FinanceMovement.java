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
 * FileName : AmortizationCalculation.java *
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
package com.pennant.backend.endofday.tasklet;

import java.sql.Connection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.ServiceHelper;
import com.pennant.app.core.StatusMovementService;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.eod.EODUtil;

public class FinanceMovement extends ServiceHelper implements Tasklet {
	private Logger logger = LogManager.getLogger(FinanceMovement.class);

	int processed = 0;
	private StatusMovementService statusMovementService;

	public FinanceMovement() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.debug("START: Amortization Caluclation for Value Date: " + valueDate);

		Connection connection = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			// Normal to PD
			statusMovementService.processMovement(context, connection, getNormalToPD(), AccountingEvent.NORM_PD,
					valueDate, processed);
			// PD to Normal
			statusMovementService.processMovement(context, connection, getPDToNormal(), AccountingEvent.PD_NORM,
					valueDate, processed);
			// PD to PIS
			statusMovementService.processMovement(context, connection, getPDToPIS(), AccountingEvent.PD_PIS, valueDate,
					processed);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("COMPLETE: Amortization Caluclation for Value Date: " + valueDate);
		return RepeatStatus.FINISHED;
	}

	/* Process Query */

	private String getNormalToPD() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" select * from FinPftDetails where CurODDays=1 and PrvODDate = ? ");
		return sqlQuery.toString();
	}

	private String getPDToNormal() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(
				"select * from (select FinReference, SUM(FinCurODAmt) FinCurODAmt,MAX(FinODTillDate) FinODTillDate   ");
		sqlQuery.append(
				" from FInODDetails group by FinReference)t inner join FinPftDetails fpd on fpd.FinReference=t.FinReference   ");
		sqlQuery.append(" where FinODTillDate=? and fpd.CurODDays=0   ");
		return sqlQuery.toString();
	}

	private String getPDToPIS() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(
				" select * from (select FinReference from FinSuspHead where FinIsInSusp=1 and FinSuspTrfDate= ?) t  ");
		sqlQuery.append(" inner join FinPftDetails fpd on fpd.FinReference=t.FinReference  ");
		return sqlQuery.toString();
	}

	@SuppressWarnings("unused")
	private String getPSIToNormal() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(
				" select * from (select FinReference from FinSuspHead where FinIsInSusp=0 and FinSuspTrfDate= ?) t  ");
		sqlQuery.append(" inner join FinPftDetails fpd on fpd.FinReference=t.FinReference  ");
		return sqlQuery.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public StatusMovementService getStatusMovementService() {
		return statusMovementService;
	}

	public void setStatusMovementService(StatusMovementService statusMovementService) {
		this.statusMovementService = statusMovementService;
	}

}
