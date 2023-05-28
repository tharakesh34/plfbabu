package com.pennanttech.external.silien.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtLienMarkingDAOImpl extends SequenceDao implements ExtLienMarkingDAO {
	private static final Logger logger = LogManager.getLogger(ExtLienMarkingDAOImpl.class);

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public List<LienMarkDetail> fetchRecordsForLienFileWriting(String status) {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<LienMarkDetail> unprocessedLienDetailsList = new ArrayList<LienMarkDetail>();

		queryStr = " Select * from LIEN_HEADER  WHERE INTERFACESTATUS = ?";
		mainNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
			ps.setString(1, status);
		}, rs -> {
			LienMarkDetail lienDetails = new LienMarkDetail();
			lienDetails.setAccNumber(rs.getString("ACCNUMBER"));
			lienDetails.setLienMark((rs.getInt("LIENSTATUS") == 0 ? "N" : "Y"));
			unprocessedLienDetailsList.add(lienDetails);
		});
		logger.debug(Literal.LEAVING);
		return unprocessedLienDetailsList;
	}

	@Override
	public void updateLienRecordStatus(LienMarkDetail lienMarkDetail) {
		logger.debug(Literal.ENTERING);
		String queryStr = "UPDATE LIEN_HEADER SET INTERFACESTATUS = ?, InterfaceRemarks = ? WHERE ACCNUMBER= ?";
		logger.debug(Literal.SQL + queryStr);
		mainNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
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

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

}
