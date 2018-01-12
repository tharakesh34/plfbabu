package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants.Status;

public class PresentmentRequestProcess extends DatabaseDataEngine {

	private List<Long> idList;
	private long presentmentId;
	private boolean isError;
	private long							processedCount	= 0;
	protected final static Logger			logger	= LoggerFactory.getLogger(PresentmentRequestProcess.class.getClass());
	protected NamedParameterJdbcTemplate	parameterJdbcTemplate;

	public PresentmentRequestProcess(DataSource dataSource, long userId, Date valueDate, List<Long> idList, long presentmentId, boolean isError) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
		this.idList = idList;
		this.presentmentId = presentmentId;
		this.isError = isError;
		parameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public void processData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap;
		StringBuilder sql = getSqlQuery();

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("IdList", idList);
		parmMap.addValue("EXCLUDEREASON", 0);

		parameterJdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource map = null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				logger.debug(Literal.ENTERING);
				boolean isBatchFail = false;
				try {
					clearTables();
					
					while (rs.next()) {
						processedCount++;
						try {
							map = mapData(rs);
							save(map);
						} catch (Exception e) {
							logger.error("Exception :", e);
							throw e;
						} finally {
							map = null;
						}
					}
				} catch (Exception e) {
					logger.error("Exception :", e);
					isBatchFail = true;
				} finally {
					
					if (!isBatchFail) {
						try {
							prepareRequestFile();
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							isBatchFail = true;
						}
					}
					
					if (isBatchFail) {
						clearTables();
					} else {
						copyDataFromTempToMainTables();

						if (isError) {
							updatePresentmentHeader(presentmentId, 3, processedCount);
						} else {
							updatePresentmentHeader(presentmentId, 4, processedCount);
						}
						updatePresentmentDetails(idList, "A");
					}
				}
				return presentmentId;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	public void prepareRequestFile() throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			DataEngineExport dataEngine = null;
			dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate());
			dataEngine.exportData("PRESENTMENT_REQUEST");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
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
	
	private void save(MapSqlParameterSource map) {
		StringBuilder sql = new StringBuilder("insert into PDC_CONSL_EMI_DTL_TEMP");
		sql.append(" (PR_KEY, BR_CODE, AGREEMENTNO, MICR_CODE, ACC_TYPE, LEDGER_FOLIO, FINWARE_ACNO,");
		sql.append(" DEST_ACC_HOLDER, PDC_BY_NAME, BANK_NAME, BANK_ADDRESS, EMI_NO, BFL_REF,");
		sql.append(" BATCHID, CHEQUEAMOUNT, PRESENTATIONDATE, RESUB_FLAG,");
		sql.append(" UMRN_NO, IFSC_CODE, SPONSER_BANK_CODE, UTILITY_CODE, MANDATE_START_DT,");
		sql.append(" MANDATE_END_DT, INSTRUMENT_MODE, PRODUCT_CODE, LESSEEID, PICKUP_BATCHID,");
		sql.append(" TXN_TYPE_CODE, SOURCE_CODE, ENTITY_CODE, POSTING_DATETIME, STATUS)");
		sql.append("  values(:PR_KEY, :BR_CODE, :AGREEMENTNO, :MICR_CODE, :ACC_TYPE, :LEDGER_FOLIO, :FINWARE_ACNO,");
		sql.append(" :DEST_ACC_HOLDER, :PDC_BY_NAME, :BANK_NAME, :BANK_ADDRESS, :EMI_NO, :BFL_REF, ");
		sql.append(" :BATCHID, :CHEQUEAMOUNT, :PRESENTATIONDATE, :RESUB_FLAG,");
		sql.append(" :UMRN_NO, :IFSC_CODE, :SPONSER_BANK_CODE, :UTILITY_CODE, :MANDATE_START_DT, ");
		sql.append(" :MANDATE_END_DT, :INSTRUMENT_MODE, :PRODUCT_CODE, :LESSEEID, :PICKUP_BATCHID, ");
		sql.append(" :TXN_TYPE_CODE, :SOURCE_CODE, :ENTITY_CODE, :POSTING_DATETIME, :STATUS ) ");

		try {
			parameterJdbcTemplate.update(sql.toString(), map);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("PR_KEY", String.valueOf(rs.getLong("PRESENTMENTID")));
		map.addValue("BR_CODE", rs.getString("BRANCHSWIFTBRNCDE"));
		map.addValue("AGREEMENTNO", rs.getString("FINREFERENCE"));
		map.addValue("MICR_CODE", rs.getString("MICR"));
		map.addValue("ACC_TYPE", rs.getInt("ACCTYPE"));
		map.addValue("LEDGER_FOLIO", "000");
		map.addValue("FINWARE_ACNO", rs.getString("ACCNUMBER"));
		map.addValue("DEST_ACC_HOLDER", rs.getString("CUSTSHRTNAME"));
		map.addValue("PDC_BY_NAME", rs.getString("ACCHOLDERNAME"));
		map.addValue("BANK_NAME", rs.getString("BANKNAME"));
		map.addValue("BANK_ADDRESS", rs.getString("BRANCHDESC"));
		map.addValue("EMI_NO", rs.getInt("EMINO"));
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

	private void updatePresentmentHeader(long presentmentId, int manualEcclude, long totalRecords) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append( " UPDATE PRESENTMENTHEADER Set STATUS = :STATUS, TOTALRECORDS = TOTALRECORDS+:TOTALRECORDS  Where ID = :ID ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("STATUS", manualEcclude);
		source.addValue("ID", presentmentId);
		source.addValue("TOTALRECORDS", totalRecords);

		try {
			this.parameterJdbcTemplate.update(sql.toString(), source);
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
			this.parameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private void clearTables() {
		logger.debug(Literal.ENTERING);

		parameterJdbcTemplate.update("TRUNCATE TABLE PDC_CONSL_EMI_DTL_TEMP", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	private void copyDataFromTempToMainTables() {
		logger.debug(Literal.ENTERING);

		parameterJdbcTemplate.update("INSERT INTO PDC_CONSL_EMI_DTL SELECT * FROM PDC_CONSL_EMI_DTL_TEMP", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

}
