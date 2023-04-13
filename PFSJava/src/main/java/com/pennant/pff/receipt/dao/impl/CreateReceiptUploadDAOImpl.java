package com.pennant.pff.receipt.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.model.ExcessTransferUpload;
import com.pennant.pff.receipt.dao.CreateReceiptUploadDAO;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.file.UploadContants.Status;

public class CreateReceiptUploadDAOImpl extends SequenceDao<ExcessTransferUpload> implements CreateReceiptUploadDAO {

	public CreateReceiptUploadDAOImpl() {
		super();
	}

	@Override
	public List<CreateReceiptUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, HeaderID, FinID, Reference, ReceiptPurpose, ExcessAdjustTo, AllocationType, ReceiptAmount");
		sql.append(", EffectSchdMethod, Remarks, ValueDate, ReceiptMode, SubReceiptMode, ReceiptChannel, PaymentRef");
		sql.append(", ChequeNumber, BankCode, ChequeAccountNumber, TransactionRef, ReceiptModeStatus, DepositDate");
		sql.append(", RealizationDate, InstrumentDate, PanNumber, ExternalRef, ReceivedFrom, BounceDate");
		sql.append(", BounceReason, BounceRemarks, Status, Progress, ErrorCode, ErrorDesc");
		sql.append(" From CREATE_RECEIPT_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CreateReceiptUpload receipt = new CreateReceiptUpload();

			receipt.setId(rs.getLong("ID"));
			receipt.setHeaderId(rs.getLong("HeaderID"));
			receipt.setReferenceID(JdbcUtil.getLong(rs.getLong("FinID")));
			receipt.setReference(rs.getString("Reference"));
			receipt.setReceiptPurpose(rs.getString("ReceiptPurpose"));
			receipt.setExcessAdjustTo(rs.getString("ExcessAdjustTo"));
			receipt.setAllocationType(rs.getString("AllocationType"));
			receipt.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			receipt.setEffectSchdMethod(rs.getString("EffectSchdMethod"));
			receipt.setRemarks(rs.getString("Remarks"));
			receipt.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
			receipt.setReceiptMode(rs.getString("ReceiptMode"));
			receipt.setSubReceiptMode(rs.getString("SubReceiptMode"));
			receipt.setReceiptChannel(rs.getString("ReceiptChannel"));
			receipt.setPaymentRef(rs.getString("PaymentRef"));
			receipt.setChequeNumber(rs.getString("ChequeNumber"));
			receipt.setBankCode(rs.getString("BankCode"));
			receipt.setChequeAccountNumber(rs.getString("ChequeAccountNumber"));
			receipt.setTransactionRef(rs.getString("TransactionRef"));
			receipt.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
			receipt.setDepositDate(JdbcUtil.getDate(rs.getDate("DepositDate")));
			receipt.setRealizationDate(JdbcUtil.getDate(rs.getDate("RealizationDate")));
			receipt.setInstrumentDate(JdbcUtil.getDate(rs.getDate("InstrumentDate")));
			receipt.setPanNumber(rs.getString("PanNumber"));
			receipt.setExternalRef(rs.getString("ExternalRef"));
			receipt.setReceivedFrom(rs.getString("ReceivedFrom"));
			receipt.setBounceDate(JdbcUtil.getDate(rs.getDate("BounceDate")));
			receipt.setBounceReason(rs.getString("BounceReason"));
			receipt.setBounceRemarks(rs.getString("BounceRemarks"));
			receipt.setStatus(rs.getString("Status"));
			receipt.setProgress(rs.getInt("Progress"));
			receipt.setErrorCode(rs.getString("ErrorCode"));
			receipt.setErrorDesc(rs.getString("ErrorDesc"));

			return receipt;
		}, headerID);
	}

	@Override
	public List<CreateReceiptUpload> getAllocations(long uploadId, long headerID) {
		StringBuilder sql = new StringBuilder("Select UploadId, Code, Amount");
		sql.append(" From KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" Where UploadId = ? and HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, uploadId);
			ps.setLong(2, headerID);
		}, (rs, rownum) -> {
			CreateReceiptUpload fc = new CreateReceiptUpload();

			fc.setId(rs.getLong("UploadId"));
			// fc.setCode(rs.getString("Code"));
			// fc.setAmount(rs.getBigDecimal("Amount"));

			return fc;
		});
	}

	@Override
	public void saveAllocations(List<CreateReceiptUpload> details) {
		StringBuilder sql = new StringBuilder("Insert into KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" (HeaderID, UploadId, Code, Amount)");
		sql.append(" Values(?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				CreateReceiptUpload detail = details.get(i);

				ps.setLong(++index, detail.getHeaderId());
				ps.setLong(++index, detail.getId());
				// ps.setString(++index, detail.getCode());
				// ps.setBigDecimal(++index, detail.getAmount());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});
	}

	@Override
	public void update(List<CreateReceiptUpload> details) {
		StringBuilder sql = new StringBuilder("Update CREATE_RECEIPT_UPLOAD set");
		sql.append(" FinID = ?, Progress = ?, Status = ?, ErrorCode = ?");
		sql.append(", ErrorDesc = ? ");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				CreateReceiptUpload detail = details.get(i);

				ps.setObject(++index, detail.getReferenceID());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, detail.getErrorCode());
				ps.setString(++index, detail.getErrorDesc());

				ps.setLong(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});
	}

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update CREATE_RECEIPT_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIds.get(i);

				ps.setInt(++index, progress);
				ps.setString(++index, (progress == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, errorCode);
				ps.setString(++index, errorDesc);

				ps.setLong(++index, headerID);
			}

			@Override
			public int getBatchSize() {
				return headerIds.size();
			}
		});
	}

	@Override
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" r.Reference, r.ReceiptPurpose, r.ExcessAdjustTo, r.AllocationType, r.ReceiptAmount");
		sql.append(", r.EffectSchdMethod, r.Remarks, r.ValueDate, r.ReceiptMode, r.SubReceiptMode");
		sql.append(", r.ReceiptChannel, r.PaymentRef, r.ChequeNumber, r.BankCode, r.ChequeAccountNumber");
		sql.append(", r.TransactionRef, r.ReceiptModeStatus, r.DepositDate, r.RealizationDate");
		sql.append(", r.InstrumentDate, r.PanNumber, r.ExternalRef, r.ReceivedFrom, r.BounceDate");
		sql.append(", r.BounceReason, r.BounceRemarks, r.Status, r.Progress, r.ErrorCode, r.ErrorDesc");
		sql.append(" From CREATE_RECEIPT_UPLOAD r");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = r.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where HeaderId = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public List<String> isDuplicateExists(CreateReceiptUpload rud) {
		boolean isOnline = StringUtils.isNotBlank(rud.getTransactionRef());
		StringBuilder sql = new StringBuilder("Select Name From File_Upload_Header");
		sql.append(" Where ID IN (");
		sql.append(" Select HeaderID From Create_Receipt_Upload");
		sql.append(" Where Reference = ?  and ValueDate = ? and ReceiptAmount = ?");
		sql.append(" and HeaderID <> ? and ProcessingStatus = ?");

		if (isOnline) {
			sql.append(" and TransactionRef = ?");
		}
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, rud.getReference());
			ps.setDate(index++, JdbcUtil.getDate(rud.getValueDate()));
			ps.setBigDecimal(index++, rud.getReceiptAmount());
			ps.setLong(index++, rud.getHeaderId());
			ps.setInt(index++, ReceiptDetailStatus.SUCCESS.getValue());

			if (isOnline) {
				ps.setString(index, rud.getTransactionRef());
			}

		}, (rs, roNum) -> {
			return rs.getString(1);
		});
	}

	@Override
	public String getLoanReference(String finReference, String fileName) {
		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" Distinct Reference");
		sql.append(" From Create_Receipt_Upload ");
		sql.append(" Where HeaderID in (");
		sql.append(" Select ID From File_Upload_Header");
		sql.append(" Where FileName not in (?) and Progress in (?, ?, ?, ?)) and Reference = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getString(1), fileName,
					finReference, Status.IN_PROCESS.getValue(), Status.DOWNLOADED.getValue(),
					Status.IMPORTED.getValue(), Status.IMPORT_IN_PROCESS.getValue());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

}
