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

		sql.append("select ca.Id, BatchId, FinReference, CollateralType, CollateralRef, CollateralCCY");
		sql.append(", CollateralValue, MarketValue, BankLTV, ThresholdLTV, CommodityId, POS");
		sql.append(", ut.TemplateCode userTemplateCode, cust.TemplateCode customerTemplateCode");
		sql.append(", c.alertToRoles, ValueDate");
		sql.append(" from Collateral_Ltv_Breaches ca");
		sql.append(" inner join commodities c on c.id = ca.commodityId");
		sql.append(" inner  join commodity_types ct on ct.id = c.commodityType");
		sql.append(" left join Templates ut on ut.TemplateId = c.userTemplate");
		sql.append(" left join Templates cust on cust.TemplateId = c.customertemplate");
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
