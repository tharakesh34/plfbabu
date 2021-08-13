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
		sql.append("select ca.Id, BatchId, FinID, FinReference, CollateralType, CollateralRef, CollateralCCY");
		sql.append(", CollateralValue, MarketValue, BankLTV, ThresholdLTV, CommodityId, POS, ValueDate");
		sql.append(", AlertToRoles, ut.TemplateCode UserTemplateCode, ct.TemplateCode CustomerTemplateCode");
		sql.append(" from Collateral_Ltv_Breaches ca");
		sql.append(" inner join COMMODITIES c on c.Id = ca.CommodityId");
		sql.append(" left join Templates ut on ut.TemplateId = c.UserTemplate");
		sql.append(" left join Templates ct on ct.TemplateId = c.CustomerTemplate");
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
