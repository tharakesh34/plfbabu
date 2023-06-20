package com.pennanttech.external.collectionreceipt.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExtCollectionReceiptDaoImpl extends SequenceDao<Object> implements ExtCollectionReceiptDao {

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	public ExtCollectionReceiptDaoImpl() {
		super();
	}

	@Override
	public void saveFile(CollReceiptHeader colletionFile) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		String sql = "INSERT INTO COLL_RECEIPT_HEADER (REQ_FILE_NAME,REQ_FILE_LOCATION,EXTRACTION,"
				+ "STATUS,WRITE_RESPONSE,RESP_FILE_STATUS,CREATED_DATE,ERROR_CODE,ERROR_MESSAGE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, colletionFile.getRequestFileName());
			ps.setString(index++, colletionFile.getRequestFileLocation());
			ps.setInt(index++, colletionFile.getExtraction());
			ps.setInt(index++, colletionFile.getStatus());
			ps.setInt(index++, colletionFile.getWriteResponse());
			ps.setInt(index++, colletionFile.getRespFileStatus());
			ps.setTimestamp(index++, curTimeStamp);
			ps.setString(index++, colletionFile.getErrorCode());
			ps.setString(index, colletionFile.getErrorMessage());

		});

	}

	@Override
	public boolean isFileProcessed(String fileName, int status) {
		String sql = "Select count(1) from COLLECTIONRECEIPTFILES Where REQUEST_FILE_NAME= ? AND REQUEST_FILE_STATUS=?";
		logger.debug(Literal.SQL, sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName, status) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public boolean isFileFound(String fileName) {
		String sql = "Select count(1) from COLL_RECEIPT_HEADER Where REQ_FILE_NAME= ?";
		logger.debug(Literal.SQL, sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void updateFileExtraction(CollReceiptHeader header) {
		String sql = "UPDATE COLL_RECEIPT_HEADER SET EXTRACTION = ?, STATUS = ?, WRITE_RESPONSE=?, ERROR_CODE=?,ERROR_MESSAGE=? WHERE ID= ? ";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, header.getExtraction());
			ps.setInt(index++, header.getStatus());
			ps.setInt(index++, header.getWriteResponse());
			ps.setString(index++, header.getErrorCode());
			ps.setString(index++, header.getErrorMessage());
			ps.setLong(index, header.getId());

		});

	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

	@Override
	public long saveFileExtractionList(List<CollReceiptDetail> collList, long headerId) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		String sql = "INSERT INTO COLL_RECEIPT_DETAIL  (HEADER_ID,RECORD_DATA,RECEIPT_ID,CREATED_DATE) VALUES(?,?,?,?) ";

		logger.debug(Literal.SQL, sql);

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				CollReceiptDetail collectionData = collList.get(index);
				int indx = 1;

				ps.setLong(indx++, headerId);
				ps.setString(indx++, collectionData.getRecordData());
				ps.setLong(indx++, collectionData.getReceiptId());
				ps.setTimestamp(indx, curTimeStamp);
			}

			@Override
			public int getBatchSize() {
				return collList.size();
			}
		}).length;

	}

	@Override
	public List<CollReceiptDetail> fetchCollectionRecordsById(long headerId) {
		logger.debug(Literal.ENTERING);

		List<CollReceiptDetail> receiptDetails = new ArrayList<>();

		String query = "SELECT ID,HEADER_ID,RECORD_DATA,RECEIPT_ID,RECEIPT_CREATED_DATE,CREATED_DATE,ERROR_CODE,"
				+ "ERROR_MESSAGE FROM COLL_RECEIPT_DETAIL  WHERE  HEADER_ID= ?";

		logger.debug(Literal.SQL, query);

		extNamedJdbcTemplate.getJdbcOperations().query(query, ps -> ps.setLong(1, headerId), rs -> {
			CollReceiptDetail details = new CollReceiptDetail();
			details.setId(rs.getLong("ID"));
			details.setHeaderId(rs.getLong("HEADER_ID"));
			details.setRecordData(rs.getString("RECORD_DATA"));
			details.setReceiptId(rs.getLong("RECEIPT_ID"));
			details.setReceiptCreatedDate(rs.getDate("RECEIPT_CREATED_DATE"));
			details.setCreatedDate(rs.getDate("CREATED_DATE"));
			details.setErrorCode(rs.getString("ERROR_CODE"));
			details.setErrorMessage(rs.getString("ERROR_MESSAGE"));
			receiptDetails.add(details);
		});
		logger.debug(Literal.LEAVING);

		return receiptDetails;

	}

	@Override
	public void updateExtCollectionReceiptProcessStatus(CollReceiptHeader collectionReceiptFile) {
		String sql = "UPDATE COLL_RECEIPT_HEADER SET STATUS = ?, WRITE_RESPONSE=?, ERROR_CODE = ?, ERROR_MESSAGE = ? WHERE ID= ?";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, collectionReceiptFile.getStatus());
			ps.setLong(index++, collectionReceiptFile.getWriteResponse());
			ps.setString(index++, collectionReceiptFile.getErrorCode());
			ps.setString(index++, collectionReceiptFile.getErrorMessage());
			ps.setLong(index, collectionReceiptFile.getId());
		});

	}

	@Override
	public void updateExtCollectionRespFileWritingStatus(CollReceiptHeader collectionReceiptFile) {
		String sql = "UPDATE COLL_RECEIPT_HEADER SET RESP_FILE_NAME=?, RESP_FILE_LOCATION=?,RESP_FILE_STATUS=? WHERE ID= ? ";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, collectionReceiptFile.getRespFileName());
			ps.setString(index++, collectionReceiptFile.getRespFileLocation());
			ps.setInt(index++, collectionReceiptFile.getRespFileStatus());
			ps.setLong(index, collectionReceiptFile.getId());
		});

	}

	@Override
	public void updateExtCollectionReceiptDetailStatus(CollReceiptDetail collectionReceiptDetail) {
		String sql = "UPDATE COLL_RECEIPT_DETAIL SET RECEIPT_ID=?,RECEIPT_CREATED_DATE=? ,ERROR_CODE = ?, ERROR_MESSAGE = ? WHERE ID= ? ";

		logger.debug(Literal.SQL, sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, collectionReceiptDetail.getReceiptId());
			ps.setDate(index++, JdbcUtil.getDate(SysParamUtil.getAppDate()));
			ps.setString(index++, collectionReceiptDetail.getErrorCode());
			ps.setString(index++, collectionReceiptDetail.getErrorMessage());
			ps.setLong(index, collectionReceiptDetail.getId());
		});
	}

	@Override
	public CollReceiptHeader getErrorFromHeader(long pId) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT  ERROR_CODE, ERROR_MESSAGE FROM COLL_RECEIPT_HEADER  WHERE ID = ?";

		logger.debug(Literal.SQL, sql);
		Object[] parameters = new Object[] { pId };
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				CollReceiptHeader collReceiptHeader = new CollReceiptHeader();
				collReceiptHeader.setErrorCode(rs.getString("ERROR_CODE"));
				collReceiptHeader.setErrorMessage(rs.getString("ERROR_MESSAGE"));
				return collReceiptHeader;
			}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean validateAgreementNumber(String agreementNumber) {
		String sql = "SELECT COUNT(*) FROM FINANCEMAIN WHERE FINREFERENCE = ?";
		logger.debug(Literal.SQL, sql);
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, agreementNumber) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public boolean validateAgencyId(long agencyId) {
		String sql = "SELECT COUNT(*) FROM FINRECEIPTHEADER WHERE COLLECTIONAGENTID = ?";
		logger.debug(Literal.SQL, sql);
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, agencyId) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
}
