package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.process.ProjectedAccrualProcess;

public class ALMRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(ALMRequestProcess.class);

	private Date appDate;

	ProjectedAccrualProcess projectedAccrualProcess;

	public ALMRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate,
			ProjectedAccrualProcess projectedAccrualProcess) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.ALM_EXTRACT_STATUS);
		this.appDate = appDate;
		this.projectedAccrualProcess = projectedAccrualProcess;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		delete(new MapSqlParameterSource(), "ALM", destinationJdbcTemplate, new String[0]);

		loadCount();

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from INT_ALM_VIEW");

		jdbcTemplate.query(sql.toString(), new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				executionStatus.setProcessedRecords(processedCount++);
				try {
					saveAccrualDetails(rs);
					executionStatus.setSuccessRecords(successCount++);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					executionStatus.setFailedRecords(failedCount++);
					String keyId = rs.getString("FINREFERENCE");

					if (StringUtils.trimToNull(keyId) == null) {
						keyId = String.valueOf(processedCount);
					}

					saveBatchLog(keyId, "F", e.getMessage());
				}

			}
		});
		logger.debug(Literal.LEAVING);
	}

	private void loadCount() {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" select count(*) from INT_ALM_VIEW");

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
			executionStatus.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void saveAccrualDetails(ResultSet rs) throws Exception {
		String finReference = rs.getString("FINREFERENCE");
		String agreementId = StringUtils.substring(finReference, finReference.length() - 8, finReference.length());
		Date startDate = rs.getDate("FINSTARTDATE");
		Date maturityDate = rs.getDate("MATURITYDATE");
		String clostingStatus = rs.getString("CLOSINGSTATUS");
		String finType = rs.getString("FINTYPE");
		String advanceFlag = rs.getString("ADVFLAG");
		BigDecimal minorCcyUnits = rs.getBigDecimal("CCYMINORCCYUNITS");
		int editField = rs.getInt("CCYEDITFIELD");

		List<ProjectedAccrual> accrualDetails = null;
		accrualDetails = projectedAccrualProcess.calculateAccrualsOnMonthEnd(finReference, startDate, maturityDate,
				appDate);

		if (accrualDetails.isEmpty()) {
			return;
		}

		List<Object[]> parameters = new ArrayList<Object[]>();

		BigDecimal schdTot = null;
		BigDecimal schdPri = null;
		BigDecimal schdPft = null;
		BigDecimal pftAccrued = null;
		BigDecimal cumulativeAccrued = null;

		for (ProjectedAccrual accrual : accrualDetails) {
			schdTot = getAmount(accrual.getSchdTot(), minorCcyUnits, editField);
			schdPri = getAmount(accrual.getSchdPri(), minorCcyUnits, editField);
			schdPft = getAmount(accrual.getSchdPft(), minorCcyUnits, editField);
			pftAccrued = getAmount(accrual.getPftAccrued(), minorCcyUnits, editField);
			cumulativeAccrued = getAmount(accrual.getCumulativeAccrued(), minorCcyUnits, editField);

			parameters.add(new Object[] { agreementId, finReference, finType, clostingStatus, schdTot, schdPri, schdPft,
					accrual.getSchdDate(), pftAccrued, accrual.getAccruedOn(), cumulativeAccrued, advanceFlag });
		}

		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ALM VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		destinationJdbcTemplate.getJdbcOperations().batchUpdate(query.toString(), parameters);

	}

	private BigDecimal getAmount(BigDecimal amount, BigDecimal minorCcyUnits, int editField) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		amount = amount.divide(minorCcyUnits, 0, RoundingMode.HALF_DOWN);

		return amount;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
