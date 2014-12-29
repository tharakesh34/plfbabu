package com.pennant.backend.endofday.monthenddownload;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.coreinterface.model.FinIncomeAccount;

public class MonthEndDownloadProcess implements Tasklet {
	private Logger logger = Logger.getLogger(MonthEndDownloadProcess.class);
	
	private DailyDownloadInterfaceService dailyDownloadInterfaceService; 
	
	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {	

		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Month End Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		try {

			Date mnthStartDate = DateUtility.getMonthStartDate(dateValueDate);

			FinIncomeAccount finIncomeAccount = new FinIncomeAccount();
			finIncomeAccount.setLastMntOn(mnthStartDate);

			//Saving new Income Account Details
			getDailyDownloadInterfaceService().processIncomeAccountDetails(finIncomeAccount);

		}catch (Exception e) {
			logger.error(e);
			throw e;
		}

		logger.debug("COMPLETE: Month End Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		
		return RepeatStatus.FINISHED;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}
	public void setDailyDownloadInterfaceService(
			DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}
	
}
