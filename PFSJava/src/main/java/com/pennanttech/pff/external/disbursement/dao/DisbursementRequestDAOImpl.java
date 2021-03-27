package com.pennanttech.pff.external.disbursement.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.DisbursementDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public class DisbursementRequestDAOImpl extends SequenceDao<DisbursementRequest> implements DisbursementDAO {
	private final Logger logger = LogManager.getLogger(DisbursementRequestDAOImpl.class);
	private static final String HEADER_ID = "HEADER_ID";
	private static final String BATCH_ID = "BATCH_ID";
	private static final String APPROVED = "APPROVED";

	private static Map<String, String> PATNER_BANKS = new HashMap<>();

	public DisbursementRequestDAOImpl() {
		super();
	}

	@Override
	public long getNextBatchId() {
		return getNextValue("SEQ_DISBURSEMENT_REQ_HEADER");
	}

	@Override
	public int lockFinAdvancePayments(long headerId, long userId, Long[] paymentIdList) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue(HEADER_ID, headerId);
		parameterSource.addValue(APPROVED, APPROVED);
		parameterSource.addValue("CREATEDBY", userId);
		parameterSource.addValue("CREATEDON", DateUtil.getSysDate());
		parameterSource.addValue("PAYMENTID", Arrays.asList(paymentIdList));

		try {
			return this.jdbcTemplate.update(DisbursementRequestsQueries.getInsertHeaderQuery(), parameterSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int clearBatch(long headerId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(HEADER_ID, headerId);
		return jdbcTemplate.update("DELETE FROM DISBURSEMENT_REQUESTS_HEADER WHERE ID = :HEADER_ID", paramMap);
	}

	@Override
	public int deleteDisbursementBatch(long headerId) {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		parmMap.addValue(HEADER_ID, headerId);
		return jdbcTemplate.update("DELETE FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = :HEADER_ID", parmMap);
	}

	@Override
	public int deleteDisbursementBatch(long headerId, long batchId) {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		parmMap.addValue(HEADER_ID, headerId);
		return jdbcTemplate.update("DELETE FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = :HEADER_ID ", parmMap);
	}

	@Override
	public List<DisbursementRequest> logDisbursementBatch(DisbursementRequest requestData) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue(HEADER_ID, requestData.getHeaderId());
		final String DISB_FI_EMAIL = SysParamUtil.getValueAsString("DISB_FI_EMAIL");

		try {
			return jdbcTemplate.query(DisbursementRequestsQueries.getSelectAllQuery(requestData), paramMap,
					new RowMapper<DisbursementRequest>() {
						@Override
						public DisbursementRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
							DisbursementRequest req = new DisbursementRequest();
							req.setAutoDownload(requestData.isAutoDownload());
							req.setDisbursementId(getValueAsLong(rs, "DISBURSEMENT_ID"));
							req.setCustCIF(getValueAsString(rs, "CUSTCIF"));
							req.setFinReference(getValueAsString(rs, "FINREFERENCE"));
							req.setDisbursementAmount(getValueAsBigDecimal(rs, "DISBURSEMENT_AMOUNT"));
							req.setDisbursementType(getValueAsString(rs, "DISBURSEMENT_TYPE"));
							req.setDisbursementDate(getValueAsDate(rs, "DISBURSEMENT_DATE"));
							req.setDraweeLocation(getValueAsString(rs, "DRAWEE_LOCATION"));
							req.setPrintLocation(getValueAsString(rs, "PRINT_LOCATION"));
							req.setCustomerName(getValueAsString(rs, "CUSTOMER_NAME"));
							req.setCustomerMobile(getValueAsString(rs, "CUSTOMER_MOBILE"));
							req.setCustomerEmail(getValueAsString(rs, "CUSTOMER_EMAIL"));
							req.setCustomerState(getValueAsString(rs, "CUSTOMER_STATE"));
							req.setCustomerCity(getValueAsString(rs, "CUSTOMER_CITY"));
							req.setCustomerAddress1(getValueAsString(rs, "CUSTOMER_ADDRESS1"));
							req.setCustomerAddress2(getValueAsString(rs, "CUSTOMER_ADDRESS2"));
							req.setCustomerAddress3(getValueAsString(rs, "CUSTOMER_ADDRESS3"));
							req.setCustomerAddress4(getValueAsString(rs, "CUSTOMER_ADDRESS4"));
							req.setCustomerAddress5(getValueAsString(rs, "CUSTOMER_ADDRESS5"));
							req.setBenficiaryBank(getValueAsString(rs, "BENFICIARY_BANK"));
							req.setBenficiaryBranch(getValueAsString(rs, "BENFICIARY_BRANCH"));
							req.setBenficiaryBranchState(getValueAsString(rs, "BENFICIARY_BRANCH_STATE"));
							req.setBenficiaryBranchCity(getValueAsString(rs, "BENFICIARY_BRANCH_CITY"));
							req.setMicrCode(getValueAsString(rs, "MICR_CODE"));
							req.setIfscCode(getValueAsString(rs, "IFSC_CODE"));
							req.setBenficiaryAccount(getValueAsString(rs, "BENFICIARY_ACCOUNT"));
							req.setBenficiaryName(getValueAsString(rs, "BENFICIARY_NAME"));
							req.setBenficiaryMobile(getValueAsString(rs, "BENFICIARY_MOBILE"));
							req.setBenficiryEmail(getValueAsString(rs, "BENFICIRY_EMAIL"));
							req.setBenficiryState(getValueAsString(rs, "BENFICIRY_STATE"));
							req.setBenficiryCity(getValueAsString(rs, "BENFICIRY_CITY"));
							req.setBenficiaryAddress1(getValueAsString(rs, "BENFICIARY_ADDRESS1"));
							req.setBenficiaryAddress2(getValueAsString(rs, "BENFICIARY_ADDRESS2"));
							req.setBenficiaryAddress3(getValueAsString(rs, "BENFICIARY_ADDRESS3"));
							req.setBenficiaryAddress4(getValueAsString(rs, "BENFICIARY_ADDRESS4"));
							req.setBenficiaryAddress5(getValueAsString(rs, "BENFICIARY_ADDRESS5"));
							req.setPaymentDetail1(getValueAsString(rs, "PAYMENT_DETAIL1"));
							req.setPaymentDetail2(getValueAsString(rs, "PAYMENT_DETAIL2"));
							req.setPaymentDetail3(getValueAsString(rs, "PAYMENT_DETAIL3"));
							req.setPaymentDetail4(getValueAsString(rs, "PAYMENT_DETAIL4"));
							req.setPaymentDetail5(getValueAsString(rs, "PAYMENT_DETAIL5"));
							req.setPaymentDetail6(getValueAsString(rs, "PAYMENT_DETAIL6"));
							req.setPaymentDetail7(getValueAsString(rs, "PAYMENT_DETAIL7"));
							req.setStatus(getValueAsString(rs, "STATUS"));
							req.setRemarks(getValueAsString(rs, "REMARKS"));
							req.setChannel(getValueAsString(rs, "CHANNEL"));
							req.setPartnerBankId(getValueAsLong(rs, "PARTNER_BANK_ID"));
							req.setPartnerBankCode(getValueAsString(rs, "PARTNER_BANK_CODE"));
							req.setAlwFileDownload(getValueAsBoolean(rs, "ALW_FILE_DOWNLOAD"));
							req.setPartnerBankAccount(getValueAsString(rs, "PARTNERBANK_ACCOUNT"));

							req.setChequeNumber(getValueAsString(rs, "CHEQUE_NUMBER"));
							// default data
							req.setPaymentDetail1(DISB_FI_EMAIL);
							req.setHeaderId(requestData.getHeaderId());
							req.setRespBatchId(new Long(0));

							long id = insertDisbursement(req);
							req.setId(id);

							logger.debug(String.format("DISBURSEMENT_ID : %d", id));
							return req;
						}

					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private long getValueAsLong(ResultSet rs, String field) throws SQLException {
		return rs.getLong(field);
	}

	private String getValueAsString(ResultSet rs, String field) throws SQLException {
		return rs.getString(field);
	}

	private boolean getValueAsBoolean(ResultSet rs, String field) throws SQLException {
		return rs.getBoolean(field);
	}

	private Date getValueAsDate(ResultSet rs, String field) throws SQLException {
		return rs.getDate(field);
	}

	private BigDecimal getValueAsBigDecimal(ResultSet rs, String field) throws SQLException {
		BigDecimal foramtedAmt = rs.getBigDecimal(field);
		return foramtedAmt.divide(new BigDecimal(100));
	}

	@Override
	public List<FinAdvancePayments> getAdvancePayments(long headerId) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue(HEADER_ID, headerId);
		parameterSource.addValue(APPROVED, APPROVED);

		try {
			return this.jdbcTemplate.query(DisbursementRequestsQueries.getSelectQuery(), parameterSource,
					new RowMapper<FinAdvancePayments>() {
						@Override
						public FinAdvancePayments mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinAdvancePayments fa = new FinAdvancePayments();
							fa.setPaymentId(rs.getLong(1));
							fa.setPaymentType(rs.getString(2));
							fa.setPartnerBankID(rs.getLong(3));
							fa.setPartnerbankCode(rs.getString(4));
							fa.setAlwFileDownload(rs.getBoolean(5));
							fa.setChannel(rs.getString(6));
							return fa;
						}
					});
		} catch (Exception e) {
			//
		}

		return new ArrayList<>();
	}

	private long insertDisbursement(DisbursementRequest req) {
		logger.debug(Literal.ENTERING);

		String sql = DisbursementRequestsQueries.getInsertQuery();

		logger.trace(Literal.SQL + sql);

		final KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(req);
			jdbcTemplate.update(sql, beanParameters, keyHolder, new String[] { "id" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void updateBatchFailureStatus(DisbursementRequest req) {
		jdbcTemplate.getJdbcOperations().update(
				"UPDATE FINADVANCEPAYMENTS SET STATUS = ? WHERE PAYMENTID IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ?)",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, req.getStatus());
						ps.setLong(2, req.getHeaderId());
					}

				});
		jdbcTemplate.getJdbcOperations().update(
				"UPDATE PAYMENTINSTRUCTIONS SET STATUS = ? WHERE PAYMENTINSTRUCTIONID IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ?)",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, req.getStatus());
						ps.setLong(2, req.getHeaderId());
					}

				});
		jdbcTemplate.getJdbcOperations().update(
				"UPDATE INSURANCEPAYMENTINSTRUCTIONS SET STATUS = ? WHERE ID IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ?)",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, req.getStatus());
						ps.setLong(2, req.getHeaderId());
					}

				});

	}

	@Override
	public int updateBatchStatus(DisbursementRequest req) {
		int batchCount = jdbcTemplate.getJdbcOperations().update(
				"UPDATE DISBURSEMENT_REQUESTS SET BATCH_ID = ?, STATUS = ? WHERE HEADER_ID = ? AND DISBURSEMENT_TYPE = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, req.getBatchId());
						ps.setString(2, req.getStatus());
						ps.setLong(3, req.getHeaderId());
						ps.setString(4, req.getDisbursementType());
					}

				});
		int disbursementsCount = 0;
		int paymentsCount = 0;
		int insurancesCount = 0;

		if (batchCount > 0) {
			if (req.isDisbursements()) {
				disbursementsCount = jdbcTemplate.getJdbcOperations().update(
						"UPDATE FINADVANCEPAYMENTS SET STATUS = ? WHERE PAYMENTID IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ? AND BATCH_ID = ?)",
						new PreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps) throws SQLException {
								ps.setString(1, req.getStatus());
								ps.setLong(2, req.getHeaderId());
								ps.setLong(3, req.getBatchId());
							}

						});
			}

			if (req.isPayments()) {
				paymentsCount = jdbcTemplate.getJdbcOperations().update(
						"UPDATE PAYMENTINSTRUCTIONS SET STATUS = ? WHERE PAYMENTINSTRUCTIONID IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ? AND BATCH_ID = ?)",
						new PreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps) throws SQLException {
								ps.setString(1, req.getStatus());
								ps.setLong(2, req.getHeaderId());
								ps.setLong(3, req.getBatchId());
							}

						});
			}

			if (req.isInsurances()) {
				insurancesCount = jdbcTemplate.getJdbcOperations().update(
						"UPDATE INSURANCEPAYMENTINSTRUCTIONS SET STATUS = ? WHERE ID IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ? AND BATCH_ID = ?)",
						new PreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps) throws SQLException {
								ps.setString(1, req.getStatus());
								ps.setLong(2, req.getHeaderId());
								ps.setLong(3, req.getBatchId());
							}

						});
			}

			if (batchCount == (disbursementsCount + paymentsCount + insurancesCount)) {
				return batchCount;
			} else {
				return 0;
			}
		}

		return 0;
	}

	@Override
	public void logDisbursementMovement(DisbursementRequest request, boolean log) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue(HEADER_ID, request.getHeaderId());
		parameterSource.addValue(BATCH_ID, request.getBatchId());
		parameterSource.addValue("TARGET_TYPE", request.getTargetType());
		parameterSource.addValue("FILE_NAME", request.getFileName());
		parameterSource.addValue("FILE_LOCATION", request.getFileLocation());
		parameterSource.addValue("DATA_ENGINE_CONFIG", request.getDataEngineConfig());
		parameterSource.addValue("POST_EVENTS", request.getPostEvents());
		parameterSource.addValue("CREATED_ON", request.getCreatedOn());
		parameterSource.addValue("CREATED_BY", request.getUserId());
		parameterSource.addValue("PROCESS_FLAG", request.getProcessFlag());
		parameterSource.addValue("PROCESSED_ON", request.getProcessedOn());
		parameterSource.addValue("FAILURE_REASON", request.getFailureReason());

		if (log) {
			this.jdbcTemplate.update(DisbursementRequestsQueries.getInsertLogMovement(), parameterSource);
		} else {
			this.jdbcTemplate.update(DisbursementRequestsQueries.getInsertMovement(), parameterSource);
		}
	}

	@Override
	public List<Long> getMovementList() {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("PROCESS_FLAG", 0);

		try {
			return jdbcTemplate.queryForList(DisbursementRequestsQueries.getMovementListQuery(), paramMap, Long.class);
		} catch (Exception e) {
			//
		}

		return new ArrayList<>();
	}

	@Override
	public void lockMovement(long requestId) {
		jdbcTemplate.getJdbcOperations().update(
				"UPDATE DISBURSEMENT_MOVEMENTS SET PROCESS_FLAG = ?, FAILURE_REASON = ? WHERE ID = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, -1);
						ps.setString(2, null);
						ps.setLong(3, requestId);

					}
				});
	}

	@Override
	public void updateMovement(DisbursementRequest request, int processFlag) {
		jdbcTemplate.getJdbcOperations().update(
				"UPDATE DISBURSEMENT_MOVEMENTS SET PROCESS_FLAG = ?, FAILURE_REASON = ?, FILE_LOCATION = ?, PROCESSED_ON = ? WHERE ID = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, processFlag);
						ps.setString(2, null);
						ps.setString(3, request.getFileLocation());
						ps.setDate(4, DateUtil.getSqlDate(DateUtil.getSysDate()));
						ps.setLong(5, request.getId());

					}
				});
	}

	@Override
	public void updateMovement(long requestId, int processFlag, String failureReason) {
		failureReason = StringUtils.trimToEmpty(failureReason);

		if (failureReason.length() > 199) {
			failureReason = failureReason.substring(0, 199);
		}

		final String reason = failureReason;

		jdbcTemplate.getJdbcOperations().update(
				"UPDATE DISBURSEMENT_MOVEMENTS SET PROCESS_FLAG = ?, FAILURE_REASON = ? WHERE ID = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, processFlag);
						ps.setString(2, reason);
						ps.setLong(3, requestId);

					}
				});
	}

	@Override
	public DisbursementRequest getMovementRequest(long requestId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("ID", requestId);
		paramMap.addValue("PROCESS_FLAG", -1);

		return jdbcTemplate.queryForObject(DisbursementRequestsQueries.getMovementQuery(), paramMap,
				new RowMapper<DisbursementRequest>() {

					@Override
					public DisbursementRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
						DisbursementRequest request = new DisbursementRequest();

						request.setId(rs.getLong(1));
						request.setHeaderId(rs.getLong(2));
						request.setBatchId(rs.getLong(3));
						request.setTargetType(rs.getString(4));
						request.setFileName(rs.getString(5));
						request.setFileLocation(rs.getString(6));
						request.setDataEngineConfig(rs.getLong(7));
						request.setPostEvents(rs.getString(8));
						request.setCreatedOn(rs.getDate(9));
						request.setUserId(rs.getLong(10));
						request.setProcessFlag(rs.getInt(11));
						request.setAppValueDate(request.getCreatedOn());

						return request;

					}
				});

	}

	@Override
	public void deleteMovement(long requestId) {
		jdbcTemplate.getJdbcOperations().update("DELETE FROM DISBURSEMENT_MOVEMENTS WHERE ID = ? AND PROCESS_FLAG = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, requestId);
						ps.setInt(2, 1);
					}
				});
	}

	@Override
	public int updateRespBatch(DisbursementDetails detail, long respBatchId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		parmMap.addValue("DISBURSEMENT_ID", detail.getDownload_Referid());
		parmMap.addValue("RESP_BATCH_ID", respBatchId);
		parmMap.addValue("TRANSACTIONREF", detail.getUtrNo());

		parmMap.addValue("STATUS", StringUtils.isNotEmpty(detail.getUtrNo()) ? "E" : "R");
		parmMap.addValue("REALIZATIONDATE", detail.getPaymentDate());
		parmMap.addValue("REJECT_REASON", null);

		if (StringUtils.isBlank(detail.getUtrNo())) {
			parmMap.addValue("REJECT_REASON", detail.getRejectReason());
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" Update DISBURSEMENT_REQUESTS set RESP_BATCH_ID = :RESP_BATCH_ID ,");
		sql.append(" STATUS = :STATUS, REALIZATION_DATE = :REALIZATIONDATE, REJECT_REASON = :REJECT_REASON,");
		sql.append(" TRANSACTIONREF = :TRANSACTIONREF WHERE DISBURSEMENT_ID = :DISBURSEMENT_ID");

		logger.debug(Literal.LEAVING);
		return jdbcTemplate.update(sql.toString(), parmMap);
	}

	@Override
	public String isDisbursementExist(DisbursementDetails detail) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		parmMap.addValue("DISBURSEMENT_ID", detail.getDownload_Referid());

		StringBuilder sql = new StringBuilder();
		sql.append(" Select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where DISBURSEMENT_ID = :DISBURSEMENT_ID ");

		String paymentId;
		try {
			paymentId = jdbcTemplate.queryForObject(sql.toString(), parmMap, String.class);
		} catch (EmptyResultDataAccessException e) {
			paymentId = "";
		}

		logger.debug(Literal.LEAVING);
		return paymentId;
	}

	public List<FinAdvancePayments> getAutoDisbInstructions(Date llDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from (");
		sql.append(" select fa.paymentid, fa.PaymentType, pb.partnerbankid, pb.PartnerbankCode");
		sql.append(", fm.FinType, fm.FinReference, div.EntityCode, 'D' Channel, fa.status, fa.llDate");
		sql.append(" from finadvancepayments fa");
		sql.append(" inner join partnerbanks pb on pb.partnerbankid = fa.partnerbankid");
		sql.append(" inner join financemain fm on fm.FinReference = fa.FinReference");
		sql.append(" inner join rmtfinancetypes ft on ft.fintype  = fm.fintype");
		sql.append(" inner join smtdivisiondetail div on div.divisioncode  = ft.findivision");
		sql.append(" union all");
		sql.append(" select paymentinstructionid paymentid,  pi.paymenttype, pb.partnerbankid, pb.PartnerbankCode");
		sql.append(", fm.FinType, fm.FinReference, div.EntityCode, 'P' Channel, pi.status, pi.postDate llDate");
		sql.append(" from paymentinstructions pi");
		sql.append(" inner join paymentheader ph on ph.paymentid = pi.paymentid");
		sql.append(" inner join partnerbanks pb on pb.partnerbankid = pi.partnerbankid");
		sql.append(" inner join financemain fm on fm.FinReference = ph.FinReference");
		sql.append(" inner join rmtfinancetypes ft on ft.fintype  = fm.fintype");
		sql.append(" inner join smtdivisiondetail div on div.divisioncode  = ft.findivision");
		sql.append(" union all");
		sql.append(" select  pi.id  paymentid, pi.paymenttype, pb.partnerbankid, pb.PartnerbankCode");
		sql.append(", '' fintype, vr.primarylinkref  finreference, pi.entitycode, 'I' channel, pi.status");
		sql.append(", pi.paymentdate llDate");
		sql.append(" from insurancepaymentinstructions pi");
		sql.append(" inner join partnerbanks pb on pb.partnerbankid = pi.partnerbankid");
		sql.append(" inner join vasrecording vr ON vr.paymentinsid = pi.id) t");
		sql.append(" where status ='APPROVED' and llDate <= ? ");
		sql.append(" and paymentid not in (SELECT disbursement_requests.disbursement_id FROM");
		sql.append(" disbursement_requests)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new Object[] { llDate },
					new RowMapper<FinAdvancePayments>() {

						@Override
						public FinAdvancePayments mapRow(ResultSet rs, int rowNum) throws SQLException {
							FinAdvancePayments fap = new FinAdvancePayments();
							fap.setPaymentId(rs.getLong("PaymentId"));
							if (StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_IFT,
									rs.getString("PaymentType"))) {
								fap.setPaymentType("I");
							} else {
								fap.setPaymentType(rs.getString("PaymentType"));
							}
							fap.setPartnerBankID(rs.getLong("PartnerbankId"));
							fap.setPartnerbankCode(rs.getString("PartnerbankCode"));
							fap.setFinType(rs.getString("FinType"));
							fap.setFinReference(rs.getString("FinReference"));
							fap.setEntityCode(rs.getString("EntityCode"));
							fap.setChannel(rs.getString("Channel"));
							fap.setStatus(rs.getString("Status"));

							if (PATNER_BANKS.get(fap.getPartnerbankCode()) == null) {
								loadPartnerBankDataEngineConfigs();
							}

							fap.setConfigName(PATNER_BANKS.get(fap.getPartnerbankCode()));

							return fap;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	private void loadPartnerBankDataEngineConfigs() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select PartnerBankCode, DataEngineConfigName FROM PartnerBanks");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("SEGMENT_TYPE", PennantConstants.PFF_CUSTCTG_INDIV);

		jdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				PATNER_BANKS.put(rs.getString("PartnerBankCode"), rs.getString("DataEngineConfigName"));
			}
		});
	}

	@Override
	public List<DisbursementRequest> getDisbursementInstructions(DisbursementRequest req) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT PAYMENTID , CUSTCIF, FINREFERENCE, FINTYPE");
		sql.append(", AMTTOBERELEASED, DISBURSEMENT_TYPE, PAYABLELOC, PRINTINGLOC");
		sql.append(", BANKNAME, MICR_CODE, IFSC_CODE, DISBDATE");
		sql.append(", BENEFICIARYACCNO , BENEFICIARYNAME, BENFICIRY_EMAIL, PAYMENTTYPE, PARTNERBANK_CODE, CHANNEL");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW WHERE");

		if (!DisbursementConstants.CHANNEL_INSURANCE.equals(req.getChannel())) {
			sql.append(" FINTYPE = ? AND");
		}
		sql.append(" ENTITYCODE = ? AND PARTNERBANK_CODE = ?");

		if (StringUtils.isNotBlank(req.getPaymentType())) {
			sql.append(" AND PAYMENTTYPE = ?");
		}

		if (StringUtils.isNotBlank(req.getChannel())) {
			sql.append(" AND channel= ?");
		}

		if (req.getFromDate() != null) {
			sql.append(" AND LLDATE >= ?");
		}

		if (req.getToDate() != null) {
			sql.append(" AND LLDATE <= ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			if (!DisbursementConstants.CHANNEL_INSURANCE.equals(req.getChannel())) {
				ps.setString(index++, req.getFinType());
			}
			ps.setString(index++, req.getEntityCode());
			ps.setString(index++, req.getPartnerBankCode());

			if (StringUtils.isNotBlank(req.getPaymentType())) {
				ps.setString(index++, req.getPaymentType());
			}

			if (StringUtils.isNotBlank(req.getChannel())) {
				ps.setString(index++, req.getChannel());
			}

			if (req.getFromDate() != null) {
				ps.setDate(index++, JdbcUtil.getDate(req.getFromDate()));
			}

			if (req.getToDate() != null) {
				ps.setDate(index++, JdbcUtil.getDate(req.getToDate()));
			}
		}, (rs, rowNum) -> {
			DisbursementRequest dr = new DisbursementRequest();

			dr.setDisbInstId(rs.getLong("PAYMENTID"));
			dr.setFinReference(rs.getString("FINREFERENCE"));
			dr.setCustCIF(rs.getString("CUSTCIF"));
			dr.setDisbType(rs.getString("PAYMENTTYPE"));
			dr.setPartnerBankCode(rs.getString("PARTNERBANK_CODE"));
			dr.setDisbursementDate(rs.getDate("DISBDATE"));
			dr.setBenficiaryName(rs.getString("BENEFICIARYNAME"));
			dr.setBenficiaryAccount(rs.getString("BENEFICIARYACCNO"));
			dr.setBenficiryEmail(rs.getString("BENFICIRY_EMAIL"));
			dr.setIfscCode(rs.getString("IFSC_CODE"));
			dr.setMicrCode(rs.getString("MICR_CODE"));

			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(dr.getPaymentType())
					|| DisbursementConstants.PAYMENT_TYPE_DD.equals(dr.getPaymentType())) {
				dr.setDraweeLocation(rs.getString("PAYABLELOC"));
				dr.setPrintLocation(rs.getString("PRINTINGLOC"));
			}

			dr.setDisbursementAmount(rs.getBigDecimal("AMTTOBERELEASED"));
			dr.setDisbursementType(rs.getString("DISBURSEMENT_TYPE"));
			dr.setBenficiaryBank(rs.getString("BANKNAME"));
			dr.setChannel(rs.getString("CHANNEL"));

			dr.setDisbCCy(ccy);
			return dr;
		});
	}

	@Override
	public FinAdvancePayments getDisbursementInstruction(long paymentId, String channel) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT PAYMENTID ,PARTNERBANK_ID, FINTYPE, BRANCHCODE");
		sql.append(", BRANCHDESC, PARTNERBANK_CODE, ALWFILEDOWNLOAD, FINREFERENCE");
		sql.append(", PAYMENTTYPE, ENTITYCODE, CUSTSHRTNAME, BENEFICIARYNAME");
		sql.append(", BENEFICIARYACCNO, AMTTOBERELEASED, DISBURSEMENT_TYPE");
		sql.append(", CHANNEL, PROVIDERID, STATUS");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW");
		sql.append(" WHERE PAYMENTID = ? AND  CHANNEL = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { paymentId, channel },
					(rs, rowNum) -> {
						FinAdvancePayments fp = new FinAdvancePayments();

						fp.setPaymentId(rs.getLong("PAYMENTID"));
						fp.setPartnerBankID(rs.getLong("PARTNERBANK_ID"));
						fp.setFinType((rs.getString("FINTYPE")));
						fp.setBranchCode(rs.getString("BRANCHCODE"));
						fp.setBranchDesc(rs.getString("BRANCHDESC"));
						fp.setPartnerbankCode(rs.getString("PARTNERBANK_CODE"));
						fp.setAlwFileDownload(rs.getBoolean(("ALWFILEDOWNLOAD")));
						fp.setFinReference(rs.getString("FINREFERENCE"));
						fp.setPaymentType(rs.getString("PAYMENTTYPE"));
						fp.setEntityCode((rs.getString("ENTITYCODE")));
						fp.setCustShrtName(rs.getString("CUSTSHRTNAME"));
						fp.setBeneficiaryName(rs.getString("BENEFICIARYNAME"));
						fp.setBeneficiaryAccNo(rs.getString("BENEFICIARYACCNO"));
						fp.setAmtToBeReleased(rs.getBigDecimal("AMTTOBERELEASED"));
						fp.setPaymentType(rs.getString("DISBURSEMENT_TYPE"));
						fp.setChannel(rs.getString("CHANNEL"));
						fp.setProviderId(rs.getLong("PROVIDERID"));
						fp.setStatus(rs.getString("STATUS"));

						return fp;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		}
	}

	@Override
	public List<DisbursementRequest> getDetailsByHeaderID(long headerID) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT FINREFERENCE, ID, CHANNEL, DISBURSEMENT_ID");
		sql.append(" FROM DISBURSEMENT_REQUESTS");
		sql.append(" WHERE HEADER_ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, headerID);
		}, (rs, rowNum) -> {
			DisbursementRequest dr = new DisbursementRequest();

			dr.setDisbReqId(rs.getLong("ID"));
			dr.setDisbInstId(rs.getLong("DISBURSEMENT_ID"));
			dr.setFinReference(rs.getString("FINREFERENCE"));
			dr.setChannel(rs.getString("CHANNEL"));
			return dr;
		});
	}

	@Override
	public FinAdvancePayments getDisbursementInstruction(long disbReqId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FA.PAYMENTID, FA.PAYMENTSEQ, FA.PAYMENTTYPE, FA.FINREFERENCE, FA.LINKEDTRANID");
		sql.append(", FA.STATUS, FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID");
		sql.append(", FA.BANKCODE, FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE");
		sql.append(", FA.AMTTOBERELEASED, FA.RECORDTYPE, FA.PARTNERBANKID");
		sql.append(", PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
		sql.append(" LEFT JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = FA.PARTNERBANKID");
		sql.append(" WHERE ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { disbReqId }, (rs, rowNum) -> {
				FinAdvancePayments fa = new FinAdvancePayments();
				fa.setPaymentId(rs.getLong("PAYMENTID"));
				fa.setPaymentSeq(rs.getInt("PAYMENTSEQ"));
				fa.setPaymentType(rs.getString("PAYMENTTYPE"));
				fa.setFinReference(rs.getString("FINREFERENCE"));
				fa.setLinkedTranId(rs.getLong("LINKEDTRANID"));
				fa.setStatus(rs.getString("STATUS"));
				fa.setBeneficiaryAccNo(rs.getString("BENEFICIARYACCNO"));
				fa.setBeneficiaryName(rs.getString("BENEFICIARYNAME"));
				fa.setBankBranchID(rs.getLong("BANKBRANCHID"));
				fa.setBankCode(rs.getString("BANKCODE"));
				fa.setPhoneCountryCode(rs.getString("PHONECOUNTRYCODE"));
				fa.setPhoneAreaCode(rs.getString("PHONEAREACODE"));
				fa.setPhoneNumber(rs.getString("PHONENUMBER"));
				fa.setAmtToBeReleased(rs.getBigDecimal("AMTTOBERELEASED"));
				fa.setRecordType(rs.getString("RECORDTYPE"));
				fa.setPartnerBankAcType(rs.getString("PARTNERBANKACTYPE"));
				fa.setPartnerBankAc(rs.getString("PARTNERBANKAC"));
				fa.setDisbCCy(rs.getString("DISBCCY"));
				fa.setLLDate(rs.getDate("LLDATE"));
				fa.setPartnerBankID(rs.getLong("PARTNERBANKID"));

				return fa;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		}
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long disbReqId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PH.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO");
		sql.append(", PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID");
		sql.append(", PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.DISBURSEMENT_ID, PI.STATUS");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
		sql.append(" WHERE ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { disbReqId }, (rs, rowNum) -> {
				PaymentInstruction pi = new PaymentInstruction();
				pi.setFinReference(rs.getString("FINREFERENCE"));
				pi.setLinkedTranId(rs.getLong("LINKEDTRANID"));
				pi.setBankBranchId(rs.getLong("BANKBRANCHID"));
				pi.setAccountNo(rs.getString("ACCOUNTNO"));
				pi.setAcctHolderName(rs.getString("ACCTHOLDERNAME"));
				pi.setPhoneCountryCode(rs.getString("PHONECOUNTRYCODE"));
				pi.setPhoneNumber(rs.getString("PHONENUMBER"));
				pi.setPaymentInstructionId(rs.getLong("PAYMENTINSTRUCTIONID"));
				pi.setPaymentAmount(rs.getBigDecimal("PAYMENTAMOUNT"));
				pi.setPaymentType(rs.getString("PAYMENTTYPE"));
				pi.setPaymentId(rs.getLong("DISBURSEMENT_ID"));
				pi.setStatus(rs.getString("STATUS"));

				return pi;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		}
	}

	@Override
	public InsurancePaymentInstructions getInsuranceInstruction(long disbReqId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PI.LINKEDTRANID, PI.ID, VPA.BANKBRANCHID, VPA.ACCOUNTNUMBER");
		sql.append(", AVD.DEALERNAME, PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS");
		sql.append(", DR.REJECT_REASON REJECTREASON, PI.PROVIDERID, DR.DISBURSEMENT_TYPE");
		sql.append(", DR.PAYMENT_DATE RESPDATE, DR.TRANSACTIONREF, DR.FINREFERENCE");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN INSURANCEPAYMENTINSTRUCTIONS PI ON PI.ID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN VASPROVIDERACCDETAIL VPA ON VPA.PROVIDERID = PI.PROVIDERID");
		sql.append(" INNER JOIN BANKBRANCHES BB ON BB.BANKBRANCHID = VPA.BANKBRANCHID");
		sql.append(" INNER JOIN AMTVEHICLEDEALER AVD ON AVD.DEALERID = VPA.PROVIDERID");
		sql.append(" WHERE DR.ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { disbReqId }, (rs, rowNum) -> {
				InsurancePaymentInstructions ipi = new InsurancePaymentInstructions();

				ipi.setLinkedTranId(rs.getLong("LINKEDTRANID"));
				ipi.setId(rs.getLong("ID"));
				ipi.setPaymentAmount(rs.getBigDecimal("PAYMENTAMOUNT"));
				ipi.setPaymentType(rs.getString("PAYMENTTYPE"));
				ipi.setStatus(rs.getString("STATUS"));
				ipi.setProviderId(rs.getLong("PROVIDERID"));
				ipi.setFinReference(rs.getString("FINREFERENCE"));
				ipi.setPaymentType(rs.getString("DISBURSEMENT_TYPE"));

				return ipi;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			return null;
		}
	}

	@Override
	public DisbursementRequest getDisbRequest(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ID, FINREFERENCE, DISBURSEMENT_ID, CHANNEL, STATUS, DISBURSEMENT_TYPE");
		sql.append(" FROM DISBURSEMENT_REQUESTS");
		sql.append(" WHERE ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, (rs, rowNum) -> {
				DisbursementRequest dr = new DisbursementRequest();

				dr.setId(rs.getLong("ID"));
				dr.setFinReference(rs.getString("FINREFERENCE"));
				dr.setPaymentId(rs.getLong("DISBURSEMENT_ID"));
				dr.setChannel(rs.getString("CHANNEL"));
				dr.setStatus(rs.getString("STATUS"));
				dr.setDisbType(rs.getString("DISBURSEMENT_TYPE"));

				return dr;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not available in DISBURSEMENT_REQUESTS table for the specified Id >> {}", id);
			return null;
		}
	}

	@Override
	public int updateDisbRequest(DisbursementRequest request) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update DISBURSEMENT_REQUESTS SET");
		sql.append(" STATUS = ?, REALIZATION_DATE = ?, REJECT_REASON = ?, TRANSACTIONREF = ?");
		sql.append(" WHERE ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, request.getStatus());
			ps.setDate(2, JdbcUtil.getDate(request.getClearingDate()));
			ps.setString(3, request.getRejectReason());
			ps.setString(4, request.getTransactionref());
			ps.setLong(5, request.getId());

		});
	}
}
