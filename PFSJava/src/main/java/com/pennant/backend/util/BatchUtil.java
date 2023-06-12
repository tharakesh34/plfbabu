package com.pennant.backend.util;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.AppException;

public class BatchUtil {
	public static DataEngineStatus EXECUTING = null;

	public static DataEngineStatus getRunningStatus(StepExecution stepExecution) {
		DataEngineStatus executionStatus = null;
		ExitStatus exitStatus = stepExecution.getExitStatus();
		String exitCode = exitStatus.getExitCode();

		if ("EXECUTING".equals(exitCode) || "UNKNOWN".equals(exitCode)) {
			return BatchUtil.EXECUTING;
		} else {
			executionStatus = (DataEngineStatus) stepExecution.getExecutionContext().get("STATUS");
		}

		if (executionStatus == null) {
			return null;
		}

		executionStatus.setReference(stepExecution.getStepName());
		executionStatus.setStartTime(stepExecution.getStartTime());
		executionStatus.setEndTime(stepExecution.getEndTime());
		executionStatus.setStatus(stepExecution.getExitStatus().getExitCode().substring(0, 1));

		return executionStatus;
	}

	public static void setExecutionStatus(ChunkContext context, DataEngineStatus status) {
		StepContext stepContext = context.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();

		status.setReference(stepExecution.getStepName());
		status.setTotalRecords(status.getTotalRecords());
		status.setProcessedRecords(status.getProcessedRecords());
		status.setStartTime(stepExecution.getStartTime());
		status.setEndTime(stepExecution.getEndTime());

		String exitCode = stepExecution.getExitStatus().getExitCode();
		status.setStatus(exitCode.substring(0, 1));

		stepExecution.getExecutionContext().put("STATUS", status);

		EXECUTING = status;

		if ("F".equals(status.getStatus())) {
			throw new AppException(status.getRemarks());
		}

		while ("I".equals(status.getStatus()) || "".equals(status.getStatus())) {
			if ("F".equals(status.getStatus())) {
				throw new AppException(status.getRemarks());
			}
		}

		if ("S".equals(status.getStatus())) {
			status.setStatus(com.pennanttech.dataengine.constants.ExecutionStatus.C.name());
		}
	}

}
