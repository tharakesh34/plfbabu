package com.pennant.backend.endofday.dailydownload;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantStaticListUtil;

public class DailyDownloadProcess implements Tasklet {
	private Logger logger = Logger.getLogger(DailyDownloadProcess.class);
	
	private DailyDownloadInterfaceService dailyDownloadInterfaceService; 
	
	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {	
		
		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Daily Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		try {
			
			List<ValueLabel> tablesList = PennantStaticListUtil.getImportTablesList();
			
			for(ValueLabel tableName : tablesList){
				
				if(tableName.getValue().equalsIgnoreCase("Currencies")){
					getDailyDownloadInterfaceService().processCurrencyDetails();
				}else if(tableName.getValue().equalsIgnoreCase("RelationshipOfficer")){
					getDailyDownloadInterfaceService().processRelationshipOfficerDetails();
				} else if(tableName.getValue().equalsIgnoreCase("CustomerType")){
					getDailyDownloadInterfaceService().processCustomerTypeDetails();
				} else if(tableName.getValue().equalsIgnoreCase("Deparment")){
					getDailyDownloadInterfaceService().processDepartmentDetails();
				} else if(tableName.getValue().equalsIgnoreCase("CustomerGroup")){
					getDailyDownloadInterfaceService().processCustomerGroupDetails();
				} else if(tableName.getValue().equalsIgnoreCase("AccountType")){
					getDailyDownloadInterfaceService().processAccountTypeDetails();
				} else if(tableName.getValue().equalsIgnoreCase("CustomerRating")){
					getDailyDownloadInterfaceService().processCustomerRatingDetails();
				}
			}
		
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}

		logger.debug("COMPLETE: Daily Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		
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
