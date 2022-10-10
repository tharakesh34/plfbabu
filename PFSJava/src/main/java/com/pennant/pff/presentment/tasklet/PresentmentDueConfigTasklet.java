package com.pennant.pff.presentment.tasklet;

import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennanttech.pennapps.core.util.DateUtil;

public class PresentmentDueConfigTasklet implements Tasklet {
	@Autowired
	private DueExtractionConfigService presentmentDueConfigService;

	@Autowired
	private DueExtractionConfigDAO dueExtractionConfigDAO;

	public PresentmentDueConfigTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		String automation = jobParameters.getString("AUTOMATION");

		if ("N".equals(automation)) {
			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();

		boolean configExists = dueExtractionConfigDAO.isConfigExists();

		Date marchFirst = DateUtil.getDate(DateUtil.getYear(appDate), 2, 1);

		if (configExists && DateUtil.compare(appDate, marchFirst) != 0) {
			return RepeatStatus.FINISHED;
		}

		Date startDate = getFinancialYearStart(appDate);
		Date startEndDate = getFinancialYearEnd(appDate);

		if (!configExists) {
			int year = DateUtil.getYear(appDate);
			int month = DateUtil.getMonth(appDate);

			startDate = (Date) appDate.clone();
			if (month == 2) {
				startDate = DateUtil.getDate(year, month, 1);
			}
		}

		presentmentDueConfigService.extarctDueConfig(startDate, startEndDate);

		return RepeatStatus.FINISHED;
	}

	private static Date getFinancialYearStart(Date date) {
		return DateUtil.getDate(DateUtil.getYear(date), 3, 1);
	}

	private static Date getFinancialYearEnd(Date date) {
		return DateUtil.getDate(DateUtil.getYear(date) + 1, 2, 31);
	}
}
