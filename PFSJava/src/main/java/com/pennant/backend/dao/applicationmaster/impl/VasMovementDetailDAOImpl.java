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
 * FileName    		:  VasMovementDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.VasMovementDetailDAO;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * DAO methods implementation for the <b>VasMovementDetail model</b> class.<br>
 * 
 */
public class VasMovementDetailDAOImpl extends BasicDao<VasMovementDetail> implements VasMovementDetailDAO {
	private static Logger logger = Logger.getLogger(VasMovementDetailDAOImpl.class);

	public VasMovementDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Check List Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VasMovementDetail
	 */
	@Override
	public List<VasMovementDetail> getVasMovementDetailById(final long id, String type) {
		logger.debug("Entering");
		VasMovementDetail vasMovementDetail = new VasMovementDetail();
		vasMovementDetail.setVasMovementId(id);

		List<VasMovementDetail> vasMovementDetailList = null;

		StringBuilder selectSql = new StringBuilder(
				"Select VasMovementId,VasMovementDetailId,FinReference,VasReference,MovementDate,MovementAmt,VasProvider,VasProduct,VasAmount");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId");
		selectSql.append(" From VasMovementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where VasMovementId =:VasMovementId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovementDetail);
		RowMapper<VasMovementDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VasMovementDetail.class);

		try {
			vasMovementDetailList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vasMovementDetailList = null;
		}
		logger.debug("Leaving");
		return vasMovementDetailList;
	}

	/**
	 * This method Deletes the Record from the RMTVasMovementDetails or RMTVasMovementDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Check List Details by key VasMovementId
	 * 
	 * @param Check
	 *            List Details (checkListDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(VasMovementDetail vasMovementDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From VasMovementDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VasMovementDetailId =:VasMovementDetailId and vasReference=:vasReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovementDetail);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the RMTVasMovementDetail or RMTVasMovementDetail_Temp.
	 * 
	 * delete Educational Expenses by key loanRefNumber
	 * 
	 */
	public void delete(long vasMovementDetailId, String type) {
		logger.debug("Entering");
		VasMovementDetail vasMovementDetail = new VasMovementDetail();
		vasMovementDetail.setVasMovementId(vasMovementDetailId);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From VasMovementDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where VasMovementId =:VasMovementId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovementDetail);
		logger.debug("DeleteSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTVasMovementDetails or RMTVasMovementDetails_Temp.
	 *
	 * save Check List Details
	 * 
	 * @param Check
	 *            List Details (checkListDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(VasMovementDetail checkListDetail, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder("Insert Into VasMovementDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (VasMovementId,VasMovementDetailId,FinReference,VasReference,MovementDate,MovementAmt,VasProvider,VasProduct,VasAmount");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		insertSql.append(", RecordType, WorkflowId)");
		insertSql.append(
				" Values( :VasMovementId,:VasMovementDetailId,:FinReference,:VasReference,:MovementDate,:MovementAmt,:VasProvider,:VasProduct,:VasAmount");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId ");
		insertSql.append(", :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return checkListDetail.getId();
	}

	/**
	 * This method updates the Record RMTVasMovementDetails or RMTVasMovementDetails_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Check List Details by key VasMovementId and Version
	 * 
	 * @param Check
	 *            List Details (checkListDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VasMovementDetail checkListDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update VasMovementDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set MovementDate=:MovementDate,MovementAmt=:MovementAmt");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus");
		updateSql.append(
				", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		updateSql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where VasMovementDetailId =:VasMovementDetailId and VasMovementId = :VasMovementId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(checkListDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<VasMovementDetail> getVasMovementDetailByRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		VasMovementDetail vasMovementDetail = new VasMovementDetail();
		vasMovementDetail.setFinReference(finReference);
		List<VasMovementDetail> vasMovementDetailList = null;
		StringBuilder selectSql = new StringBuilder("Select");
		selectSql.append(" VasMovementId,VasMovementDetailId,FinReference,VasReference,");
		selectSql.append(" MovementDate, MovementAmt,VasProvider,VasProduct,VasAmount");
		selectSql.append(" From VasMovementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovementDetail);
		RowMapper<VasMovementDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VasMovementDetail.class);

		try {
			vasMovementDetailList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vasMovementDetailList = null;
		}
		logger.debug(Literal.LEAVING);
		return vasMovementDetailList;
	}

	@Override
	public BigDecimal getVasMovementDetailByRef(String finReference, String finStartDate, String finEndDate,
			String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", DateUtil.parse(finStartDate, "dd/MM/yyyy"));
		source.addValue("FinEndDate", DateUtil.parse(finEndDate, "dd/MM/yyyy"));

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT SUM(MovementAmt) ");
		selectSql.append(" From VasMovementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		selectSql.append(" And MOVEMENTDATE >=:FinstartDate and MOVEMENTDATE <=:FinEndDate");
		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal movementAmt = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		if (movementAmt == null) {
			movementAmt = BigDecimal.ZERO;
		}
		return movementAmt;
	}
}