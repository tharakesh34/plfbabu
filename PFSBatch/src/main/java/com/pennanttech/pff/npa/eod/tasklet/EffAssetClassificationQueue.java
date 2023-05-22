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

public class EffAssetClassificationQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(EffAssetClassificationQueue.class);

	private AssetClassificationService assetClassificationService;

	public EffAssetClassificationQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		if (!NpaAndProvisionExtension.ALLOW_NPA) {
			return RepeatStatus.FINISHED;
		}

		long count = assetClassificationService.prepareQueue();

		StepUtil.EFF_NPA_CLASSIFICATION.setTotalRecords(count);

		logger.info("Queueing preparation for EFF-NPA Clasification completed with total customers {}", count);

		EffAssetClassificationTaskLet.processedCount.set(0);
		EffAssetClassificationTaskLet.failedCount.set(0);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}
}
