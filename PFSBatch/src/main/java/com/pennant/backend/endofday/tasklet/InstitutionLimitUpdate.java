package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.service.limitservice.impl.InstitutionLimitRebuild;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class InstitutionLimitUpdate implements Tasklet {
	private Logger logger = LogManager.getLogger(InstitutionLimitUpdate.class);

	private transient InstitutionLimitRebuild institutionLimitRebuild;

	public InstitutionLimitUpdate() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START Limit Customer Groups On {}", valueDate);

		BatchUtil.setExecutionStatus(context, StepUtil.INSTITUTION_LIMITS_UPDATE);

		institutionLimitRebuild.executeLimitRebuildProcess();

		logger.info("COMPLETE Limit Customer Groups On  {}", valueDate);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setInstitutionLimitRebuild(InstitutionLimitRebuild institutionLimitRebuild) {
		this.institutionLimitRebuild = institutionLimitRebuild;
	}

}
