package com.pennant.backend.util;

import java.util.Date;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ExecutionStatus;

public class BatchUtil {
	
	public static ExecutionStatus EXECUTING;
	
	/**
	 * Set current executing details and stores the key value pair in StepExecutionContext. The key's should be (TOTAL,
	 * PROCESSED, INFO) only. The value of key's(TOTAL, PROCESSED) should be a String representation of integers
	 * 
	 * @param ChunkContext
	 *            (context)
	 * @param String
	 *            (key)
	 * @param String
	 *            (value)
	 */
	public static void setExecution(ChunkContext context, String key, String value) {
		
		if(EXECUTING == null) {
			EXECUTING = new ExecutionStatus();
		}
		StepContext stepContext = context.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		int intValue = 0;
		
		if("N".equals(SystemParameterDetails.getSystemParameterValue("EOD_BATCH_MONITOR"))) {
			return;
		}

		EXECUTING.setExecutionName(stepExecution.getStepName());
		EXECUTING.setValueDate((Date)SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE));
		stepExecution.getExecutionContext().put("VDATE", (Date)SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE));
		
		if(!"INFO".equals(key)) {
			if(value != null) {
				intValue = Integer.parseInt(value);
			}
		}
		
		if("TOTAL".equals(key)) {			
			EXECUTING.setActualCount(intValue);
			stepExecution.getExecutionContext().put(key, intValue);
		} else if("PROCESSED".equals(key)){
			EXECUTING.setProcessedCount(intValue);
			stepExecution.getExecutionContext().put(key, Integer.parseInt(value));
		} else if("INFO".equals(key)) {
			EXECUTING.setInfo(value);
			stepExecution.getExecutionContext().put(key, value);
		}
		
		EXECUTING.setStatus(stepExecution.getExitStatus().getExitCode());

		if(!"INFO".equals(key) && "EXECUTING".equals(EXECUTING.getStatus())) {
			EXECUTING.setInfo(null);
		}

		EXECUTING.setStartTime(context.getStepContext().getStepExecution().getStartTime());
		EXECUTING.setEndTime(new Date(System.currentTimeMillis()));
		
		if(!"UNKNOWN".equals(stepContext.getStepExecution().getJobExecution().getExitStatus().getExitCode())) {
			EXECUTING = new ExecutionStatus(); 
		}
	}
	
}
