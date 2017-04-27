package com.pennant.backend.endofday.tasklet.ahb;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.BatchUtil;

public class PostFXRevaluation extends ServiceHelper implements Tasklet {
	private static final long	serialVersionUID	= 426232865118229782L;
	private static Logger		logger				= Logger.getLogger(PostFXRevaluation.class);

	public PostFXRevaluation() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		logger.debug("Entering");

		logger.debug("START: Next payment Details for Value Date: " + DateUtility.getValueDate());

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setString(1, SysParamUtil.getAppCurrency());
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setString(1, SysParamUtil.getAppCurrency());
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				String finRef = resultSet.getString("FinReference");
				String fincccy = resultSet.getString("FinCcy");

				List<ReturnDataSet> processlist = getFinanceAmtByCategory(finRef);

				for (ReturnDataSet returnDataSet : processlist) {

					BigDecimal postAmt = getValue(returnDataSet.getPostAmount());
					BigDecimal postAmtLcCcy = getValue(returnDataSet.getPostAmountLcCcy());
					//today
					BigDecimal postAmtLcCcyNow = CalculationUtil.getConvertedAmount(fincccy, fincccy, postAmt);

					BigDecimal fxAmount = postAmtLcCcyNow.subtract(postAmtLcCcy);

					//DataSet Object preparation for AccountingSet Execution
					HashMap<String, Object> executingMap = new HashMap<String, Object>();
					executingMap.put("fm_finReference", finRef);
					executingMap.put("finEvent", AccountEventConstants.FX_REVALUATION);
					executingMap.put("fm_finBranch", "9999");
					executingMap.put("fm_finType", "");
					executingMap.put("fxAmount", fxAmount);

					//Postings Process
					List<ReturnDataSet> list = processAccountingByEvent(executingMap);
					saveAccounting(list);

				}

			}

		} catch (SQLException e) {
			logger.error("Finrefernce :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Next payment Details for Value Date: " + DateUtility.getAppDate());
		return RepeatStatus.FINISHED;
	}

	/**
	 * @param value
	 * @return
	 */
	private BigDecimal getValue(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		} else {
			return value;
		}
	}

	private String prepareSelectQuery() {
		StringBuilder query = new StringBuilder(" SELECT  FinReference,FinCcy from FinPftDetails where FinCcy != ? ");

		return query.toString();
	}

	private String getCountQuery() {
		StringBuilder query = new StringBuilder(" SELECT  count(*) from FinPftDetails where FinCcy != ? ");
		return query.toString();
	}

	private List<ReturnDataSet> getFinanceAmtByCategory(String finreference) throws SQLException {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());

		StringBuilder query = new StringBuilder("select Account, sum(case Drorcr when 'D' then (PostAmount*-1) else PostAmount end) PostAmount");
		query.append(" ,sum(case Drorcr when 'D' then (PostAmountLcCcy*-1) else PostAmountLcCcy end) PostAmountLcCcy");
		query.append(" from postings where  Finreference = :Finreference and PostToSys= :PostToSys");
		query.append(" group by Account ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Finreference", finreference);
		paramSource.addValue("PostToSys", "E");
		RowMapper<ReturnDataSet> mapper = ParameterizedBeanPropertyRowMapper.newInstance(ReturnDataSet.class);

		return jdbcTemplate.query(query.toString(), paramSource, mapper);

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

}
