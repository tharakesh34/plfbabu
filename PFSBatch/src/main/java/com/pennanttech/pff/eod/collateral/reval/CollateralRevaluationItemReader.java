package com.pennanttech.pff.eod.collateral.reval;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

public class CollateralRevaluationItemReader extends JdbcCursorItemReader<CollateralRevaluation> {
	public CollateralRevaluationItemReader() {
		super.setSql(getSql());
	}

	private long batchId;

	@Override
	public String getSql() {

		StringBuilder sql = new StringBuilder();
		sql.append("select BatchId, FinReference, CollateralType, CollateralRef, CollateralCCY");
		sql.append(", CollateralValue, MarketValue, BankLTV, ThresholdLTV, CommodityId, POS");
		sql.append(", ValueDate");
		sql.append(" from Collateral_Ltv_Breaches ca");
		sql.append(" where BatchId = ").append(batchId);
		sql.append(" and SendAlert = 0");

		return sql.toString();

	}

	@BeforeStep
	public void getInterstepData(StepExecution stepExecution) {
		this.batchId = stepExecution.getJobExecution().getJobInstance().getInstanceId();
		super.setSql(getSql());
	}

}
