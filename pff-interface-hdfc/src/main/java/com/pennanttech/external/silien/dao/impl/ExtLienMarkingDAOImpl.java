package com.pennanttech.external.silien.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class ExtLienMarkingDAOImpl extends SequenceDao<PresentmentDetail> implements ExtLienMarkingDAO {
	private static final Logger logger = LogManager.getLogger(ExtLienMarkingDAOImpl.class);

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public void processAllLoansWithSIAndSave(String lienMark, int isActive) {
		logger.debug(Literal.ENTERING);
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		StringBuilder query = new StringBuilder();
		query.append(" INSERT INTO LOAN_SILIEN(FINID,CUSTID,ACCNUMBER,LIEN_MARK,STATUS,CREATED_DATE) ");
		query.append(" SELECT FM.FINID,FM.CUSTID,MD.ACCNUMBER,?,?,? FROM FINANCEMAIN FM ");
		query.append(" INNER JOIN MANDATES MD ON MD.MANDATEID = FM.MANDATEID WHERE MD.MANDATETYPE = 'SI'");
		query.append(" AND FM.FINID NOT IN (SELECT FINID FROM LOAN_SILIEN)");

		String queryStr = query.toString();
		logger.debug(Literal.SQL + queryStr);

		extNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setString(index++, lienMark);
			ps.setLong(index++, isActive);
			ps.setTimestamp(index, curTimeStamp);
		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateLienMarkRecord(LienMarkDetail lienDetail) {
		StringBuilder sql = new StringBuilder("UPDATE LOAN_SILIEN");
		sql.append("SET LIEN_MARK = ? WHERE FINID=? AND CUSTID=? AND ACCNUMBER = ? AND STATUS=?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, lienDetail.getLienMark());
			ps.setLong(index++, lienDetail.getFinId());
			ps.setLong(index++, lienDetail.getCustId());
			ps.setString(index++, lienDetail.getAccNumber());
			ps.setInt(index, lienDetail.getStatus());
		});
	}

	@Override
	public List<LienMarkDetail> fetchUnprocessedLienMarkingRecords(int status) {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<LienMarkDetail> unprocessedLienDetailsList = new ArrayList<LienMarkDetail>();

		queryStr = fetchUnprocessedLienMarkQuery();
		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
			ps.setInt(1, status);
		}, rs -> {
			LienMarkDetail lienDetails = new LienMarkDetail();
			lienDetails.setAccNumber(rs.getString("ACCNUMBER"));
			lienDetails.setCustId(rs.getLong("CUSTID"));
			lienDetails.setFinId(rs.getLong("FINID"));
			lienDetails.setLienMark(rs.getString("LIEN_MARK"));
			lienDetails.setStatus(rs.getInt("STATUS"));
			unprocessedLienDetailsList.add(lienDetails);
		});
		logger.debug(Literal.LEAVING);
		return unprocessedLienDetailsList;
	}

	private String fetchUnprocessedLienMarkQuery() {
		StringBuilder query = new StringBuilder();
		query.append(" Select * from LOAN_SILIEN Where STATUS = ?");
		return query.toString();
	}

	@Override
	public List<LienMarkDetail> fetchRecordsForLienFileWriting(int status) {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<LienMarkDetail> unprocessedLienDetailsList = new ArrayList<LienMarkDetail>();

		queryStr = " Select * from SILIEN_STATUS  WHERE FILE_STATUS = ?";
		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
			ps.setInt(1, status);
		}, rs -> {
			LienMarkDetail lienDetails = new LienMarkDetail();
			lienDetails.setAccNumber(rs.getString("ACCNUMBER"));
			lienDetails.setLienMark(rs.getString("LIEN_MARK"));
			unprocessedLienDetailsList.add(lienDetails);
		});
		logger.debug(Literal.LEAVING);
		return unprocessedLienDetailsList;
	}

	@Override
	public String fetchRepaymentMode(LienMarkDetail detail) {
		StringBuilder query = new StringBuilder();

		query.append(" SELECT FINREPAYMETHOD FROM FINANCEMAIN FM ");
		query.append(" WHERE FM.FINID = ? AND FM.CUSTID = ?");

		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(query.toString(), String.class,
					detail.getFinId(), detail.getCustId());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public long verifyLoanWithLienMarking(String accNumber, String lienStatus) {
		String query = " SELECT COUNT(DISTINCT ACCNUMBER)  FROM LOAN_SILIEN WHERE LIEN_MARK = ? AND ACCNUMBER= ?";
		logger.debug(Literal.SQL + query);

		return this.extNamedJdbcTemplate.getJdbcOperations().queryForObject(query, Long.class, lienStatus, accNumber);
	}

	@Override
	public String getRecordStatus(String accNumber) {
		String sql = "Select LIEN_MARK from SILIEN_STATUS Where ACCNUMBER= ?";

		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, String.class, accNumber);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public void insertLienMarkStatusRecord(String accNumber, String lienMark, int fileStatus) {
		logger.debug(Literal.ENTERING);
		String queryStr = "INSERT INTO SILIEN_STATUS (ACCNUMBER,LIEN_MARK,FILE_STATUS) VALUES(?,?,?)";

		extNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setString(index++, accNumber);
			ps.setString(index++, lienMark);
			ps.setLong(index, fileStatus);
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void insertOrUpdateLienMarkStatusRecord(String accNumber, String lienMark, int fileStatus) {
		logger.debug(Literal.ENTERING);
		StringBuilder query = new StringBuilder();
		query.append(" MERGE INTO SILIEN_STATUS lms ");
		query.append(" USING (SELECT ? ACCNUMBER from dual) sq ");
		query.append(" ON (lms.ACCNUMBER = sq.ACCNUMBER) ");
		query.append(" WHEN MATCHED THEN UPDATE SET lms.LIEN_MARK = ?, FILE_STATUS = ? ");
		query.append(" WHEN NOT MATCHED THEN INSERT (ACCNUMBER, LIEN_MARK, FILE_STATUS) VALUES (sq.ACCNUMBER, ?,?) ");

		extNamedJdbcTemplate.getJdbcOperations().update(query.toString(), ps -> {
			int index = 1;
			ps.setString(index++, accNumber);
			ps.setString(index++, lienMark);
			ps.setLong(index++, fileStatus);
			ps.setString(index++, lienMark);
			ps.setLong(index, fileStatus);
		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateLienRecordStatus(String accNumber, int fileStatus, String errorCode, String errorMessage) {
		logger.debug(Literal.ENTERING);
		String queryStr = "UPDATE SILIEN_STATUS SET FILE_STATUS = ?, ERROR_CODE = ?, ERROR_MESSAGE = ? WHERE ACCNUMBER= ?";
		logger.debug(Literal.SQL + queryStr);
		extNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, fileStatus);
			ps.setString(index++, errorCode);
			ps.setString(index++, errorMessage);
			ps.setString(index, accNumber);
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void updateLienInterfaceStatus(LienMarkDetail lienMarkDetail) {
		logger.debug(Literal.ENTERING);
		String queryStr = "UPDATE SILIEN_STATUS SET LIEN_MARK=?, INTERFACE_STATUS = ?, INTERFACE_REASON = ? WHERE ACCNUMBER= ?";
		logger.debug(Literal.SQL + queryStr);
		extNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setString(index++, lienMarkDetail.getLienMark());
			ps.setString(index++, lienMarkDetail.getInterfaceStatus());
			ps.setString(index++, lienMarkDetail.getInterfaceReason());
			ps.setString(index, lienMarkDetail.getAccNumber());
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean isFileProcessed(String fileName) {
		String sql = "Select count(1) from SILIEN_FILE_STATUS Where FILE_NAME= ?";
		logger.debug(Literal.SQL + sql);
		try {
			return this.extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void insertSILienResponseFileStatus(String fileName, int status) {
		String sql = "INSERT INTO SILIEN_FILE_STATUS (FILE_NAME,STATUS,CREATED_DATE) VALUES(?,?,?)";
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		logger.debug(Literal.SQL + sql);
		try {
			extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
				int index = 1;
				ps.setString(index++, fileName);
				ps.setInt(index++, status);
				ps.setTimestamp(index, curTimeStamp);
			});
		} catch (Exception e) {

		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateLienResponseFileStatus(long id, int status, String errorCode, String errorMessage) {
		String sql = "UPDATE SILIEN_FILE_STATUS SET STATUS = ?, ERROR_CODE=?, ERROR_MESSAGE=? Where ID= ? ";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setString(index++, errorCode);
			ps.setString(index++, errorMessage);
			ps.setLong(index, id);

		});
		logger.debug(Literal.LEAVING);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}
}
