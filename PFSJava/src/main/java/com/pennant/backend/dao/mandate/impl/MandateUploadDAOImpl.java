package com.pennant.backend.dao.mandate.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.mandate.MandateUploadDAO;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class MandateUploadDAOImpl extends SequenceDao<MandateUpload> implements MandateUploadDAO {

	@Override
	public List<MandateUpload> loadRecordData(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderID");
		sql.append(", MandateID, RecordSeq, CustID, CustCIF, MandateRef, MandateType, BankBranchID, AccNumber");
		sql.append(", AccHolderName, JointAccHolderName, AccType, OpenMandate, StartDate, ExpiryDate, MaxLimit");
		sql.append(", Periodicity, PhoneCountryCode, PhoneAreaCode, PhoneNumber, MandateStatus, ApprovalID, InputDate");
		sql.append(", Active, Reason, MandateCcy, OrgReference, ExternalRef, DocumentName, DocumentRef, BarCodeNumber");
		sql.append(", SwapIsActive, PrimaryMandateID, EntityCode, PartnerBankID, DefaultMandate, EmandateSource");
		sql.append(", EmandateReferenceNo, SwapEffectiveDate, HoldReason, SecurityMandate, EmployerID, EmployeeNo");
		sql.append(", Ifsc, Micr, ExternalMandate, Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Mandates_Upload");
		sql.append(" Where HeaderID = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			MandateUpload upload = new MandateUpload();

			upload.setId(rs.getLong("ID"));
			upload.setHeaderId(rs.getLong("HeaderID"));
			upload.setReferenceID(JdbcUtil.getLong(rs.getObject("MandateID")));
			upload.setRecordSeq(rs.getLong("RecordSeq"));
			upload.setReference(rs.getString("OrgReference"));
			upload.setProgress(rs.getInt("Progress"));
			upload.setStatus(rs.getString("Status"));
			upload.setErrorCode(rs.getString("ErrorCode"));
			upload.setErrorDesc(rs.getString("ErrorDesc"));

			Mandate mndts = new Mandate();
			mndts.setCustID(rs.getLong("CustID"));
			mndts.setCustCIF(rs.getString("CustCIF"));
			mndts.setMandateRef(rs.getString("MandateRef"));
			mndts.setMandateType(rs.getString("MandateType"));
			mndts.setBankBranchID(JdbcUtil.getLong(rs.getObject("BankBranchID")));
			mndts.setAccNumber(rs.getString("AccNumber"));
			mndts.setAccHolderName(rs.getString("AccHolderName"));
			mndts.setJointAccHolderName(rs.getString("JointAccHolderName"));
			mndts.setAccType(rs.getString("AccType"));
			mndts.setStrOpenMandate(rs.getString("OpenMandate"));
			mndts.setStartDate(rs.getTimestamp("StartDate"));
			mndts.setExpiryDate(rs.getTimestamp("ExpiryDate"));
			mndts.setMaxLimit(rs.getBigDecimal("MaxLimit"));
			mndts.setPeriodicity(rs.getString("Periodicity"));
			mndts.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
			mndts.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
			mndts.setPhoneNumber(rs.getString("PhoneNumber"));
			mndts.setStatus(rs.getString("MandateStatus"));
			mndts.setApprovalID(rs.getString("ApprovalID"));
			mndts.setInputDate(rs.getTimestamp("InputDate"));
			mndts.setActive(rs.getBoolean("Active"));
			mndts.setReason(rs.getString("Reason"));
			mndts.setMandateCcy(rs.getString("MandateCcy"));
			mndts.setOrgReference(rs.getString("OrgReference"));
			mndts.setFinReference(rs.getString("OrgReference"));
			mndts.setExternalRef(rs.getString("ExternalRef"));
			mndts.setDocumentName(rs.getString("DocumentName"));
			mndts.setDocumentRef(JdbcUtil.getLong(rs.getObject("DocumentRef")));
			mndts.setBarCodeNumber(rs.getString("BarCodeNumber"));
			mndts.setStrSwapIsActive(rs.getString("SwapIsActive"));
			mndts.setPrimaryMandateId(rs.getLong("PrimaryMandateId"));
			mndts.setEntityCode(rs.getString("EntityCode"));
			mndts.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			mndts.setDefaultMandate(rs.getBoolean("DefaultMandate"));
			mndts.seteMandateSource(rs.getString("EMandateSource"));
			mndts.seteMandateReferenceNo(rs.getString("EMandateReferenceNo"));
			mndts.setSwapEffectiveDate(rs.getTimestamp("SwapEffectiveDate"));
			mndts.setHoldReason(rs.getString("HoldReason"));
			mndts.setStrSecurityMandate(rs.getString("SecurityMandate"));
			mndts.setEmployerID(JdbcUtil.getLong(rs.getObject("EmployerID")));
			mndts.setEmployeeNo(rs.getString("EmployeeNo"));
			mndts.setIFSC(rs.getString("Ifsc"));
			mndts.setMICR(rs.getString("Micr"));
			mndts.setStrExternalMandate(rs.getString("ExternalMandate"));

			upload.setMandate(mndts);

			return upload;
		}, headerID, "S");
	}

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update Mandates_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
	public void update(List<MandateUpload> details) {
		String sql = "Update Mandates_Upload set MandateID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				MandateUpload detail = details.get(i);

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
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" mu.EntityCode, mu.CustCIF, mu.OrgReference, mu.MandateType, mu.Micr, mu.Ifsc, mu.AccNumber");
		sql.append(", mu.AccHolderName, mu.JointAccHolderName, mu.AccType, mu.MaxLimit, mu.Periodicity");
		sql.append(", mu.OpenMandate, mu.StartDate, mu.ExpiryDate, mu.PartnerBankID, mu.MandateRef");
		sql.append(", mu.ExternalMandate, mu.SwapIsActive, mu.SwapEffectiveDate, mu.EmandateSource");
		sql.append(", mu.EmandateReferenceNo, mu.EmployerID, mu.EmployeeNo, mu.MandateStatus, mu.Reason");
		sql.append(", mu.SecurityMandate, uh.CreatedOn, uh.ApprovedOn, mu.Status, mu.ErrorCode, mu.ErrorDesc");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append(" From Mandates_Upload mu");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = mu.HeaderID");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

}
