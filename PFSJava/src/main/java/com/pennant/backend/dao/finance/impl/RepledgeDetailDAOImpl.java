package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.RepledgeDetailDAO;
import com.pennant.backend.model.finance.RepledgeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class RepledgeDetailDAOImpl extends BasicDao<RepledgeDetail> implements RepledgeDetailDAO {
	private static Logger logger = LogManager.getLogger(RepledgeDetailDAOImpl.class);

	public RepledgeDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record RepledgeDetail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepledgeDetail
	 */
	@Override
	public RepledgeDetail getRepledgeDetailById(final String finReference, String type) {
		logger.debug(Literal.ENTERING);
		RepledgeDetail repledgeDetail = new RepledgeDetail();
		repledgeDetail.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder("SELECT FinReference,");
		selectSql.append(" ValueDate, PromotionCode, PromotionSeqId, FinAmount,");
		selectSql.append(" GdrAvailable, PacketNumber, RackNumber, HowAquired, WhenAquired,");
		selectSql.append(
				" DmaCode, FinPurpose, TransactionType,ReceivableAmount, RepledgeRef, DocImage,WaiverRejected, RealizeAdjusted, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId, EligibleAmt ");
		selectSql.append(" From RepledgeDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where finReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repledgeDetail);
		RowMapper<RepledgeDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(RepledgeDetail.class);

		try {
			repledgeDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			repledgeDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return repledgeDetail;
	}

	@Override
	public void save(RepledgeDetail repledgeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RepledgeDetail");
		sql.append(tableType.getSuffix());

		sql.append(" (FinReference, ValueDate, PromotionCode, PromotionSeqId, FinAmount,");
		sql.append(" GdrAvailable, PacketNumber, RackNumber, HowAquired, WhenAquired,");
		sql.append(
				" DmaCode, FinPurpose, TransactionType,ReceivableAmount,RepledgeRef, DocImage,WaiverRejected, RealizeAdjusted, ");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId, EligibleAmt)");
		sql.append(" values (:FinReference, :ValueDate, :PromotionCode, :PromotionSeqId, :FinAmount,");
		sql.append(" :GdrAvailable, :PacketNumber, :RackNumber, :HowAquired, :WhenAquired,");
		sql.append(
				" :DmaCode, :FinPurpose, :TransactionType,:ReceivableAmount, :RepledgeRef, :DocImage,:WaiverRejected, :RealizeAdjusted, ");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId, :EligibleAmt)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(repledgeDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(RepledgeDetail repledgeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RepledgeDetail");
		sql.append(tableType.getSuffix());
		sql.append(" set FinReference = :FinReference, ValueDate = :ValueDate, PromotionCode = :PromotionCode,");
		sql.append(" PromotionSeqId = :PromotionSeqId, FinAmount = :FinAmount, GdrAvailable = :GdrAvailable,");
		sql.append(
				" PacketNumber = :PacketNumber, RackNumber = :RackNumber, HowAquired = :HowAquired, RepledgeRef=:RepledgeRef, ");
		sql.append(" DocImage=:DocImage,WaiverRejected=:WaiverRejected, RealizeAdjusted=:RealizeAdjusted, ");
		sql.append(
				" WhenAquired = :WhenAquired, DmaCode = :DmaCode, FinPurpose = :FinPurpose, TransactionType = :TransactionType,ReceivableAmount=:ReceivableAmount, ");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, EligibleAmt = :EligibleAmt");
		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(repledgeDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(RepledgeDetail repledgeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from RepledgeDetail");
		sql.append(tableType.getSuffix());
		sql.append(" where FinReference = :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(repledgeDetail);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
}
