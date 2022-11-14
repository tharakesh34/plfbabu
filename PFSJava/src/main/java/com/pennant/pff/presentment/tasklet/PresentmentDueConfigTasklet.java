package com.pennant.pff.presentment.tasklet;

import java.util.Date;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennanttech.pennapps.core.util.DateUtil;

public class PresentmentDueConfigTasklet implements Tasklet {
	private DueExtractionConfigService decService;
	private DueExtractionConfigDAO decDao;

	public PresentmentDueConfigTasklet(DueExtractionConfigService decService, DueExtractionConfigDAO decDao) {
		super();
		this.decService = decService;
		this.decDao = decDao;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Date appDate = SysParamUtil.getLastBusinessdate();

		Date march3st = DateUtil.getDate(DateUtil.getYear(appDate), 2, 31);

		if (DateUtil.compare(appDate, march3st) == 0) {

		}

		boolean configExists = decDao.isConfigExists();

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
				startDate = DateUtil.getDate(year, month - 1, 1);
			}
		}

		decService.extarctDueConfig();

		return RepeatStatus.FINISHED;
	}

	private static Date getFinancialYearStart(Date date) {
		int year = DateUtil.getYear(date);

		int month = DateUtil.getMonth(date);

		if (month <= 3) {
			year = year - 1;
		}

		return DateUtil.getDate(year, 3, 1);
	}

	private static Date getFinancialYearEnd(Date date) {
		int year = DateUtil.getYear(date);

		int month = DateUtil.getMonth(date);

		if (month <= 3) {
			year = year - 1;
		}

		return DateUtil.getDate(year + 1, 2, 31);
	}
}
