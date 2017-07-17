package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;

public class ControlDumpRequestProcess extends DatabaseDataEngine {
	private static final Logger	logger			= Logger.getLogger(ControlDumpRequestProcess.class);

	Date						appDate			= null;
	Date						monthStartDate	= null;
	Date						monthEndDate	= null;

	public ControlDumpRequestProcess(DataSource dataSource, long userId,Date valueDate, Date appDate, Date monthStartDate, Date monthEndDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.CONTROL_DUMP_REQUEST_STATUS);
		this.appDate = appDate;
		this.monthStartDate = monthStartDate;
		this.monthEndDate = monthEndDate;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		try {
			
		// Handling retry on same day.
		deleteData("CF_CONTROL_DUMP_LOG", "CREATED_ON");
		deleteData("CF_CONTROL_DUMP", "CREATED_ON");

		// Moving last run data to log table.
		copyDataFromMainToLogTable(appDate);
		
		loadcount();
		
		execute();
		
		} catch(Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			deleteOldData("CF_CONTROL_DUMP", "CREATED_ON");
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void loadcount() {
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) from INT_CF_CONTROL_VIEW");
		sql.append(" WHERE LOAN_STATUS = :LOAN_STATUS");
		sql.append(" AND LASTREPAYDATE >= :monthStartDate AND LASTREPAYDATE <= :monthEndDate ");

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("LOAN_STATUS", "A");
		parmMap.addValue("monthStartDate", monthStartDate);
		parmMap.addValue("monthEndDate", monthEndDate);

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
			executionStatus.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private void execute() {
		// Saving the data into main table.
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_CF_CONTROL_VIEW");
		sql.append(" WHERE LOAN_STATUS = :LOAN_STATUS");
		sql.append(" AND LASTREPAYDATE >= :monthStartDate AND LASTREPAYDATE <= :monthEndDate ");

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("LOAN_STATUS", "A");
		parmMap.addValue("monthStartDate", monthStartDate);
		parmMap.addValue("monthEndDate", monthEndDate);

		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
			MapSqlParameterSource	map	= null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				executionStatus.setProcessedRecords(processedCount++);
				try {
					map = mapData(rs);
					save(map, "CF_CONTROL_DUMP", destinationJdbcTemplate);
					executionStatus.setSuccessRecords(successCount++);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					logger.debug("Control dump record: " + map.toString());
					executionStatus.setFailedRecords(failedCount++);

					String keyId = rs.getString("AGREEMENTNO");
					String  error = StringUtils.substring(e.getMessage(), e.getMessage().length() - 1999, e.getMessage().length());
					
					if (StringUtils.trimToNull(keyId) == null) {
						keyId = String.valueOf(processedCount);
					}
					
					saveBatchLog(keyId, "F", error);
				} finally {
					map = null;
				}
			
			}
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("ADVANCE_EMI", rs.getObject("ADVANCE_EMI"));
		map.addValue("ADVANCE_EMI_BILLED", rs.getObject("ADVANCE_EMI_BILLED"));
		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("AGREEMENTDATE", rs.getObject("AGREEMENTDATE"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("AMTFIN", rs.getObject("AMTFIN"));
		map.addValue("ASSETCOST", rs.getObject("ASSETCOST"));
		map.addValue("ASSETDESC", rs.getObject("ASSETDESC"));
		map.addValue("BALANCE_UMFC", rs.getObject("BALANCE_UMFC"));
		map.addValue("BOUNCE_CHARGES_DUE", rs.getObject("BOUNCE_CHARGES_DUE"));
		map.addValue("BRANCHID", rs.getObject("BRANCHID"));
		map.addValue("BRANCH_NAME", rs.getObject("BRANCH_NAME"));
		map.addValue("BULK_REFUND", rs.getObject("BULK_REFUND"));
		map.addValue("CHASISNUM", rs.getObject("CHASISNUM"));
		map.addValue("CLOSED_CAN_ADV_EMI", rs.getObject("CLOSED_CAN_ADV_EMI"));
		map.addValue("CLOS_CAN_ADV_EMI_INTCOMP", rs.getObject("CLOS_CAN_ADV_EMI_INTCOMP"));
		map.addValue("CLOS_CAN_ADV_EMI_PRINCOMP", rs.getObject("CLOS_CAN_ADV_EMI_PRINCOMP"));
		map.addValue("CLOSUREDATE", rs.getObject("CLOSUREDATE"));
		map.addValue("CURRENT_BUCKET", rs.getObject("CURRENT_BUCKET"));
		map.addValue("CUSTOMERID", rs.getObject("CUSTOMERID"));
		map.addValue("CUSTOMERNAME", rs.getObject("CUSTOMERNAME"));
		map.addValue("DERIVED_BUCKET", rs.getObject("DERIVED_BUCKET"));
		map.addValue("DISBURSALDATE", rs.getObject("DISBURSALDATE"));
		map.addValue("DISBURSED_AMOUNT", rs.getObject("DISBURSED_AMOUNT"));
		map.addValue("DISB_STATUS", rs.getObject("DISB_STATUS"));
		map.addValue("DPD", rs.getObject("DPD"));
		map.addValue("EFFECTIVE_RATE", rs.getObject("EFFECTIVE_RATE"));
		map.addValue("EMI_DUE", rs.getObject("EMI_DUE"));
		map.addValue("EMI_INTEREST_WAIVED", rs.getObject("EMI_INTEREST_WAIVED"));
		map.addValue("EMI_IN_ADVANCE_BILLED", rs.getObject("EMI_IN_ADVANCE_BILLED"));
		map.addValue("EMI_IN_ADVANCE_RECEIVED_MAKER", rs.getObject("EMI_IN_ADVANCE_RECEIVED_MAKER"));
		map.addValue("EMI_IN_ADVANCE_UNBILLED", rs.getObject("EMI_IN_ADVANCE_UNBILLED"));
		map.addValue("EMI_IN_ADV_BILLED_INTCOMP", rs.getObject("EMI_IN_ADV_BILLED_INTCOMP"));
		map.addValue("EMI_IN_ADV_BILLED_PRINCOMP", rs.getObject("EMI_IN_ADV_BILLED_PRINCOMP"));
		map.addValue("EMI_IN_ADV_UNBILLED_INTCOMP", rs.getObject("EMI_IN_ADV_UNBILLED_INTCOMP"));
		map.addValue("EMI_IN_ADV_UNBILLED_PRINCOMP", rs.getObject("EMI_IN_ADV_UNBILLED_PRINCOMP"));
		map.addValue("EMI_OS", rs.getObject("EMI_OS"));
		map.addValue("EMI_PRINCIPAL_WAIVED", rs.getObject("EMI_PRINCIPAL_WAIVED"));
		map.addValue("EMI_RECEIVED", rs.getObject("EMI_RECEIVED"));
		map.addValue("ENGINENUM", rs.getObject("ENGINENUM"));
		map.addValue("FIRST_DUE_DATE", rs.getObject("FIRST_DUE_DATE"));
		map.addValue("FIRST_REPAYDUE_DATE", rs.getObject("FIRST_REPAYDUE_DATE"));
		map.addValue("FLAT_RATE", rs.getObject("FLAT_RATE"));
		map.addValue("FORECLOSURE_CHARGES_DUE", rs.getObject("FORECLOSURE_CHARGES_DUE"));
		map.addValue("FORECLOSURE_CHARGES_RECEIVED", rs.getObject("FORECLOSURE_CHARGES_RECEIVED"));
		map.addValue("INSTRUMENT", rs.getObject("INSTRUMENT"));
		map.addValue("INSUR_PAID", rs.getObject("INSUR_PAID"));
		map.addValue("INSUR_PAYABLE", rs.getObject("INSUR_PAYABLE"));
		map.addValue("INSUR_RECEIVABLE", rs.getObject("INSUR_RECEIVABLE"));
		map.addValue("INSUR_RECEIVED", rs.getObject("INSUR_RECEIVED"));
		map.addValue("INSUR_RENEW_CHARGE", rs.getObject("INSUR_RENEW_CHARGE"));
		map.addValue("INSUR_RENEW_CHARGE_RECD", rs.getObject("INSUR_RENEW_CHARGE_RECD"));
		map.addValue("INTEREST_BALANCE", rs.getObject("INTEREST_BALANCE"));
		map.addValue("INTEREST_DUE", rs.getObject("INTEREST_DUE"));
		map.addValue("INTEREST_OS", rs.getObject("INTEREST_OS"));
		map.addValue("INTEREST_RECEIVED", rs.getObject("INTEREST_RECEIVED"));
		map.addValue("INVOICEAMT", rs.getObject("INVOICEAMT"));
		map.addValue("LOAN_EMI", rs.getObject("LOAN_EMI"));
		map.addValue("LOAN_STATUS", rs.getObject("LOAN_STATUS"));
		map.addValue("LOCAL_OUTSTATION_FLAG", rs.getObject("LOCAL_OUTSTATION_FLAG"));
		map.addValue("LPP_CHARGES_RECEIVABLE", rs.getObject("LPP_CHARGES_RECEIVABLE"));
		map.addValue("LPP_CHARGES_RECEIVED", rs.getObject("LPP_CHARGES_RECEIVED"));
		map.addValue("MAKE", rs.getObject("MAKE"));
		map.addValue("MATURITY_DATE", rs.getObject("MATURITY_DATE"));
		map.addValue("MIGRATED_ADVANCE_EMI", rs.getObject("MIGRATED_ADVANCE_EMI"));
		map.addValue("MIGRATED_ADVANCE_EMI_BILLED", rs.getObject("MIGRATED_ADVANCE_EMI_BILLED"));
		map.addValue("MIGRATED_ADVANCE_EMI_UNBILLED", rs.getObject("MIGRATED_ADVANCE_EMI_UNBILLED"));
		map.addValue("MIG_ADV_EMI_BILLED_INTCOMP", rs.getObject("MIG_ADV_EMI_BILLED_INTCOMP"));
		map.addValue("MIG_ADV_EMI_BILLED_PRINCOMP", rs.getObject("MIG_ADV_EMI_BILLED_PRINCOMP"));
		map.addValue("MIG_ADV_EMI_UNBILLED_PRINCOMP", rs.getObject("MIG_ADV_EMI_UNBILLED_PRINCOMP"));
		map.addValue("MIG_DIFFERENCE_PAID", rs.getObject("MIG_DIFFERENCE_PAID"));
		map.addValue("MIG_DIFFERENCE_PAYABLE", rs.getObject("MIG_DIFFERENCE_PAYABLE"));
		map.addValue("MIG_DIFFERENCE_RECEIVABLE", rs.getObject("MIG_DIFFERENCE_RECEIVABLE"));
		map.addValue("MIG_DIFFERENCE_RECEIVED", rs.getObject("MIG_DIFFERENCE_RECEIVED"));
		map.addValue("NET_EXCESS_ADJUSTED", rs.getObject("NET_EXCESS_ADJUSTED"));
		map.addValue("NET_EXCESS_RECEIVED", rs.getObject("NET_EXCESS_RECEIVED"));
		map.addValue("NO_OF_ADVANCE_EMIS", rs.getObject("NO_OF_ADVANCE_EMIS"));
		map.addValue("NO_OF_EMI_OS", rs.getObject("NO_OF_EMI_OS"));
		map.addValue("NO_OF_UNBILLED_EMI", rs.getObject("NO_OF_UNBILLED_EMI"));
		map.addValue("NPA_STAGEID", rs.getObject("NPA_STAGEID"));
		map.addValue("PDC_SWAP_CHARGES_RECEIVABLE", rs.getObject("PDC_SWAP_CHARGES_RECEIVABLE"));
		map.addValue("PDC_SWAP_CHARGES_RECEIVED", rs.getObject("PDC_SWAP_CHARGES_RECEIVED"));
		map.addValue("PRINCIPAL_BALANCE", rs.getObject("PRINCIPAL_BALANCE"));
		map.addValue("PRINCIPAL_DUE", rs.getObject("PRINCIPAL_DUE"));
		map.addValue("PRINCIPAL_OS", rs.getObject("PRINCIPAL_OS"));
		map.addValue("PRINCIPAL_RECEIVED", rs.getObject("PRINCIPAL_RECEIVED"));
		map.addValue("PRINCIPAL_WAIVED", rs.getObject("PRINCIPAL_WAIVED"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("REGDNUM", rs.getObject("REGDNUM"));
		map.addValue("REPO_CHARGES_RECEIVABLE", rs.getObject("REPO_CHARGES_RECEIVABLE"));
		map.addValue("REPO_CHARGES_RECEIVED", rs.getObject("REPO_CHARGES_RECEIVED"));
		map.addValue("REPO_DATE", rs.getObject("REPO_DATE"));
		map.addValue("ROUNDING_DIFF_RECEIVABLE", rs.getObject("ROUNDING_DIFF_RECEIVABLE"));
		map.addValue("ROUNDING_DIFF_RECEIVED", rs.getObject("ROUNDING_DIFF_RECEIVED"));
		map.addValue("SANCTIONED_TENURE", rs.getObject("SANCTIONED_TENURE"));
		map.addValue("SCHEMEID", rs.getObject("SCHEMEID"));
		map.addValue("SCHEME_NAME", rs.getObject("SCHEME_NAME"));
		map.addValue("SECURITY_DEPOSIT", rs.getObject("SECURITY_DEPOSIT"));
		map.addValue("SECURITY_DEPOSIT_ADJUSTED", rs.getObject("SECURITY_DEPOSIT_ADJUSTED"));
		map.addValue("SOH_BALANCE", rs.getObject("SOH_BALANCE"));
		map.addValue("SOLD_SEIZE_PAID", rs.getObject("SOLD_SEIZE_PAID"));
		map.addValue("SOLD_SEIZE_PAYABLE", rs.getObject("SOLD_SEIZE_PAYABLE"));
		map.addValue("SOLD_SEIZE_RECEIVABLE", rs.getObject("SOLD_SEIZE_RECEIVABLE"));
		map.addValue("SOLD_SEIZE_RECEIVED", rs.getObject("SOLD_SEIZE_RECEIVED"));
		map.addValue("SUPPLIERDESC", rs.getObject("SUPPLIERDESC"));
		map.addValue("TOTAL_INTEREST", rs.getObject("TOTAL_INTEREST"));
		map.addValue("WRITEOFF_DUE", rs.getObject("WRITEOFF_DUE"));
		map.addValue("WRITEOFF_RECEIVED", rs.getObject("WRITEOFF_RECEIVED"));
		map.addValue("CREATED_ON", appDate);

		return map;
	}

	private void copyDataFromMainToLogTable(final Date currentDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from CF_CONTROL_DUMP");

		jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new ResultSetExtractor<String>() {
			MapSqlParameterSource	map	= null;

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					try {
						map = mapData(rs);
						map.addValue("CREATED_ON", rs.getObject("CREATED_ON"));
						map.addValue("LOGGED_ON", currentDate);

						save(map, "CF_CONTROL_DUMP_LOG", destinationJdbcTemplate);
					} catch (Exception e) {
						logger.error(Literal.ENTERING);
					} finally {
						map = null;
					}
				}
				return null;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	private void deleteData(String tableName, String columnName) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource logMap = new MapSqlParameterSource();
		logMap.addValue(columnName, appDate);

		final String[] filterFields = new String[1];
		filterFields[0] = columnName;
		delete(logMap, tableName, destinationJdbcTemplate, filterFields);

		logger.debug(Literal.LEAVING);
	}
	
	private void deleteOldData(String tableName, String columnName) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource logMap = new MapSqlParameterSource();
		logMap.addValue(columnName, appDate);
		
		destinationJdbcTemplate.update("delete from "+tableName+" where CREATED_ON !=:CREATED_ON", logMap);

		logger.debug(Literal.LEAVING);
	}
}
