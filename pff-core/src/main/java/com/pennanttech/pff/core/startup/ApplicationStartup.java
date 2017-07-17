package com.pennanttech.pff.core.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.job.scheduler.AbstractJobScheduler;

public class ApplicationStartup implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(ApplicationStartup.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.debug(Literal.LEAVING);

		try {
			AbstractJobScheduler.servletContext = event.getServletContext();
			AbstractJobScheduler.schedular = AbstractJobScheduler.sf.getScheduler();
			logger.debug("<<<<<< THE APPLICATION STARTED >>>>>>");
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			AbstractJobScheduler.shutdown();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		AbstractJobScheduler.shutdown();
		logger.debug("<<<<<< THE APPLICATION STOPED >>>>>>");
	}
}