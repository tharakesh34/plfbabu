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
 * FileName    		:  BankBranchDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.bmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>BankBranch model</b> class.<br>
 * 
 */

public class BankBranchDAOImpl extends SequenceDao<BankBranch> implements BankBranchDAO {
	private static Logger logger = LogManager.getLogger(BankBranchDAOImpl.class);

	public BankBranchDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new BankBranch
	 * 
	 * @return BankBranch
	 */

	@Override
	public BankBranch getBankBranch() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BankBranch");
		BankBranch bankBranch = new BankBranch();
		if (workFlowDetails != null) {
			bankBranch.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug(Literal.LEAVING);
		return bankBranch;
	}

	/**
	 * This method get the module from method getBankBranch() and set the new record flag as true and return
	 * BankBranch()
	 * 
	 * @return BankBranch
	 */

	@Override
	public BankBranch getNewBankBranch() {
		logger.debug(Literal.ENTERING);
		BankBranch bankBranch = getBankBranch();
		bankBranch.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return bankBranch;
	}

	/**
	 * Fetch the Record Bank Branch details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BankBranch
	 */
	@Override
	public BankBranch getBankBranchById(final long id, String type) {
		logger.debug(Literal.ENTERING);
		BankBranch bankBranch = getBankBranch();

		bankBranch.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active, ParentBranch, ParentBranchDesc, Emandate, AllowedSources");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankName,PcCityName");
		}
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankBranchID =:BankBranchID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = BeanPropertyRowMapper.newInstance(BankBranch.class);

		try {
			bankBranch = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}
		logger.debug(Literal.LEAVING);
		return bankBranch;
	}

	/**
	 * Fetch the Record Bank Branch details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BankBranch
	 */
	@Override
	public int getBankBranchByIFSC(final String iFSC, long id, String type) {
		logger.debug(Literal.ENTERING);
		BankBranch bankBranch = getBankBranch();

		bankBranch.setIFSC(iFSC);
		bankBranch.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IFSC =:IFSC AND BankBranchID !=:BankBranchID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * This method Deletes the Record from the BankBranches or BankBranches_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Bank Branch by key BankBranchID
	 * 
	 * @param Bank
	 *            Branch (bankBranch)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(BankBranch bankBranch, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From BankBranches");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BankBranchID =:BankBranchID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into BankBranches or BankBranches_Temp. it fetches the available Sequence form
	 * SeqBankBranches by using getNextidviewDAO().getNextId() method.
	 *
	 * save Bank Branch
	 * 
	 * @param Bank
	 *            Branch (bankBranch)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(BankBranch bankBranch, String type) {
		logger.debug(Literal.ENTERING);
		if (bankBranch.getId() == Long.MIN_VALUE) {
			bankBranch.setId(getNextValue("SeqBankBranches"));
			logger.debug("get NextValue:" + bankBranch.getId());
		}
		//since it has foreign key
		bankBranch.setCity(StringUtils.trimToNull(bankBranch.getCity()));

		StringBuilder insertSql = new StringBuilder("Insert Into BankBranches");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active, ParentBranch, ParentBranchDesc, Emandate, Allowedsources");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:BankBranchID, :BankCode, :BranchCode, :BranchDesc, :City, :MICR, :IFSC, :AddOfBranch, :Nach, :Dd, :Dda, :Ecs, :Cheque, :Active, :ParentBranch, :ParentBranchDesc, :Emandate, :AllowedSources");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return bankBranch.getId();
	}

	/**
	 * This method updates the Record BankBranches or BankBranches_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Bank Branch by key BankBranchID and Version
	 * 
	 * @param Bank
	 *            Branch (bankBranch)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(BankBranch bankBranch, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		//since it has foreign key
		bankBranch.setCity(StringUtils.trimToNull(bankBranch.getCity()));
		StringBuilder updateSql = new StringBuilder("Update BankBranches");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set BankCode = :BankCode, BranchCode = :BranchCode, BranchDesc = :BranchDesc, City = :City, MICR = :MICR, IFSC = :IFSC");
		updateSql.append(
				",AddOfBranch = :AddOfBranch, Nach = :Nach, Dd = :Dd, Dda = :Dda, Ecs = :Ecs, Cheque = :Cheque, Active = :Active, ParentBranch = :ParentBranch, ParentBranchDesc =:ParentBranchDesc, Emandate=:Emandate, AllowedSources=:AllowedSources");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BankBranchID =:BankBranchID");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * fetch BankBranch details by it's IFSC code.
	 * 
	 * @param ifsc
	 * @param type
	 */
	@Override
	public BankBranch getBankBrachByIFSC(String ifsc, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR");
		sql.append(", IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		sql.append(", ParentBranch, ParentBranchDesc, Emandate, AllowedSources");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BankName");
		}
		sql.append(" from BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where IFSC = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { ifsc },
					new RowMapper<BankBranch>() {
						@Override
						public BankBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
							BankBranch bb = new BankBranch();

							bb.setBankBranchID(rs.getLong("BankBranchID"));
							bb.setBankCode(rs.getString("BankCode"));
							bb.setBranchCode(rs.getString("BranchCode"));
							bb.setBranchDesc(rs.getString("BranchDesc"));
							bb.setCity(rs.getString("City"));
							bb.setMICR(rs.getString("MICR"));
							bb.setIFSC(rs.getString("IFSC"));
							bb.setAddOfBranch(rs.getString("AddOfBranch"));
							bb.setNach(rs.getBoolean("Nach"));
							bb.setDd(rs.getBoolean("Dd"));
							bb.setDda(rs.getBoolean("Dda"));
							bb.setEcs(rs.getBoolean("Ecs"));
							bb.setCheque(rs.getBoolean("Cheque"));
							bb.setActive(rs.getBoolean("Active"));
							bb.setParentBranch(rs.getString("ParentBranch"));
							bb.setParentBranchDesc(rs.getString("ParentBranchDesc"));
							bb.setEmandate(rs.getBoolean("Emandate"));
							bb.setAllowedSources(rs.getString("AllowedSources"));

							if (StringUtils.trimToEmpty(type).contains("View")) {
								bb.setBankName(rs.getString("BankName"));
							}

							return bb;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public BankBranch getBankBrachByCode(String bankCode, String branchCode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR, IFSC, AddOfBranch");
		sql.append(", Nach, Dd, Dda, Ecs, Cheque, Active, ParentBranch, ParentBranchDesc, Emandate, AllowedSources");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BankName, PCCityName");
		}

		sql.append(" from BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ? and BranchCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { bankCode, branchCode },
					new RowMapper<BankBranch>() {
						@Override
						public BankBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
							BankBranch bb = new BankBranch();

							bb.setBankBranchID(rs.getLong("BankBranchID"));
							bb.setBankCode(rs.getString("BankCode"));
							bb.setBranchCode(rs.getString("BranchCode"));
							bb.setBranchDesc(rs.getString("BranchDesc"));
							bb.setCity(rs.getString("City"));
							bb.setMICR(rs.getString("MICR"));
							bb.setIFSC(rs.getString("IFSC"));
							bb.setAddOfBranch(rs.getString("AddOfBranch"));
							bb.setNach(rs.getBoolean("Nach"));
							bb.setDd(rs.getBoolean("Dd"));
							bb.setDda(rs.getBoolean("Dda"));
							bb.setEcs(rs.getBoolean("Ecs"));
							bb.setCheque(rs.getBoolean("Cheque"));
							bb.setActive(rs.getBoolean("Active"));
							bb.setParentBranch(rs.getString("ParentBranch"));
							bb.setParentBranchDesc(rs.getString("ParentBranchDesc"));
							bb.setEmandate(rs.getBoolean("Emandate"));
							bb.setAllowedSources(rs.getString("AllowedSources"));

							if (StringUtils.trimToEmpty(type).contains("View")) {
								bb.setBankName(rs.getString("BankName"));
								bb.setPCCityName(rs.getString("PCCityName"));
							}

							return bb;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record not found in BankBranches {} table/view for the specified BankCode >> {} and BranchCode >> {} ",
					type, bankCode, branchCode);
		}

		return null;
	}

	@Override
	public int getBankBrachByBank(String bankCode, String type) {
		BankBranch bankBranch = getBankBranch();
		bankBranch.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public BankBranch getBankBrachByMicr(String micr, String type) {
		logger.debug(Literal.ENTERING);

		BankBranch bankBranch = getBankBranch();
		bankBranch.setMICR(micr);

		bankBranch.setActive(true);
		StringBuilder selectSql = new StringBuilder(
				"Select b.BankCode,bb.micr,bb.branchcode,bb.bankbranchId,b.accnolength,b.BankName, bb.BranchDesc From BMTBankDetail b ");
		selectSql.append("Inner Join BankBranches bb on b.Bankcode = bb.bankCode");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MICR =:MICR AND bb.ACTIVE = :Active");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = BeanPropertyRowMapper.newInstance(BankBranch.class);

		try {
			bankBranch = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}

		logger.debug(Literal.LEAVING);
		return bankBranch;
	}

	/**
	 * Fetch the Record Bank Branch details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BankBranch
	 */
	@Override
	public int getBankBranchByMICR(final String mICR, long id, String type) {
		logger.debug(Literal.ENTERING);
		BankBranch bankBranch = getBankBranch();

		bankBranch.setMICR(mICR);
		bankBranch.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where MICR =:MICR AND BankBranchID !=:BankBranchID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public BankBranch getBankBrachByIFSCandMICR(String ifsc, String micr, String type) {
		logger.debug(Literal.ENTERING);

		BankBranch bankBranch = getBankBranch();
		bankBranch.setIFSC(ifsc);
		bankBranch.setMICR(micr);

		StringBuilder selectSql = new StringBuilder("Select BankBranchID, BankCode, BranchCode,");
		selectSql.append("BranchDesc, City, MICR, IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active, ParentBranch");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",BankName");
		}
		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IFSC =:IFSC And MICR = :MICR");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = BeanPropertyRowMapper.newInstance(BankBranch.class);

		try {
			bankBranch = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}

		logger.debug(Literal.LEAVING);
		return bankBranch;
	}

	@Override
	public boolean isDuplicateKey(String bankCode, String branchCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "BankCode = :bankCode and BranchCode = :branchCode";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BankBranches", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BankBranches_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BankBranches_Temp", "BankBranches" }, whereClause);
			break;
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bankCode", bankCode);
		paramSource.addValue("branchCode", branchCode);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public int getAccNoLengthByIFSC(String ifscCode, String type) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setIfsc(String.valueOf(ifscCode));

		StringBuilder selectSql = new StringBuilder("Select AccNoLength");

		selectSql.append(" From BankBranches");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Ifsc =:Ifsc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
			return 0;
		}
	}
}
