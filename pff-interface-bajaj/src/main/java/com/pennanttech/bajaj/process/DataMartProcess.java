package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;

import com.pennanttech.bajaj.process.datamart.DataMartMapper;
import com.pennanttech.bajaj.process.datamart.DataMartTable;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.util.DateUtil;

public class DataMartProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(DataMartProcess.class);
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("DATA_MART_REQUEST");

	private long batchID;
	private String summary = null;
	public AtomicLong completedThreads = null;
	public long totalThreads;
	private int btachSize = 10000;
	private Date appDate;
	public static boolean running = false;
	public AtomicLong processedRecords;

	public DataMartProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);

		this.totalThreads = 0;
		this.completedThreads = new AtomicLong(0L);
		this.processedRecords = new AtomicLong(0L);
		running = true;
	}

	@Override
	public void processData() {
		transDef.setTimeout(-1);

		batchID = logHeader();

		try {
			loadCount();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new ApplicantDetailsDataMart(new String[] { "CUSTOMERID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new ApplicationDetailsDataMart(new String[] { "APPLID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new AddressDetailsDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new BounceDetailsDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new CoApplicantDetailsDataMart(new String[] { "APPLID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new DisbursementDataMart(new String[] { "AGREEMENTNO", "DISBURSEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new HTSUnadjustedAmtDataMart(new String[] { "APPLID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new ForeClosureChargesDataMart(new String[] { "AGREEMENTID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new InsuranceDetailsDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new IVRFlexiDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new LeaDocDetailsDataMart(new String[] { "AGREEMENTID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new LoanDetailDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new LoanVoucherDetailsDataMart(new String[] { "AGREEMENTID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new LoanWiseChargeDataMart(new String[] { "AGREEMENTID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new LoanWiseRepayScheduleDataMart(new String[] { "AGREEMENTNO", "DUEDATE" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new NOCEligibleLoansDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new OpenEcsDetailDataMart(new String[] { "ECS_ID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new PrePaymentDetailsDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new PresentationDetailsDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new PropertyDetailsDataMart(new String[] { "APPLICATIONID" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new ReschDetailsDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new SendSOAEmailDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new SubQDisbDataMart(new String[] { "AGREEMENTNO", "DISBURSEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new WriteOffDetailsDataMart(new String[] { "AGREEMENTNO" })).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		while (true) {
			if (totalThreads == completedThreads.get()) {
				processedCount = this.processedRecords.get();
				executionStatus.setProcessedRecords(processedCount);
				updateHeader();
				running = false;
				break;
			}
		}

	}

	private void loadCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(count) from (");
		sql.append("select count(*) count from DM_APPLICANT_DETAILS_VIEW");
		sql.append(" union all ");
		sql.append("select count(*) count from DM_ADDRESS_DETAILS_VIEW");
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
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
			executionStatus.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private long logHeader() {
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			MapSqlParameterSource paramMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO DATAMART_HEADER (STATUS, INSERT_TIMESTAMP) VALUES(");
			sql.append(":STATUS, :INSERT_TIMESTAMP)");

			paramMap = new MapSqlParameterSource();
			paramMap.addValue("STATUS", "S");
			paramMap.addValue("INSERT_TIMESTAMP", DateUtil.getSysDate());

			parameterJdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "BATCHID" });

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();
	}

	private void updateHeader() {
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE DATAMART_HEADER  SET STATUS = :STATUS, COMPLETION_TIMESTAMP = :COMPLETION_TIMESTAMP");
		sql.append(" ,ERR_DESCRIPTION = :ERR_DESCRIPTION");
		sql.append(" WHERE BATCHID = :BATCHID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("STATUS", "I");
		paramMap.addValue("COMPLETION_TIMESTAMP", DateUtil.getSysDate());
		paramMap.addValue("BATCHID", batchID);
		paramMap.addValue("ERR_DESCRIPTION", summary);

		try {
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public class ApplicantDetailsDataMart implements Runnable {
		private String[] keyFields;

		public ApplicantDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_APPLICANT_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_APPLICANT_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_APPLICANT_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_APPLICANT_DETAILS.name(), map, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class AddressDetailsDataMart implements Runnable {
		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_ADDRESS_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "ADDRESSID", "CUSTOMERID" };

						try {
							map = DataMartMapper.mapData(DataMartTable.DM_ADDRESS_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, "DM_ADDRESS_DETAILS", destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							logger.debug("Data " + map.toString());
							String keyId = getKeyId("DM_ADDRESS_DETAILS", map, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class ApplicationDetailsDataMart implements Runnable {
		private String[] keyFields;

		public ApplicationDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_APPLICATION_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_APPLICATION_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_APPLICATION_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							logger.debug("Data " + map.toString());
							String keyId = getKeyId(DataMartTable.DM_APPLICATION_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class BounceDetailsDataMart implements Runnable {
		private String[] keyFields;

		public BounceDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_BOUNCE_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_BOUNCE_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_BOUNCE_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_BOUNCE_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class CoApplicantDetailsDataMart implements Runnable {
		String[] keyFields;

		public CoApplicantDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_COAPPLICANT_DTLS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_COAPPLICANT_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_COAPPLICANT_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								transManager.commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_COAPPLICANT_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class DisbursementDataMart implements Runnable {
		private String[] keyFields;

		public DisbursementDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_DISB_DETAILS_DAILY_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_DISB_DETAILS_DAILY, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_DISB_DETAILS_DAILY.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_DISB_DETAILS_DAILY.name(), map, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class ForeClosureChargesDataMart implements Runnable {
		private String[] keyFields;

		public ForeClosureChargesDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from FORECLOSURECHARGES_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.FORECLOSURECHARGES, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.FORECLOSURECHARGES.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.FORECLOSURECHARGES.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class HTSUnadjustedAmtDataMart implements Runnable {
		private String[] keyFields;

		public HTSUnadjustedAmtDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_HTS_UNADJUSTED_AMT_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_HTS_UNADJUSTED_AMT, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_HTS_UNADJUSTED_AMT.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_HTS_UNADJUSTED_AMT.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class InsuranceDetailsDataMart implements Runnable {
		private String[] keyFields;

		public InsuranceDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_INSURANCE_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_INSURANCE_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_INSURANCE_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_INSURANCE_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class IVRFlexiDataMart implements Runnable {
		private String[] keyFields;

		public IVRFlexiDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_IVR_GATEWAY_FLEXI_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {
				TransactionStatus txnStatus = null;
				MapSqlParameterSource map = null;
				int inserted = 0;

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
					while (rs.next()) {
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_IVR_GATEWAY_FLEXI, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_IVR_GATEWAY_FLEXI.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_IVR_GATEWAY_FLEXI.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class LeaDocDetailsDataMart implements Runnable {
		private String[] keyFields;

		public LeaDocDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);

			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LEA_DOC_DTLE_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_LEA_DOC_DTL, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_LEA_DOC_DTL.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LEA_DOC_DTL.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class LoanDetailDataMart implements Runnable {
		private String[] keyFields;

		public LoanDetailDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOAN_DETAILS_DAILY_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {

					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_LOAN_DETAILS_DAILY, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_LOAN_DETAILS_DAILY.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOAN_DETAILS_DAILY.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class LoanVoucherDetailsDataMart implements Runnable {
		private String[] keyFields;

		public LoanVoucherDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOAN_VOUCHER_DETAILS_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_LOAN_VOUCHER_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							save(map, DataMartTable.DM_LOAN_VOUCHER_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOAN_VOUCHER_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}

					return txnStatus;
				}
			});
		}
	}

	public class LoanWiseChargeDataMart implements Runnable {
		private String[] keyFields;

		public LoanWiseChargeDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOANWISE_CHARGE_DTLS_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_LOANWISE_CHARGE_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_LOANWISE_CHARGE_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOANWISE_CHARGE_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class LoanWiseRepayScheduleDataMart implements Runnable {
		private String[] keyFields;

		public LoanWiseRepayScheduleDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_LOANWISE_REPAYSCHEDULE_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class NOCEligibleLoansDataMart implements Runnable {
		private String[] keyFields;

		public NOCEligibleLoansDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_NOC_ELIGIBLE_LOANS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {

					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_NOC_ELIGIBLE_LOANS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_NOC_ELIGIBLE_LOANS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_NOC_ELIGIBLE_LOANS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class OpenEcsDetailDataMart implements Runnable {
		private String[] keyFields;

		public OpenEcsDetailDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_OPENECS_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {

					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_OPENECS_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_OPENECS_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_OPENECS_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class PrePaymentDetailsDataMart implements Runnable {
		private String[] keyFields;

		public PrePaymentDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_PREPAYMENT_DETAILS_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {

					TransactionStatus txnStatus = null;
					MapSqlParameterSource map = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_PREPAYMENT_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_PREPAYMENT_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_PREPAYMENT_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class PresentationDetailsDataMart implements Runnable {
		private String[] keyFields;

		public PresentationDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_PRESENTATION_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());

						try {
							map = DataMartMapper.mapData(DataMartTable.DM_PRESENTATION_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_PRESENTATION_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_PRESENTATION_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class PropertyDetailsDataMart implements Runnable {
		private String[] keyFields;

		public PropertyDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_PROPERTY_DTL_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_PROPERTY_DTL, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_PROPERTY_DTL.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_PROPERTY_DTL.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class ReschDetailsDataMart implements Runnable {
		private String[] keyFields;

		public ReschDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_RESCH_DETAILS_DAILY_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_RESCH_DETAILS_DAILY, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_RESCH_DETAILS_DAILY.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_RESCH_DETAILS_DAILY.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;

				}
			});
		}
	}

	public class SendSOAEmailDataMart implements Runnable {
		private String[] keyFields;

		public SendSOAEmailDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_SEND_SOA_EMAIL_VIEW");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_SEND_SOA_EMAIL, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_SEND_SOA_EMAIL.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_SEND_SOA_EMAIL.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class SubQDisbDataMart implements Runnable {
		private String[] keyFields;

		public SubQDisbDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_SUBQ_DISB_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_SUBQ_DISB_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_SUBQ_DISB_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_SUBQ_DISB_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;

				}
			});
		}
	}

	public class WriteOffDetailsDataMart implements Runnable {
		private String[] keyFields;

		public WriteOffDetailsDataMart(String[] keyFields) {
			this.keyFields = keyFields;
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * from DM_WRITEOFF_DETAILS_VIEW ");

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql);
			} catch (Exception e) {
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql) {
			return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					MapSqlParameterSource map = null;
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							map = DataMartMapper.mapData(DataMartTable.DM_WRITEOFF_DETAILS, rs, appDate);
							map.addValue("BATCH_ID", batchID);

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							save(map, DataMartTable.DM_WRITEOFF_DETAILS.name(), destinationJdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_WRITEOFF_DETAILS.name(), map, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						} finally {
							map = null;
						}
					}
					return txnStatus;
				}
			});
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

	private void conclude(TransactionStatus txnStatus) {
		try {
			completedThreads.incrementAndGet();
			if (txnStatus != null && !txnStatus.isCompleted()) {
				transManager.commit(txnStatus);
				txnStatus.flush();
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			txnStatus.flush();
		}
	}

	private void commit(TransactionStatus txnStatus) {
		transManager.commit(txnStatus);
		txnStatus.flush();
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
