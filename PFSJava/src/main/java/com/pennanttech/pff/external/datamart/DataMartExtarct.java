package com.pennanttech.pff.external.datamart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.external.DataMartProcess;

public class DataMartExtarct extends DatabaseDataEngine implements DataMartProcess {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("DATA_MART_REQUEST");

	private long batchID;
	private Date lastRunDate;
	private String summary = null;
	public AtomicLong completedThreads = null;
	public long totalThreads;
	private int btachSize = 5000;
	private Date appDate;
	public static boolean running = false;
	public AtomicLong processedRecords;
	private MapSqlParameterSource paramMap = new MapSqlParameterSource();
	private String lastMntOnReq = null;

	public DataMartExtarct(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);
		this.appDate = appDate;
		this.totalThreads = 0;
		this.completedThreads = new AtomicLong(0L);
		this.processedRecords = new AtomicLong(0L);
		running = true;
	}

	@Override
	public void process(Object... objects) {
		try {
			process("DATA_MART_REQUEST");
		} catch (Exception e) {
			throw new InterfaceException("DATA_MART_REQUEST", e.getMessage());
		}
	}

	@Override
	public void processData() {
		transDef.setTimeout(-1);

		batchID = logHeader();
		lastRunDate = getLatestRunDate();

		if (lastRunDate != null) {
			paramMap.addValue("LASTMNTON", lastRunDate);
		}

		lastMntOnReq = loadParameters();

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

		try {
			new Thread(new GoldLoanPolicyDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		try {
			new Thread(new GoldLoanStatePolicyDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new GoldRateDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new GoldPromotionDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new GoldPromotionSlabRatesDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new GoldPromotionBranchesDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new GoldPromotionStatesDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new GoldOrnamentTypeDataMart()).start();
			totalThreads++;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			new Thread(new OrnamentDetailsDataMart()).start();
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

	private Date getLatestRunDate() {
		StringBuilder sql = new StringBuilder();
		sql.append("select Max(COMPLETION_TIMESTAMP) from DATAMART_HEADER");
		try {
			return jdbcTemplate.queryForObject(sql.toString(), Date.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private void loadCount() {
		logger.debug(Literal.ENTERING);
		List<DataMartView> enumValues = Arrays.asList(DataMartView.values());
		for (DataMartView dataMartView : enumValues) {
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				if (StringUtils.equals(dataMartView.name(), "DM_HTS_UNADJUSTED_AMT_VIEW")) {
					sql.append("select count(*) count from " + dataMartView + " Where Finreference "
							+ "IN (select Finreference from FinanceMain where LASTMNTON > :LASTMNTON) ");
				} else {
					sql.append("select count(*) count from " + dataMartView + " where LASTMNTON > :LASTMNTON ");
				}
			} else {
				sql.append("select count(*) count from " + dataMartView);
			}
			int count = parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);
			totalRecords = totalRecords + count;
		}
		executionStatus.setTotalRecords(totalRecords);
		logger.debug(Literal.LEAVING);
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_APPLICANT_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_APPLICANT_DETAILS_VIEW");
			}
			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveApplicantDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_APPLICANT_DETAILS.name(), rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
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
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_ADDRESS_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_ADDRESS_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "ADDRESSID", "CUSTOMERID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveAddressDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_ADDRESS_DETAILS", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldLoanPolicyDataMart implements Runnable {

		public GoldLoanPolicyDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_GOLDLOANPOLICY_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_GOLDLOANPOLICY_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "ID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveGoldLoanPolicyDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_GOLDLOANPOLICY", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldLoanStatePolicyDataMart implements Runnable {

		public GoldLoanStatePolicyDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_GOLDLOANSTATEPOLICY_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_GOLDLOANSTATEPOLICY_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "POLICYID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveGoldLoanStatePolicyDetails(rs, appDate, valueDate, batchID,
									jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_GOLDLOANSTATEPOLICY", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldRateDataMart implements Runnable {

		public GoldRateDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_GOLDRATE_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_GOLDRATE_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "ID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveGoldRateDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_GOLDRATE", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldPromotionDataMart implements Runnable {

		public GoldPromotionDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PROMOTIONS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PROMOTIONS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "PROMOTIONID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.savePromotionDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_PROMOTIONS", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldPromotionSlabRatesDataMart implements Runnable {

		public GoldPromotionSlabRatesDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PROMOTIONSLABWISERATES_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PROMOTIONSLABWISERATES_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "REFERENCEID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveGoldPromotionSlabRateDetails(rs, appDate, valueDate, batchID,
									jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_PROMOTIONALSLABWISERATES", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldPromotionBranchesDataMart implements Runnable {

		public GoldPromotionBranchesDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PROMOTIONBRANCHES_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PROMOTIONBRANCHES_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "REFERENCEID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveGoldPromotionBranchDetails(rs, appDate, valueDate, batchID,
									jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_PROMOTIONBRANCHES", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldPromotionStatesDataMart implements Runnable {

		public GoldPromotionStatesDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PROMOTIONSTATES_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PROMOTIONSTATES_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "REFERENCEID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveGoldPromotionStatesDetails(rs, appDate, valueDate, batchID,
									jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_PROMOTIONSTATES", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class GoldOrnamentTypeDataMart implements Runnable {

		public GoldOrnamentTypeDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_ORNAMENTTYPE_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_ORNAMENTTYPE_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "ORNAMENTTYPE" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveOrnamentTypeDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_ORNAMENTTYPE", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	public class OrnamentDetailsDataMart implements Runnable {

		public OrnamentDetailsDataMart() {
		    super();
		}

		@Override
		public void run() {
			logger.debug(Literal.ENTERING);
			StringBuilder sql = new StringBuilder();
			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_ORNAMENTDETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_ORNAMENTDETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
			logger.debug(Literal.LEAVING);

		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						String[] keyFields = new String[] { "APPL_ID", "CUSTOMER_ID" };
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveOrnamentDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId("DM_ORNAMENTDETAILS", rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_APPLICATION_DETAILS_VIEW  where LASTMNTON > :LASTMNTON ");
			} else {
				sql.append(" SELECT * from DM_APPLICATION_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveApplicationDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_APPLICATION_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_BOUNCE_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_BOUNCE_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveBounceDetailsMap(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_BOUNCE_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_COAPPLICANT_DTLS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_COAPPLICANT_DTLS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveCoApplicantDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								transManager.commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_COAPPLICANT_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_DISB_DETAILS_DAILY_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_DISB_DETAILS_DAILY_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveDisbDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_DISB_DETAILS_DAILY.name(), rs, keyFields);
							executionStatus.setFailedRecords(failedCount++);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from FORECLOSURECHARGES_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from FORECLOSURECHARGES_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveForeClosureChargesMap(rs, appDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.FORECLOSURECHARGES.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(
						" SELECT * from DM_HTS_UNADJUSTED_AMT_VIEW Where Finreference IN (select Finreference from FinanceMain where LastMntON >:LASTMNTON)");
			} else {
				sql.append(" SELECT * from DM_HTS_UNADJUSTED_AMT_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramMap) {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveHTSUnadjustedMap(rs, appDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_HTS_UNADJUSTED_AMT.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_INSURANCE_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_INSURANCE_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveInsuranceDetailsMap(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_INSURANCE_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_IVR_GATEWAY_FLEXI_VIEW where LASTMNTON > :LASTMNTON ");
			} else {
				sql.append(" SELECT * from DM_IVR_GATEWAY_FLEXI_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {
				TransactionStatus txnStatus = null;
				int inserted = 0;

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
					while (rs.next()) {
						try {

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveIVRDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_IVR_GATEWAY_FLEXI.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_LEA_DOC_DTLE_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_LEA_DOC_DTLE_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveLeaDocDtl(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LEA_DOC_DTL.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_LOAN_DETAILS_DAILY_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_LOAN_DETAILS_DAILY_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {

					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveLoanDetail(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOAN_DETAILS_DAILY.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_LOAN_VOUCHER_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_LOAN_VOUCHER_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveLoanVoucherDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOAN_VOUCHER_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_LOANWISE_CHARGE_DTLS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_LOANWISE_CHARGE_DTLS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveLoanWiseChargeDetail(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOANWISE_CHARGE_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_LOANWISE_REPAYSCHEDULE_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_LOANWISE_REPAYSCHEDULE_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					TransactionStatus txnStatus = null;
					int inserted = 0;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {

							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveLoanWiseRepayScheduleDetailMap(rs, appDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_LOANWISE_REPAYSCHD_DTLS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_NOC_ELIGIBLE_LOANS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_NOC_ELIGIBLE_LOANS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveNoceligibleLoans(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_NOC_ELIGIBLE_LOANS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_OPENECS_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_OPENECS_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {
				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int count = 0;
					TransactionStatus txnStatus = null;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (count == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveOpenEcsDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);
							if (count++ > btachSize) {
								commit(txnStatus);
								count = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_OPENECS_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PREPAYMENT_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PREPAYMENT_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {

					TransactionStatus txnStatus = null;
					int inserted = 0;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.savePrePaymentDetail(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_PREPAYMENT_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PRESENTATION_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PRESENTATION_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.savePresentationDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);
							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_PRESENTATION_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());

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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_PROPERTY_DTL_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_PROPERTY_DTL_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paramap) {
			return parameterJdbcTemplate.query(sql.toString(), paramap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;
					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.savePropertyDetail(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_PROPERTY_DTL.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_RESCH_DETAILS_DAILY_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_RESCH_DETAILS_DAILY_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveReschDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_RESCH_DETAILS_DAILY.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_SEND_SOA_EMAIL_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_SEND_SOA_EMAIL_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveSoaEmailDetails(rs, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);
							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}
						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_SEND_SOA_EMAIL.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_SUBQ_DISB_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_SUBQ_DISB_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}
							DataMartMapper.saveSubQDisbDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_SUBQ_DISB_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
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

			if (StringUtils.equalsIgnoreCase("Y", lastMntOnReq)) {
				sql.append(" SELECT * from DM_WRITEOFF_DETAILS_VIEW where LASTMNTON > :LASTMNTON");
			} else {
				sql.append(" SELECT * from DM_WRITEOFF_DETAILS_VIEW");
			}

			TransactionStatus txnStatus = null;
			try {
				txnStatus = extract(sql, paramMap);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			} finally {
				conclude(txnStatus);
			}

			logger.debug(Literal.LEAVING);
		}

		private TransactionStatus extract(StringBuilder sql, MapSqlParameterSource paraMap) {
			return parameterJdbcTemplate.query(sql.toString(), paraMap, new ResultSetExtractor<TransactionStatus>() {

				@Override
				public TransactionStatus extractData(ResultSet rs) throws SQLException, DataAccessException {
					int inserted = 0;
					TransactionStatus txnStatus = null;

					while (rs.next()) {
						executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
						try {
							if (inserted == 0) {
								txnStatus = transManager.getTransaction(transDef);
							}

							DataMartMapper.saveWriteOffDetails(rs, appDate, valueDate, batchID, jdbcTemplate);
							executionStatus.setSuccessRecords(successCount++);

							if (inserted++ > btachSize) {
								commit(txnStatus);
								inserted = 0;
							}

						} catch (Exception e) {
							executionStatus.setFailedRecords(failedCount++);
							logger.error(Literal.EXCEPTION, e);
							String keyId = getKeyId(DataMartTable.DM_WRITEOFF_DETAILS.name(), rs, keyFields);
							saveBatchLog(keyId, "F", e.getMessage());
						}
					}
					return txnStatus;
				}
			});
		}
	}

	private String getKeyId(String tableName, ResultSet rs, String[] ketFields) {
		StringBuilder builder = new StringBuilder();
		if (ketFields != null) {
			for (String key : ketFields) {
				if (builder.length() > 0) {
					builder.append(",");
				}
				try {
					builder.append(rs.getObject(key));
				} catch (SQLException e) {

				}
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
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (txnStatus != null) {
				txnStatus.flush();
			}
		}
	}

	private void commit(TransactionStatus txnStatus) {
		transManager.commit(txnStatus);
		txnStatus.flush();
	}

	private String loadParameters() {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE like :SYSPARMCODE");
		paramSource.addValue("SYSPARMCODE", "DM_LMN_PROCESSINGREQ");

		try {
			return parameterJdbcTemplate.queryForObject(sql.toString(), paramSource, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}
}
