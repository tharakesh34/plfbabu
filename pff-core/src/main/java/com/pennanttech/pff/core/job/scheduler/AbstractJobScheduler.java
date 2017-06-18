package com.pennanttech.pff.core.job.scheduler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public abstract class AbstractJobScheduler {
	public static final Logger logger = Logger.getLogger(AbstractJobScheduler.class);
	public static Map<String, JobSchedulerDetails> JOB_SCHEDULER_MAP = new HashMap<String, JobSchedulerDetails>();
	public static Map<String, Object> contextMap = new HashMap<String, Object>();
	
	public static SchedulerFactory sf = new StdSchedulerFactory();
	public static Scheduler schedular = null;
	public static ServletContext servletContext = null;
	
	public AbstractJobScheduler() {
		super();
	}
	
	public abstract void addJobs() throws ParseException;
	static {
		// Default Job
	}
	
	protected void startJob() throws ParseException {
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
			logger.error("Exception: ", e);
		}
	}
	
	public static void start() throws SchedulerException {
		if(!schedular.isStarted()) {
			schedular.start();
		} else {
			schedular.shutdown();
			schedular.start();
		}
	}

}
