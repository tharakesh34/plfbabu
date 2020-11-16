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
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
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
							req.setPartnerbankAccount(getValueAsString(rs, "PARTNERBANK_ACCOUNT"));

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

}
