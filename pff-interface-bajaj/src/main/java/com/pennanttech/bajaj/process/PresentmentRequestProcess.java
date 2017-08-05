package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants.Status;
import com.pennanttech.pff.core.App;

public class PresentmentRequestProcess extends DatabaseDataEngine {

	private List<Long> idList;
	private long presentmentId;
	private boolean isError;

	public PresentmentRequestProcess(DataSource dataSource, long userId, Date valueDate, List<Long> idList, long presentmentId, boolean isError) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
		this.idList = idList;
		this.presentmentId = presentmentId;
		this.isError = isError;
	}

	@Override
	public void processData() {
		logger.debug("Entering");

		executionStatus.setRemarks("Loading data..");

		MapSqlParameterSource parmMap;
		StringBuilder sql = getSqlQuery();

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("IdList", idList);
		parmMap.addValue("EXCLUDEREASON", "0");

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource map = null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				logger.debug("Entering");
				boolean isBatchFail = false;
				try {
					clearTables();
					
					while (rs.next()) {
						executionStatus.setRemarks("processing the record " + ++totalRecords);
						processedCount++;
						try {
							map = mapData(rs);
							save(map, "PDC_CONSL_EMI_DTL_TEMP", destinationJdbcTemplate);
							successCount++;
						} catch (Exception e) {
							logger.error("Exception :", e);
							failedCount++;
							saveBatchLog(rs.getString("PRESENTMENTID"), "F", e.getMessage());
							throw e;
						} finally {
							map = null;
						}
					}
				} catch (Exception e) {
					logger.error("Exception :", e);
					isBatchFail = true;
				} finally {
					if (isBatchFail) {
						clearTables();
					} else {
						copyDataFromTempToMainTables();
						if (isError) {
							updatePresentmentHeader(presentmentId, 3, executionStatus.getId(), processedCount);
						} else {
							updatePresentmentHeader(presentmentId, 4, executionStatus.getId(), processedCount);
						}
						updatePresentmentDetails(idList, "A");
					}
				}
				return totalRecords;
			}
		});
		logger.debug("Leaving");
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder();
		sql = new StringBuilder();
		sql.append(" SELECT  T2.FINBRANCH, T1.FINREFERENCE, T4.MICR, T3.ACCTYPE, ");
		sql.append(" T3.ACCNUMBER, T5.CUSTSHRTNAME, T3.ACCHOLDERNAME, T6.BANKNAME, ");
		sql.append(" T6.BANKNAME, T1.PRESENTMENTID, T1.PRESENTMENTAMT,");
		sql.append(" T0.PRESENTMENTDATE, T3.MANDATEREF, T4.IFSC, ");
		sql.append(" T7.PARTNERBANKCODE, T7.UTILITYCODE, T3.STARTDATE, T3.EXPIRYDATE, T3.MANDATETYPE, ");
		sql.append(" T2.FINTYPE, T2.CUSTID , T7.PARTNERBANKCODE, T1.EMINO, T4.BRANCHDESC, T4.BRANCHCODE, T1.ID, T1.PresentmentRef, ");
		sql.append(" T8.BRANCHSWIFTBRNCDE, T9.FINDIVISION ENTITYCODE, T10.CCYMINORCCYUNITS FROM PRESENTMENTHEADER T0 ");
		sql.append(" INNER JOIN PRESENTMENTDETAILS T1 ON T0.ID = T1.PRESENTMENTID ");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE ");
		sql.append(" INNER JOIN CuSTOMERS T5 ON T5.CUSTID = T2.CUSTID ");
		sql.append(" INNER JOIN MANDATES T3 ON T2.MANDATEID = T3.MANDATEID ");
		sql.append(" INNER JOIN BANKBRANCHES T4 ON T3.BANKBRANCHID = T4.BANKBRANCHID ");
		sql.append(" INNER JOIN BMTBANKDETAIL T6 ON T4.BANKCODE = T6.BANKCODE ");
		sql.append(" INNER JOIN PARTNERBANKS T7 ON T7.PARTNERBANKID = T0.PARTNERBANKID ");
		sql.append(" INNER JOIN RMTBRANCHES T8 ON T8.BRANCHCODE = T2.FINBRANCH ");
		sql.append(" INNER JOIN RMTFINANCETYPES T9 ON T9.FINTYPE = T2.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES T10 ON T10.CCYCODE = T2.FINCCY");
		sql.append(" WHERE T1.ID IN(:IdList) AND T1.EXCLUDEREASON = :EXCLUDEREASON ");
		return sql;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("PR_KEY", String.valueOf(rs.getLong("PRESENTMENTID")));
		map.addValue("BR_CODE", rs.getString("BRANCHSWIFTBRNCDE"));
		map.addValue("AGREEMENTNO", rs.getString("FINREFERENCE"));
		map.addValue("MICR_CODE", rs.getString("MICR"));
		map.addValue("ACC_TYPE", rs.getString("ACCTYPE"));
		map.addValue("LEDGER_FOLIO", "000");
		map.addValue("FINWARE_ACNO", rs.getString("ACCNUMBER"));
		map.addValue("DEST_ACC_HOLDER", rs.getString("CUSTSHRTNAME"));
		map.addValue("PDC_BY_NAME", rs.getString("ACCHOLDERNAME"));
		map.addValue("BANK_NAME", rs.getString("BANKNAME"));
		map.addValue("BANK_ADDRESS", rs.getString("BRANCHDESC"));
		map.addValue("EMI_NO", rs.getString("EMINO"));
		String mnadteType = rs.getString("MANDATETYPE");

		if (StringUtils.equals(mnadteType, "ECS") || StringUtils.equals(mnadteType, "DDM")) {
			map.addValue("BFL_REF", "405");
		} else {
			map.addValue("BFL_REF", rs.getString("BRANCHSWIFTBRNCDE"));
		}
		map.addValue("BATCHID", rs.getString("PresentmentRef"));
		
		//Presentment amount convertion using currency minor units..
		BigDecimal presentAmt = rs.getBigDecimal("PRESENTMENTAMT");
		int ccyMinorUnits = rs.getInt("CCYMINORCCYUNITS");
		BigDecimal checqueAmt = presentAmt.divide(new BigDecimal(ccyMinorUnits));
		
		map.addValue("CHEQUEAMOUNT", checqueAmt);
		map.addValue("PRESENTATIONDATE", rs.getDate("PRESENTMENTDATE"));
		map.addValue("RESUB_FLAG", Status.N.name());
		map.addValue("UMRN_NO", rs.getString("MANDATEREF"));

		map.addValue("IFSC_CODE", rs.getString("IFSC"));
		map.addValue("SPONSER_BANK_CODE", rs.getString("PARTNERBANKCODE"));
		map.addValue("UTILITY_CODE", rs.getString("UTILITYCODE"));
		map.addValue("MANDATE_START_DT", rs.getDate("STARTDATE"));
		map.addValue("MANDATE_END_DT", rs.getDate("EXPIRYDATE"));

		if (StringUtils.equals(mnadteType, "ECS")) {
			map.addValue("INSTRUMENT_MODE", "E");
		} else if (StringUtils.equals(mnadteType, "DDM")) {
			map.addValue("INSTRUMENT_MODE", "A");
		} else if (StringUtils.equals(mnadteType, "NACH")) {
			map.addValue("INSTRUMENT_MODE", "Z");
		}
		map.addValue("PRODUCT_CODE", rs.getString("FINTYPE"));
		map.addValue("LESSEEID", rs.getInt("CUSTID"));
		map.addValue("PICKUP_BATCHID", -1);
		map.addValue("TXN_TYPE_CODE", 1);
		map.addValue("SOURCE_CODE", 1);
		map.addValue("ENTITY_CODE", 1);
		map.addValue("POSTING_DATETIME", DateUtil.getSysDate());
		map.addValue("STATUS", Status.N.name());

		return map;
	}

	private void updatePresentmentHeader(long presentmentId, int manualEcclude, long dBStatusId, long totalRecords) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append( " UPDATE PRESENTMENTHEADER Set STATUS = :STATUS, DBSTATUSID = :DBSTATUSID, TOTALRECORDS = TOTALRECORDS+:TOTALRECORDS  Where ID = :ID ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("STATUS", manualEcclude);
		source.addValue("DBSTATUSID", dBStatusId);
		source.addValue("ID", presentmentId);
		source.addValue("TOTALRECORDS", totalRecords);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}
	
	private void updatePresentmentDetails(List<Long> idList, String status) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" UPDATE PRESENTMENTDETAILS Set STATUS = :STATUS,  ErrorDesc = :ErrorDesc Where ID IN(:IDList) AND EXCLUDEREASON = :EXCLUDEREASON ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("IDList", idList);
		source.addValue("STATUS", status);
		source.addValue("ErrorDesc", null);
		source.addValue("EXCLUDEREASON", 0);

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private void clearTables() {
		logger.debug(Literal.ENTERING);

		destinationJdbcTemplate.update("TRUNCATE TABLE PDC_CONSL_EMI_DTL_TEMP", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	private void copyDataFromTempToMainTables() {
		logger.debug(Literal.ENTERING);

		destinationJdbcTemplate.update("INSERT INTO PDC_CONSL_EMI_DTL SELECT * FROM PDC_CONSL_EMI_DTL_TEMP", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

}
