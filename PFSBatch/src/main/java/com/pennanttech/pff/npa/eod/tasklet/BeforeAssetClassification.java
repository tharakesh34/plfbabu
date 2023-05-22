package com.pennanttech.pff.npa.eod.tasklet;

import java.util.Date;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.extension.NpaAndProvisionExtension;
import com.pennanttech.pff.npa.service.AssetClassificationService;

public class BeforeAssetClassification implements Tasklet {
	private AssetClassificationService assetClassificationService;

	public BeforeAssetClassification() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date appDate = SysParamUtil.getAppDate();

		if (NpaAndProvisionExtension.ALLOW_NPA) {
			assetClassificationService.createSnapshots(appDate);
		}

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}
}
