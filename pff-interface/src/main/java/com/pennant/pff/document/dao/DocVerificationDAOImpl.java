package com.pennant.pff.document.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.pff.document.model.DocVerificationAddress;
import com.pennant.pff.document.model.DocVerificationDetail;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class DocVerificationDAOImpl extends BasicDao<DocVerificationHeader> implements DocVerificationDAO {

	@Override
	public long saveHeader(DocVerificationHeader header) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into DOC_VERIFICATION_HEADER");
		sql.append(" (CustCif, DocType, DocNumber, Verified, VerifiedOn");
		sql.append(", DocReference, ClientId, DocRequest, DocResponse, Status)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				int index = 0;

				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

				ps.setString(++index, header.getCustCif());
				ps.setString(++index, header.getDocType());
				ps.setString(++index, header.getDocNumber());
				ps.setBoolean(++index, header.isVerified());
				ps.setTimestamp(++index, header.getVerifiedOn());
				ps.setString(++index, header.getDocReference());
				ps.setString(++index, header.getClientId());
				ps.setString(++index, header.getDocRequest());
				ps.setString(++index, header.getDocResponse());
				ps.setString(++index, header.getStatus());

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return Long.MIN_VALUE;
		}

		return key.longValue();
	}

	@Override
	public void saveDetail(DocVerificationDetail detail) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into DOC_VERIFICATION_DETAILS");
		sql.append(" (Headerid, FullName, FatherOrHusbandName, Gender, DOB");
		sql.append(", Age, PanNumber, AadhaarNumber)");
		sql.append(" Values (");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, detail.getHeaderId());
			ps.setString(++index, detail.getFullName());
			ps.setString(++index, detail.getFatherOrHusbandName());
			ps.setString(++index, detail.getGender());
			ps.setString(++index, detail.getDob());
			ps.setInt(++index, detail.getAge());
			ps.setString(++index, detail.getPanNumber());
			ps.setString(++index, detail.getAadhaarNumber());
		});
	}

	@Override
	public void saveAddress(DocVerificationAddress address) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into DOC_VERIFICATION_ADDRESS");
		sql.append(" (HeaderId, HouseNo, Street, Ditrict, State");
		sql.append(", Country, ZIP)");
		sql.append(" Values (");
		sql.append("?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, address.getHeaderId());
			ps.setString(++index, address.getHouseNo());
			ps.setString(++index, address.getStreet());
			ps.setString(++index, address.getDistrict());
			ps.setString(++index, address.getState());
			ps.setString(++index, address.getCountry());
			ps.setString(++index, address.getZip());
		});

	}

	@Override
	public boolean isVerified(String docNumber, DocType docType) {
		String type = "";
		switch (docType) {
		case PAN:
			type = "PAN";
			break;
		case AADHAAR:
			type = "AADHAAR";
			break;
		case CIBIL:
			type = "CIBIL";
			break;
		default:
			break;
		}

		String sql = "Select Count(DocNumber) From DOC_VERIFICATION_HEADER Where DocNumber = ? and DocType= ? and Verified = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, docNumber, type, 1) > 0;
	}

	@Override
	public DocVerificationDetail getDetail(String docNumber) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DD.Id, DD.Headerid, DD.FullName, DD.FatherOrHusbandName, DD.Gender");
		sql.append(", DD.DOB, DD.Age, DD.PanNumber, DD.AadhaarNumber");
		sql.append(" From DOC_VERIFICATION_DETAILS DD");
		sql.append(" Inner Join DOC_VERIFICATION_HEADER DH On DD.Headerid = DH.Id");
		sql.append(" Where DH.DocNumber = ? and Verified = ?");

		switch (App.DATABASE) {
		case ORACLE:
			sql.append(" And rownum = 1");
			break;
		case POSTGRES:
			sql.append(" Limit 1");
			break;
		default:
			sql.append(" FETCH FIRST 1 ROWS ONLY");
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DocVerificationDetail dd = new DocVerificationDetail();

				dd.setId(rs.getLong("Id"));
				dd.setHeaderId(rs.getLong("Headerid"));
				dd.setFullName(rs.getString("FullName"));
				dd.setFatherOrHusbandName(rs.getString("FatherOrHusbandName"));
				dd.setGender(rs.getString("Gender"));
				dd.setDob(rs.getString("DOB"));
				dd.setAge(rs.getInt("Age"));
				dd.setPanNumber(rs.getString("PanNumber"));
				dd.setAadhaarNumber(rs.getString("AadhaarNumber"));

				return dd;
			}, docNumber, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public DocVerificationHeader getHeader(String docNumber, String docType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CustCif, DocType, DocNumber, Verified, VerifiedOn");
		sql.append(", DocReference, ClientId, DocRequest, DocResponse, Status");
		sql.append(" From DOC_VERIFICATION_HEADER");
		sql.append(" Where DocNumber = ? and DocType = ? and Verified = ?");

		switch (App.DATABASE) {
		case ORACLE:
			sql.append(" And rownum = 1");
			break;
		case POSTGRES:
			sql.append(" Limit 1");
			break;
		default:
			sql.append(" FETCH FIRST 1 ROWS ONLY");
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DocVerificationHeader dh = new DocVerificationHeader();

				dh.setId(rs.getLong("Id"));
				dh.setCustCif(rs.getString("CustCif"));
				dh.setDocType(rs.getString("DocType"));
				dh.setDocNumber(rs.getString("DocNumber"));
				dh.setVerified(rs.getBoolean("Verified"));
				dh.setVerifiedOn(rs.getTimestamp("VerifiedOn"));
				dh.setDocReference(rs.getString("DocReference"));
				dh.setClientId(rs.getString("ClientId"));
				dh.setDocRequest(rs.getString("DocRequest"));
				dh.setDocResponse(rs.getString("DocResponse"));
				dh.setStatus(rs.getString("Status"));

				return dh;
			}, docNumber, docType, 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateHeader(DocVerificationHeader header) {
		StringBuilder sql = new StringBuilder("Update DOC_VERIFICATION_HEADER Set");
		sql.append(" CustCif = ?, DocType = ?, DocNumber = ?, Verified = ?, VerifiedOn = ?");
		sql.append(", DocReference = ?, ClientId = ?, DocRequest = ?, DocResponse = ?, Status = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, header.getCustCif());
			ps.setString(++index, header.getDocType());
			ps.setString(++index, header.getDocNumber());
			ps.setBoolean(++index, header.isVerified());
			ps.setTimestamp(++index, header.getVerifiedOn());
			ps.setString(++index, header.getDocReference());
			ps.setString(++index, header.getClientId());
			ps.setString(++index, header.getDocRequest());
			ps.setString(++index, header.getDocResponse());
			ps.setString(++index, header.getStatus());

			ps.setLong(++index, header.getId());
		});
	}

	@Override
	public void updateDetail(DocVerificationDetail details) {
		StringBuilder sql = new StringBuilder("Update DOC_VERIFICATION_DETAILS Set");
		sql.append(" FullName = ?, FatherOrHusbandName = ?, Gender = ?, DOB = ?");
		sql.append(", Age = ?, PanNumber = ?, AadhaarNumber = ?");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, details.getFullName());
			ps.setString(++index, details.getFatherOrHusbandName());
			ps.setString(++index, details.getGender());
			ps.setString(++index, details.getDob());
			ps.setInt(++index, details.getAge());
			ps.setString(++index, details.getPanNumber());
			ps.setString(++index, details.getAadhaarNumber());

			ps.setLong(++index, details.getHeaderId());
		});
	}

	@Override
	public void updateAddress(DocVerificationAddress address) {
		StringBuilder sql = new StringBuilder("Update DOC_VERIFICATION_ADDRESS Set");
		sql.append(" HouseNo = ?, Street = ?, Ditrict = ?, State = ?");
		sql.append(", Country = ?, ZIP = ?");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, address.getHouseNo());
			ps.setString(++index, address.getStreet());
			ps.setString(++index, address.getDistrict());
			ps.setString(++index, address.getState());
			ps.setString(++index, address.getCountry());
			ps.setString(++index, address.getZip());

			ps.setLong(++index, address.getHeaderId());
		});
	}
}
