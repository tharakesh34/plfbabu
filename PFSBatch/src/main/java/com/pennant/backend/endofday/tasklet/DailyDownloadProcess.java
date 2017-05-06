package com.pennant.backend.endofday.tasklet;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantStaticListUtil;

public class DailyDownloadProcess implements Tasklet {
	private Logger logger = Logger.getLogger(DailyDownloadProcess.class);
	
	private DailyDownloadInterfaceService dailyDownloadInterfaceService; 
	
	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;
	private String allowedDailyDownloadList = SysParamUtil.getValueAsString("DAILY_DOWNLOADS");

	public DailyDownloadProcess() {
		//
	}
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {	
		int downloadCount = 0;

		dateValueDate = DateUtility.getAppValueDate();

		logger.debug("START: Daily Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		try {

			List<ValueLabel> tablesList = PennantStaticListUtil.getImportTablesList();
			
			BatchUtil.setExecution(context,  "TOTAL", String.valueOf(getDownloadCount(tablesList)));

			for(ValueLabel tableName : tablesList){
				if(allowForDownload(tableName.getValue())){
//					if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CURRENCY)){
//						getDailyDownloadInterfaceService().processCurrencyDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_RELATIONSHIPOFFICER)){
//						getDailyDownloadInterfaceService().processRelationshipOfficerDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTTYPE)){
//						getDailyDownloadInterfaceService().processCustomerTypeDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_DEPARMENT)){
//						getDailyDownloadInterfaceService().processDepartmentDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTGROUP)){
//						getDailyDownloadInterfaceService().processCustomerGroupDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_ACCOUNTTYPE)){
//						getDailyDownloadInterfaceService().processAccountTypeDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTRATING)){
//						getDailyDownloadInterfaceService().processCustomerRatingDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_ABUSERS)){
//						getDailyDownloadInterfaceService().processAbuserDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTOMERS)){
//						getDailyDownloadInterfaceService().processCustomerDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_COUNTRY)){
//						getDailyDownloadInterfaceService().processCountryDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTSTATUSCODES)){
//						getDailyDownloadInterfaceService().processCustStatusCodeDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_INDUSTRY)){
//						getDailyDownloadInterfaceService().processIndustryDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_BRANCH)){
//						getDailyDownloadInterfaceService().processBranchDetails();
//					}else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_SYSINTACCOUNTDEF)){
//						getDailyDownloadInterfaceService().processInternalAccDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_TRANSACTIONCODE)){
//						getDailyDownloadInterfaceService().processTransactionCodeDetails();
//					} else if(tableName.getValue().equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_IDENTITYTYPE)){
//						getDailyDownloadInterfaceService().processIdentityTypeDetails();
//					}
					downloadCount++;
					BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(downloadCount));
				}
			}
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(downloadCount));
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("COMPLETE: Daily Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		
		return RepeatStatus.FINISHED;
	}
	
	private int getDownloadCount(List<ValueLabel> tablesList){
		int count = 0;
		for(ValueLabel tableName : tablesList){
			if(allowForDownload(tableName.getValue())){
				count++;
			}
		}
		return count;
	}
	
	private boolean allowForDownload(String code){
		String[] dailyDownloads = allowedDailyDownloadList.split(",");
		for (String downloadName : dailyDownloads) {
			if(code.equalsIgnoreCase(StringUtils.trimToEmpty(downloadName))){
				return true;
			}
		}
		return false;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}
	public void setDailyDownloadInterfaceService(
			DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}
	
}
