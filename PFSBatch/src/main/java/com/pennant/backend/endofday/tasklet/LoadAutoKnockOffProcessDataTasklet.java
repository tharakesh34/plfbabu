package com.pennant.backend.endofday.tasklet;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class LoadAutoKnockOffProcessDataTasklet extends BasicDao<AutoKnockOff> implements Tasklet {
	private Logger logger = LogManager.getLogger(LoadAutoKnockOffProcessDataTasklet.class);

	private AutoKnockOffDAO autoKnockOffDAO;
	public static Set<String> processedRecords = null;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {

		if (!ImplementationConstants.ALLOW_AUTO_KNOCK_OFF) {
			logger.debug(Literal.LEAVING);
			return RepeatStatus.FINISHED;
		}

		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);
		Date valueDate = eventProperties.getAppDate();

		if (!ImplementationConstants.AUTO_KNOCK_OFF_ON_DUE_DATE) {
			valueDate = DateUtil.addDays(valueDate, 1);
		}

		/**
		 * Deleting the data in staging tables.
		 */
		autoKnockOffDAO.truncateData();

		/**
		 * Deriving the Execution Day.
		 */
		String executionDay = String.valueOf(DateUtil.getDay(valueDate));
		executionDay = StringUtils.leftPad(executionDay, 2, "0");

		BatchUtil.setExecutionStatus(context, StepUtil.AUTO_KNOCKOFF_PROCESS);

		String thresholdValue = eventProperties.getThresholdValue();

		/**
		 * Storing all the Excess and Payble in AUTO_KNOCKOFF_EXCESS table.
		 */
		autoKnockOffDAO.logExcessForKnockOff(valueDate, executionDay, thresholdValue);

		/**
		 * Storing all the Fee details tagged with knock-off and the knock-off tagged with loanType
		 */
		long totalRecords = autoKnockOffDAO.logKnockOffDetails(valueDate, executionDay);

		StepUtil.AUTO_KNOCKOFF_PROCESS.setTotalRecords(totalRecords);

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	public void setAutoKnockOffDAO(AutoKnockOffDAO autoKnockOffDAO) {
		this.autoKnockOffDAO = autoKnockOffDAO;
	}

}
