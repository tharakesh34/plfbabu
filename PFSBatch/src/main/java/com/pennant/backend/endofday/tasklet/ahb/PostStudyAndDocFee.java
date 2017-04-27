package com.pennant.backend.endofday.tasklet.ahb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.BatchUtil;

public class PostStudyAndDocFee extends ServiceHelper implements Tasklet {
	private static final long		serialVersionUID		= 426232865118229782L;
	private static Logger			logger					= Logger.getLogger(PostStudyAndDocFee.class);

	private static final BigDecimal	UPFRONTFEE_PERCENTAGE	= new BigDecimal(0.25);

	public PostStudyAndDocFee() {
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
			Date valueDate = DateUtility.getValueDate();

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				Date enddate = resultSet.getDate("StudyFeeExpiryDate");
				Date startdate = resultSet.getDate("StudyFeeStartDate");

				if (enddate.compareTo(valueDate) >= 0 && startdate.compareTo(valueDate) <= 0) {

					boolean amzCalculated = false;
					boolean upFrontCalcualted = false;
					BigDecimal approvedLimit = getValue(resultSet.getBigDecimal("ApprovedLimit"));

					String custref = resultSet.getString("CustomerReference");
					String limitRef = resultSet.getString("LimitRef");
					BigDecimal totalFee = getValue(resultSet.getBigDecimal("StudyFee"));

					BigDecimal upFrontFee = getValue(resultSet.getBigDecimal("UpFrontFee"));
					BigDecimal feeToAmz = getValue(resultSet.getBigDecimal("FeeToAmz"));
					BigDecimal tdFeeAmz = getValue(resultSet.getBigDecimal("TdFeeAmz"));

					if (feeToAmz.compareTo(BigDecimal.ZERO) == 0) {

						if (upFrontFee.compareTo(BigDecimal.ZERO) == 0 && startdate.compareTo(valueDate) == 0) {
							//calculate Up front fee
							upFrontFee = approvedLimit.multiply(UPFRONTFEE_PERCENTAGE).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
							upFrontCalcualted = true;
						}

						if (upFrontFee.compareTo(totalFee) < 0) {
							feeToAmz = totalFee.subtract(upFrontFee);
						}

					}

					BigDecimal feeToAmzNow = feeToAmz.subtract(tdFeeAmz);
					BigDecimal amortizedToday = BigDecimal.ZERO;

					if (feeToAmzNow.compareTo(BigDecimal.ZERO) > 0) {
						//assuming that for one day amortization i.e if start and end date are same
						int totalDays = DateUtility.getDaysBetween(enddate, startdate);
						if (totalDays == 0) {
							amortizedToday = feeToAmz;
						} else {
							BigDecimal perDay = feeToAmz.divide(new BigDecimal(totalDays), 0, RoundingMode.HALF_DOWN);
							int days = DateUtility.getDaysBetween(DateUtility.getValueDate(), startdate) + 1;
							amortizedToday = new BigDecimal(days).multiply(perDay);

						}
						amzCalculated = true;
					}

					if (upFrontCalcualted || amzCalculated) {
						//Postings Process
						HashMap<String, Object> executingMap = new HashMap<String, Object>();
						executingMap.put("fm_finReference", custref + limitRef);
						executingMap.put("finEvent", AccountEventConstants.STUDY_DOCUMENTATION);
						executingMap.put("fm_finBranch", "9999");
						executingMap.put("fm_finType", "");
						BigDecimal fxAmount = BigDecimal.ZERO;
						if (upFrontCalcualted) {
							fxAmount = upFrontFee;
						} 
						executingMap.put("UpFrontSDFee", fxAmount);
						executingMap.put("TdSDFeeAmz", amortizedToday);

						//Postings Process
						List<ReturnDataSet> list = processAccountingByEvent(executingMap);
						
						saveAccounting(list);
					}
					//update details
					update(custref, limitRef, upFrontFee, feeToAmz, tdFeeAmz);
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
		logger.debug("COMPLETE: Next payment Details for Value Date: " + DateUtility.getValueDate());
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
		StringBuilder query = new StringBuilder(" SELECT * from StudyFeeCharges");
		query.append(" where  StudyFeeExpiryDate >=? ");
		return query.toString();
	}

	private String getCountQuery() {
		StringBuilder query = new StringBuilder(" SELECT Count(*)  from StudyFeeCharges");
		query.append(" where  StudyFeeExpiryDate >= ? ");
		return query.toString();
	}

	private void update(String custref, String limitRef, BigDecimal upFrontFee, BigDecimal feeToAmz, BigDecimal tdFeeAmz) throws SQLException {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());

		StringBuilder query = new StringBuilder("Update StudyFeeCharges set UpFrontFee = :UpFrontFee , FeeToAmz = :FeeToAmz , TdFeeAmz = :TdFeeAmz ");
		query.append(" where  CustomerReference=:CustomerReference and LimitRef=:LimitRef");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("UpFrontFee", upFrontFee);
		paramSource.addValue("FeeToAmz", feeToAmz);
		paramSource.addValue("TdFeeAmz", tdFeeAmz);
		paramSource.addValue("CustomerReference", custref);
		paramSource.addValue("LimitRef", limitRef);

		jdbcTemplate.update(query.toString(), paramSource);

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

}
