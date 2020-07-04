package com.pennanttech.pff.eod.auto.knockoff.reval;

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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
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

		/**
		 * Deriving the value date by reducing 1 day from application date, since the application date already changed
		 * for the current EOD.
		 */
		Date valueDate = SysParamUtil.getAppDate();
		valueDate = DateUtility.addDays(valueDate, -1);

		/**
		 * Deleting records with the value date to handle failure case.
		 */
		autoKnockOffDAO.deleteKnockOffExcessLog(valueDate);

		/**
		 * Deriving the Execution Day.
		 */
		String executionDay = String.valueOf(DateUtil.getDay(valueDate));
		executionDay = StringUtils.leftPad(executionDay, 2, "0");

		BatchUtil.setExecutionStatus(context, StepUtil.AUTO_KNOCKOFF_PROCESS);

		String thresholdValue = (String) SysParamUtil.getValue(SMTParameterConstants.AUTO_KNOCKOFF_THRESHOLD);

		/**
		 * Storing all the Excess and Payble in AUTO_KNOCKOFF_EXCESS table.
		 */
		autoKnockOffDAO.logExcessForKnockOff(valueDate, executionDay, thresholdValue);

		/**
		 * Storing all the Fee details tagged with knock-off and the knock-off tagged with loanType
		 */
		long totalRecords = autoKnockOffDAO.logKnockOffDetails(valueDate, executionDay);

		StepUtil.AUTO_KNOCKOFF_PROCESS.setTotalRecords(totalRecords);
		context.getStepContext().getStepExecution().getExecutionContext().put("VALUE_DATE", valueDate);

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	public void setAutoKnockOffDAO(AutoKnockOffDAO autoKnockOffDAO) {
		this.autoKnockOffDAO = autoKnockOffDAO;
	}

}
