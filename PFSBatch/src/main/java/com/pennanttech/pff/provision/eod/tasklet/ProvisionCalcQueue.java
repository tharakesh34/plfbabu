package com.pennanttech.pff.provision.eod.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.extension.NpaAndProvisionExtension;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.provision.service.ProvisionService;

public class ProvisionCalcQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(ProvisionCalcQueue.class);

	private ProvisionService provisionService;

	public ProvisionCalcQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		if (!NpaAndProvisionExtension.ALLOW_PROVISION) {
			return RepeatStatus.FINISHED;
		}

		long count = 0;

		Date appDate = SysParamUtil.getAppDate();

		Date monthStart = DateUtil.getMonthStart(appDate);
		Date monthEnd = DateUtil.getMonthEnd(appDate);

		if (appDate.compareTo(monthStart) == 0) {
			count = provisionService.prepareQueueForSOM();
		}

		if (appDate.compareTo(monthEnd) == 0) {
			count = provisionService.prepareQueueForEOM();
		}

		StepUtil.PROVISION_CALC.setTotalRecords(count);

		ProvisionClacTaskLet.processedCount.set(0);
		ProvisionClacTaskLet.failedCount.set(0);

		logger.info("Queueing preparation for provision calculation completed with total loans {}", count);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

}
