package com.pennant.backend.dao.finance.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import java.sql.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.RateChangeUploadDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.ratechangeupload.RateChangeUpload;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class RateChangeUploadDAOImpl extends SequenceDao<RateChangeUpload> implements RateChangeUploadDAO {
	private static Logger logger = LogManager.getLogger(RateChangeUploadDAOImpl.class);

	@Override
	public List<RateChangeUpload> getRateChangeUploadDetails(long batchId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT Id, BatchId, FINREFERENCE, BaseRateCode, Margin, ActualRate, ReCalculationType, RecalFromDate, RecalToDate, SpecialRate");
		sql.append(" FROM RATECHANGE_UPLOAD_DETAILS");
		sql.append(" WHERE BATCHID = ?");

		logger.trace(Literal.SQL + sql.toString());
		
		return this.jdbcOperations.query(sql.toString(), new Object[] { batchId }, (rs, rowNum) -> {
			RateChangeUpload rateChange = new RateChangeUpload();
	
			rateChange.setId(rs.getLong("Id"));
			rateChange.setBatchId(rs.getLong("BatchId"));
			rateChange.setFinReference(rs.getString("FINREFERENCE"));
			rateChange.setBaseRateCode(rs.getString("BaseRateCode"));
			rateChange.setMargin(rs.getBigDecimal("Margin"));
			rateChange.setActualRate(rs.getBigDecimal("ActualRate"));
			rateChange.setRecalFromDate(rs.getDate("RecalFromDate"));
			rateChange.setRecalToDate(rs.getDate("RecalToDate"));
			rateChange.setRecalType(rs.getString("ReCalculationType"));
			rateChange.setSpecialRate(rs.getString("SpecialRate"));
			
			return rateChange;
		});
	}

	@Override
	public boolean isFileExists(String name) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select count(ID) from RATECHANGE_UPLOAD_HEADER");
		sql.append(" where FileName = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { name }, Integer.class) > 0 ? true
				: false;
	}

	@Override
	public long saveHeader(String fileName, String entityCode) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" RATECHANGE_UPLOAD_HEADER");
		sql.append(" (FileName, EntityCode, TotalRecords, SucessRecords, FailureRecords)");
		sql.append(" values(?, ?, ?, ?, ?) ");

		logger.trace(Literal.SQL + sql.toString());
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setString(index++, fileName);
					ps.setString(index++, entityCode);
					ps.setInt(index++, 0);
					ps.setInt(index++, 0);
					ps.setInt(index++, 0);

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return 0;
	}

	@Override
	public int logRcUpload(List<ErrorDetail> errDetail, Long id) {
		StringBuilder sql = new StringBuilder("Insert Into RATECHANGE_UPLOAD_LOG");
		sql.append(" (DetailId, ErrorCode, ErrorDescription)");
		sql.append(" Values( ? , ?, ?)");
		
		logger.trace(Literal.SQL+ sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				ErrorDetail err = errDetail.get(index);

				ps.setLong(i++, id);
				ps.setString(i++, err.getCode());
				ps.setString(i++, err.getError());
			}

			public int getBatchSize() {
				return errDetail.size();
			}
		}).length;
	}

	@Override
	public void updateRemarks(RateChangeUploadHeader rateChangeUploadHeader) {
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE RATECHANGE_UPLOAD_HEADER Set");
		sql.append(" TotalRecords = ?, SucessRecords = ?, FailureRecords = ?, Status = ? ");
		sql.append(" WHERE Id = ?");

		logger.trace(Literal.SQL+sql.toString());
		
		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setInt(index++, rateChangeUploadHeader.getTotalRecords());
				ps.setInt(index++, rateChangeUploadHeader.getSucessRecords());
				ps.setInt(index++, rateChangeUploadHeader.getFailureRecords());
				ps.setString(index++, rateChangeUploadHeader.getStatus());
				ps.setLong(index++, rateChangeUploadHeader.getId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public int saveRateChangeUpload(List<RateChangeUpload> rateChangeUpload, long id) {
		StringBuilder sql = new StringBuilder("Insert into RATECHANGE_UPLOAD_DETAILS");
		sql.append(" ( BatchId, Finreference, BaseRateCodce, Margin, EffectiveFrom,SpecialRate) ");
		sql.append(" values");
		sql.append(" ( ?, ?, ?, ?, ?,?)");

		logger.trace(Literal.SQL+sql.toString());
		
		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				RateChangeUpload frr = rateChangeUpload.get(index);

				ps.setLong(i++, id);
				ps.setString(i++, frr.getFinReference());
				ps.setString(i++, frr.getBaseRateCode());
				ps.setBigDecimal(i++, frr.getMargin());
				ps.setDate(index++, (Date) frr.getEffectiveFrom());
				ps.setString(index++, frr.getSpecialRate());
			}

			public int getBatchSize() {
				return rateChangeUpload.size();
			}
		}).length;
	}

	@Override
	public boolean getRateCodes(String brCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(*) FROM RMTBaseRateCodes");
		sql.append(" WHERE BRType = ?");
		
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { brCode }, Integer.class) > 0 ? true
				: false;
	}

	public boolean getsplRateCodes(String srCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(*) FROM RMTSplRateCodes");
		sql.append(" WHERE SRType = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { srCode }, Integer.class) > 0 ? true
				: false;
	}

	@Override
	public List<FinanceMain> getFinanceMain(long batchId) {
		StringBuilder sql = new StringBuilder("Select ");
		sql.append(" FM.FinReference, FT.FinType, SD.EntityCode, FM.FinBranch, FM.CustID ");
		sql.append(", FM.FinCcy, FM.FinIsActive");
		sql.append(" From Financemain FM");
		sql.append(" INNER JOIN Customers Cust on FM.CUSTID = Cust.CUSTID");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN SMTDivisiondetail SD On FT.FINDIVISION = SD.DivisionCode");
		sql.append(" Where FM.FinReference in (select FinReference from RATECHANGE_UPLOAD_DETAILS where BatchId = ?)");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new Object[] { batchId }, FinanceMainRowMapper());

	}

	private RowMapper<FinanceMain> FinanceMainRowMapper() {
		return (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			
			return fm;
		};
	}

	@Override
	public void updateRateChangeDetails(RateChangeUpload rcUpload) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update RATECHANGE_UPLOAD_DETAILS Set ");
		sql.append(" REMARKS = ? , STATUS = ?");
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());
		
		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				//ps.setObject(index++, rcUpload.getFinServInstId());
				ps.setString(index++, StringUtils.trimToEmpty(rcUpload.getRemarks()));
				ps.setString(index++, rcUpload.getStatus());
				ps.setLong(index++, rcUpload.getId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	public void updateDeRemarks(RateChangeUploadHeader header, DataEngineStatus deStatus) {
		StringBuilder remarks = new StringBuilder(deStatus.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(header.getSucessRecords());
		remarks.append(", Rejected: ");
		remarks.append(header.getFailureRecords());

		deStatus.setRemarks(remarks.toString());

		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE DATA_ENGINE_STATUS set EndTime = ?, Remarks = ?, Status = ?");
		sql.append(" WHERE Name = ?");
		
		logger.trace(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setString(index++, remarks.toString());
				ps.setString(index++, deStatus.getStatus());
				ps.setString(index++, header.getBatchRef());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

}
