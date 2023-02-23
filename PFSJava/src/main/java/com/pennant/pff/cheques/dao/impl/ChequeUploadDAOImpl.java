package com.pennant.pff.cheques.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.pdc.upload.ChequeUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.cheques.dao.ChequeUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ChequeUploadDAOImpl extends SequenceDao<ChequeUpload> implements ChequeUploadDAO {

	@Override
	public List<ChequeUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, ChequeDetailsId");
		sql.append(", ChequeHeaderId, BankBranchId, FinID, FinReference, AccountNo, ChequeSerialNo, ChequeDate");
		sql.append(", EmiRefNo, Amount, ChequeCcy,Active, DocumentName, DocumentRef, ChequeType, ChequeStatus");
		sql.append(", AccountType, AccHolderName, Action, IfscCode, Micr, Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From CHEQUES_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID), (rs, Num) -> {
			ChequeUpload pdc = new ChequeUpload();

			pdc.setId(rs.getLong("ID"));
			pdc.setHeaderId(rs.getLong("HeaderId"));
			pdc.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			pdc.setReference(rs.getString("FinReference"));

			ChequeDetail cd = new ChequeDetail();

			cd.setChequeDetailsID(rs.getLong("ChequeDetailsId"));
			cd.setHeaderID(rs.getLong("ChequeHeaderId"));
			cd.setBankBranchID(rs.getLong("BankBranchId"));
			cd.setAccountNo(rs.getString("AccountNo"));
			cd.setChequeSerialNumber(rs.getString("ChequeSerialNo"));
			cd.setChequeDate(JdbcUtil.getDate(rs.getDate("ChequeDate")));
			cd.seteMIRefNo(rs.getInt("EmiRefNo"));
			cd.setAmount(rs.getBigDecimal("Amount"));
			cd.setChequeCcy(rs.getString("ChequeCcy"));
			cd.setActive(rs.getBoolean("Active"));
			cd.setDocumentName(rs.getString("DocumentName"));
			cd.setDocumentRef(JdbcUtil.getLong(rs.getObject("DocumentRef")));
			cd.setChequeType(rs.getString("ChequeType"));
			cd.setChequeStatus(rs.getString("ChequeStatus"));
			cd.setAccountType(rs.getString("AccountType"));
			cd.setAccHolderName(rs.getString("AccHolderName"));
			cd.setIfsc(rs.getString("IfscCode"));
			cd.setMicr(rs.getString("Micr"));

			pdc.setChequeDetail(cd);

			pdc.setAction(rs.getString("Action"));
			pdc.setProgress(rs.getInt("Progress"));
			pdc.setStatus(rs.getString("Status"));
			pdc.setErrorCode(rs.getString("ErrorCode"));
			pdc.setErrorDesc(rs.getString("ErrorDesc"));

			return pdc;
		});

	}

	@Override
	public void update(List<ChequeUpload> detailsList) {
		String sql = "Update CHEQUES_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ChequeUpload detail = detailsList.get(i);

				ps.setObject(++index, detail.getReferenceID());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, detail.getErrorCode());
				ps.setString(++index, detail.getErrorDesc());

				ps.setLong(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return detailsList.size();
			}
		});
	}

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update CHEQUES_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append(" cu.ACTION, cu.CHEQUETYPE, cu.FINREFERENCE");
		sql.append(", cu.CHEQUESERIALNO, cu.ACCOUNTTYPE, cu.ACCHOLDERNAME, cu.ACCOUNTNO");
		sql.append(",cu.IFSCCODE, cu.MICR, cu.AMOUNT,cu.ID, cu.CHEQUEDETAILSID");
		sql.append(", uh.APPROVEDON, uh.CREATEDON, cu.STATUS, cu.ERRORCODE, cu.ERRORDESC");
		sql.append(" ,uh.CREATEDBY,uh.APPROVEDBY,cu.CHEQUEDATE");
		sql.append(" From CHEQUES_UPLOAD cu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = cu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

}
