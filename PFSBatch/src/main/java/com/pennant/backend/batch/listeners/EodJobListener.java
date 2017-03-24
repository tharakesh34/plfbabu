package com.pennant.backend.batch.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.pennant.eod.util.EODProperties;


public class EodJobListener implements JobExecutionListener{
	
	private EODProperties eodProperties;
	
	public EodJobListener() {
		super();
	}
	
	@Override
	public void afterJob(JobExecution arg0) {
		getEodProperties().destroy();
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		getEodProperties().init();
	}

	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}

	public EODProperties getEodProperties() {
		return eodProperties;
	}

}
