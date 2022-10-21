package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class GroupingTasklet implements Tasklet {

	private PresentmentEngine presentmentEngine;

	public GroupingTasklet(PresentmentEngine presentmentEngine) {
		super();
		this.presentmentEngine = presentmentEngine;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		PresentmentHeader ph = new PresentmentHeader();

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BTACH_ID");

		ph.setBatchID(batchId);
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

		presentmentEngine.grouping(ph);

		return RepeatStatus.FINISHED;
	}

}
