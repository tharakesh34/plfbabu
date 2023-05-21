package com.pennant.pff.letter.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.pff.letter.LetterType;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class AutoLetterGenerationDAOImpl extends SequenceDao<GenerateLetter> implements AutoLetterGenerationDAO {

	@Override
	public void save(GenerateLetter gl) {
		StringBuilder sql = new StringBuilder("Insert Into Letter_Generation_Stage");
		sql.append("(FinID, RequestType, LetterType, FeeTypeId");
		sql.append(", CreatedDate, CreatedOn, AgreementTemplate, ModeOfTransfer)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, gl.getFinID());
				ps.setString(++index, gl.getRequestType());
				ps.setString(++index, gl.getLetterType());
				ps.setLong(++index, gl.getFeeId());
				ps.setDate(++index, JdbcUtil.getDate(gl.getCreatedDate()));
				ps.setDate(++index, JdbcUtil.getDate(gl.getCreatedOn()));
				ps.setLong(++index, gl.getAgreementTemplate());
				ps.setString(++index, gl.getModeofTransfer());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public GenerateLetter getLetter(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, RequestType, LetterType");
		sql.append(", AgreementTemplate, FeeTypeID");
		sql.append(", ModeofTransfer, EmailTemplate");
		sql.append(", CreatedDate");
		sql.append(" From Letter_Generation_Stage");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				GenerateLetter generateLetter = new GenerateLetter();

				generateLetter.setId(rs.getLong("Id"));
				generateLetter.setFinID(rs.getLong("FinID"));
				generateLetter.setRequestType(rs.getString("RequestType"));
				generateLetter.setLetterType(rs.getString("LetterType"));
				generateLetter.setAgreementTemplate(rs.getLong("AgreementTemplate"));
				generateLetter.setFeeId(rs.getLong("FeeTypeID"));
				generateLetter.setModeofTransfer(rs.getString("ModeofTransfer"));
				generateLetter.setEmailTemplate(rs.getLong("EmailTemplate"));
				generateLetter.setCreatedDate(rs.getDate("CreatedDate"));

				return generateLetter;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public String getCSDCode(String finType, String finBranch) {
		StringBuilder sql = new StringBuilder("Select sb.Code");
		sql.append(" From Service_Branches sb");
		sql.append(" Inner Join Service_Branches_Loantype sbl on sbl.HeaderID = sb.ID");
		sql.append(" Where sb.FinType = ? and sb.Branch = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getString(1), finType, finBranch);

	}

	@Override
	public int getNextSequence(long finID, LetterType letterType) {
		String sql = "Select count(FinID)+1 from Letter_Generation Where FinID = ? and LetterType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), finID, letterType.name());
	}

	@Override
	public EventProperties getEventProperties(String configName) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" dep.Storage_Type, dep.Region_Name, dep.Bucket_Name, dep.Access_Key, dep.Secret_Key");
		sql.append(", dep.Prefix, dep.Sse_Algorithm, dep.Host_Name, dep.Port, dep.Private_Key");
		sql.append(" From Data_Engine_Event_Properties dep");
		sql.append(" Inner Join Data_Engine_Config dc on dc.ID = dep.Config_ID");
		sql.append(" Where dc.Name = ? and dep.Storage_Type = ?");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				EventProperties ep = new EventProperties();

				ep.setStorageType(rs.getString("Storage_Type"));
				ep.setRegionName(rs.getString("Region_Name"));
				ep.setBucketName(rs.getString("Bucket_Name"));
				ep.setAccessKey(rs.getString("Access_Key"));
				ep.setSecretKey(rs.getString("Secret_Key"));
				ep.setPrefix(rs.getString("Prefix"));
				ep.setSseAlgorithm(rs.getString("Sse_Algorithm"));
				ep.setHostName(rs.getString("Host_Name"));
				ep.setPort(rs.getString("Port"));
				ep.setPrivateKey(rs.getString("Private_Key"));

				return ep;
			}, configName);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
