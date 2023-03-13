package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class OTSQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(OTSQueue.class);

	private SettlementService settlementService;

	public OTSQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);

		if (!eventProperties.isAllowOTSOnEOD()) {
			return RepeatStatus.FINISHED;
		}

		long count = settlementService.prepareQueue();

		StepUtil.OTS.setTotalRecords(count);

		logger.info("Queueing preparation for OTS completed with total loans{}", count);

		OTSTasklet.processedCount.set(0);
		OTSTasklet.failedCount.set(0);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}
}
