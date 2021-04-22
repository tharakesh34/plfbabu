package com.pennanttech.pff.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.disbursement.service.DisbAutoUploadService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DisbAutoUploadJob implements Job {
	private static final Logger logger = LogManager.getLogger(DisbAutoUploadJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));
		DisbAutoUploadService disbAutoUploadService = null;
		try {
			disbAutoUploadService = (DisbAutoUploadService) SpringBeanUtil.getBean("disbAutoUploadService");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		if (disbAutoUploadService != null) {
			try {
				disbAutoUploadService.uploadDisbursements();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
}
