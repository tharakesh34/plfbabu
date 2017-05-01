package com.pennant.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.pennant.app.util.SysParamUtil;

/**
 * 
 *@Deprecated Please refer BajaJobScheduler class to configure new JOB.
 */
@Deprecated
public class ApplicationStartup implements ServletContextListener {
	private final static Logger logger = Logger.getLogger(ApplicationStartup.class);
	
	Map<String, Object> contextMap = new HashMap<String, Object>();

	public ApplicationStartup() {
		//
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.debug("------------->THE APPLICATION STARTED");
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler schedular = null;
		try{
			@SuppressWarnings("unused")
			ServletContext sc = event.getServletContext();
			schedular = sf.getScheduler();
			//cornExpression is the String value which represents job shecdule frequency for e.g one job has to execute 
			        //  for every 20 minutes of every hour every day  then cornExpression is  "0  * / 20 * / 1 * * ? *" 
			// FIXME murthy responsibility to address this.
			String cornExpression=SysParamUtil.getValueAsString("ADT_STATS_FREQUENCY_CORNEXP");
			logger.debug("cornExpression:"+cornExpression);

			//Next Processing Date Calculation	
//			JobDetail     job 	  = newJob(StatisticsSheduler.class).withIdentity("AUDIT_STATISTICS", "AUDITSTATISTICS").build();
//			CronTrigger   trigger = newTrigger().withIdentity("AUDIT_STATISTICS_TRIGGER", "AUDITSTATISTICS").
//			withSchedule(cronSchedule("0  */5 */1 * * ?")).build();	
			/*if(schedular!=null){
				schedular.scheduleJob(job, trigger);
				contextMap.put("jobScheduler", schedular);
				sc.setAttribute("jobScheduler", schedular);
				schedular.start();

			}*/
		}catch (Exception se) {
			logger.error("Exception: ", se);
			try {
				if(schedular!=null){
					schedular.shutdown();
				}
			} catch (SchedulerException e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving ");
	
	}
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger.debug("Entering ");

		Scheduler scheduler=(Scheduler)contextMap.get("jobScheduler");

		try {
			if(scheduler!=null){
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("---------------->THE APPLICATION STOPED");
		logger.debug("Leaving ");
	}
}

