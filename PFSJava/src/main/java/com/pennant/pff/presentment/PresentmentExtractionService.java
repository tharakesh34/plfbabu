package com.pennant.pff.presentment;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.batch.job.BatchJobService;
import com.pennant.pff.batch.job.model.BatchJob;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

@Component
public class PresentmentExtractionService extends BatchJobService {

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private Job peExtractionJob;

	public PresentmentExtractionService() throws Exception {
		super("BATCH");
	}

	public void extractPresentment() {
		PresentmentExtractionThread target = new PresentmentExtractionThread();
		Thread thread = new Thread(target);
		thread.start();
	}

	public void extractPresentment(PresentmentHeader ph) {
		Thread thread = new Thread(new PresentmentExtractionThread(ph));
		thread.start();
	}

	public void extractRePresentment(PresentmentHeader ph) {
		Thread thread = new Thread(new PresentmentExtractionThread(ph));
		thread.start();
	}

	public class PresentmentExtractionThread implements Runnable {
		private PresentmentHeader ph;

		public PresentmentExtractionThread() {
			super();
		}

		public PresentmentExtractionThread(PresentmentHeader ph) {
			this.ph = ph;
		}

		@Override
		public void run() {
			try {
				start(ph);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

	}

	public void start(PresentmentHeader ph) throws Exception {
		Date appDate = SysParamUtil.getAppDate();

		long batchID = presentmentDAO.createBatch("EXTRACTOIN");

		JobParametersBuilder builder = new JobParametersBuilder();

		builder.addLong("BTACH_ID", batchID);
		builder.addDate("AppDate", appDate);
		if (ph != null) {
			builder.addString("MandateType", ph.getMandateType());
			builder.addString("EmandateSource", ph.getEmandateSource());
			builder.addString("LoanType", ph.getLoanType());
			builder.addString("EntityCode", ph.getEntityCode());
			builder.addString("FinBranch", ph.getFinBranch());
			builder.addDate("FromDate", ph.getFromDate());
			builder.addDate("ToDate", ph.getToDate());
			builder.addDate("DueDate", ph.getDueDate());
			builder.addString("PresentmentType", ph.getPresentmentType());
			builder.addString("AUTOMATION", "N");
		} else {
			builder.addString("AUTOMATION", "Y");
			builder.addString("PresentmentType", "P");
			builder.addDate("DueDate", appDate);
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
