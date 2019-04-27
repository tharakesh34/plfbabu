package com.pennanttech.pff.eod.collateral.reval;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.endofday.tasklet.LedgerDownload;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

public class LoadCollateralRevaluationDataTasklet extends BasicDao<CollateralRevaluation> implements Tasklet {
	private Logger logger = Logger.getLogger(LedgerDownload.class);

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		long batchId = context.getStepContext().getStepExecution().getJobExecution().getJobInstance().getInstanceId();

		StringBuilder sql = new StringBuilder();
		sql.append("insert into Collateral_Ltv_Breaches (BatchId, FinReference, CollateralType, CollateralRef");
		sql.append(", Collateralccy, MarketValue, CollateralValue, BankLTV");
		sql.append(", ThresholdLTV, POS, CommodityId, ValueDate)");
		sql.append(" select :BatchId, fm.FinReference, cs.CollateralType, ca.CollateralRef");
		sql.append(", cs.Collateralccy, c.Currentvalue, cs.CollateralValue, cs.BankLTV");
		sql.append(", ce.thresholdLtvPercentage, fpt.odprincipal, c.id, :ValueDate");
		sql.append(" from collateralassignment ca");
		sql.append(" inner join collateralsetup cs on cs.collateralref = ca.collateralref");
		sql.append(" inner join CollateralStructure ce on ce.CollateralType = cs.CollateralType");
		sql.append(" inner join commodities c on c.id = ce.commodityId");
		sql.append(" left  join commodity_types ct on ct.id = c.commodityType");
		sql.append(" inner join financemain fm on fm.finreference = ca.reference");
		sql.append(" inner join finpftdetails fpt on fpt.finreference = ca.reference");
		sql.append(" where fm.finisactive = 1");

		try {
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("BatchId", batchId);
			paramMap.addValue("ValueDate", valueDate);

			jdbcTemplate.update(sql.toString(), paramMap);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return RepeatStatus.FINISHED;
	}
}
