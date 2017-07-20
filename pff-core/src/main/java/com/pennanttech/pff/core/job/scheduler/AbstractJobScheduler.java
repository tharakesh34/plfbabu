package com.pennanttech.pff.core.job.scheduler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.pennanttech.pennapps.core.resource.Literal;

public abstract class AbstractJobScheduler {
	public static final Logger logger = Logger.getLogger(AbstractJobScheduler.class);
	public static Map<String, JobSchedulerDetails> JOB_SCHEDULER_MAP = new HashMap<String, JobSchedulerDetails>();
	public static Map<String, Object> contextMap = new HashMap<String, Object>();

	public static SchedulerFactory sf = new StdSchedulerFactory();
	public static Scheduler schedular = null;
	public static ServletContext servletContext = null;

	@Value("${scheduler.start-jobs}")
	private boolean startJobs = false;

	public AbstractJobScheduler() {
		super();
	}

	public abstract void addJobs() throws ParseException;

	static {
		// Default Job
	}

	protected void startJob() throws ParseException {
		if (!startJobs) {
			logger.info("scheduled jobs not started on this server, since scheduler.start-jobs propery is false.");
			return;
		}

		addJobs();
		JobSchedulerDetails jobDetails = null;
		try {
			if (schedular != null) {
				for (String jobName : JOB_SCHEDULER_MAP.keySet()) {
					jobDetails = JOB_SCHEDULER_MAP.get(jobName);
					schedular.scheduleJob(jobDetails.getJobDetail(), jobDetails.getTrigger());
				}
				contextMap.put("jobScheduler", schedular);
				servletContext.setAttribute("jobScheduler", schedular);
				start();
			}
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		}
	}

	public static void start() {
		try {
			if (!schedular.isStarted()) {
				schedular.start();
			} else {
				schedular.shutdown();
				schedular.start();
			}
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		}
	}

	public static void shutdown() {
		try {
			if (schedular != null && schedular.isStarted()) {
				schedular.shutdown();
			}
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
		}
	}
}
