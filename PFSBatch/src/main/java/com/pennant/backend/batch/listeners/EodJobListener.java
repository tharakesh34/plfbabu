package com.pennant.backend.batch.listeners;

import com.pennant.app.util.DateUtility;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class EodJobListener implements JobExecutionListener {

	public EodJobListener() {
		super();
	}

	@Override
	public void afterJob(JobExecution arg0) {
		
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		// TO Handle Restart case
		if (arg0.getExecutionContext().get("APP_VALUEDATE") == null) {
			arg0.getExecutionContext().put("APP_VALUEDATE", DateUtility.getAppValueDate());
			arg0.getExecutionContext().put("APP_DATE", DateUtility.getAppDate());
		}
		
	}
}
