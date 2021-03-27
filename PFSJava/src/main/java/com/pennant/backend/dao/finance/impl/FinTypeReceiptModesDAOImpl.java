package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinTypeReceiptModesDAO;
import com.pennant.backend.model.financemanagement.FinTypeReceiptModes;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class FinTypeReceiptModesDAOImpl extends BasicDao<FinTypeReceiptModes> implements FinTypeReceiptModesDAO {
	private static Logger logger = LogManager.getLogger(FinTypeReceiptModesDAOImpl.class);

	public FinTypeReceiptModesDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into FinTypeReceiptModes or FinTypeVASProducts_Temp.
	 * 
	 * save FinTypeReceiptModes
	 * 
	 * @param FinTypeReceiptModes
	 *            (finTypeReceiptModes)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinTypeReceiptModes finTypeReceiptModes, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinTypeReceiptModes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType,ReceiptMode,");
		insertSql.append(
				" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:FinType,:ReceiptMode, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");

		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeReceiptModes);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	/**
	 * This method Deletes the Record from the FinTypeReceiptModes or FinTypeVASProducts_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Finance Flags by key finRef
	 * 
	 * @param Sukuk
	 *            Brokers (finType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(String finType, String receiptMode, String type) {
		logger.debug("Entering");
		FinTypeReceiptModes finTypeReceiptModes = new FinTypeReceiptModes();
		finTypeReceiptModes.setFinType(finType);
		finTypeReceiptModes.setReceiptMode(receiptMode);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinTypeReceiptModes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType AND ReceiptMode =:ReceiptMode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeReceiptModes);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Updating Finance FinTypeReceiptModes Details
	 * 
	 * @param finTypeReceiptModes
	 * @param type
	 */
	@Override
	public void update(FinTypeReceiptModes finTypeReceiptModes, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinTypeReceiptModes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ReceiptMode = :ReceiptMode, Version = :Version,");
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		updateSql.append(
				" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType  AND ReceiptMode =:ReceiptMode ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeReceiptModes);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public List<FinTypeReceiptModes> getReceiptModesByFinType(String finType, String type) {
		logger.debug("Entering");

		FinTypeReceiptModes finTypeReceiptModes = new FinTypeReceiptModes();
		finTypeReceiptModes.setFinType(finType);

		StringBuilder selectSql = new StringBuilder(" Select FinType, ReceiptMode, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinTypeReceiptModes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeReceiptModes);
		RowMapper<FinTypeReceiptModes> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeReceiptModes.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Record Finance Flags details by key field
	 * 
	 * @param finRef
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return finFlagsDetail
	 */
	@Override
	public FinTypeReceiptModes getFinTypeReceiptModes(final String finType, String receiptMode, String type) {
		logger.debug("Entering");
		FinTypeReceiptModes finTypeReceiptModes = new FinTypeReceiptModes();
		finTypeReceiptModes.setFinType(finType);
		finTypeReceiptModes.setReceiptMode(receiptMode);

		StringBuilder selectSql = new StringBuilder(" Select FinType,ReceiptMode, ");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinTypeReceiptModes");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND VasProduct =:VasProduct ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeReceiptModes);
		RowMapper<FinTypeReceiptModes> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeReceiptModes.class);

		try {
			finTypeReceiptModes = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTypeReceiptModes = null;
		}
		logger.debug("Leaving");
		return finTypeReceiptModes;
	}

	@Override
	public void deleteList(String finType, String type) {
		logger.debug("Entering");
		FinTypeReceiptModes finTypeReceiptModes = new FinTypeReceiptModes();
		finTypeReceiptModes.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinTypeReceiptModes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeReceiptModes);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
