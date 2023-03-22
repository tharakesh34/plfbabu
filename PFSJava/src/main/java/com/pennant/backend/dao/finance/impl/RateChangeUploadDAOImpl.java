package com.pennant.backend.dao.finance.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.dao.finance.RateChangeUploadDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.ratechangeupload.RateChangeUpload;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class RateChangeUploadDAOImpl extends SequenceDao<RateChangeUpload> implements RateChangeUploadDAO {
	private static Logger logger = LogManager.getLogger(RateChangeUploadDAOImpl.class);

	@Override
	public List<RateChangeUpload> getRateChangeUploadDetails(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, BatchId, FinID, FinReference, BaseRateCode, Margin, ActualRate, ReCalType");
		sql.append(", RecalFromDate, RecalToDate, SpecialRate, Remarks, FromDate, ToDate, UploadStatusRemarks");
		sql.append(" From RateChange_Upload_Details");
		sql.append(" Where BatchId = ?");

		logger.debug(Literal.SQL + sql);

		List<RateChangeUpload> list = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			RateChangeUpload rc = new RateChangeUpload();

			rc.setId(JdbcUtil.getLong(rs.getObject("Id")));
			rc.setBatchId(JdbcUtil.getLong(rs.getObject("BatchId")));
			rc.setFinID(rs.getLong("FinID"));
			rc.setFinReference(rs.getString("FinReference"));
			rc.setBaseRateCode(rs.getString("BaseRateCode"));
			rc.setMargin(rs.getBigDecimal("Margin"));
			rc.setActualRate(rs.getBigDecimal("ActualRate"));
			rc.setRecalType(rs.getString("ReCalType"));
			rc.setRecalFromDate(rs.getDate("RecalFromDate"));
			rc.setRecalToDate(rs.getDate("RecalToDate"));
			rc.setSpecialRate(rs.getString("SpecialRate"));
			rc.setRemarks(rs.getString("Remarks"));
			rc.setFromDate(JdbcUtil.getDate(rs.getDate("FromDate")));
			rc.setToDate(JdbcUtil.getDate(rs.getDate("ToDate")));
			rc.setUploadStatusRemarks((rs.getString("UploadStatusRemarks")));

			return rc;
		}, batchId);

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getId(), l2.getId())).collect(Collectors.toList());
	}

	@Override
	public boolean isFileExists(String name) {
		String sql = "Select Count(ID) From Ratechange_Upload_Header Where FileName = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, name) > 0 ? true : false;
	}

	@Override
	public long saveHeader(String fileName, String entityCode) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" Ratechange_Upload_Header");
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
					ps.setInt(index, 0);

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public int logRcUpload(List<ErrorDetail> errDetail, Long id) {
		String sql = "Insert Into Ratechange_Upload_Log (DetailId, ErrorCode, ErrorDescription) Values(?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;

				ErrorDetail err = errDetail.get(i);

				ps.setObject(index++, id);
				ps.setString(index++, err.getCode());
				ps.setString(index, err.getError());
			}

			public int getBatchSize() {
				return errDetail.size();
			}
		}).length;
	}

	@Override
	public void updateRemarks(RateChangeUploadHeader rcuh) {
		String sql = "Update Ratechange_Upload_Header Set TotalRecords = ?, SucessRecords = ?, FailureRecords = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, rcuh.getTotalRecords());
			ps.setInt(index++, rcuh.getSucessRecords());
			ps.setInt(index++, rcuh.getFailureRecords());
			ps.setString(index++, rcuh.getStatus());

			ps.setObject(index, rcuh.getId());

		});
	}

	@Override
	public boolean getRateCodes(String brCode) {
		String sql = "Select Count(BRType) From RMTBaseRateCodes Where BRType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, brCode) > 0;
	}

	public boolean getsplRateCodes(String srCode) {
		String sql = "Select count(SRType) FROM RMTSplRateCodes Where SRType = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, srCode) > 0;
	}

	@Override
	public List<FinanceMain> getFinanceMain(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinID, FM.FinReference, FT.FinType, SD.EntityCode, FM.FinBranch, FM.CustID ");
		sql.append(", FM.FinCcy, FM.FinIsActive");
		sql.append(" From Financemain FM");
		sql.append(" Inner Join Customers Cust on FM.CustId = Cust.CustId");
		sql.append(" Inner Join RmtFinanceTypes FT on FT.FinType = FM.FinType");
		sql.append(" Inner Join SmtDivisiondetail SD On FT.FinDivision = SD.DivisionCode");
		sql.append(" Where FM.FinReference in (Select FinReference From Ratechange_Upload_Details Where BatchId = ?)");
		sql.append(" and FM.fincategory != ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, batchId);
			ps.setString(2, "CD");
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));

			return fm;
		});

	}

	@Override
	public void updateRateChangeDetails(RateChangeUpload rcUpload) {
		String sql = "Update Ratechange_Upload_Details Set FinID = ?, UploadStatusRemarks = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, rcUpload.getFinID());
			ps.setString(index++, StringUtils.trimToEmpty(rcUpload.getUploadStatusRemarks()));
			ps.setString(index++, rcUpload.getStatus());
			ps.setObject(index, rcUpload.getId());

		});
	}

	public void updateDeRemarks(RateChangeUploadHeader header, DataEngineStatus deStatus) {
		StringBuilder remarks = new StringBuilder(deStatus.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(header.getSucessRecords());
		remarks.append(", Rejected: ");
		remarks.append(header.getFailureRecords());

		deStatus.setRemarks(remarks.toString());

		String sql = "Update Data_Engine_Status Set EndTime = ?, Remarks = ?, Status = ? Where Name = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setString(index++, remarks.toString());
			ps.setString(index++, deStatus.getStatus());
			ps.setString(index, header.getBatchRef());

		});
	}
}
