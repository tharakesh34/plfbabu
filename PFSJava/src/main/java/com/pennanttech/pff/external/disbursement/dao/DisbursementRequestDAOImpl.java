package com.pennanttech.pff.external.disbursement.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
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

	public DisbursementRequestDAOImpl() {
		super();
	}

	@Override
	public long getNextBatchId() {
		return getNextValue("SEQ_DISBURSEMENT_REQ_HEADER");
	}

	@Override
	public int lockFinAdvancePayments(long headerId, long userId, Long[] paymentIdList) {
		try {
			return this.jdbcOperations.update(DisbursementRequestsQueries.getInsertHeaderQuery(), ps -> {
				int index = 1;

				ps.setLong(index++, headerId);
				ps.setLong(index++, userId);
				ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setObject(index++, Arrays.asList(paymentIdList));
				ps.setString(index++, "APPROVED");
			});
		} catch (Exception e) {
			//
		}

		return 0;
	}

	@Override
	public int clearBatch(long headerId) {
		return execute("Delete From Disbursement_Requests_Header Where ID = ?", headerId);
	}

	@Override
	public int deleteDisbursementBatch(long headerId) {
		return execute("DELETE FROM DISBURSEMENT_REQUESTS_HEADER WHERE HEADER_ID = ?", headerId);
	}

	@Override
	public int deleteDisbursementBatch(long headerId, long batchId) {
		return execute("DELETE FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ?", headerId);
	}

	private int execute(String sql, long headerId) {
		logger.debug(Literal.SQL + sql);

		return jdbcOperations.update(sql, ps -> ps.setLong(1, headerId));
	}

	@Override
	public List<DisbursementRequest> logDisbursementBatch(DisbursementRequest requestData) {
		final String DISB_FI_EMAIL = SysParamUtil.getValueAsString("DISB_FI_EMAIL");

		return jdbcOperations.query(DisbursementRequestsQueries.getSelectAllQuery(), ps -> {
			ps.setLong(1, requestData.getHeaderId());
		}, (rs, rowNum) -> {
			DisbursementRequest req = new DisbursementRequest();

			req.setAutoDownload(requestData.isAutoDownload());
			req.setDisbursementId(getValueAsLong(rs, "DISBURSEMENT_ID"));
			req.setCustCIF(getValueAsString(rs, "CUSTCIF"));
			req.setFinID(getValueAsLong(rs, "FINID"));
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
			req.setLoanAmount(getValueAsBigDecimal(rs, "FINAMOUNT"));
			// default data
			req.setPaymentDetail1(DISB_FI_EMAIL);
			req.setHeaderId(requestData.getHeaderId());
			req.setRespBatchId(0L);
			req.setDownloadedOn(requestData.getAppValueDate());

			long id = insertDisbursement(req);
			req.setId(id);

			logger.debug(String.format("DISBURSEMENT_ID : %d", id));

			return req;
		});
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
		return this.jdbcOperations.query(DisbursementRequestsQueries.getSelectQuery(), ps -> {
			int index = 1;

			ps.setLong(index++, headerId);
			ps.setString(index++, "APPROVED");

		}, (rs, rowNum) -> {
			FinAdvancePayments fa = new FinAdvancePayments();

			fa.setPaymentId(rs.getLong("PAYMENTID"));
			fa.setPaymentType(rs.getString("PAYMENTTYPE"));
			fa.setPartnerBankID(rs.getLong("PARTNERBANKID"));
			fa.setPartnerbankCode(rs.getString("PARTNERBANKCODE"));
			fa.setAlwFileDownload(rs.getBoolean("ALWFILEDOWNLOAD"));
			fa.setChannel(rs.getString("CHANNEL"));

			return fa;
		});
	}

	private long insertDisbursement(DisbursementRequest req) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			this.jdbcOperations.update(con -> {
				PreparedStatement ps = con.prepareStatement(DisbursementRequestsQueries.getInsertQuery(),
						new String[] { "id" });
				int index = 1;

				ps.setLong(index++, req.getDisbursementId());
				ps.setString(index++, req.getCustCIF());
				ps.setLong(index++, req.getFinID());
				ps.setString(index++, req.getFinReference());
				ps.setBigDecimal(index++, req.getDisbursementAmount());
				ps.setString(index++, req.getDisbursementType());
				ps.setDate(index++, JdbcUtil.getDate(req.getDisbursementDate()));
				ps.setString(index++, req.getDraweeLocation());
				ps.setString(index++, req.getPrintLocation());
				ps.setString(index++, req.getCustomerName());
				ps.setString(index++, req.getCustomerMobile());
				ps.setString(index++, req.getCustomerEmail());
				ps.setString(index++, req.getCustomerState());
				ps.setString(index++, req.getCustomerCity());
				ps.setString(index++, req.getCustomerAddress1());
				ps.setString(index++, req.getCustomerAddress2());
				ps.setString(index++, req.getCustomerAddress3());
				ps.setString(index++, req.getCustomerAddress4());
				ps.setString(index++, req.getCustomerAddress5());
				ps.setString(index++, req.getBenficiaryBank());
				ps.setString(index++, req.getBenficiaryBranch());
				ps.setString(index++, req.getBenficiaryBranchState());
				ps.setString(index++, req.getBenficiaryBranchCity());
				ps.setString(index++, req.getMicrCode());
				ps.setString(index++, req.getIfscCode());
				ps.setString(index++, req.getBenficiaryAccount());
				ps.setString(index++, req.getBenficiaryName());
				ps.setString(index++, req.getBenficiaryMobile());
				ps.setString(index++, req.getBenficiryEmail());
				ps.setString(index++, req.getBenficiryState());
				ps.setString(index++, req.getBenficiryCity());
				ps.setString(index++, req.getBenficiaryAddress1());
				ps.setString(index++, req.getBenficiaryAddress2());
				ps.setString(index++, req.getBenficiaryAddress3());
				ps.setString(index++, req.getBenficiaryAddress4());
				ps.setString(index++, req.getBenficiaryAddress5());
				ps.setString(index++, req.getPaymentDetail1());
				ps.setString(index++, req.getPaymentDetail2());
				ps.setString(index++, req.getPaymentDetail3());
				ps.setString(index++, req.getPaymentDetail4());
				ps.setString(index++, req.getPaymentDetail5());
				ps.setString(index++, req.getPaymentDetail6());
				ps.setString(index++, req.getPaymentDetail7());
				ps.setString(index++, req.getStatus());
				ps.setString(index++, req.getRemarks());
				ps.setString(index++, req.getChannel());
				ps.setLong(index++, req.getHeaderId());
				ps.setBoolean(index++, req.isAutoDownload());
				ps.setLong(index++, req.getHeaderId());
				ps.setLong(index++, req.getPartnerBankId());
				ps.setString(index++, req.getPartnerBankCode());
				ps.setString(index++, req.getPartnerBankAccount());
				ps.setString(index++, req.getChequeNumber());
				ps.setDate(index++, JdbcUtil.getDate(req.getDownloadedOn()));

				return ps;
			}, keyHolder);

		} catch (Exception e) {
			//
		}

		return keyHolder.getKey().longValue();
	}

	@Override
	public void updateBatchFailureStatus(DisbursementRequest req) {
		updateStatus("FINADVANCEPAYMENTS", "PAYMENTID", req);
		updateStatus("PAYMENTINSTRUCTIONS", "PAYMENTINSTRUCTIONID", req);
		updateStatus("INSURANCEPAYMENTINSTRUCTIONS", "ID", req);
	}

	private void updateStatus(String tableName, String colName, DisbursementRequest req) {
		StringBuilder sql = new StringBuilder("Update ");
		sql.append(tableName);
		sql.append(" SET STATUS = ? WHERE ");
		sql.append(colName);
		sql.append(" IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, req.getStatus());
			ps.setLong(2, req.getHeaderId());
		});
	}

	@Override
	public int updateBatchStatus(DisbursementRequest req) {
		String sql = "UPDATE DISBURSEMENT_REQUESTS SET BATCH_ID = ?, STATUS = ? WHERE HEADER_ID = ? AND DISBURSEMENT_TYPE = ?";

		logger.debug(Literal.SQL + sql);

		int batchCount = jdbcOperations.update(sql, ps -> {
			ps.setLong(1, req.getBatchId());
			ps.setString(2, req.getStatus());
			ps.setLong(3, req.getHeaderId());
			ps.setString(4, req.getDisbursementType());
		});

		int disbursementsCount = 0;
		int paymentsCount = 0;
		int insurancesCount = 0;

		if (batchCount > 0) {
			if (req.isDisbursements()) {
				disbursementsCount = updateStatusWithBatchID("FINADVANCEPAYMENTS", "PAYMENTID", req);
			}

			if (req.isPayments()) {
				paymentsCount = updateStatusWithBatchID("PAYMENTINSTRUCTIONS", "PAYMENTINSTRUCTIONID", req);
			}

			if (batchCount == (disbursementsCount + paymentsCount + insurancesCount)) {
				return batchCount;
			} else {
				return 0;
			}
		}

		return 0;
	}

	private int updateStatusWithBatchID(String tableName, String colName, DisbursementRequest req) {
		StringBuilder sql = new StringBuilder("Update ");
		sql.append(tableName);
		sql.append(" SET STATUS = ? WHERE ");
		sql.append(colName);
		sql.append(" IN (SELECT DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ? and BATCH_ID = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, req.getStatus());
			ps.setLong(2, req.getHeaderId());
			ps.setLong(3, req.getBatchId());
		});
	}

	@Override
	public void logDisbursementMovement(DisbursementRequest request, boolean log) {
		String sql = "";

		if (log) {
			sql = DisbursementRequestsQueries.getInsertLogMovement();

		} else {
			sql = DisbursementRequestsQueries.getInsertMovement();
		}

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, request.getHeaderId());
			ps.setLong(index++, request.getBatchId());
			ps.setString(index++, request.getTargetType());
			ps.setString(index++, request.getFileName());
			ps.setString(index++, request.getFileLocation());
			ps.setLong(index++, request.getDataEngineConfig());
			ps.setString(index++, request.getPostEvents());
			ps.setDate(index++, JdbcUtil.getDate(request.getCreatedOn()));
			ps.setLong(index++, request.getUserId());
			ps.setInt(index++, request.getProcessFlag());
			ps.setDate(index++, JdbcUtil.getDate(request.getProcessedOn()));
			ps.setString(index++, request.getFailureReason());
		});
	}

	@Override
	public List<Long> getMovementList() {
		return jdbcOperations.query(DisbursementRequestsQueries.getMovementListQuery(), ps -> ps.setInt(1, 0),
				(rs, rowNum) -> {
					return rs.getLong(1);
				});
	}

	@Override
	public void lockMovement(long requestId) {
		String sql = "UPDATE DISBURSEMENT_MOVEMENTS SET PROCESS_FLAG = ?, FAILURE_REASON = ? WHERE ID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setInt(1, -1);
			ps.setString(2, null);
			ps.setLong(3, requestId);
		});
	}

	@Override
	public void updateMovement(DisbursementRequest request, int processFlag) {
		String sql = "UPDATE DISBURSEMENT_MOVEMENTS SET PROCESS_FLAG = ?, FAILURE_REASON = ?, FILE_LOCATION = ?, PROCESSED_ON = ? WHERE ID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setInt(1, processFlag);
			ps.setString(2, null);
			ps.setString(3, request.getFileLocation());
			ps.setDate(4, DateUtil.getSqlDate(DateUtil.getSysDate()));
			ps.setLong(5, request.getId());
		});

	}

	@Override
	public void updateMovement(long requestId, int processFlag, String failureReason) {
		failureReason = StringUtils.trimToEmpty(failureReason);

		if (failureReason.length() > 199) {
			failureReason = failureReason.substring(0, 199);
		}

		final String reason = failureReason;

		String sql = "UPDATE DISBURSEMENT_MOVEMENTS SET PROCESS_FLAG = ?, FAILURE_REASON = ? WHERE ID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setInt(1, processFlag);
			ps.setString(2, reason);
			ps.setLong(3, requestId);

		});
	}

	@Override
	public DisbursementRequest getMovementRequest(long requestId) {
		return jdbcOperations.queryForObject(DisbursementRequestsQueries.getMovementQuery(), (rs, rowNum) -> {
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
		}, requestId);

	}

	@Override
	public void deleteMovement(long requestId) {
		String sql = "DELETE FROM DISBURSEMENT_MOVEMENTS WHERE ID = ? AND PROCESS_FLAG = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setLong(1, requestId);
			ps.setInt(2, 1);
		});
	}

	@Override
	public int updateRespBatch(DisbursementDetails detail, long respBatchId) {
		String sql = "Update DISBURSEMENT_REQUESTS Set RESP_BATCH_ID = ?, STATUS = ?, REALIZATION_DATE = ?, REJECT_REASON = ?, TRANSACTIONREF = ? WHERE DISBURSEMENT_ID = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, respBatchId);
			ps.setString(index++, StringUtils.isNotEmpty(detail.getUtrNo()) ? "E" : "R");
			ps.setDate(index++, JdbcUtil.getDate(detail.getPaymentDate()));

			if (StringUtils.isBlank(detail.getUtrNo())) {
				ps.setString(index++, detail.getRejectReason());
			} else {
				ps.setString(index++, null);
			}

			ps.setString(index++, detail.getUtrNo());

			ps.setLong(index++, detail.getDownload_Referid());
		});
	}

	@Override
	public String isDisbursementExist(DisbursementDetails detail) {
		String sql = "Select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where DISBURSEMENT_ID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), String.class, detail.getDownload_Referid());
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return "";
	}

	public List<FinAdvancePayments> getAutoDisbInstructions(Date llDate) {
		StringBuilder sql = new StringBuilder("Select * from (");
		sql.append(" Select fa.PaymentId, fa.PaymentType, pb.PartnerBankId, pb.PartnerBankCode");
		sql.append(", fm.FinType, fm.FinID, fm.FinReference, div.EntityCode, 'D' Channel, fa.Status");
		sql.append(", fa.LlDate, pb.DownloadType");
		sql.append(" From FinAdvancePayments fa");
		sql.append(" Inner Join PartnerBanks pb on pb.PartnerBankId = fa.PartnerBankId");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = fa.FinID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType  = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail div on div.DivisionCode  = ft.FinDivision");
		sql.append(" Union All");
		sql.append(" Select PaymentInstructionId PaymentId,  pi.PaymentType, pb.PartnerBankId, pb.PartnerBankCode");
		sql.append(", fm.FinType, fm.FinID, fm.FinReference, div.EntityCode, 'P' Channel, pi.Status");
		sql.append(", pi.PostDate LlDate, pb.DownloadType");
		sql.append(" From paymentinstructions pi");
		sql.append(" Inner Join PaymentHeader ph on ph.PaymentId = pi.PaymentId");
		sql.append(" Inner Join PartnerBanks pb on pb.PartnerBankId = pi.PartnerBankId");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ph.FinID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType  = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail div on div.DivisionCode  = ft.FinDivision");
		sql.append(" Union All");
		sql.append(" Select pi.Id PaymentId, pi.PaymentType, pb.PartnerBankId, pb.PartnerBankCode");
		sql.append(", '' FinType, fm.FinID, vr.PrimaryLinkRef  FinReference, pi.EntityCode, 'I' Channel, pi.Status");
		sql.append(", pi.PaymentDate LlDate, pb.DownloadType");
		sql.append(" From InsurancePaymentInstructions pi");
		sql.append(" Inner Join PartnerBanks pb on pb.PartnerBankId = pi.PartnerBankId");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = vr.PrimaryLinkRef");
		sql.append(" Inner Join VasRecording vr on vr.PaymentInsId = pi.Id) t");
		sql.append(" Where status = ? and llDate <= ?");
		sql.append(" and paymentid not in (Select disbursement_requests.disbursement_id From disbursement_requests)");

		logger.debug(Literal.SQL + sql.toString());

		Map<String, String> PATNER_BANKS = new HashMap<>();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "APPROVED");
			ps.setDate(index++, JdbcUtil.getDate(llDate));
		}, (rs, rowNum) -> {
			FinAdvancePayments fap = new FinAdvancePayments();
			fap.setPaymentId(rs.getLong("PaymentId"));
			if (DisbursementConstants.PAYMENT_TYPE_IFT.equals(rs.getString("PaymentType"))) {
				fap.setPaymentType("I");
			} else {
				fap.setPaymentType(rs.getString("PaymentType"));
			}
			fap.setPartnerBankID(rs.getLong("PartnerbankId"));
			fap.setPartnerbankCode(rs.getString("PartnerbankCode"));
			fap.setFinType(rs.getString("FinType"));
			fap.setFinID(rs.getLong("FinID"));
			fap.setFinReference(rs.getString("FinReference"));
			fap.setEntityCode(rs.getString("EntityCode"));
			fap.setChannel(rs.getString("Channel"));
			fap.setStatus(rs.getString("Status"));
			fap.setDownloadType(rs.getString("downloadType"));

			if (PATNER_BANKS.get(fap.getPartnerbankCode()) == null) {
				loadPartnerBankDataEngineConfigs(PATNER_BANKS);
			}

			fap.setConfigName(PATNER_BANKS.get(fap.getPartnerbankCode()));

			return fap;
		});
	}

	private void loadPartnerBankDataEngineConfigs(Map<String, String> PATNER_BANKS) {
		String sql = "Select PartnerBankCode, DataEngineConfigName FROM PartnerBanks";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.query(sql, rs -> {
			PATNER_BANKS.put(rs.getString(1), rs.getString(2));
		});
	}

	@Override
	public List<DisbursementRequest> getDisbursementInstructions(DisbursementRequest req) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" PAYMENTID, CUSTCIF, FINID, FINREFERENCE, FINTYPE");
		sql.append(", AMTTOBERELEASED, DISBURSEMENT_TYPE, PAYABLELOC, PRINTINGLOC");
		sql.append(", BANKNAME, MICR_CODE, IFSC_CODE, DISBDATE");
		sql.append(", BENEFICIARYACCNO, BENEFICIARYNAME, BENFICIRY_EMAIL, PAYMENTTYPE");
		sql.append(", PARTNERBANK_CODE, CHANNEL, PAYMENTDETAIL");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW WHERE");
		sql.append(" FINTYPE = ? AND ENTITYCODE = ? AND PARTNERBANK_CODE = ?");

		if (StringUtils.isNotBlank(req.getPaymentType())) {
			sql.append(" AND PAYMENTTYPE = ?");
		}

		if (req.getFinID() > 0) {
			sql.append(" AND FinID = ?");
		}

		if (StringUtils.isNotBlank(req.getChannel())) {
			sql.append(" AND channel= ?");
		}

		if (StringUtils.isNotBlank(req.getDisbParty())) {
			sql.append(" AND PaymentDetail = ?");
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

			ps.setString(index++, req.getFinType());
			ps.setString(index++, req.getEntityCode());
			ps.setString(index++, req.getPartnerBankCode());

			if (StringUtils.isNotBlank(req.getPaymentType())) {
				ps.setString(index++, req.getPaymentType());
			}

			if (req.getFinID() > 0) {
				ps.setLong(index++, req.getFinID());
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
			dr.setFinID(rs.getLong("FINID"));
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
			dr.setDisbParty(rs.getString("PAYMENTDETAIL"));

			dr.setDisbCCy(ccy);

			return dr;
		});
	}

	@Override
	public FinAdvancePayments getDisbursementInstruction(long paymentId, String channel) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" PAYMENTID, PARTNERBANK_ID, FINTYPE, BRANCHCODE");
		sql.append(", BRANCHDESC, PARTNERBANK_CODE, ALWFILEDOWNLOAD, FinID, FINREFERENCE");
		sql.append(", PAYMENTTYPE, ENTITYCODE, CUSTSHRTNAME, BENEFICIARYNAME");
		sql.append(", BENEFICIARYACCNO, AMTTOBERELEASED, DISBURSEMENT_TYPE");
		sql.append(", CHANNEL, PROVIDERID, STATUS, PAYMENTDETAIL");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW");
		sql.append(" WHERE PAYMENTID = ? AND CHANNEL = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinAdvancePayments fp = new FinAdvancePayments();

				fp.setPaymentId(rs.getLong("PAYMENTID"));
				fp.setPartnerBankID(rs.getLong("PARTNERBANK_ID"));
				fp.setFinType((rs.getString("FINTYPE")));
				fp.setBranchCode(rs.getString("BRANCHCODE"));
				fp.setBranchDesc(rs.getString("BRANCHDESC"));
				fp.setPartnerbankCode(rs.getString("PARTNERBANK_CODE"));
				fp.setAlwFileDownload(rs.getBoolean(("ALWFILEDOWNLOAD")));
				fp.setFinID(rs.getLong("FINID"));
				fp.setFinReference(rs.getString("FINREFERENCE"));
				fp.setPaymentType(rs.getString("PAYMENTTYPE"));
				fp.setEntityCode((rs.getString("ENTITYCODE")));
				fp.setCustShrtName(rs.getString("CUSTSHRTNAME"));
				fp.setBeneficiaryName(rs.getString("BENEFICIARYNAME"));
				fp.setBeneficiaryAccNo(rs.getString("BENEFICIARYACCNO"));
				fp.setAmtToBeReleased(rs.getBigDecimal("AMTTOBERELEASED"));
				fp.setPaymentType(rs.getString("DISBURSEMENT_TYPE"));
				fp.setChannel(rs.getString("CHANNEL"));
				fp.setProviderId(JdbcUtil.getLong(rs.getObject("PROVIDERID")));
				fp.setStatus(rs.getString("STATUS"));
				fp.setPaymentDetail(rs.getString("PAYMENTDETAIL"));

				return fp;
			}, paymentId, channel);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<DisbursementRequest> getDetailsByHeaderID(long headerID) {
		String sql = "SELECT FINID, FINREFERENCE, ID, CHANNEL, DISBURSEMENT_ID FROM DISBURSEMENT_REQUESTS WHERE HEADER_ID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;
			ps.setLong(index, headerID);
		}, (rs, rowNum) -> {
			DisbursementRequest dr = new DisbursementRequest();

			dr.setFinID(rs.getLong("FINID"));
			dr.setFinReference(rs.getString("FINREFERENCE"));
			dr.setDisbReqId(rs.getLong("ID"));
			dr.setChannel(rs.getString("CHANNEL"));
			dr.setDisbInstId(rs.getLong("DISBURSEMENT_ID"));

			return dr;
		});
	}

	@Override
	public FinAdvancePayments getDisbursementInstruction(long disbReqId) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" FA.PAYMENTID, FA.PAYMENTSEQ, FA.PAYMENTTYPE, FM.FINID, FM.FINREFERENCE, FA.LINKEDTRANID");
		sql.append(", FA.STATUS, FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID");
		sql.append(", FA.BANKCODE, FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE");
		sql.append(", FA.AMTTOBERELEASED, FA.RECORDTYPE, FA.PARTNERBANKID");
		sql.append(", PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = FA.FINID");
		sql.append(" LEFT JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = FA.PARTNERBANKID");
		sql.append(" WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinAdvancePayments fa = new FinAdvancePayments();
				fa.setPaymentId(rs.getLong("PAYMENTID"));
				fa.setPaymentSeq(rs.getInt("PAYMENTSEQ"));
				fa.setPaymentType(rs.getString("PAYMENTTYPE"));
				fa.setFinID(rs.getLong("FINID"));
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
			}, disbReqId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long disbReqId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FINID, FM.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO");
		sql.append(", PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID");
		sql.append(", PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.DISBURSEMENT_ID, PI.STATUS");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = PH.FINID");
		sql.append(" WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PaymentInstruction pi = new PaymentInstruction();

				pi.setFinID(rs.getLong("FINID"));
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
			}, disbReqId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public InsurancePaymentInstructions getInsuranceInstruction(long disbReqId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PI.LINKEDTRANID, PI.ID, VPA.BANKBRANCHID, VPA.ACCOUNTNUMBER");
		sql.append(", AVD.DEALERNAME, PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS");
		sql.append(", DR.REJECT_REASON REJECTREASON, PI.PROVIDERID, DR.DISBURSEMENT_TYPE");
		sql.append(", DR.PAYMENT_DATE RESPDATE, DR.TRANSACTIONREF, FM.FINID, FM.FINREFERENCE");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = DR.FINID");
		sql.append(" INNER JOIN INSURANCEPAYMENTINSTRUCTIONS PI ON PI.ID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN VASPROVIDERACCDETAIL VPA ON VPA.PROVIDERID = PI.PROVIDERID");
		sql.append(" INNER JOIN BANKBRANCHES BB ON BB.BANKBRANCHID = VPA.BANKBRANCHID");
		sql.append(" INNER JOIN AMTVEHICLEDEALER AVD ON AVD.DEALERID = VPA.PROVIDERID");
		sql.append(" WHERE DR.ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				InsurancePaymentInstructions ipi = new InsurancePaymentInstructions();

				ipi.setLinkedTranId(rs.getLong("LINKEDTRANID"));
				ipi.setId(rs.getLong("ID"));
				ipi.setPaymentAmount(rs.getBigDecimal("PAYMENTAMOUNT"));
				ipi.setPaymentType(rs.getString("PAYMENTTYPE"));
				ipi.setStatus(rs.getString("STATUS"));
				ipi.setProviderId(rs.getLong("PROVIDERID"));
				ipi.setFinID(rs.getLong("FINID"));
				ipi.setFinReference(rs.getString("FINREFERENCE"));
				ipi.setPaymentType(rs.getString("DISBURSEMENT_TYPE"));

				return ipi;
			}, disbReqId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public DisbursementRequest getDisbRequest(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ID, FINID, FINREFERENCE, DISBURSEMENT_ID, CHANNEL, STATUS, DISBURSEMENT_TYPE");
		sql.append(" FROM DISBURSEMENT_REQUESTS");
		sql.append(" WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DisbursementRequest dr = new DisbursementRequest();

				dr.setId(rs.getLong("ID"));
				dr.setFinID(rs.getLong("FINID"));
				dr.setFinReference(rs.getString("FINREFERENCE"));
				dr.setPaymentId(rs.getLong("DISBURSEMENT_ID"));
				dr.setChannel(rs.getString("CHANNEL"));
				dr.setStatus(rs.getString("STATUS"));
				dr.setDisbType(rs.getString("DISBURSEMENT_TYPE"));

				return dr;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public int updateDisbRequest(DisbursementRequest request) {
		String sql = "Update DISBURSEMENT_REQUESTS SET STATUS = ?, REALIZATION_DATE = ?, REJECT_REASON = ?, TRANSACTIONREF = ? WHERE ID = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.update(sql, ps -> {
			ps.setString(1, request.getStatus());
			ps.setDate(2, JdbcUtil.getDate(request.getClearingDate()));
			ps.setString(3, request.getRejectReason());
			ps.setString(4, request.getTransactionref());
			ps.setLong(5, request.getId());
		});
	}

	@Override
	public List<FinAdvancePayments> getDisbRequestsByRespBatchId(long respBatchId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FA.PAYMENTID, FM.FINID, FM.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE DISBDATE");
		sql.append(", FA.PAYMENTTYPE, FA.STATUS, DR.STATUS CLEARINGSTATUS, FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME");
		sql.append(", FA.BANKBRANCHID, FA.BANKCODE, FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE");
		sql.append(", FA.AMTTOBERELEASED, FA.RECORDTYPE, DR.CHEQUE_NUMBER LLREFERENCENO");
		sql.append(", DR.REJECT_REASON REJECTREASON, DR.REALIZATION_DATE REALIZATIONDATE");
		sql.append(", DR.DOWNLOADED_ON DOWNLOADEDON, DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF, FA.PAYMENTSEQ");
		sql.append(", PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
		sql.append(", FA.VASREFERENCE, AV.SHORTCODE AS DEALERSHORTCODE, VS.SHORTCODE AS PRODUCTSHORTCODE");
		sql.append(", FA.PAYMENTDETAIL");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = FA.FINID");
		sql.append(" LEFT JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = FA.PARTNERBANKID");
		sql.append(" LEFT JOIN VASRECORDING VR ON VR.VASREFERENCE = FA.VASREFERENCE");
		sql.append(" LEFT JOIN VASSTRUCTURE VS ON VS.PRODUCTCODE = VR.PRODUCTCODE");
		sql.append(" LEFT JOIN AMTVEHICLEDEALER AV ON  AV.DEALERID = VS.MANUFACTURERID");
		sql.append(" WHERE RESP_BATCH_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, respBatchId);
		}, (rs, rowNum) -> {
			FinAdvancePayments fap = new FinAdvancePayments();

			fap.setPaymentId(rs.getLong("PAYMENTID"));
			fap.setFinID(rs.getLong("FINID"));
			fap.setFinReference(rs.getString("FINREFERENCE"));
			fap.setLinkedTranId(JdbcUtil.getLong(rs.getObject("LINKEDTRANID")));
			fap.setPaymentType(rs.getString("PAYMENTTYPE"));
			fap.setStatus(rs.getString("STATUS"));
			fap.setClearingStatus(rs.getString("CLEARINGSTATUS"));
			fap.setBeneficiaryAccNo(rs.getString("BENEFICIARYACCNO"));
			fap.setBeneficiaryName(rs.getString("BENEFICIARYNAME"));
			fap.setBankBranchID(JdbcUtil.getLong(rs.getObject("BANKBRANCHID")));
			fap.setBankCode(rs.getString("BANKCODE"));
			fap.setPhoneCountryCode(rs.getString("PHONECOUNTRYCODE"));
			fap.setPhoneAreaCode(rs.getString("PHONEAREACODE"));
			fap.setPhoneNumber(rs.getString("PHONENUMBER"));
			fap.setAmtToBeReleased(rs.getBigDecimal("AMTTOBERELEASED"));
			fap.setRecordType(rs.getString("RECORDTYPE"));
			fap.setLLReferenceNo(rs.getString("LLREFERENCENO"));
			fap.setRejectReason(rs.getString("REJECTREASON"));
			fap.setRealizationDate(rs.getDate("REALIZATIONDATE"));
			fap.setDownloadedon(rs.getDate("DOWNLOADEDON"));
			fap.setClearingDate(rs.getDate("CLEARINGDATE"));
			fap.setTransactionRef(rs.getString("TRANSACTIONREF"));
			fap.setPaymentSeq(rs.getInt("PAYMENTSEQ"));
			fap.setPartnerBankAcType(rs.getString("PARTNERBANKACTYPE"));
			fap.setPartnerBankAc(rs.getString("PARTNERBANKAC"));
			fap.setDisbCCy(rs.getString("DISBCCY"));
			fap.setLLDate(rs.getDate("LLDATE"));
			fap.setVasReference(rs.getString("VASREFERENCE"));
			fap.setDealerShortCode(rs.getString("DEALERSHORTCODE"));
			fap.setProductShortCode(rs.getString("PRODUCTSHORTCODE"));
			fap.setPaymentDetail(rs.getString("PAYMENTDETAIL"));

			return fap;
		});
	}

	@Override
	public List<PaymentInstruction> getPaymentInstructionsByRespBatchId(long respBatchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FINID, FM.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO");
		sql.append(", PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID");
		sql.append(", PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, PI.STATUS, DR.STATUS CLEARINGSTATUS");
		sql.append(", DR.REJECT_REASON REJECTREASON, DR.REALIZATION_DATE REALIZATIONDATE");
		sql.append(", DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
		sql.append(" FROM DISBURSEMENT_REQUESTS DR");
		sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
		sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = PH.FINID");
		sql.append(" WHERE RESP_BATCH_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, respBatchId);
		}, (rs, rowNum) -> {
			PaymentInstruction pi = new PaymentInstruction();

			pi.setFinID(rs.getLong("FINID"));
			pi.setFinReference(rs.getString("FINREFERENCE"));
			pi.setLinkedTranId(JdbcUtil.getLong(rs.getObject("LINKEDTRANID")));
			pi.setPaymentId(rs.getLong("PAYMENTID"));
			pi.setBankBranchId(JdbcUtil.getLong(rs.getObject("BANKBRANCHID")));
			pi.setAccountNo(rs.getString("ACCOUNTNO"));
			pi.setAcctHolderName(rs.getString("ACCTHOLDERNAME"));
			pi.setPhoneCountryCode(rs.getString("PHONECOUNTRYCODE"));
			pi.setPhoneNumber(rs.getString("PHONENUMBER"));
			pi.setPaymentInstructionId(JdbcUtil.getLong(rs.getObject("PAYMENTINSTRUCTIONID")));
			pi.setPaymentAmount(rs.getBigDecimal("PAYMENTAMOUNT"));
			pi.setPaymentType(rs.getString("PAYMENTTYPE"));
			pi.setStatus(rs.getString("STATUS"));
			pi.setClearingStatus(rs.getString("CLEARINGSTATUS"));
			pi.setRejectReason(rs.getString("REJECTREASON"));
			pi.setRealizationDate(rs.getDate("REALIZATIONDATE"));
			pi.setClearingDate(rs.getDate("CLEARINGDATE"));
			pi.setTransactionRef(rs.getString("TRANSACTIONREF"));

			return pi;
		});
	}
}
