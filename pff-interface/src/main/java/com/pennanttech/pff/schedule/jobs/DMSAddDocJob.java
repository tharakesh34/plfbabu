package com.pennanttech.pff.schedule.jobs;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.process.DMSAddDocJobProcess;

public class DMSAddDocJob implements Job, Serializable {
	
	private static final Logger logger = Logger.getLogger(DMSAddDocJob.class);

	private static final long serialVersionUID = -8885818824679309674L;
	
	private DMSAddDocJobProcess dmsAddDocJobProcess;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		
		try{
			dmsAddDocJobProcess=SpringBeanUtil.getBean(DMSAddDocJobProcess.class);
			dmsAddDocJobProcess.process();
		}catch(Exception e){
			logger.debug(Literal.LEAVING);
		}
		
		logger.debug(Literal.LEAVING);
		
	}

}
