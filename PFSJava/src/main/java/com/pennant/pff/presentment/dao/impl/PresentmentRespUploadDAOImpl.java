package com.pennant.pff.presentment.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.PresentmentRespUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.model.presentment.PresentmentRespUpload;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentRespUploadDAOImpl extends SequenceDao<PresentmentRespUpload>
		implements PresentmentRespUploadDAO {

	public PresentmentRespUploadDAOImpl() {
		super();
	}

	@Override
	public List<PresentmentRespUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderId, RecordSeq");
		sql.append(", FinId, FinReference, Presentment_Reference, Host_Reference, Instalment_No, Amount_Cleared");
		sql.append(", Clearing_Date, Clearing_Status, Bounce_Code, Bounce_Remarks, Reason_Code, Bank_Code");
		sql.append(", Bank_Name, Branch_Code, Branch_Name, Partner_Bank_Code, Partner_Bank_Name, Bank_Address");
		sql.append(", Account_Number, Ifsc_Code, Umrn_No, Micr_Code, Cheque_Serial_No, Corporate_User_No");
		sql.append(", Corporate_User_Name, Dest_Acc_Holder, Debit_Credit_Flag, Process_Flag, Thread_Id, Utr_Number");
		sql.append(", FateCorrection, ErrorCode, ErrorDesc, Progress, Status");
		sql.append(" From PRESENTMENT_RESP_UPLOAD");
		sql.append(" Where HeaderID = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentRespUpload fc = new PresentmentRespUpload();

			fc.setId(rs.getLong("Id"));
			fc.setHeaderId(rs.getLong("HeaderId"));
			fc.setRecordSeq(rs.getLong("RecordSeq"));
			fc.setReferenceID(rs.getLong("FinId"));
			fc.setReference(rs.getString("FinReference"));
			fc.setPresentmentReference(rs.getString("Presentment_Reference"));
			fc.setHostReference(rs.getString("Host_Reference"));
			fc.setInstalmentNo(rs.getString("Instalment_No"));
			fc.setAmountCleared(rs.getString("Amount_Cleared"));
			fc.setClearingDate(JdbcUtil.getDate(rs.getDate("Clearing_Date")));
			fc.setClearingStatus(rs.getString("Clearing_Status"));
			fc.setBounceCode(rs.getString("Bounce_Code"));
			fc.setBounceRemarks(rs.getString("Bounce_Remarks"));
			fc.setReasonCode(rs.getString("Reason_Code"));
			fc.setBankCode(rs.getString("Bank_Code"));
			fc.setBankName(rs.getString("Bank_Name"));
			fc.setBranchCode(rs.getString("Branch_Code"));
			fc.setBranchName(rs.getString("Branch_Name"));
			fc.setPartnerBankCode(rs.getString("Partner_Bank_Code"));
			fc.setPartnerBankName(rs.getString("Partner_Bank_Name"));
			fc.setBankAddress(rs.getString("Bank_Address"));
			fc.setAccountNumber(rs.getString("Account_Number"));
			fc.setIfscCode(rs.getString("Ifsc_Code"));
			fc.setUmrnNo(rs.getString("Umrn_No"));
			fc.setMicrCode(rs.getString("Micr_Code"));
			fc.setChequeSerialNo(rs.getString("Cheque_Serial_No"));
			fc.setCorporateUserNo(rs.getString("Corporate_User_No"));
			fc.setCorporateUserName(rs.getString("Corporate_User_Name"));
			fc.setDestAccHolder(rs.getString("Dest_Acc_Holder"));
			fc.setDebitCreditFlag(rs.getString("Debit_Credit_Flag"));
			fc.setProcessFlag(rs.getInt("Process_Flag"));
			fc.setThreadId(rs.getInt("Thread_Id"));
			fc.setUtrNumber(rs.getString("Utr_Number"));
			fc.setFateCorrection(rs.getString("FateCorrection"));
			fc.setErrorCode(rs.getString("ErrorCode"));
			fc.setErrorDesc(rs.getString("ErrorDesc"));
			fc.setProgress(rs.getInt("Progress"));
			fc.setStatus(rs.getString("Status"));

			return fc;
		}, headerID, "S");

	}

	@Override
	public void update(List<PresentmentRespUpload> detailsList) {
		StringBuilder sql = new StringBuilder("Update PRESENTMENT_RESP_UPLOAD set");
		sql.append(" FinID = ?, Presentment_Reference = ?, Progress = ?, Status = ?");
		sql.append(", ErrorCode = ?, ErrorDesc = ?, Clearing_Status = ?, Account_Number = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				PresentmentRespUpload detail = detailsList.get(i);

				ps.setLong(++index, detail.getReferenceID());
				ps.setString(++index, detail.getPresentmentReference());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, detail.getErrorCode());
				ps.setString(++index, detail.getErrorDesc());
				ps.setString(++index, detail.getClearingStatus());
				ps.setString(++index, detail.getAccountNumber());

				ps.setLong(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return detailsList.size();
			}
		});
	}

	@Override
	public long saveRespHeader(FileUploadHeader header) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Presentment_Resp_Header");
		sql.append(" (BATCH_NAME, ENTITY_CODE, EVENT, TOTAL_RECORDS, START_TIME, DE_EXECUTION_ID)");
		sql.append(" VALUES(?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		this.jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

				int index = 0;

				ps.setString(++index, header.getFileName());
				ps.setString(++index, header.getEntityCode());
				ps.setString(++index, "IMPORT");
				ps.setInt(++index, header.getTotalRecords());
				ps.setTimestamp(++index, new Timestamp(System.currentTimeMillis()));
				ps.setLong(++index, header.getId());

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public void saveRespDetails(long uploadID, long headerID) {
		StringBuilder sql = new StringBuilder("Insert Into PRESENTMENT_RESP_DTLS (Header_ID");
		sql.append(", FinId, FinReference, Presentment_Reference, Host_Reference, Instalment_No, Amount_Cleared");
		sql.append(", Clearing_Date, Clearing_Status, Bounce_Code, Bounce_Remarks, Reason_Code, Bank_Code");
		sql.append(", Bank_Name, Branch_Code, Branch_Name, Partner_Bank_Code, Partner_Bank_Name, Bank_Address");
		sql.append(", Account_Number, Ifsc_Code, Umrn_No, Micr_Code, Cheque_Serial_No, Corporate_User_No");
		sql.append(", Corporate_User_Name, Dest_Acc_Holder, Debit_Credit_Flag, Process_Flag, Thread_Id, Utr_Number");
		sql.append(", FateCorrection, Error_Code, Error_Description)");
		sql.append(" Select ?");
		sql.append(", FinId, FinReference, Presentment_Reference, Host_Reference, Instalment_No, Amount_Cleared");
		sql.append(", Clearing_Date, Clearing_Status, Bounce_Code, Bounce_Remarks, Reason_Code, Bank_Code");
		sql.append(", Bank_Name, Branch_Code, Branch_Name, Partner_Bank_Code, Partner_Bank_Name, Bank_Address");
		sql.append(", Account_Number, Ifsc_Code, Umrn_No, Micr_Code, Cheque_Serial_No, Corporate_User_No");
		sql.append(", Corporate_User_Name, Dest_Acc_Holder, Debit_Credit_Flag, Process_Flag, Thread_Id, Utr_Number");
		sql.append(", FateCorrection, ErrorCode, ErrorDesc");
		sql.append(" From PRESENTMENT_RESP_UPLOAD");
		sql.append(" Where HeaderID = ? and Progress = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), headerID, uploadID, EodConstants.PROGRESS_SUCCESS);
	}

	@Override
	public void updateProcessingFlag(long headerID) {
		String sql = "Update Presentment_Resp_Header Set Progress = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, 1, headerID);
	}

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc) {
		String sql = "Update PRESENTMENT_RESP_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIds.get(i);

				ps.setInt(++index, -1);
				ps.setString(++index, "R");
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
		sql.append(" ru.FinReference, ru.Presentment_Reference, ru.Amount_Cleared, ru.Clearing_Date");
		sql.append(", ru.Clearing_Status, ru.Umrn_No, ru.FateCorrection, ru.Status, ru.ErrorCode");
		sql.append(", ru.ErrorDesc, uh.CreatedBy, uh.ApprovedBy");
		sql.append(" From PRESENTMENT_RESP_UPLOAD ru");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = ru.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isProcessed(String reference, Date clearingDate) {
		StringBuilder sql = new StringBuilder("Select count(ID) From FILE_UPLOAD_HEADER Where Type = ? and Id IN (");
		sql.append("Select HeaderId From PRESENTMENT_RESP_UPLOAD ru");
		sql.append(" Inner Join PresentmentDetails pd on pd.PRESENTMENTID = ru.PRESENTMENTID");
		sql.append(" and pd.FateCorrection = ? Where ru.FinReference = ?");
		sql.append(" and ru.CLEARING_DATE = ? and ru.Progress = ?)");
		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, UploadTypes.FATE_CORRECTION.name(),
				"Y", reference, JdbcUtil.getDate(clearingDate), ReceiptDetailStatus.SUCCESS.getValue()) > 0;
	}

	@Override
	public PresentmentDetail getPresentmentDetail(String reference, Date clearingDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, pd.PresentmentRef, pd.Status, b.BranchSwiftBrnCde, ph.PresentmentType, PB.AccountNo");
		sql.append(" From PresentmentDetails pd");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = pd.FinID");
		sql.append(" Inner Join RMTBranches b on b.BranchCode = fm.FinBranch");
		sql.append(" Inner Join PresentmentHeader ph on ph.id = pd.PresentmentId");
		sql.append(" Inner Join PartnerBanks PB ON PB.PartnerBankID = PH.PartnerBankID");
		sql.append(" Where fm.FinReference = ? and pd.SchDate = ?");
		sql.append(" Order by PresentmentID desc");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<PresentmentDetail> list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, reference);
			ps.setDate(2, JdbcUtil.getDate(clearingDate));
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setFinID(rs.getLong("FinID"));
			pd.setPresentmentRef(rs.getString("PresentmentRef"));
			pd.setStatus(rs.getString("Status"));
			pd.setBranchCode(rs.getString("BranchSwiftBrnCde"));
			pd.setPresentmentType(rs.getString("PresentmentType"));
			pd.setAccountNo(rs.getString("AccountNo"));

			return pd;
		});

		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}

		return null;
	}

	@Override
	public boolean isDuplicateKeyPresent(String hostReference, String clearingStatus, Date dueDate) {

		Object[] obj = new Object[] { hostReference, clearingStatus, JdbcUtil.getDate(dueDate) };

		String sql = "select count(*) from PRESENTMENT_RESP_DTLS where FinReference = ? and clearing_status = ? and clearing_Date = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;

	}
}
