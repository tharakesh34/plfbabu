package com.pennant.backend.endofday.tasklet.bajaj;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ControlDumpStoredProcedure extends StoredProcedure implements Tasklet {
	private Logger logger = Logger.getLogger(ControlDumpStoredProcedure.class);

	protected NamedParameterJdbcTemplate jdbcTemplate;
	
	Map<String,Date> inputParameters = new HashMap<String, Date>();

	/**
	 * Constructor for stored procedure Tasklet
	 * 
	 * @param dataSource
	 */
	public ControlDumpStoredProcedure(DataSource dataSource, String spName) {
		super(dataSource, spName);
		super.setDataSource(dataSource);

		
		declareParameter(new SqlParameter("APP_DATE", Types.DATE));
		
		declareParameter(new SqlOutParameter("ERROR_CODE", Types.BIGINT));
		declareParameter(new SqlOutParameter("ERROR_DESC", Types.VARCHAR));
		declareParameter(new SqlOutParameter("ERRORSTEP", Types.VARCHAR));
		compile();
	}

	/**
	 * RepeatStatus method will be called by job definition by passing job details
	 * 
	 * @param dataSource
	 */
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		Date valueDate =(Date) arg1.getStepContext().getJobExecutionContext().get("APP_VALUEDATE");
		Date appDate = (Date) arg1.getStepContext().getJobExecutionContext().get("APP_DATE");
		
		inputParameters.put("APP_DATE", appDate);
		
		logger.debug("START: Control-Dump Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));

		deleteData(appDate);
		copyDataFromMainToLogTable(appDate);

		Map<String, Object> results;
		//results = execute(new HashMap<>());
		results = execute(inputParameters);

		BatchUtil.setExecution(arg1, "TOTAL", String.valueOf(0));
		BatchUtil.setExecution(arg1, "PROCESSED", String.valueOf(0));

		if (((long) results.get("ERROR_CODE")) != 0) {
			throw new Exception(results.get("ERROR_DESC").toString());
		}
		deleteOldData(appDate);
		logger.debug("COMPLETED: Control-Dump Process for the value date: ".concat(DateUtil.format(valueDate, DateFormat.LONG_DATE)));
		return RepeatStatus.FINISHED;
	}

	// Handling retry on same day.
	private void deleteData(Date appDate) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("CREATED_ON", appDate);

		jdbcTemplate.update("DELETE FROM CF_CONTROL_DUMP_LOG WHERE CREATED_ON = :CREATED_ON", parameterMap);
		jdbcTemplate.update("DELETE FROM CF_CONTROL_DUMP WHERE CREATED_ON = :CREATED_ON", parameterMap);

		logger.debug(Literal.LEAVING);
	}

	private void copyDataFromMainToLogTable(Date appDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO CF_CONTROL_DUMP_LOG SELECT");
		sql.append(" AGREEMENTNO, AGREEMENTID, PRODUCTFLAG, SCHEMEID, BRANCHID,");
		sql.append(" NPA_STAGEID, LOAN_STATUS, DISB_STATUS, FIRST_DUE_DATE,");
		sql.append(" MATURITY_DATE, AMTFIN, DISBURSED_AMOUNT, EMI_DUE,");
		sql.append(" PRINCIPAL_DUE, INTEREST_DUE, EMI_RECEIVED, PRINCIPAL_RECEIVED,");
		sql.append(" INTEREST_RECEIVED, EMI_OS, PRINCIPAL_OS, INTEREST_OS,");
		sql.append(" BULK_REFUND, PRINCIPAL_WAIVED, EMI_PRINCIPAL_WAIVED,");
		sql.append(" EMI_INTEREST_WAIVED, PRINCIPAL_AT_TERM, ADVANCE_EMI, ADVANCE_EMI_BILLED,");
		sql.append(" MIGRATED_ADVANCE_EMI, MIGRATED_ADVANCE_EMI_BILLED, MIGRATED_ADVANCE_EMI_UNBILLED,");
		sql.append(" CLOSED_CAN_ADV_EMI, PRINCIPAL_BALANCE, INTEREST_BALANCE, SOH_BALANCE, NO_OF_UNBILLED_EMI,");
		sql.append(" TOTAL_INTEREST, ACCRUED_AMOUNT, BALANCE_UMFC, EMI_IN_ADVANCE_RECEIVED_MAKER,");
		sql.append(" EMI_IN_ADVANCE_BILLED, EMI_IN_ADVANCE_UNBILLED, MIG_ADV_EMI_BILLED_PRINCOMP,");
		sql.append(" MIG_ADV_EMI_BILLED_INTCOMP, MIG_ADV_EMI_UNBILLED_PRINCOMP, MIG_ADV_EMI_UNBILLED_INTCOMP,");
		sql.append(" EMI_IN_ADV_BILLED_PRINCOMP, EMI_IN_ADV_BILLED_INTCOMP, EMI_IN_ADV_UNBILLED_PRINCOMP,");
		sql.append(" EMI_IN_ADV_UNBILLED_INTCOMP, CLOS_CAN_ADV_EMI_PRINCOMP, CLOS_CAN_ADV_EMI_INTCOMP,");
		sql.append(" SECURITY_DEPOSIT, SECURITY_DEPOSIT_ADJUSTED, ROUNDING_DIFF_RECEIVABLE, ROUNDING_DIFF_RECEIVED,");
		sql.append(" MIG_DIFFERENCE_RECEIVABLE, MIG_DIFFERENCE_RECEIVED, MIG_DIFFERENCE_PAYABLE, MIG_DIFFERENCE_PAID,");
		sql.append(" WRITEOFF_DUE, WRITEOFF_RECEIVED, SOLD_SEIZE_RECEIVABLE, SOLD_SEIZE_RECEIVED, SOLD_SEIZE_PAYABLE,");
		sql.append(" SOLD_SEIZE_PAID, NET_EXCESS_RECEIVED, NET_EXCESS_ADJUSTED,LPP_CHARGES_RECEIVABLE,");
		sql.append(" LPP_CHARGES_RECEIVED, PDC_SWAP_CHARGES_RECEIVABLE, PDC_SWAP_CHARGES_RECEIVED,");
		sql.append(" REPO_CHARGES_RECEIVABLE, REPO_CHARGES_RECEIVED, FORECLOSURE_CHARGES_DUE,");
		sql.append(" FORECLOSURE_CHARGES_RECEIVED, BOUNCE_CHARGES_DUE, BOUNCE_CHARGES_RECEIVED, INSUR_RENEW_CHARGE,");
		sql.append(" INSUR_RENEW_CHARGE_RECD, INSUR_RECEIVABLE, INSUR_RECEIVED, INSUR_PAYABLE, INSUR_PAID,");
		sql.append(" CUSTOMERID, CUSTOMERNAME, SANCTIONED_TENURE, LOAN_EMI, FLAT_RATE, EFFECTIVE_RATE,");
		sql.append(" AGREEMENTDATE, DISBURSALDATE, CLOSUREDATE, NO_OF_ADVANCE_EMIS, ASSETCOST, NO_OF_EMI_OS, DPD,");
		sql.append(" CURRENT_BUCKET, BRANCH_NAME, SCHEME_NAME, DERIVED_BUCKET,");
		sql.append(" ASSETDESC, MAKE, CHASISNUM, REGDNUM,");
		sql.append(" ENGINENUM, INVOICEAMT, SUPPLIERDESC, INSTRUMENT,");
		sql.append(" REPO_DATE, LOCAL_OUTSTATION_FLAG, FIRST_REPAYDUE_DATE, CREATED_ON, :LOGGED_ON");
		sql.append(" from CF_CONTROL_DUMP");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("LOGGED_ON", appDate);

		jdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}
	
	private void deleteOldData(Date appDate) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("CREATED_ON", appDate);

		jdbcTemplate.update("DELETE FROM CF_CONTROL_DUMP WHERE CREATED_ON != :CREATED_ON", parameterMap);

		logger.debug(Literal.LEAVING);
	}
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
