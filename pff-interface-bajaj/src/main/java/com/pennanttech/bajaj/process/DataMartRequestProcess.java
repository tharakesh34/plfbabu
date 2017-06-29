package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.bajaj.process.datamart.DataMartMapper;
import com.pennanttech.bajaj.process.datamart.DataMartTable;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class DataMartRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(DataMartRequestProcess.class);

	public DataMartRequestProcess(DataSource dataSource, long userId, Date valueDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.DATA_MART_STATUS);
	}

	@Override
	public void processData() {

		try {
			loadCount();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			ApplicantDetailsDataMart appDM = new ApplicantDetailsDataMart();
			appDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			ApplicationDetailsDataMart appDM = new ApplicationDetailsDataMart();
			appDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			BounceDetailsDataMart bouncedetailsDM = new BounceDetailsDataMart();
			bouncedetailsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			CoApplicantDetailsDataMart coAppDM = new CoApplicantDetailsDataMart();
			coAppDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			DisbursementDataMart disbDM = new DisbursementDataMart();
			disbDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			HTSUnadjustedAmtDataMart htsDM = new HTSUnadjustedAmtDataMart();
			htsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			ForeClosureChargesDataMart fCCDM = new ForeClosureChargesDataMart();
			fCCDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			InsuranceDetailsDataMart insuranceDM = new InsuranceDetailsDataMart();
			insuranceDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			IVRFlexiDataMart ivrDM = new IVRFlexiDataMart();
			ivrDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			LeaDocDetailsDataMart leaDM = new LeaDocDetailsDataMart();
			leaDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			LoanDetailDataMart loanDM = new LoanDetailDataMart();
			loanDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			LoanVoucherDetailsDataMart loanVoucherDM = new LoanVoucherDetailsDataMart();
			loanVoucherDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			LoanWiseChargeDataMart loanWiseChargeDM = new LoanWiseChargeDataMart();
			loanWiseChargeDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			LoanWiseRepayScheduleDataMart loanWiseRepayDM = new LoanWiseRepayScheduleDataMart();
			loanWiseRepayDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			NOCEligibleLoansDataMart nOCLoanDM = new NOCEligibleLoansDataMart();
			nOCLoanDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			OpenEcsDetailDataMart openECSDM = new OpenEcsDetailDataMart();
			openECSDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			PrePaymentDetailsDataMart prePaymentDetailsDM = new PrePaymentDetailsDataMart();
			prePaymentDetailsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			PresentationDetailsDataMart presentationDM = new PresentationDetailsDataMart();
			presentationDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			PropertyDetailsDataMart propertyDetailDM = new PropertyDetailsDataMart();
			propertyDetailDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			ReschDetailsDataMart prePaymentDetailsDM = new ReschDetailsDataMart();
			prePaymentDetailsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			SendSOAEmailDataMart soaEmailDetailsDM = new SendSOAEmailDataMart();
			soaEmailDetailsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			SubQDisbDataMart subQDisbDetailsDM = new SubQDisbDataMart();
			subQDisbDetailsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			WriteOffDetailsDataMart writeOffDetailsDM = new WriteOffDetailsDataMart();
			writeOffDetailsDM.run();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private void loadCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(count) from (");
		sql.append("select count(*) count from DM_APPLICANT_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_ADDRESS_DETAILS");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_APPLICATION_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_BOUNCE_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_COAPPLICANT_DTLS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_DISB_DETAILS_DAILY_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from FORECLOSURECHARGES_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_HTS_UNADJUSTED_AMT_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_INSURANCE_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_IVR_GATEWAY_FLEXI_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_LEA_DOC_DTLE_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_LOAN_DETAILS_DAILY_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_LOAN_VOUCHER_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_LOANWISE_CHARGE_DTLS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_LOANWISE_REPAYSCHEDULE_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_NOC_ELIGIBLE_LOANS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_OPENECS_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_PREPAYMENT_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_PRESENTATION_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_PROPERTY_DTL_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_RESCH_DETAILS_DAILY_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_SEND_SOA_EMAIL_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_SUBQ_DISB_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_WRITEOFF_DETAILS_VIEW");
		sql.append(") T ");

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Integer.class);
			executionStatus.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	public class ApplicantDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_APPLICANT_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "CUSTOMERID";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_APPLICANT_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_APPLICANT_DETAILS.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_APPLICANT_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}

				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class AddressDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_ADDRESS_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[] { "ADDRESSID", "CUSTOMERID" };
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_ADDRESS_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, "DM_ADDRESS_DETAILS", destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId("DM_ADDRESS_DETAILS", map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}

				}
			});
			logger.debug(Literal.LEAVING);

		}
	}

	public class ApplicationDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_APPLICATION_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "APPLID";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_APPLICATION_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_APPLICATION_DETAILS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_APPLICATION_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class BounceDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_BOUNCE_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_BOUNCE_DETAILS, rs);
						saveOrUpdate(map, DataMartTable.DM_BOUNCE_DETAILS.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_BOUNCE_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class CoApplicantDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_COAPPLICANT_DTLS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[] { "APPLID" };

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_COAPPLICANT_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_COAPPLICANT_DETAILS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_COAPPLICANT_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class DisbursementDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_DISB_DETAILS_DAILY_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[] { "AGREEMENTNO", "DISBURSEMENTNO" };

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_DISB_DETAILS_DAILY, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_DISB_DETAILS_DAILY.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_DISB_DETAILS_DAILY.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class ForeClosureChargesDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from FORECLOSURECHARGES_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTID";

					try {
						map = DataMartMapper.mapData(DataMartTable.FORECLOSURECHARGES, rs);
						saveOrUpdate(map, DataMartTable.FORECLOSURECHARGES.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.FORECLOSURECHARGES.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class HTSUnadjustedAmtDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_HTS_UNADJUSTED_AMT_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "APPLID";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_HTS_UNADJUSTED_AMT, rs);
						saveOrUpdate(map, DataMartTable.DM_HTS_UNADJUSTED_AMT.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_HTS_UNADJUSTED_AMT.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class InsuranceDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_INSURANCE_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_INSURANCE_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_INSURANCE_DETAILS.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_INSURANCE_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class IVRFlexiDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_IVR_GATEWAY_FLEXI_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_IVR_GATEWAY_FLEXI, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_IVR_GATEWAY_FLEXI.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_IVR_GATEWAY_FLEXI.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class LeaDocDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);

			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LEA_DOC_DTLE_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTID";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_LEA_DOC_DTL, rs);
						saveOrUpdate(map, DataMartTable.DM_LEA_DOC_DTL.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_LEA_DOC_DTL.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class LoanDetailDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOAN_DETAILS_DAILY_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_LOAN_DETAILS_DAILY, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_LOAN_DETAILS_DAILY.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_LOAN_DETAILS_DAILY.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class LoanVoucherDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOAN_VOUCHER_DETAILS_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTID";
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_LOAN_VOUCHER_DETAILS, rs);
						saveOrUpdate(map, DataMartTable.DM_LOAN_VOUCHER_DETAILS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_LOAN_VOUCHER_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class LoanWiseChargeDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOANWISE_CHARGE_DTLS_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTID";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_LOANWISE_CHARGE_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_LOANWISE_CHARGE_DETAILS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_LOANWISE_CHARGE_DETAILS.name(), map, keyFields);
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class LoanWiseRepayScheduleDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOANWISE_REPAYSCHEDULE_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[] { "AGREEMENTNO", "DUEDATE" };

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class NOCEligibleLoansDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_NOC_ELIGIBLE_LOANS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_NOC_ELIGIBLE_LOANS, rs);
						saveOrUpdate(map, DataMartTable.DM_NOC_ELIGIBLE_LOANS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_NOC_ELIGIBLE_LOANS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class OpenEcsDetailDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_OPENECS_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "ECS_ID";
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_OPENECS_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_OPENECS_DETAILS.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_OPENECS_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class PrePaymentDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_PREPAYMENT_DETAILS_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_PREPAYMENT_DETAILS, rs);
						saveOrUpdate(map, DataMartTable.DM_PREPAYMENT_DETAILS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_PREPAYMENT_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class PresentationDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_PRESENTATION_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_PRESENTATION_DETAILS, rs);
						saveOrUpdate(map, DataMartTable.DM_PRESENTATION_DETAILS.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_PRESENTATION_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class PropertyDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_PROPERTY_DTL_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "APPLICATIONID";
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_PROPERTY_DTL, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_PROPERTY_DTL.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_PROPERTY_DTL.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class ReschDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_RESCH_DETAILS_DAILY_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";

					try {
						map = DataMartMapper.mapData(DataMartTable.DM_RESCH_DETAILS_DAILY, rs);
						saveOrUpdate(map, DataMartTable.DM_RESCH_DETAILS_DAILY.name(), destinationJdbcTemplate,
								keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_RESCH_DETAILS_DAILY.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class SendSOAEmailDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_SEND_SOA_EMAIL_VIEW");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_SEND_SOA_EMAIL, rs);
						saveOrUpdate(map, DataMartTable.DM_SEND_SOA_EMAIL.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_SEND_SOA_EMAIL.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class SubQDisbDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_SUBQ_DISB_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[] { "AGREEMENTNO", "DISBURSEMENTNO" };
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_SUBQ_DISB_DETAILS, rs);
						map.addValue("BATCH_ID", executionStatus.getId());
						saveOrUpdate(map, DataMartTable.DM_SUBQ_DISB_DETAILS.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_SUBQ_DISB_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	public class WriteOffDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			MapSqlParameterSource parmMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_WRITEOFF_DETAILS_VIEW ");

			parmMap = new MapSqlParameterSource();

			jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
				MapSqlParameterSource map = null;

				@Override
				public void processRow(ResultSet rs) throws SQLException, DataAccessException {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					String[] keyFields = new String[1];
					keyFields[0] = "AGREEMENTNO";
					try {
						map = DataMartMapper.mapData(DataMartTable.DM_WRITEOFF_DETAILS, rs);

						saveOrUpdate(map, DataMartTable.DM_WRITEOFF_DETAILS.name(), destinationJdbcTemplate, keyFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						logger.debug("Data " + map.toString());
						String keyId = getKeyId(DataMartTable.DM_WRITEOFF_DETAILS.name(), map, keyFields);
						failedCount++;
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
			});
			logger.debug(Literal.LEAVING);
		}
	}

	private String getKeyId(String tableName, MapSqlParameterSource map, String[] ketFields) {
		StringBuilder builder = new StringBuilder();
		if (ketFields != null) {
			for (String key : ketFields) {
				if (builder.length() > 0) {
					builder.append(",");
				}
				builder.append(map.getValue(key));
			}
		}
		return tableName + ": " + builder.toString();
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
