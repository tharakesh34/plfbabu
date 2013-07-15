package com.pennant.backend.batch.admin;

import java.util.List;

import org.springframework.batch.core.StepExecution;

public interface BatchAdminDAO {	
	public BatchProcess getCurrentBatch();
	public List<BatchProcess> getStepDetails(StepExecution stepExecution);		
	public void saveStepDetails(String finRef, String details, long stepId);	
}
