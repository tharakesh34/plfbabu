package com.pennanttech.pff.schedule.jobs;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.process.DMSAddDocJobProcess;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DMSAddDocJob implements Job, Serializable {

	private static final Logger logger = LogManager.getLogger(DMSAddDocJob.class);

	private static final long serialVersionUID = -8885818824679309674L;

	private DMSAddDocJobProcess dmsAddDocJobProcess;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		try {
			dmsAddDocJobProcess = SpringBeanUtil.getBean(DMSAddDocJobProcess.class);
			dmsAddDocJobProcess.process();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING);
		}

		logger.debug(Literal.LEAVING);

	}

}
