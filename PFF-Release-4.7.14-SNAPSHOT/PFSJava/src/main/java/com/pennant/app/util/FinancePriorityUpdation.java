package com.pennant.app.util;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.backend.service.finance.FinanceDetailService;

public class FinancePriorityUpdation extends QuartzJobBean implements StatefulJob, Serializable {

	private static final long serialVersionUID = 4480912264156310688L;
	private static final Logger	logger	= Logger.getLogger(FinancePriorityUpdation.class);
	private FinanceDetailService  financeDetailService;
	static String status = "";

	public FinancePriorityUpdation() {
		super();
	}
	
	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering");
		try {
			getFinanceDetailService().updateFinancePriority();
		}catch(Exception e){
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}


	public  FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}