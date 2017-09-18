/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ManualAdviseDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>ManualAdvise</code> with set of CRUD operations.
 */
public class ManualAdviseDAOImpl extends BasisNextidDaoImpl<ManualAdvise> implements ManualAdviseDAO {
	private static Logger				logger	= Logger.getLogger(ManualAdviseDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ManualAdviseDAOImpl() {
		super();
	}
	
	@Override
	public ManualAdvise getManualAdviseById(long adviseID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" adviseID, adviseType, finReference, feeTypeID, sequence, adviseAmount, BounceID, ReceiptID, ");
		sql.append(" paidAmount, waivedAmount, remarks, ValueDate, PostDate,ReservedAmt, BalanceAmt, ");
		if(type.contains("View")){
			sql.append(" FeeTypeCode, FeeTypeDesc," );
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where adviseID = :adviseID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(adviseID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);

		try {
			manualAdvise = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			manualAdvise = null;
		}

		logger.debug(Literal.LEAVING);
		return manualAdvise;
	}		

	@Override
	public ManualAdvise getManualAdviseByReceiptId(long receiptID,String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" AdviseID, AdviseType, FinReference, FeeTypeID, Sequence, AdviseAmount, BounceID, ReceiptID, ");
		sql.append(" PaidAmount, WaivedAmount, Remarks, ValueDate, PostDate, ReservedAmt, BalanceAmt, ");
		if(type.contains("View")){
			sql.append(" FeeTypeCode, FeeTypeDesc, BounceCode, BounceCodeDesc, " );
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where ReceiptID = :ReceiptID");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setReceiptID(receiptID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);

		try {
			manualAdvise = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			manualAdvise = null;
		}

		logger.debug(Literal.LEAVING);
		return manualAdvise;
	}		
	
	@Override
	public String save(ManualAdvise manualAdvise,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append("(adviseID, adviseType, finReference, feeTypeID, sequence, adviseAmount, BounceID, ReceiptID, ");
		sql.append(" paidAmount, waivedAmount, remarks, ValueDate, PostDate,ReservedAmt, BalanceAmt,  ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		sql.append(" values(");
		sql.append(" :adviseID, :adviseType, :finReference, :feeTypeID, :sequence, :adviseAmount, :BounceID, :ReceiptID,");
		sql.append(" :paidAmount, :waivedAmount, :remarks, :ValueDate, :PostDate, :ReservedAmt, :BalanceAmt, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		// Get the identity sequence number.
		if (manualAdvise.getAdviseID() <= 0) {
			manualAdvise.setAdviseID(getNextidviewDAO().getNextId("seqManualAdvise"));
		}

		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(manualAdvise.getAdviseID());
	}	

	@Override
	public void update(ManualAdvise manualAdvise,TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update ManualAdvise" );
		sql.append(tableType.getSuffix());
		sql.append("  set adviseType = :adviseType, finReference = :finReference, feeTypeID = :feeTypeID, ");
		sql.append(" sequence = :sequence, adviseAmount = :adviseAmount, paidAmount = :paidAmount, ");
		sql.append(" waivedAmount = :waivedAmount, remarks = :remarks,BounceID=:BounceID, ReceiptID=:ReceiptID, ");
		sql.append(" ValueDate=:ValueDate, PostDate=:PostDate, ReservedAmt=:ReservedAmt, BalanceAmt=:BalanceAmt, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where adviseID = :adviseID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ManualAdvise manualAdvise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" where adviseID = :adviseID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void deleteByAdviseId(ManualAdvise manualAdvise, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdvise");
		sql.append(tableType.getSuffix());
		sql.append(" where adviseID = :adviseID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
 
	@Override
	public List<ManualAdvise> getManualAdviseByRef(String finReference , int adviseType, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Select AdviseID, AdviseAmount, PaidAmount, WaivedAmount, ReservedAmt, BalanceAmt, BounceId, ReceiptId " );
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" ,FeeTypeCode, FeeTypeDesc, BounceCode,BounceCodeDesc ");
		}
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference AND AdviseType =:AdviseType ");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		manualAdvise.setAdviseType(adviseType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);

		List<ManualAdvise> adviseList = namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		logger.debug(Literal.LEAVING);
		return adviseList;
	}	
	
	/**
	 * Method for updating Manual advise Payment Details
	 * @param adviseID
	 * @param paidAmount
	 * @param waivedAmount
	 * @param tableType
	 */
	@Override
	public void updateAdvPayment(long adviseID, BigDecimal paidAmount , BigDecimal waivedAmount, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(adviseID);
		manualAdvise.setPaidAmount(paidAmount);
		manualAdvise.setWaivedAmount(waivedAmount);
		
		StringBuilder	sql =new StringBuilder("update ManualAdvise" );
		sql.append(tableType.getSuffix());
		sql.append(" set ");
		if(paidAmount.compareTo(BigDecimal.ZERO) != 0){
			sql.append(" PaidAmount = PaidAmount + :PaidAmount ");
		}
		if(waivedAmount.compareTo(BigDecimal.ZERO) != 0){
			if(paidAmount.compareTo(BigDecimal.ZERO) != 0){
				sql.append(" , ");
			}
			sql.append(" WaivedAmount = WaivedAmount+:WaivedAmount ");
		}
		sql.append(" WHERE AdviseID = :AdviseID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);
		this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}	
	
	@Override
	public void saveMovement(ManualAdviseMovements movement, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql =new StringBuilder(" insert into ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" ( MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, WaivedAmount, Status, ReceiptID, ReceiptSeqID)" );
		sql.append(" VALUES(:MovementID, :AdviseID, :MovementDate, :MovementAmount, :PaidAmount, :WaivedAmount, :Status, :ReceiptID, :ReceiptSeqID)");
		
		// Get the identity sequence number.
		if (movement.getMovementID() <= 0) {
			movement.setMovementID(getNextidviewDAO().getNextId("SeqManualAdviseMovements"));
		}
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(movement);
		this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}	
	
	@Override
	public List<ManualAdviseMovements> getAdviseMovementsByReceipt(long receiptID, String type) {
		logger.debug("Entering");

		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setReceiptID(receiptID);

		StringBuilder selectSql = new StringBuilder(" Select MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, ");
		selectSql.append(" WaivedAmount, Status, ReceiptID, ReceiptSeqID ");
		selectSql.append(" From ManualAdviseMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReceiptID = :ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movements);
		RowMapper<ManualAdviseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ManualAdviseMovements.class);

		List<ManualAdviseMovements> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return list;
	}		
	
	@Override
	public List<ManualAdviseMovements> getAdviseMovements(long id) {
		logger.debug("Entering");

		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setAdviseID(id);

		StringBuilder selectSql = new StringBuilder(" Select T1.MovementID ,T1.MovementDate, T1.MovementAmount, ");
		selectSql.append(" T1.PaidAmount , T1.WaivedAmount, T1.Status, T2.ReceiptMode ");
		selectSql.append(" From ManualAdviseMovements T1 LEFT OUTER JOIN FinReceiptHeader T2 ON T1.ReceiptID = T2.ReceiptID ");
		selectSql.append(" Where AdviseID = :AdviseID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movements);
		RowMapper<ManualAdviseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ManualAdviseMovements.class);

		List<ManualAdviseMovements> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return list;
	}

	@Override
	public void deleteMovementsByReceiptID(long receiptID, String type) {
		logger.debug(Literal.ENTERING);
		
		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setReceiptID(receiptID);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ManualAdviseMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where ReceiptID = :ReceiptID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(movements);
		namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ManualAdviseMovements> getAdvMovementsByReceiptSeq(long receiptID, long receiptSeqID, String type) {
		logger.debug("Entering");

		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setReceiptID(receiptID);
		movements.setReceiptSeqID(receiptSeqID);

		StringBuilder selectSql = new StringBuilder(" Select MovementID, AdviseID, MovementDate, MovementAmount, PaidAmount, ");
		selectSql.append(" WaivedAmount, Status, ReceiptID, ReceiptSeqID ");
		if(StringUtils.contains(type, "View")){
			selectSql.append(" , FeeTypeCode ");
		}
		selectSql.append(" From ManualAdviseMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReceiptID = :ReceiptID AND ReceiptSeqID=:ReceiptSeqID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movements);
		RowMapper<ManualAdviseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ManualAdviseMovements.class);

		List<ManualAdviseMovements> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return list;
	}

	@Override
	public void updateMovementStatus(long receiptID, long receiptSeqID, String status, String type) {
		logger.debug(Literal.ENTERING);
		
		ManualAdviseMovements movements = new ManualAdviseMovements();
		movements.setReceiptID(receiptID);
		movements.setReceiptSeqID(receiptSeqID);
		movements.setStatus(status);
		
		// Prepare the SQL.
		StringBuilder	sql =new StringBuilder("update ManualAdviseMovements" );
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" set Status = :Status ");
		sql.append(" Where ReceiptID = :ReceiptID AND ReceiptSeqID=:ReceiptSeqID ");
	
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(movements);
		namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		logger.debug(Literal.LEAVING);
	}	
	
	/**
	 * Method for Fetch the Reserved Payable Amounts Log details
	 */
	@Override
	public List<ManualAdviseReserve> getPayableReserveList(long receiptSeqID) {
		logger.debug("Entering");

		ManualAdviseReserve reserve = new ManualAdviseReserve();
		reserve.setReceiptSeqID(receiptSeqID);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptSeqID, AdviseID , ReservedAmt ");
		selectSql.append(" From ManualAdviseReserve ");
		selectSql.append(" Where ReceiptSeqID =:ReceiptSeqID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		RowMapper<ManualAdviseReserve> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdviseReserve.class);

		List<ManualAdviseReserve> reserveList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return reserveList;
	}

	/**
	 * Method for Fetch the Reserved Payable Amounts Log details
	 */
	@Override
	public ManualAdviseReserve getPayableReserve(long receiptSeqID, long payAgainstID) {
		logger.debug("Entering");

		ManualAdviseReserve reserve = new ManualAdviseReserve();
		reserve.setReceiptSeqID(receiptSeqID);
		reserve.setAdviseID(payAgainstID);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptSeqID, AdviseID , ReservedAmt ");
		selectSql.append(" From ManualAdviseReserve ");
		selectSql.append(" Where ReceiptSeqID =:ReceiptSeqID AND AdviseID=:AdviseID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		RowMapper<ManualAdviseReserve> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdviseReserve.class);

		try {
			reserve = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			reserve = null;
		}

		logger.debug("Leaving");
		return reserve;
	}

	/**
	 * Method for Save Reserved amount against Advise ID
	 */
	@Override
	public void savePayableReserveLog(long receiptSeqID, long payAgainstID, BigDecimal reserveAmt) {
		logger.debug("Entering");
		
		ManualAdviseReserve reserve = new ManualAdviseReserve();
		reserve.setReceiptSeqID(receiptSeqID);
		reserve.setAdviseID(payAgainstID);
		reserve.setReservedAmt(reserveAmt);
		
		StringBuilder insertSql = new StringBuilder("Insert Into ManualAdviseReserve ");
		insertSql.append(" (AdviseID, ReceiptSeqID, ReservedAmt )");
		insertSql.append(" Values(:AdviseID, :ReceiptSeqID, :ReservedAmt)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reserve);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for updating Reserved excess Amount after modifications
	 */
	@Override
	public void updatePayableReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptID);
		source.addValue("AdviseID", payAgainstID);
		source.addValue("PaidNow", diffInReserve);

		StringBuilder updateSql = new StringBuilder("Update ManualAdviseReserve ");
		updateSql.append(" Set ReservedAmt = ReservedAmt + :PaidNow ");
		updateSql.append(" Where ReceiptSeqID =:ReceiptSeqID AND AdviseID =:AdviseID ");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Deleting Reserved Amounts against Advise ID Processed for Utilization
	 */
	@Override
	public void deletePayableReserve(long receiptID, long payAgainstID) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptID);
		source.addValue("AdviseID", payAgainstID);

		StringBuilder updateSql = new StringBuilder("Delete From ManualAdviseReserve ");
		updateSql.append(" Where ReceiptSeqID =:ReceiptSeqID ");
		if(payAgainstID != 0){
			updateSql.append(" AND AdviseID =:AdviseID ");
		}

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Method for updating Reserved amount against Advise ID
	 */
	@Override
	public void updatePayableReserve(long payAgainstID, BigDecimal reserveAmt) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", payAgainstID);
		source.addValue("PaidNow", reserveAmt);

		StringBuilder updateSql = new StringBuilder("Update ManualAdvise ");
		updateSql.append(" Set ReservedAmt = ReservedAmt + :PaidNow, BalanceAmt = BalanceAmt - :PaidNow ");
		updateSql.append(" Where AdviseID =:AdviseID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Update utilization amount after amounts Approval
	 */
	@Override
	public void updateUtilise(long adviseID, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", adviseID);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update ManualAdvise");
		updateSql.append(" Set PaidAmount = PaidAmount + :PaidNow, ReservedAmt = ReservedAmt - :PaidNow ");
		updateSql.append(" Where AdviseID =:AdviseID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Update utilization amount after amounts Reversal
	 */
	@Override
	public void reverseUtilise(long adviseID, BigDecimal amount) {
		logger.debug("Entering");

		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AdviseID", adviseID);
		source.addValue("PaidNow", amount);

		StringBuilder updateSql = new StringBuilder("Update ManualAdvise");
		updateSql.append(" Set PaidAmount = PaidAmount - :PaidNow, BalanceAmt = BalanceAmt + :PaidNow ");
		updateSql.append(" Where AdviseID =:AdviseID");

		logger.debug("updateSql: " + updateSql.toString());
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public Date getPresentmentBounceDueDate(long receiptId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptId", receiptId);

		StringBuilder selectSql = new StringBuilder("SELECT T5.schDate	FROM MANUALADVISE M ");
		selectSql.append(" INNER Join PRESENTMENTDETAILS T4 on T4.RECEIPTID = M.RECEIPTID and M.RECEIPTID !=0 and T4.RECEIPTID !=0 ");
		selectSql.append(" INNER Join FINSCHEDULEDETAILS T5 on T4.FInreference = T5.FInreference and T4.schdate = T5.schdate ");
		selectSql.append(" where M.AdviseType <> 2 and	m.ADVISEAMOUNT > 0 and FEETYPEID not in (Select FEETYPEID from FEETYPES) ");
		selectSql.append(" AND M.ReceiptId =:ReceiptId ");

		logger.debug("selectSql: " + selectSql.toString());
		Date schDate = null;
		try {
			schDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		} catch (EmptyResultDataAccessException ede) {
			logger.warn("Warning:", ede);
		}
		logger.debug("Leaving");

		return schDate;
	}
	
	/**
	 * Method for Fetch All Bounce ID List using Reference
	 */
	@Override
	public List<Long> getBounceAdvisesListByRef(String finReference , int adviseType, String type) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" Select AdviseId " );
		sql.append(" From ManualAdvise");
		sql.append(type);
		sql.append(" Where FinReference = :FinReference AND AdviseType =:AdviseType AND BounceId > 0 ");
		
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		manualAdvise.setAdviseType(adviseType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(manualAdvise);

		List<Long> bounceIDList = namedParameterJdbcTemplate.queryForList(sql.toString(), paramSource, Long.class);
		logger.debug(Literal.LEAVING);
		return bounceIDList;
	}	
	
}	
