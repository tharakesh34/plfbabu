package com.pennant.pff.presentment;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.batch.job.BatchJobManager;
import com.pennant.pff.batch.job.model.BatchJob;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

@Configuration
public class ExtractionJobManager extends BatchJobManager {

	@Autowired
	private Job peExtractionJob;

	public ExtractionJobManager(DataSource dataSource) throws Exception {
		super(dataSource, "BATCH_");
	}

	public void extractPresentment(PresentmentHeader ph) {
		start(ph);
	}

	public void extractRePresentment(PresentmentHeader ph) {
		start(ph);
	}

	public void start(PresentmentHeader ph) {
		Date appDate = SysParamUtil.getAppDate();

		JobParametersBuilder builder = new JobParametersBuilder();

		builder.addLong("BATCH_ID", ph.getBatchID());
		builder.addDate("AppDate", appDate);
		builder.addString("MandateType", ph.getMandateType());
		builder.addString("EmandateSource", ph.getEmandateSource());
		builder.addString("LoanType", ph.getLoanType());
		builder.addString("EntityCode", ph.getEntityCode());
		builder.addString("FinBranch", ph.getFinBranch());
		builder.addDate("FromDate", ph.getFromDate());
		builder.addDate("ToDate", ph.getToDate());
		builder.addDate("DueDate", ph.getDueDate());
		builder.addString("PresentmentType", ph.getPresentmentType());

		if (ph.isAutoExtract()) {
			builder.addString("AUTOMATION", "Y");
		} else {
			builder.addString("AUTOMATION", "N");
		}

		builder.addString("BpiPaidOnInstDate",
				(String) SysParamUtil.getValue(SMTParameterConstants.BPI_PAID_ON_INSTDATE));
		builder.addString("GroupByBank", (String) SysParamUtil.getValue(SMTParameterConstants.GROUP_BATCH_BY_BANK));

		JobParameters jobParameters = builder.toJobParameters();

		start(peExtractionJob, jobParameters);
	}

	public void restart(BatchJob eodJob) throws Exception {
		restart(eodJob.getJobExecutionId());
	}

}
