package com.pennant.backend.endofday.tasklet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AccountNumberUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.SubHeadRule;

public class MonthEndDownloadProcess implements Tasklet {
	private Logger logger = Logger.getLogger(MonthEndDownloadProcess.class);
	
	private DailyDownloadInterfaceService dailyDownloadInterfaceService; 
	private AccountNumberUtil accountNumberUtil; 
	
	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;

	public MonthEndDownloadProcess() {
		//
	}
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getAppValueDate();

		logger.debug("START: Month End Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		try {
			List<FinanceType> financeTypeList =  getDailyDownloadInterfaceService().fetchFinanceTypeDetails();
			if(financeTypeList != null){
				
				List<FinanceProfitDetail> updateFinPftDetailList = processIncomeAccountDetails(financeTypeList);
				
				if(!updateFinPftDetailList.isEmpty()){
					getDailyDownloadInterfaceService().updateFinProfitIncomeAccounts(updateFinPftDetailList);
				}
			} 

		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("COMPLETE: Month End Download Details for Value Date: "+ DateUtility.addDays(dateValueDate,-1));		
		return RepeatStatus.FINISHED;
	}
	
	private List<FinanceProfitDetail> processIncomeAccountDetails(List<FinanceType> financeTypeList){
		logger.debug("Entering");

		Map<Long,List<TransactionEntry>> eventCodes  = new HashMap<Long,List<TransactionEntry>>(); 
		List<FinanceProfitDetail> finPftDetailList = new ArrayList<FinanceProfitDetail>();
		List<TransactionEntry> transactionEntries = null;
		FinanceProfitDetail financeProfitDetail = null;
		SubHeadRule subHeadRule = null;

		for (FinanceType financeType : financeTypeList) {
			//Maintaining Map To Avoid Multiple Database Hits For Same Transaction Entries
			Long accSetID = financeType.getAccEventValue(AccountEventConstants.ACCEVENT_AMZ);
			if(eventCodes.containsKey(accSetID)){
				transactionEntries = eventCodes.get(accSetID);
			}else{
				transactionEntries = getDailyDownloadInterfaceService().fetchTransactionEntryDetails(accSetID);
				eventCodes.put(accSetID,transactionEntries);
			}
			if(transactionEntries != null){
				subHeadRule = new SubHeadRule();
				subHeadRule.setReqFinAcType(financeType.getFinAcType());
				subHeadRule.setReqProduct(financeType.getFinCategory());
				subHeadRule.setReqFinType(financeType.getFinType());
				subHeadRule.setReqFinDivision(financeType.getFinDivision());

				financeProfitDetail = new FinanceProfitDetail();
				financeProfitDetail.setFinType(financeType.getFinType());

				finPftDetailList.add(financeProfitDetail);
			}
		}

		logger.debug("Leaving"); 
		return finPftDetailList;
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

	public AccountNumberUtil getAccountNumberUtil() {
		return accountNumberUtil;
	}
	public void setAccountNumberUtil(AccountNumberUtil accountNumberUtil) {
		this.accountNumberUtil = accountNumberUtil;
	}
	
}
