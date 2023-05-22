package com.pennanttech.pff.npa.eod.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.extension.NpaAndProvisionExtension;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.npa.service.AssetClassificationService;

public class AssetClassificationQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(AssetClassificationQueue.class);

	private AssetClassificationService assetClassificationService;

	public AssetClassificationQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		if (!NpaAndProvisionExtension.ALLOW_NPA) {
			return RepeatStatus.FINISHED;
		}

		long count = assetClassificationService.prepareQueue();

		StepUtil.NPA_CLASSIFICATION.setTotalRecords(count);

		logger.info("Queueing preparation for NPA Clasification completed with total loans {}", count);

		AssetClassificationTaskLet.processedCount.set(0);
		AssetClassificationTaskLet.failedCount.set(0);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}
}
