package com.pennant.pff.letter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class AutoLetterGenerationDAOImpl extends SequenceDao<Presentment> implements AutoLetterGenerationDAO {

	@Override
	public void updateEndTimeStatus(BatchJobQueue jobQueue) {
		String sql = "Update BATCH_JOBS Set End_Time = ?, Status = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, jobQueue.getBatchStatus());
			ps.setLong(3, jobQueue.getBatchId());
		});
	}

	@Override
	public long createBatch(String batchType, int totalRecords) {
		String sql = "Insert into BATCH_JOBS (Batch_Type, Start_Time, Total_Records) values (?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });

				ps.setString(1, batchType);
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps.setInt(3, totalRecords);

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
	public GenerateLetter getLetter(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, RequestType, LetterType");
		sql.append(", AgreementTemplate, FeeTypeID");
		sql.append(", ModeofTransfer, EmailTemplate, Adviseid");
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
				generateLetter.setAdviseID(rs.getLong("Adviseid"));

				return generateLetter;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public void updateBatch(long batchID, String errMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> getResponseHeadersByBatch(Long batchId, String responseType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public int getRecordsByWaiting(String clearingStatus) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(ID) From Letter_Generation_Stage prh");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, 1, "IMPORT", 0, clearingStatus);
	}

	@Override
	public int updateRespProcessFlag(long batchID, int i, String string) {
		return 0;
	}

	@Override
	public int getLetterGenerationCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(*) from Letter_Generation_Stage");
		sql.append(" Where Generated <> ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), 1);
	}

}
