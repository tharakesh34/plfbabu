package com.pennant.backend.endofday.tasklet;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.eod.EODNotificationService;

public class LedgerNotification  implements Tasklet {
	private Logger					logger	= Logger.getLogger(LedgerNotification.class);
	
	
	@Autowired(required = false)
	private EODNotificationService eodNotificationService;

	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);
		
		if(eodNotificationService!=null){
			eodNotificationService.sendLedgerNotifycation();
		}else{
			logger.debug("EODNotificationService Not Configured");
		}
		
		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}
	
	

}
