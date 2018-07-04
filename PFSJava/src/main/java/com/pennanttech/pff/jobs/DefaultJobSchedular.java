package com.pennanttech.pff.jobs;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.scheduler.AbstractJobScheduler;
import com.pennanttech.pennapps.core.scheduler.Job;

@Component()
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DefaultJobSchedular extends AbstractJobScheduler {
	private static final String GST_INVOICE_GENERATE_JOB = "GST_INVOICE_GENERATE_JOB";
	private static final boolean GENERATE_GST_INVOICE_NO = Boolean.valueOf(App.getProperty("gstInvoice.job.enabled"));
	private static final String GST_INVOICE_SCHEDULE_TIME = App.getProperty("gstInvoice.scheduleTime");

	@Override
	protected void registerJobs() throws Exception {
		registerGstInvoiceJob();
	}

	/**
	 * Invoice number Auto Generation
	 */
	private void registerGstInvoiceJob() {
		
		if (GENERATE_GST_INVOICE_NO) {
			Job job = new Job();
			job.setJobDetail(JobBuilder.newJob(GSTInvoiceGeneratorJob.class)
					.withIdentity(GST_INVOICE_GENERATE_JOB, GST_INVOICE_GENERATE_JOB)
					.withDescription("GST Invoice Preparation").build());
			job.setTrigger(TriggerBuilder.newTrigger().withIdentity("GST_INVOICE_GENERATE_JOB", "GST_INVOICE_GENERATE_JOB")
					.withDescription("GST Invoice job trigger")
					.withSchedule(CronScheduleBuilder.cronSchedule(GST_INVOICE_SCHEDULE_TIME)).build());
			
			jobs.put(GST_INVOICE_GENERATE_JOB, job);
		}

	}

}