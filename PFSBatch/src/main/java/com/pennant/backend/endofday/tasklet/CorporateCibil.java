package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.cibil.CorporateCibilReport;

public class CorporateCibil implements Tasklet {
	private Logger logger = LogManager.getLogger(CorporateCibil.class);

	private CorporateCibilReport corporateCibil;

	public CorporateCibil() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		try {
			logger.info("START CIBIL Process for the value date {}", valueDate);

			BatchUtil.setExecutionStatus(context, StepUtil.CIBIL_EXTRACT_CORPORATE);
			CorporateCibilReport.executing = true;
			if (!ImplementationConstants.CIBIL_BASED_ON_ENTITY) {
				corporateCibil.generateReport();
			} else {
				corporateCibil.generateReportBasedOnEntity();
			}

			logger.info("COMPLETED: CIBIL Process for the value date {}", valueDate);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			CorporateCibilReport.executing = false;
		}

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setCorporateCibil(CorporateCibilReport corporateCibil) {
		this.corporateCibil = corporateCibil;
	}

}