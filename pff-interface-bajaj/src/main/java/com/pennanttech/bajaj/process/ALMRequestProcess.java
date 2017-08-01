package com.pennanttech.bajaj.process;

import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennanttech.bajaj.model.alm.ALM;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.process.ProjectedAccrualProcess;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

public class ALMRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(ALMRequestProcess.class);

	private Date appDate;
	private ProjectedAccrualProcess projectedAccrualProcess;
		
	public ALMRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate,
			ProjectedAccrualProcess projectedAccrualProcess) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.ALM_EXTRACT_STATUS);
		this.appDate = appDate;
		this.projectedAccrualProcess = projectedAccrualProcess;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);
		
		// Handle retry case.
		delete();

		loadCount();

		extractData();
		logger.debug(Literal.LEAVING);
	}

	private void extractData() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from INT_ALM_VIEW");

		extract(sql);
	}

	private void extract(StringBuilder sql) {
		jdbcTemplate.query(sql.toString(), new RowCallbackHandler() {
			List<ALM> list = null;
			ALM alm = null;
			String finReference = null;
			String agreementId = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				executionStatus.setProcessedRecords(processedCount++);
				try {
					alm = new ALM();

					finReference = rs.getString("FINREFERENCE");
					agreementId = StringUtils.substring(finReference, finReference.length() - 8, finReference.length());

					alm.setAgreementNo(finReference);
					alm.setAgreementId(Long.parseLong(agreementId));
					alm.setProductFlag(rs.getString("FINTYPE"));
					alm.setNpaStageId(rs.getString("CLOSINGSTATUS"));
					alm.setAdvFlag(rs.getString("ADVFLAG"));

					list = getAccruedAmounts(alm, rs.getDate("FINSTARTDATE"), rs.getDate("MATURITYDATE"),
							rs.getBigDecimal("CCYMINORCCYUNITS"), rs.getInt("CCYEDITFIELD"));

					if (!list.isEmpty()) {
						save(list);
						list = null;
					}

					executionStatus.setSuccessRecords(successCount++);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					executionStatus.setFailedRecords(failedCount++);
					String keyId = finReference;

					if (StringUtils.trimToNull(keyId) == null) {
						keyId = String.valueOf(processedCount);
					}

					saveBatchLog(keyId, "F", e.getMessage());
				}
			}
		});
	}
	
	
	private void delete() {
		MapSqlParameterSource paramMap =  new MapSqlParameterSource();
		paramMap.addValue("ACCRUEDON", appDate);
		delete(paramMap, "ALM", destinationJdbcTemplate, " where ACCRUEDON >=  :ACCRUEDON");
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

	private List<ALM> getAccruedAmounts(ALM alm, Date finStartDate, Date maturityDate, BigDecimal minorCcyUnits,
			int editField) throws Exception {
		List<ALM> list = new ArrayList<>();
		List<ProjectedAccrual> accrualDetails = null;
		accrualDetails = projectedAccrualProcess.calculateAccrualsOnMonthEnd(alm.getAgreementNo(), finStartDate,
				maturityDate, appDate);

		ALM item = null;
		for (ProjectedAccrual accrual : accrualDetails) {
			item = new ALM();

			item.setAgreementNo(alm.getAgreementNo());
			item.setAgreementId(alm.getAgreementId());
			item.setProductFlag(alm.getProductFlag());
			item.setInstallment(getAmount(accrual.getSchdTot(), minorCcyUnits, editField));
			item.setPrinComp(getAmount(accrual.getSchdPri(), minorCcyUnits, editField));
			item.setIntComp(getAmount(accrual.getSchdPft(), minorCcyUnits, editField));
			item.setAccruedAmt(getAmount(accrual.getPftAccrued(), minorCcyUnits, editField));
			item.setCumulativeAccrualAmt(getAmount(accrual.getCumulativeAccrued(), minorCcyUnits, editField));
			item.setDueDate(accrual.getSchdDate());
			item.setAccruedOn(accrual.getAccruedOn());

			list.add(item);
		}
		return list;
	}
	
	private BigDecimal getAmount(BigDecimal amount, BigDecimal minorCcyUnits, int editField) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		amount = amount.divide(minorCcyUnits, 0, RoundingMode.HALF_DOWN);

		return amount;
	}

	private void save(List<ALM> list) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append(" INSERT INTO ALM VALUES(");
		query.append(" :AgreementId,");
		query.append(" :AgreementNo,");
		query.append(" :ProductFlag,");
		query.append(" :NpaStageId,");
		query.append(" :Installment,");
		query.append(" :PrinComp,");
		query.append(" :IntComp,");
		query.append(" :DueDate,");
		query.append(" :AccruedAmt,");
		query.append(" :AccruedOn,");
		query.append(" :CumulativeAccrualAmt,");
		query.append(" :AdvFlag)");

		destinationJdbcTemplate.batchUpdate(query.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));

	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
