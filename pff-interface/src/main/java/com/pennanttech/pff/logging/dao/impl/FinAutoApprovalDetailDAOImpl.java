package com.pennanttech.pff.logging.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

/**
 * DAO methods implementation for the <b>FinAutoApprovalDetail model</b> class.<br>
 * 
 */
public class FinAutoApprovalDetailDAOImpl extends SequenceDao<FinAutoApprovalDetails>
		implements FinAutoApprovalDetailDAO {
	private static Logger logger = LogManager.getLogger(FinAutoApprovalDetailDAOImpl.class);

	public FinAutoApprovalDetailDAOImpl() {
		super();
	}

	@Override
	public void logFinAutoApprovalDetails(List<FinAutoApprovalDetails> autoAppList) {
		logger.debug(Literal.ENTERING);

		try {
			StringBuilder sql = new StringBuilder("Insert Into FinAutoApprovalDetails");
			sql.append(" (BatchId, FinReference, DisbId,  RealizedDate,  Status,  ErrorDesc,  UserId, Downloaded_on)");
			sql.append(
					" Values(:BatchId, :FinReference, :DisbId, :RealizedDate, :Status, :ErrorDesc, :UserId ,:Downloadedon)");
			SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(autoAppList.toArray());
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinAutoApprovalDetails> getUploadedDisbursementsWithBatchId(long batchId) {
		logger.debug(Literal.ENTERING);

		List<FinAutoApprovalDetails> finAutoApprovalDetails = new ArrayList<FinAutoApprovalDetails>();
		try {
			//String sql = " where BatchId = :batchId and Status = :Status";

			StringBuilder sql = new StringBuilder(
					"select Batchid, fad.FinReference, disbid, fad.RealizedDate, fad.Status, ErrorDesc, UserId, paymentType,fad.downloaded_on downloadedon  from FinAutoApprovalDetails fad");
			sql.append("  inner join finadvancepayments fap on fap.paymentid = fad.disbid");
			sql.append("  where fad.BatchId = :batchId and fad.Status = :Status");

			RowMapper<FinAutoApprovalDetails> typeRowMapper = BeanPropertyRowMapper
					.newInstance(FinAutoApprovalDetails.class);
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("batchId", batchId);
			paramMap.addValue("Status", DisbursementConstants.AUTODISB_STATUS_PENDING);
			finAutoApprovalDetails = this.jdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e.getCause());
		}
		logger.debug(Literal.LEAVING);
		return finAutoApprovalDetails;
	}

	@Override
	public List<FinAutoApprovalDetails> getUploadedDisbursementsFinRefBatchId(long batchId) {
		logger.debug(Literal.ENTERING);

		List<FinAutoApprovalDetails> finAutoApprovalDetails = new ArrayList<FinAutoApprovalDetails>();
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("select Batchid, fad.FinReference, disbid, fad.RealizedDate, fad.Status, ErrorDesc,");
			sql.append(" UserId, paymentType, Downloaded_On DownloadedOn from FinAutoApprovalDetails fad");
			sql.append("  inner join finadvancepayments fap on fap.paymentid = fad.disbid");
			sql.append("  where fad.BatchId = :batchId");

			RowMapper<FinAutoApprovalDetails> typeRowMapper = BeanPropertyRowMapper
					.newInstance(FinAutoApprovalDetails.class);
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("batchId", batchId);
			finAutoApprovalDetails = this.jdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return finAutoApprovalDetails;
	}

	@Override
	public void updateFinAutoApprovals(FinAutoApprovalDetails finAutoApprovalDetails) {
		StringBuilder sql = new StringBuilder("update FinAutoApprovalDetails");
		sql.append("  set Status = :Status, ErrorDesc = :ErrorDesc ");
		sql.append(" where DisbId = :DisbId and BatchId = :BatchId and FinReference = :FinReference ");
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finAutoApprovalDetails);
		jdbcTemplate.update(sql.toString(), paramSource);

	}

	@Override
	public Map<String, Integer> loadQDPValidityDays() {
		Map<String, Integer> qdpDays = new HashMap<>();
		logger.info("Loading QDP validity Days..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("Select DisbMode, NoOfDays FROM QDPValidityDays");

		jdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				qdpDays.put(rs.getString("DisbMode"), rs.getInt("NoOfDays"));
			}
		});
		return qdpDays;
	}

	@Override
	public void deleteNonQDPRecords(List<FinAutoApprovalDetails> nonQDPList) {

		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From FinAutoApprovalDetails");
		deleteSql.append(" Where DisbId =:DisbId and Batchid = :batchId and FinReference = :FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(nonQDPList.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean getFinanceIfApproved(String finReference) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(" Select Count(1)  ");
		sql.append(" from Financemain ");
		sql.append(" where FinReference = :FinReference");

		parmSource.addValue("FinReference", finReference);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return recordCount == 0 ? false : true;
	}

	@Override
	public int getProcessingCount(Date appDate) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(" Select Count(*)  From  FinAutoApprovalDetails ");
		sql.append(" Where RealizedDate = :RealizedDate and Status = :Status");

		parmSource.addValue("RealizedDate", appDate);
		parmSource.addValue("Status", DisbursementConstants.AUTODISB_STATUS_PENDING);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			recordCount = 0;
		}

		logger.debug(Literal.LEAVING);
		return recordCount;
	}

	@Override
	public boolean getFinanceServiceInstruction(String finReference) {

		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(" Select Count(1) ");
		sql.append(" from FinServiceInstruction_Temp ");
		sql.append(" where Finreference = :Finreference");
		sql.append(" and  FinEvent = :FinEvent");

		parmSource.addValue("Finreference", finReference);
		parmSource.addValue("FinEvent", FinanceConstants.FINSER_EVENT_ADDDISB);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return recordCount == 0 ? false : true;

	}

	@Override
	public boolean isFinQdpIsInProgress(String finReference) {
		int recordCount = 0;
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(" Select Count(*)  From  FinAutoApprovalDetails ");
		sql.append(" Where FinReference = :FinReference and Status = :Status");

		parmSource.addValue("FinReference", finReference);
		parmSource.addValue("Status", DisbursementConstants.AUTODISB_STATUS_PENDING);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return recordCount == 0 ? false : true;
	}

	@Override
	public boolean CheckDisbForQDP(String finReference) {
		MapSqlParameterSource parmSource = new MapSqlParameterSource();
		boolean isQDP = false;
		StringBuilder sql = new StringBuilder(" Select  QuickDisb  From  FinDisbursementDetails_Temp ");
		sql.append(" Where FinReference = :FinReference and DisbStatus is null");

		parmSource.addValue("FinReference", finReference);
		parmSource.addValue("DisbStatus", "D");

		try {
			isQDP = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e.getCause());
			isQDP = false;
		}
		return isQDP;
	}

	@Override
	public FinanceType getQDPflagByFinref(String finReference) {
		FinanceType financeType = null;
		MapSqlParameterSource parmSource = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" Select  ft.QuickDisb , rmt.autoapprove, rmt.Fintype From  Financemain_Temp ft ");
		sql.append(" inner join RMTfinancetypes rmt on  rmt.fintype=ft.fintype AND ft.quickDisb= :quickDisb ");
		sql.append(" Where ft.FinReference = :FinReference");

		parmSource.addValue("FinReference", finReference);
		parmSource.addValue("quickDisb", true);
		RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);

		try {
			financeType = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return financeType;
	}

	@Override
	public boolean getAutoApprovalFlag(String finType) {
		MapSqlParameterSource parmSource = new MapSqlParameterSource();
		boolean autoApprove = false;
		StringBuilder sql = new StringBuilder(" Select  autoApprove  From  RMTFinanceTypes ");
		sql.append(" Where Fintype = :Fintype");

		parmSource.addValue("Fintype", finType);

		try {
			autoApprove = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			autoApprove = false;
		}

		return autoApprove;
	}
}
