package com.pennanttech.pff.logging.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.InstBasedSchdDetails;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InstBasedSchdDetailDAO;

/**
 * DAO methods implementation for the <b>FinAutoApprovalDetail model</b> class.<br>
 * 
 */
public class InstBasedSchdDetailDAOImpl extends SequenceDao<InstBasedSchdDetails> implements InstBasedSchdDetailDAO {
	private static Logger logger = LogManager.getLogger(InstBasedSchdDetailDAOImpl.class);

	public InstBasedSchdDetailDAOImpl() {
		super();
	}

	@Override
	public void saveInstBasedSchdDetails(List<InstBasedSchdDetails> instBasedSchdList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into InstBasedSchdDetails");
		sql.append(" (BatchId, FinReference, DisbId,  RealizedDate,  Status,  ErrorDesc,");
		sql.append(" UserId, Downloaded_on,DisbAmount,LinkedTranId)");
		sql.append(" Values(:BatchId, :FinReference, :DisbId, :RealizedDate, :Status, :ErrorDesc, ");
		sql.append(":UserId ,:Downloadedon,:DisbAmount,:LinkedTranId)");
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(instBasedSchdList.toArray());

		try {
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<InstBasedSchdDetails> getUploadedDisbursementsWithBatchId(long batchId) {
		logger.debug(Literal.ENTERING);

		List<InstBasedSchdDetails> InstBasedSchdDetails = new ArrayList<InstBasedSchdDetails>();

		StringBuilder sql = new StringBuilder("");
		sql.append(" select Batchid, fad.FinReference, disbid, fad.RealizedDate, fad.Status, ErrorDesc, UserId,");
		sql.append(" paymentType,fad.downloaded_on downloadedon,disbamount  from InstBasedSchdDetails fad ");
		sql.append("  inner join finadvancepayments fap on fap.paymentid = fad.disbid");
		sql.append("  where fad.BatchId = :batchId and fad.Status = :Status");

		RowMapper<InstBasedSchdDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(InstBasedSchdDetails.class);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("batchId", batchId);
		paramMap.addValue("Status", DisbursementConstants.AUTODISB_STATUS_PENDING);

		try {
			InstBasedSchdDetails = this.jdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e.getCause());
		}
		logger.debug(Literal.LEAVING);
		return InstBasedSchdDetails;
	}

	@Override
	public List<InstBasedSchdDetails> getUploadedDisbursementsFinRefBatchId(long batchId) {
		logger.debug(Literal.ENTERING);

		List<InstBasedSchdDetails> InstBasedSchdDetails = new ArrayList<InstBasedSchdDetails>();

		StringBuilder sql = new StringBuilder();
		sql.append("select Batchid, fad.FinReference, disbid, fad.RealizedDate, fad.Status, ErrorDesc,");
		sql.append(" UserId, paymentType, Downloaded_On DownloadedOn from InstBasedSchdDetails fad");
		sql.append("  inner join finadvancepayments fap on fap.paymentid = fad.disbid");
		sql.append("  where fad.BatchId = :batchId");

		RowMapper<InstBasedSchdDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(InstBasedSchdDetails.class);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("batchId", batchId);
		try {
			InstBasedSchdDetails = this.jdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return InstBasedSchdDetails;
	}

	@Override
	public void updateFinAutoApprovals(InstBasedSchdDetails InstBasedSchdDetails) {
		StringBuilder sql = new StringBuilder("update InstBasedSchdDetails");
		sql.append("  set Status = :Status, ErrorDesc = :ErrorDesc ");
		sql.append(" where DisbId = :DisbId and BatchId = :BatchId and FinReference = :FinReference ");
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(InstBasedSchdDetails);
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

		StringBuilder sql = new StringBuilder(" Select Count(*)  From  InstBasedSchdDetails ");
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
	public boolean checkReBuildSchd(long paymentId) {
		logger.debug(Literal.ENTERING);

		//Should check any disbursements exist against payment id  
		MapSqlParameterSource parmSource = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select rebuildschd from finadvancepayments t1 ");
		sql.append(
				" inner join findisbursementdetails_temp t2 on t2.disbseq=t1.disbseq  and t1.finreference=t2.finreference ");
		sql.append(" where paymentid = :paymentid");

		parmSource.addValue("paymentid", paymentId);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Boolean.class);
		} catch (DataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return false;
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

	@Override
	public FinAdvancePayments getPaymentInstructionDetails(String id) {
		logger.debug(Literal.ENTERING);

		FinAdvancePayments finAdvancePayments = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ID", NumberUtils.toLong(id));

		StringBuilder selectSql = new StringBuilder(
				"SELECT FA.STATUS, FA.PAYMENTTYPE, FA.POSTDATE, DS.NAME FROM DISBURSEMENT_REQUESTS DR INNER JOIN PAYMENTINSTRUCTIONS FA");
		selectSql.append(
				" ON FA.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID INNER JOIN DATA_ENGINE_STATUS DS ON DR.BATCH_ID = DS.ID WHERE DR.ID=:ID");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinAdvancePayments> typeRowMapper = BeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
		try {
			finAdvancePayments = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finAdvancePayments = null;
		}
		logger.debug("Leaving");
		return finAdvancePayments;
	}

	@Override
	public String getDisbType(String id) {
		logger.debug("Entering");

		String disbType = "";
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", NumberUtils.toLong(id));

		StringBuilder selectSql = new StringBuilder(" Select Channel ");
		selectSql.append(" From DISBURSEMENT_REQUESTS");
		selectSql.append(" Where Id = :Id ");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			disbType = this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			disbType = "";
		}
		logger.debug("Leaving");
		return disbType;
	}

	@Override
	public boolean isPaymentDateExist(String finReference, int disbSeq, Date paymentDate) {

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(T1.FinReference)");
		selectSql.append(" From FinAdvancePayments T1");
		selectSql.append(" Inner Join FinDisbursementDetails_View T2 ON T1.FinReference = T2.FinReference");
		selectSql.append(" Inner Join FinanceMain_View T3 ON T1.FinReference = T3.FinReference");
		selectSql.append(
				" Where T1.FinReference = :FinReference And T1.DisbSeq = :DisbSeq And T2.DisbDate <= :PaymentDate And T3.MaturityDate >= :PaymentDate");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FinReference", finReference);
		paramMap.addValue("DisbSeq", disbSeq);
		paramMap.addValue("PaymentDate", paymentDate);

		logger.debug("selectSql: " + selectSql.toString());

		int count = this.jdbcTemplate.queryForObject(selectSql.toString(), paramMap, Integer.class);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public FinAdvancePayments getFinAdvancePaymentDetails(String id) {
		logger.debug(Literal.ENTERING);

		FinAdvancePayments finAdvancePayments = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ID", NumberUtils.toLong(id));

		StringBuilder selectSql = new StringBuilder(
				" SELECT FA.STATUS,FA.PAYMENTTYPE,DS.NAME, FA.LLDATE, DS.VALUEDATE, FA.FINREFERENCE, FA.DISBSEQ");
		selectSql.append(" FROM DISBURSEMENT_REQUESTS DR INNER JOIN FINADVANCEPAYMENTS FA");
		selectSql.append(
				" ON FA.PAYMENTID = DR.DISBURSEMENT_ID INNER JOIN DATA_ENGINE_STATUS DS ON DR.BATCH_ID = DS.ID WHERE DR.ID = :ID");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinAdvancePayments> typeRowMapper = BeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
		try {
			finAdvancePayments = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finAdvancePayments = null;
		}
		logger.debug("Leaving");
		return finAdvancePayments;
	}

	@Override
	public boolean checkInstBasedSchd(String finReference) {

		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder(" ");
		sql.append(" select instbasedschd  from (SELECT instbasedschd,finreference ");
		sql.append(" FROM financemain_temp t1 ");
		sql.append(" UNION ALL ");
		sql.append(" SELECT instbasedschd, finreference FROM financemain t1 ");
		sql.append(" WHERE NOT (EXISTS ( SELECT 1  FROM financemain_temp ");
		sql.append(" WHERE financemain_temp.finreference::text = t1.finreference::text))) t ");
		sql.append(" Where finreference=:FinReference");

		logger.debug("selectSql: " + sql.toString());

		try {
			logger.debug("Leaving");
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Boolean.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			return false;
		}
	}

	@Override
	public boolean isDisbRecordProceed(long paymentId) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		MapSqlParameterSource parmSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder(" Select Count(1)  ");
		sql.append(" from instbasedschddetails ");
		sql.append(" where disbid = :disbid");

		parmSource.addValue("disbid", paymentId);

		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), parmSource, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return recordCount == 0 ? false : true;
	}

}
