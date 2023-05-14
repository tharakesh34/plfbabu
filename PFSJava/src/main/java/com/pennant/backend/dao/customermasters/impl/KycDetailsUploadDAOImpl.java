package com.pennant.backend.dao.customermasters.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.customermasters.KycDetailsUploadDAO;
import com.pennant.backend.model.bulkAddressUpload.CustomerKycDetail;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadContants.Status;

public class KycDetailsUploadDAOImpl extends SequenceDao<CustomerKycDetail> implements KycDetailsUploadDAO {

	public KycDetailsUploadDAOImpl() {
		super();
	}

	@Override
	public List<CustomerKycDetail> loadRecordData(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, RecordSeq, CustId, CustCif, FinID, FinReference");
		sql.append(", CustAddrType, CustAddrPriority, CustAddrLine3, CustAddrHNbr, CustFlatNbr, CustAddrStreet");
		sql.append(", CustAddrLine1, CustAddrLine2, CustAddrCity, CustAddrLine4, CustDistrict, CustAddrProvince");
		sql.append(", CustAddrCountry, CustAddrZIP, PhoneTypeCode, PhoneTypePriority");
		sql.append(", PhoneNumber, CustEMailTypeCode, CustEMailPriority, CustEMail");
		sql.append(", Status, Progress, ErrorCode, ErrorDesc");
		sql.append(" From Customer_kyc_details_upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CustomerKycDetail ckc = new CustomerKycDetail();

			ckc.setId(rs.getLong("Id"));
			ckc.setHeaderId(rs.getLong("HeaderId"));
			ckc.setRecordSeq(rs.getLong("RecordSeq"));
			ckc.setReferenceID(JdbcUtil.getLong(rs.getObject("CustId")));
			ckc.setReference(rs.getString("CustCif"));
			ckc.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
			ckc.setFinReference(rs.getString("FinReference"));
			ckc.setCustAddrType(rs.getString("CustAddrType"));
			ckc.setCustAddrPriority(rs.getInt("CustAddrPriority"));
			ckc.setCustAddrLine3(rs.getString("CustAddrLine3"));
			ckc.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
			ckc.setCustFlatNbr(rs.getString("CustFlatNbr"));
			ckc.setCustAddrStreet(rs.getString("CustAddrStreet"));
			ckc.setCustAddrLine1(rs.getString("CustAddrLine1"));
			ckc.setCustAddrLine2(rs.getString("CustAddrLine2"));
			ckc.setCustAddrCity(rs.getString("CustAddrCity"));
			ckc.setCustAddrLine4(rs.getString("CustAddrLine4"));
			ckc.setCustDistrict(rs.getString("CustDistrict"));
			ckc.setCustAddrProvince(rs.getString("CustAddrProvince"));
			ckc.setCustAddrCountry(rs.getString("custAddrCountry"));
			ckc.setCustAddrZIP(rs.getString("custAddrZIP"));
			ckc.setPhoneTypeCode(rs.getString("phoneTypeCode"));
			ckc.setPhoneTypePriority(rs.getInt("phoneTypePriority"));
			ckc.setPhoneNumber(rs.getString("phoneNumber"));
			ckc.setCustEMailTypeCode(rs.getString("custEMailTypeCode"));
			ckc.setCustEMailPriority(rs.getInt("CustEMailPriority"));
			ckc.setCustEMail(rs.getString("CustEMail"));
			ckc.setStatus(rs.getString("Status"));
			ckc.setProgress(rs.getInt("Progress"));
			ckc.setErrorCode(rs.getString("ErrorCode"));
			ckc.setErrorDesc(rs.getString("ErrorDesc"));

			return ckc;
		}, headerID, "S");
	}

	@Override
	public void update(List<CustomerKycDetail> details) {
		String sql = "Update Customer_Kyc_Details_Upload set  CustId = ?, FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				CustomerKycDetail detail = details.get(i);

				ps.setObject(++index, detail.getReferenceID());
				ps.setObject(++index, detail.getFinID());
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
	public void update(List<Long> headerIds, String errorCode, String errorDesc) {
		String sql = "Update Customer_Kyc_Details_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
	public void update(CustomerKycDetail detail) {
		String sql = "Update Customer_Kyc_Details_Upload Set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ?  Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, ps -> {
			int index = 0;

			ps.setInt(++index, EodConstants.PROGRESS_FAILED);
			ps.setString(++index, "R");
			ps.setString(++index, detail.getErrorCode());
			ps.setString(++index, detail.getErrorDesc());

			ps.setLong(++index, detail.getId());
		});
	}

	@Override
	public boolean isInProgress(long headerID, String custCif) {
		StringBuilder sql = new StringBuilder("Select Count(custCif)");
		sql.append(" From Customer_Kyc_Details_Upload bcd");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = bcd.HeaderID");
		sql.append(" Where bcd.CustCif = ? and uh.Id <> ? and uh.progress in (?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, custCif, headerID,
				Status.DOWNLOADED.getValue(), Status.IMPORT_IN_PROCESS.getValue(), Status.IMPORTED.getValue(),
				Status.IN_PROCESS.getValue()) > 0;
	}

	@Override
	public boolean isInMaintanance(String custCif) {
		String sql = "Select Count(custCif) From Customers_Temp where CustCif = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, custCif) > 0;
	}

	@Override
	public boolean isInLoanQueue(long custId) {
		String sql = "Select Count(FinID) From Financemain_Temp where CustId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, custId) > 0;
	}

	@Override
	public List<String> getReceiptQueueList(long custId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Reference From FinReceiptHeader_Temp fr");
		sql.append(" Inner Join FinanceMain fm on fm.Finreference = fr.Reference");
		sql.append(" Where fm.CustId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> rs.getString(1), custId);
	}

	@Override
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" bcd.CustCif, bcd.FinReference, bcd.CustAddrType, bcd.CustAddrPriority");
		sql.append(", bcd.CustAddrLine3, bcd.CustAddrHnbr, bcd.CustFlatNbr, bcd.CustAddrStreet, bcd.CustAddrLine1");
		sql.append(", bcd.CustAddrLine2, bcd.CustAddrCity, bcd.CustAddrLine4, bcd.CustDistrict, bcd.CustAddrProvince");
		sql.append(", bcd.CustAddrCountry, bcd.CustAddrZip, bcd.PhoneTypeCode, bcd.PhoneNumber, bcd.PhoneTypePriority");
		sql.append(", bcd.CustEmailTypeCode, bcd.CustEmail, bcd.CustEmailPriority, bcd.Status, bcd.progress");
		sql.append(", bcd.ErrorCode, bcd.ErrorDesc, su1.UsrLogin CreatedName");
		sql.append(", uh.CreatedOn, su2.UsrLogin ApprovedName, uh.ApprovedOn");
		sql.append(" From Customer_Kyc_Details_Upload bcd");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = bcd.HeaderID");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}
}
