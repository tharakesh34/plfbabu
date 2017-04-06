package com.pennanttech.dbengine.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DBConfiguration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.dbengine.DBProcessEngine;

public class ControlDumpRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(ControlDumpRequest.class);
	
	private Connection destConnection = null;
	private Connection sourceConnection = null;

	public ControlDumpRequest(DataSource dataSource, String appDBName) {
		super(dataSource, appDBName);
	}

	public void process(long userId, DataEngineStatus executionStatus, Configuration config) {
		logger.debug("Entering");

		executionStatus.setStartTime(DateUtil.getSysDate());
		executionStatus.setName(config.getName());
		executionStatus.setUserId(userId);
		executionStatus.setReference(config.getName());
		executionStatus.setStatus(ExecutionStatus.I.name());
		executionStatus.setRemarks("Loading configuration..");

		ResultSet resultSet = null;
		StringBuilder remarks = new StringBuilder();
		try {
			saveBatchStatus(executionStatus);

			executionStatus.setRemarks("Loading destination database connection...");
			DBConfiguration dbConfiguration = config.getDbConfiguration();
			destConnection = getConnection(dbConfiguration);
			executionStatus.setRemarks("Fetching data from source table...");
			resultSet = getSourceData();

			if (resultSet != null) {
				resultSet.last();
				totalRecords = resultSet.getRow();
				resultSet.beforeFirst();
				executionStatus.setTotalRecords(totalRecords);
			}
			while (resultSet.next()) {
				executionStatus.setRemarks("Saving data to destination table...");
				try {
					processedCount++;
					saveData(resultSet);
					successCount++;
				} catch (Exception e) {
					failedCount++;
					logger.error("Exception :", e);
				}
				executionStatus.setProcessedRecords(processedCount);
				executionStatus.setSuccessRecords(successCount);
				executionStatus.setFailedRecords(failedCount);
			}

			if (totalRecords > 0) {
				remarks.append("Processed successfully with record count: ");
				remarks.append(totalRecords);
				updateBatchStatus(ExecutionStatus.S.name(), remarks.toString(), processedCount, processedCount,
						failedCount, totalRecords, executionStatus);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage(), processedCount, processedCount, failedCount,
					totalRecords, executionStatus);
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(resultSet, destConnection, sourceConnection);
			resultSet = null;
			executionStatus.setRemarks(remarks.toString());
		}

		logger.debug("Leaving");
	}

	private void saveData(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO  CF_CONTROL_DUMP (");
			sb.append("	AGREEMENTNO, AGREEMENTID, PRODUCTFLAG, SCHEMEID, BRANCHID, NPA_STAGEID, LOAN_STATUS, DISB_STATUS, FIRST_DUE_DATE,");
			sb.append(" MATURITY_DATE, AMTFIN, DISBURSED_AMOUNT, EMI_DUE, PRINCIPAL_DUE, INTEREST_DUE, EMI_RECEIVED, PRINCIPAL_RECEIVED, INTEREST_RECEIVED,");
			sb.append(" EMI_OS, PRINCIPAL_OS, INTEREST_OS, BULK_REFUND, PRINCIPAL_WAIVED, EMI_PRINCIPAL_WAIVED, EMI_INTEREST_WAIVED,");
			sb.append(" PRINCIPAL_AT_TERM, ADVANCE_EMI, ADVANCE_EMI_BILLED, MIGRATED_ADVANCE_EMI, MIGRATED_ADVANCE_EMI_BILLED, MIGRATED_ADVANCE_EMI_UNBILLED, CLOSED_CAN_ADV_EMI,");
			sb.append(" PRINCIPAL_BALANCE, INTEREST_BALANCE, SOH_BALANCE, NO_OF_UNBILLED_EMI, TOTAL_INTEREST, ACCRUED_AMOUNT, BALANCE_UMFC, EMI_IN_ADVANCE_RECEIVED_MAKER,");
			sb.append(" EMI_IN_ADVANCE_BILLED, EMI_IN_ADVANCE_UNBILLED, MIG_ADV_EMI_BILLED_PRINCOMP, MIG_ADV_EMI_BILLED_INTCOMP, MIG_ADV_EMI_UNBILLED_PRINCOMP,MIG_ADV_EMI_UNBILLED_INTCOMP," );
			sb.append(" EMI_IN_ADV_BILLED_PRINCOMP, EMI_IN_ADV_BILLED_INTCOMP, EMI_IN_ADV_UNBILLED_PRINCOMP, EMI_IN_ADV_UNBILLED_INTCOMP, CLOS_CAN_ADV_EMI_PRINCOMP, CLOS_CAN_ADV_EMI_INTCOMP," );
			sb.append(" SECURITY_DEPOSIT, SECURITY_DEPOSIT_ADJUSTED, ROUNDING_DIFF_RECEIVABLE,, ROUNDING_DIFF_RECEIVED, MIG_DIFFERENCE_RECEIVABLE, MIG_DIFFERENCE_PAYABLE, MIG_DIFFERENCE_PAID," );
			sb.append(" WRITEOFF_DUE, WRITEOFF_RECEIVED, SOLD_SEIZE_RECEIVABLE, SOLD_SEIZE_RECEIVED, SOLD_SEIZE_PAYABLE, SOLD_SEIZE_PAID, NET_EXCESS_RECEIVED, NET_EXCESS_ADJUSTED," );
			sb.append(" LPP_CHARGES_RECEIVABLE, LPP_CHARGES_RECEIVED, PDC_SWAP_CHARGES_RECEIVABLE, PDC_SWAP_CHARGES_RECEIVED, REPO_CHARGES_RECEIVABLE, REPO_CHARGES_RECEIVED, FORECLOSURE_CHARGES_DUE," );
			sb.append(" FORECLOSURE_CHARGES_RECEIVED, BOUNCE_CHARGES_DUE, BOUNCE_CHARGES_RECEIVED, INSUR_RENEW_CHARGE, INSUR_RENEW_CHARGE_RECD, INSUR_RECEIVABLE, INSUR_RECEIVED,INSUR_PAYABLE," );
			sb.append(" INSUR_PAID, CUSTOMERID, CUSTOMERNAME, SANCTIONED_TENURE, LOAN_EMI, FLAT_RATE, EFFECTIVE_RATE, AGREEMENTDATE, DISBURSALDATE, CLOSUREDATE, NO_OF_ADVANCE_EMIS, ASSETCOST," );
			sb.append(" NO_OF_EMI_OS, DPD, CURRENT_BUCKET, BRANCH_NAME, SCHEME_NAME, , DERIVED_BUCKET, ASSETDESC, MAKE, CHASISNUM, REGDNUM, ENGINENUM, INVOICEAMT, SUPPLIERDESC, INSTRUMENT," );
			sb.append(" REPO_DATE, LOCAL_OUTSTATION_FLAG, FIRST_REPAYDUE_DATE)" );
			
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
			sb.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
			sb.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
			sb.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			

			ps = destConnection.prepareStatement(sb.toString());

			ps.setString(1, getValue(rs, "AGREEMENTNO"));
			ps.setInt(2, getIntValue(rs, "AGREEMENTID"));
			ps.setString(3, getValue(rs, "PRODUCTFLAG"));
			ps.setInt(4, getIntValue(rs, "SCHEMEID"));
			ps.setInt(5, getIntValue(rs, "BRANCHID"));
			ps.setString(6, getValue(rs, "NPA_STAGEID"));
			ps.setString(7, getValue(rs, "LOAN_STATUS"));
			ps.setString(8, getValue(rs, "DISB_STATUS"));
			ps.setDate(9, getDateValue(rs, "FIRST_DUE_DATE"));
			
			ps.setDate(10, getDateValue(rs, "MATURITY_DATE"));
			ps.setBigDecimal(11, getBigDecimal(rs, "AMTFIN"));
			ps.setBigDecimal(12, getBigDecimal(rs, "DISBURSED_AMOUNT"));
			ps.setBigDecimal(13, getBigDecimal(rs, "EMI_DUE"));
			ps.setBigDecimal(14, getBigDecimal(rs, "PRINCIPAL_DUE"));
			ps.setBigDecimal(15, getBigDecimal(rs, "INTEREST_DUE"));
			ps.setBigDecimal(16, getBigDecimal(rs, "EMI_RECEIVED"));
			ps.setBigDecimal(17, getBigDecimal(rs, "PRINCIPAL_RECEIVED"));
			ps.setBigDecimal(18, getBigDecimal(rs, "INTEREST_RECEIVED"));
			ps.setBigDecimal(19, getBigDecimal(rs, "EMI_OS"));
			
			ps.setBigDecimal(20, getBigDecimal(rs, "PRINCIPAL_OS"));
			ps.setBigDecimal(21, getBigDecimal(rs, "INTEREST_OS"));
			ps.setBigDecimal(22, getBigDecimal(rs, "BULK_REFUND"));
			ps.setBigDecimal(23, getBigDecimal(rs, "PRINCIPAL_WAIVED"));
			ps.setBigDecimal(24, getBigDecimal(rs, "EMI_PRINCIPAL_WAIVED"));
			ps.setBigDecimal(25, getBigDecimal(rs, "EMI_INTEREST_WAIVED"));
			ps.setBigDecimal(26, getBigDecimal(rs, "PRINCIPAL_AT_TERM"));
			ps.setBigDecimal(27, getBigDecimal(rs, "ADVANCE_EMI"));
			ps.setBigDecimal(28, getBigDecimal(rs, "ADVANCE_EMI_BILLED"));
			ps.setBigDecimal(29, getBigDecimal(rs, "MIGRATED_ADVANCE_EMI"));
			
			ps.setBigDecimal(30, getBigDecimal(rs, "MIGRATED_ADVANCE_EMI_BILLED"));
			ps.setBigDecimal(31, getBigDecimal(rs, "MIGRATED_ADVANCE_EMI_UNBILLED"));
			ps.setBigDecimal(32, getBigDecimal(rs, "CLOSED_CAN_ADV_EMI"));
			ps.setBigDecimal(33, getBigDecimal(rs, "PRINCIPAL_BALANCE"));
			ps.setBigDecimal(34, getBigDecimal(rs, "INTEREST_BALANCE"));
			ps.setBigDecimal(35, getBigDecimal(rs, "SOH_BALANCE"));
			ps.setBigDecimal(36, getBigDecimal(rs, "NO_OF_UNBILLED_EMI"));
			ps.setBigDecimal(37, getBigDecimal(rs, "TOTAL_INTEREST"));
			ps.setBigDecimal(38, getBigDecimal(rs, "ACCRUED_AMOUNT"));
			ps.setBigDecimal(39, getBigDecimal(rs, "BALANCE_UMFC"));
			
			ps.setBigDecimal(40, getBigDecimal(rs, "EMI_IN_ADVANCE_RECEIVED_MAKER"));
			ps.setBigDecimal(41, getBigDecimal(rs, "EMI_IN_ADVANCE_BILLED"));
			ps.setBigDecimal(42, getBigDecimal(rs, "EMI_IN_ADVANCE_UNBILLED"));
			ps.setBigDecimal(43, getBigDecimal(rs, "MIG_ADV_EMI_BILLED_PRINCOMP"));
			ps.setBigDecimal(44, getBigDecimal(rs, "MIG_ADV_EMI_BILLED_INTCOMP"));
			ps.setBigDecimal(45, getBigDecimal(rs, "MIG_ADV_EMI_UNBILLED_PRINCOMP"));
			ps.setBigDecimal(46, getBigDecimal(rs, "MIG_ADV_EMI_UNBILLED_INTCOMP"));
			ps.setBigDecimal(47, getBigDecimal(rs, "EMI_IN_ADV_BILLED_PRINCOMP"));
			ps.setBigDecimal(48, getBigDecimal(rs, "EMI_IN_ADV_BILLED_INTCOMP"));
			ps.setBigDecimal(49, getBigDecimal(rs, "EMI_IN_ADV_UNBILLED_PRINCOMP"));
			
			ps.setBigDecimal(50, getBigDecimal(rs, "EMI_IN_ADV_UNBILLED_INTCOMP"));
			ps.setBigDecimal(51, getBigDecimal(rs, "CLOS_CAN_ADV_EMI_PRINCOMP"));
			ps.setBigDecimal(52, getBigDecimal(rs, "CLOS_CAN_ADV_EMI_INTCOMP"));
			ps.setBigDecimal(53, getBigDecimal(rs, "SECURITY_DEPOSIT"));
			ps.setBigDecimal(54, getBigDecimal(rs, "SECURITY_DEPOSIT_ADJUSTED"));
			ps.setBigDecimal(55, getBigDecimal(rs, "ROUNDING_DIFF_RECEIVABLE"));
			ps.setBigDecimal(56, getBigDecimal(rs, "ROUNDING_DIFF_RECEIVED"));
			ps.setBigDecimal(57, getBigDecimal(rs, "MIG_DIFFERENCE_RECEIVABLE"));
			ps.setBigDecimal(58, getBigDecimal(rs, "MIG_DIFFERENCE_RECEIVED"));
			ps.setBigDecimal(59, getBigDecimal(rs, "MIG_DIFFERENCE_PAYABLE"));
			
			ps.setBigDecimal(60, getBigDecimal(rs, "MIG_DIFFERENCE_PAID"));
			ps.setBigDecimal(61, getBigDecimal(rs, "WRITEOFF_DUE"));
			ps.setBigDecimal(62, getBigDecimal(rs, "WRITEOFF_RECEIVED"));
			ps.setBigDecimal(63, getBigDecimal(rs, "SOLD_SEIZE_RECEIVABLE"));
			ps.setBigDecimal(64, getBigDecimal(rs, "SOLD_SEIZE_RECEIVED"));
			ps.setBigDecimal(65, getBigDecimal(rs, "SOLD_SEIZE_PAYABLE"));
			ps.setBigDecimal(66, getBigDecimal(rs, "SOLD_SEIZE_PAID"));
			ps.setBigDecimal(67, getBigDecimal(rs, "NET_EXCESS_RECEIVED"));
			ps.setBigDecimal(68, getBigDecimal(rs, "NET_EXCESS_ADJUSTED"));
			ps.setBigDecimal(69, getBigDecimal(rs, "LPP_CHARGES_RECEIVABLE"));
			
			ps.setBigDecimal(70, getBigDecimal(rs, "LPP_CHARGES_RECEIVED"));
			ps.setBigDecimal(71, getBigDecimal(rs, "PDC_SWAP_CHARGES_RECEIVABLE"));
			ps.setBigDecimal(72, getBigDecimal(rs, "PDC_SWAP_CHARGES_RECEIVED"));
			ps.setBigDecimal(73, getBigDecimal(rs, "REPO_CHARGES_RECEIVABLE"));
			ps.setBigDecimal(74, getBigDecimal(rs, "REPO_CHARGES_RECEIVED"));
			ps.setBigDecimal(75, getBigDecimal(rs, "FORECLOSURE_CHARGES_DUE"));
			ps.setBigDecimal(76, getBigDecimal(rs, "FORECLOSURE_CHARGES_RECEIVED"));
			ps.setBigDecimal(77, getBigDecimal(rs, "BOUNCE_CHARGES_DUE"));
			ps.setBigDecimal(78, getBigDecimal(rs, "BOUNCE_CHARGES_RECEIVED"));
			ps.setBigDecimal(79, getBigDecimal(rs, "INSUR_RENEW_CHARGE"));
			
			ps.setBigDecimal(80, getBigDecimal(rs, "INSUR_RENEW_CHARGE_RECD"));
			ps.setBigDecimal(81, getBigDecimal(rs, "INSUR_RECEIVABLE"));
			ps.setBigDecimal(82, getBigDecimal(rs, "INSUR_RECEIVED"));
			ps.setBigDecimal(83, getBigDecimal(rs, "INSUR_PAYABLE"));
			ps.setBigDecimal(84, getBigDecimal(rs, "INSUR_PAID"));
			ps.setBigDecimal(85, getBigDecimal(rs, "CUSTOMERID"));
			ps.setString(86, getValue(rs, "CUSTOMERNAME"));
			ps.setInt(87, getIntValue(rs, "SANCTIONED_TENURE"));
			ps.setBigDecimal(88, getBigDecimal(rs, "LOAN_EMI"));
			ps.setBigDecimal(89, getBigDecimal(rs, "FLAT_RATE"));
			
			ps.setBigDecimal(90, getBigDecimal(rs, "EFFECTIVE_RATE"));
			ps.setDate(91, getDateValue(rs, "AGREEMENTDATE"));
			ps.setDate(92, getDateValue(rs, "DISBURSALDATE"));
			ps.setDate(93, getDateValue(rs, "CLOSUREDATE"));
			ps.setInt(94, getIntValue(rs, "NO_OF_ADVANCE_EMIS"));
			ps.setDate(95, getDateValue(rs, "ASSETCOST"));
			ps.setInt(96, getIntValue(rs, "NO_OF_EMI_OS"));
			ps.setInt(97, getIntValue(rs, "DPD"));
			ps.setInt(98, getIntValue(rs, "CURRENT_BUCKET"));
			ps.setString(99, getValue(rs, "BRANCH_NAME"));
			
			ps.setString(100, getValue(rs, "SCHEME_NAME"));
			ps.setInt(101, getIntValue(rs, "DERIVED_BUCKET"));
			ps.setString(102, getValue(rs, "ASSETDESC"));
			ps.setString(103, getValue(rs, "MAKE"));
			ps.setString(104, getValue(rs, "CHASISNUM"));
			ps.setString(105, getValue(rs, "REGDNUM"));
			ps.setString(106, getValue(rs, "ENGINENUM"));
			ps.setBigDecimal(107, getBigDecimal(rs, "INVOICEAMT"));
			ps.setString(108, getValue(rs, "SUPPLIERDESC"));
			ps.setString(109, getValue(rs, "INSTRUMENT"));
						
			ps.setDate(110, getDateValue(rs, "REPO_DATE"));
			ps.setString(111, getValue(rs, "LOCAL_OUTSTATION_FLAG"));
			ps.setDate(112, getDateValue(rs, "FIRST_REPAYDUE_DATE"));
			
			
			// execute query
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			ps = null;
		}
		logger.debug("Leaving");
	}

	private ResultSet getSourceData() throws Exception {
		logger.debug("Entering");

		ResultSet rs = null;
		StringBuilder sql = null;
		
		try {
			sourceConnection = DataSourceUtils.doGetConnection(appDataSource);
			sql = new StringBuilder();
			sql.append(" SELECT * from INT_CF_CONTROL_VIEW ");

			PreparedStatement stmt = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug("Leaving");
		return rs;
	}
}
