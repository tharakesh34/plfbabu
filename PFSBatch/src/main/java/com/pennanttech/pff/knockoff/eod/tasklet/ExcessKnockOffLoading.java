package com.pennanttech.pff.knockoff.eod.tasklet;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.knockoff.dao.ExcessKnockOffDAO;

public class ExcessKnockOffLoading implements Tasklet {
	private Logger logger = LogManager.getLogger(ExcessKnockOffLoading.class);

	private ExcessKnockOffDAO excessKnockOffDAO;

	public ExcessKnockOffLoading() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		if (!ImplementationConstants.ALW_AUTO_CROSS_LOAN_KNOCKOFF) {
			logger.debug(Literal.LEAVING);
			return RepeatStatus.FINISHED;
		}

		excessKnockOffDAO.deleteQueue();

		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);
		Date valueDate = eventProperties.getAppDate();

		String executionDay = String.valueOf(DateUtil.getDay(valueDate));
		executionDay = StringUtils.leftPad(executionDay, 2, "0");

		String thresholdValue = eventProperties.getThresholdValue();

		long totalRecords = excessKnockOffDAO.logExcessForCrossLoanKnockOff(valueDate, executionDay, thresholdValue);

		StepUtil.CROSS_LOAN_KNOCKOFF.setTotalRecords(totalRecords);

		excessKnockOffDAO.logExcessForCrossLoanDetails(valueDate, executionDay);

		BatchUtil.setExecutionStatus(context, StepUtil.CROSS_LOAN_KNOCKOFF);

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setExcessKnockOffDAO(ExcessKnockOffDAO excessKnockOffDAO) {
		this.excessKnockOffDAO = excessKnockOffDAO;
	}
}
