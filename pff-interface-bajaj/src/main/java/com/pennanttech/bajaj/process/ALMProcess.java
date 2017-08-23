package com.pennanttech.bajaj.process;

import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennanttech.bajaj.model.alm.ALM;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.process.ProjectedAccrualProcess;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

public class ALMProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(ALMProcess.class);
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("ALM_REQUEST");
	
	private Date appDate;
	private ProjectedAccrualProcess projectedAccrualProcess;
	private MapSqlParameterSource paramMap = null;
		
	public ALMProcess(DataSource dataSource, long userId, Date valueDate, Date appDate,
			ProjectedAccrualProcess projectedAccrualProcess) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);
		this.appDate = appDate;
		this.projectedAccrualProcess = projectedAccrualProcess;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);
		
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("FINISACTIVE", 1);
		paramMap.addValue("PAYMENTTYPE", "EMIINADV");
		paramMap.addValue("ACCRUEDON", appDate);
		
		try {
			// Handle retry case.
			delete();

			loadCount();

			extractData();
		} catch (Exception e) {
			EXTRACT_STATUS.setStatus("F");
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void extractData() throws SQLException {
		Map<String, Integer> advanceEmis = getadvanceEmis();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FINREFERENCE, FINTYPE, FINSTARTDATE, MATURITYDATE,");
		sql.append(" CLOSINGSTATUS, CCYMINORCCYUNITS, CCYEDITFIELD FROM FINANCEMAIN FM"); 
		sql.append(" INNER JOIN RMTCURRENCIES CCY ON CCY.CCYCODE = FM.FINCCY");
		sql.append(" WHERE FINISACTIVE=:FINISACTIVE");

		extract(sql, advanceEmis);
	}

	
	
	private Map<String, Integer> getadvanceEmis() throws SQLException {
		Map<String, Integer> advanceEmis = new HashMap<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE, COUNT(RH.REFERENCE) EMIADV");
		sql.append(" FROM FINRECEIPTDETAIL RD");
		sql.append(" INNER JOIN FINRECEIPTHEADER RH ON RH.RECEIPTID=RH.RECEIPTID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON RH.REFERENCE = FM.FINREFERENCE");
		sql.append(" WHERE PAYMENTTYPE=:PAYMENTTYPE AND FINISACTIVE=:FINISACTIVE");
		sql.append(" GROUP BY FM.FINREFERENCE");

		return extractCustomers(advanceEmis, sql);
	}

	private Map<String, Integer> extractCustomers(Map<String, Integer> advanceEmis, StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					advanceEmis.put(rs.getString("FINREFERENCE"), rs.getInt("EMIADV"));
				}
				return advanceEmis;
			}
		});
	}
	

	private void extract(StringBuilder sql, Map<String, Integer> advanceEmis) {
		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				executionStatus.setProcessedRecords(processedCount++);
				try {
					ALM	alm = new ALM();

					String finReference = rs.getString("FINREFERENCE");
					String agreementId = StringUtils.substring(finReference, finReference.length() - 8, finReference.length());

					alm.setAgreementNo(finReference);
					alm.setAgreementId(Long.parseLong(agreementId));
					alm.setProductFlag(rs.getString("FINTYPE"));
					alm.setNpaStageId(rs.getString("CLOSINGSTATUS"));
					
					Integer advanceEmi = advanceEmis.get(finReference);
					
					if (advanceEmi != null && advanceEmi.intValue() > 0) {
						alm.setAdvFlag("Y");
					} else {
						alm.setAdvFlag("N");
					}
					
					List<ALM> list = getAccruedAmounts(alm, rs.getDate("FINSTARTDATE"), rs.getDate("MATURITYDATE"),
							rs.getBigDecimal("CCYMINORCCYUNITS"), rs.getInt("CCYEDITFIELD"));

					if (!list.isEmpty()) {
						save(list);
						list = null;
					}

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
	}
	
	private void delete() {
		delete(paramMap, "ALM", destinationJdbcTemplate, " WHERE ACCRUEDON >= :ACCRUEDON");
	}

	private void loadCount() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(*) from FINANCEMAIN WHERE FINISACTIVE=:FINISACTIVE ");

		try {
			totalRecords = parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);
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
			item.setAdvFlag(alm.getAdvFlag());
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
