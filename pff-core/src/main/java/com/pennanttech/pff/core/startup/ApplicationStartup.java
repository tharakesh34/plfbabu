package com.pennanttech.pff.core.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.job.scheduler.AbstractJobScheduler;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ApplicationStartup implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(ApplicationStartup.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			AbstractJobScheduler.servletContext = event.getServletContext();
			AbstractJobScheduler.schedular = AbstractJobScheduler.sf.getScheduler();
			String dateTime = DateUtil.getSysDate(DateFormat.LONG_DATE_TIME);
			logger.info("<<<<<The ".concat(App.NAME).concat(" Started on ").concat(dateTime).concat(">>>>>"));
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			AbstractJobScheduler.shutdown();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		AbstractJobScheduler.shutdown();
		String dateTime = DateUtil.getSysDate(DateFormat.LONG_DATE_TIME);
		logger.info("<<<<<The ".concat(App.NAME).concat(" Stopped on ").concat(dateTime).concat(">>>>>"));
	}
}