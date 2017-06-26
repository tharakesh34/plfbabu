package com.pennanttech.pff.core.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.job.scheduler.AbstractJobScheduler;

public class ApplicationStartup implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(ApplicationStartup.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.debug("<<<<<< THE APPLICATION STARTED >>>>>>");

		try {
			AbstractJobScheduler.servletContext = event.getServletContext();
			AbstractJobScheduler.schedular = AbstractJobScheduler.sf.getScheduler();

		} catch (Exception se) {
			logger.error(se.toString());
			try {
				if (AbstractJobScheduler.schedular != null && AbstractJobScheduler.schedular.isStarted()) {
					AbstractJobScheduler.schedular.shutdown();
				}
			} catch (SchedulerException e) {
				logger.error(se.toString());
			}
		}
		logger.debug("Leaving ");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.debug("Entering ");

		Scheduler scheduler = (Scheduler) AbstractJobScheduler.contextMap.get("jobScheduler");

		try {
			if (scheduler != null && scheduler.isStarted()) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug("<<<<<< THE APPLICATION STOPED >>>>>>");
		logger.debug("Leaving ");
	}
}