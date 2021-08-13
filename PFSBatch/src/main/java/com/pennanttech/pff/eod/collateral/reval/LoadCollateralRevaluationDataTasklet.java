package com.pennanttech.pff.eod.collateral.reval;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;
import com.pennanttech.pff.eod.step.StepUtil;

public class LoadCollateralRevaluationDataTasklet extends BasicDao<CollateralRevaluation> implements Tasklet {
	private Logger logger = LogManager.getLogger(LoadCollateralRevaluationDataTasklet.class);

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		long batchId = context.getStepContext().getStepExecution().getJobExecution().getJobInstance().getInstanceId();
		BatchUtil.setExecutionStatus(context, StepUtil.COLLATERAL_REVALUATION);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into Collateral_Ltv_Breaches (BatchId, FinID, FinReference, CollateralType, CollateralRef");
		sql.append(", Collateralccy, MarketValue, CollateralValue, BankLTV, BankValuation");
		sql.append(", ThresholdLTV, POS, CommodityId, ValueDate)");
		sql.append(" select :BatchId, fm.FinID, fm.FinReference, cs.CollateralType, ca.CollateralRef");
		sql.append(", cs.Collateralccy, 0 , cs.CollateralValue, cs.BankLTV, cs.BankValuation");
		sql.append(", ce.thresholdLtvPercentage, fpt.TotalPriBal, null , :ValueDate");
		sql.append(" from collateralassignment ca");
		sql.append(" inner join collateralsetup cs on cs.collateralref = ca.collateralref");
		sql.append(" inner join CollateralStructure ce on ce.CollateralType = cs.CollateralType");
		sql.append(" and ce.marketablesecurities = 1 and (CommodityId > 0 and CommodityId is not null)");
		sql.append(" inner join financemain fm on fm.finreference = ca.reference");
		sql.append(" inner join finpftdetails fpt on fpt.finreference = ca.reference");
		sql.append(" where fm.finisactive = 1"); /* FIXME : change to FinID */

		try {
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("BatchId", batchId);
			paramMap.addValue("ValueDate", valueDate);

			int totalRecords = jdbcTemplate.update(sql.toString(), paramMap);
			StepUtil.COLLATERAL_REVALUATION.setTotalRecords(totalRecords);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("Unable to load Collateral LTV Breaches");
		}
		return RepeatStatus.FINISHED;
	}
}
