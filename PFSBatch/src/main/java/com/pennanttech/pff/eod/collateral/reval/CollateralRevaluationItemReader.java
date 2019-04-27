package com.pennanttech.pff.eod.collateral.reval;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;

import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

public class CollateralRevaluationItemReader extends JdbcCursorItemReader<CollateralRevaluation> {
	public CollateralRevaluationItemReader() {
		super();
	}

	private long batchId;

	@Override
	public String getSql() {
		StringBuilder sql = new StringBuilder();

		sql.append("select Id, BatchId, FinReference, CollateralType, CollateralRef, CollateralCCY");
		sql.append(", CollateralValue, MarketValue, BankLTV, ThresholdLTV, CommodityId, POS");
		sql.append(", ut.TemplateCode userTemplateCode, cust.TemplateCode customerTemplateCode");
		sql.append(", c.alertToRoles");
		sql.append(" from Collateral_Ltv_Breaches ca");
		sql.append(" inner join commodities c on c.id = ca.commodityId");
		sql.append(" inner  join commodity_types ct on ct.id = c.commodityId");
		sql.append(" left join Templates ut on ut.TemplateId = ct.userTemplate");
		sql.append(" left join Templates cust on cust.TemplateId = ct.customertemplate");
		sql.append(" where BatchId = ").append(batchId);

		return sql.toString();

	}

	@Value("#{jobExecutionContext['jobId']}")
	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}
}
