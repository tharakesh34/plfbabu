package com.pennant.pff.presentment;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class PresentmentExtractionService {

	private static JobLauncher peJobLauncher;

	private static Job peExtractionJob;

	public PresentmentExtractionService() {
		super();
	}

	public static void extractPresentment() {
		Thread thread = new Thread(new PresentmentExtractionThread());
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

	public static class PresentmentExtractionThread implements Runnable {
		private PresentmentHeader ph;

		public PresentmentExtractionThread() {
			super();
		}

		public PresentmentExtractionThread(PresentmentHeader ph) {
			this.ph = ph;
		}

		@Override
		public void run() {
			JobParametersBuilder builder = new JobParametersBuilder();

			builder.addDate("AppDate", SysParamUtil.getAppDate());
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
			}

			builder.addString("BpiPaidOnInstDate",
					(String) SysParamUtil.getValue(SMTParameterConstants.BPI_PAID_ON_INSTDATE));
			builder.addString("GroupByBank", (String) SysParamUtil.getValue(SMTParameterConstants.GROUP_BATCH_BY_BANK));

			JobParameters jobParameters = builder.toJobParameters();

			try {
				peJobLauncher.run(peExtractionJob, jobParameters);
			} catch (JobExecutionAlreadyRunningException e) {
				e.printStackTrace();
			} catch (JobRestartException e) {
				e.printStackTrace();
			} catch (JobInstanceAlreadyCompleteException e) {
				e.printStackTrace();
			} catch (JobParametersInvalidException e) {
				e.printStackTrace();
			}
		}

	}

	public static void setPeJobLauncher(JobLauncher peJobLauncher) {
		PresentmentExtractionService.peJobLauncher = peJobLauncher;
	}

	public static void setPeExtractionJob(Job peExtractionJob) {
		PresentmentExtractionService.peExtractionJob = peExtractionJob;
	}

}
