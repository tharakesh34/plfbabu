package com.pennant.pff.presentment.dao.impl;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class PresentmentItemProcessor implements ItemProcessor<PresentmentDetail, PresentmentDetail> {
	private PresentmentEngine presentmentEngine;

	private PresentmentHeader ph;

	public PresentmentItemProcessor(PresentmentEngine presentmentEngine) {
		this.presentmentEngine = presentmentEngine;
	}

	@BeforeStep
	public void getInterstepData(StepExecution stepExecution) {
		JobParameters jobParameters = stepExecution.getJobParameters();

		ph = new PresentmentHeader();

		ph.setAppDate(jobParameters.getDate("AppDate"));
		ph.setMandateType(jobParameters.getString("MandateType"));
		ph.setEmandateSource(jobParameters.getString("EmandateSource"));
		ph.setLoanType(jobParameters.getString("LoanType"));
		ph.setEntityCode(jobParameters.getString("EntityCode"));
		ph.setFinBranch(jobParameters.getString("FinBranch"));
		ph.setFromDate(jobParameters.getDate("FromDate"));
		ph.setToDate(jobParameters.getDate("ToDate"));
		ph.setDueDate(jobParameters.getDate("DueDate"));
		ph.setPresentmentType(jobParameters.getString("PresentmentType"));
		ph.setBpiPaidOnInstDate(Boolean.valueOf(jobParameters.getString("BpiPaidOnInstDate")));
		ph.setGroupByBank(Boolean.valueOf(jobParameters.getString("GroupByBank")));
		ph.setGroupByPartnerBank(ImplementationConstants.GROUP_BATCH_BY_PARTNERBANK);
	}

	@Override
	public PresentmentDetail process(PresentmentDetail pd) throws Exception {
		presentmentEngine.extract(ph, pd);
		return pd;
	}
}
