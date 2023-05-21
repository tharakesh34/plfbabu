package com.pennanttech.pff.autowriteoff.eod.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.autowriteoff.service.AutoWriteOffService;
import com.pennanttech.pff.eod.step.StepUtil;

public class AutoWriteOffCalcQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(AutoWriteOffCalcQueue.class);

	private AutoWriteOffService autoWriteOffService;

	public AutoWriteOffCalcQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		long count = 0;
		Date appDate = SysParamUtil.getAppDate();

		Date monthEnd = DateUtil.getMonthEnd(appDate);

		if (appDate.compareTo(monthEnd) == 0) {
			count = autoWriteOffService.prepareQueueForEOM();
		}

		StepUtil.AUTO_WRITE_OFF.setTotalRecords(count);

		AutoWriteOffClacTaskLet.processedCount.set(0);
		AutoWriteOffClacTaskLet.failedCount.set(0);

		logger.info("Queueing preparation for Auto Write Off completed with total loans {}", count);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setAutoWriteOffService(AutoWriteOffService autoWriteOffService) {
		this.autoWriteOffService = autoWriteOffService;
	}

}