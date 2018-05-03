package com.pennant.backend.dao.finance.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinTaxDetailsDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinTaxDetailsDAOImpl extends BasisNextidDaoImpl<FinTaxDetails> implements FinTaxDetailsDAO {

private static Logger logger = Logger.getLogger(FinTaxDetailsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinTaxDetailsDAOImpl() {
		super();
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(FinTaxDetails finTaxDetails, String tableType) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		
		if (finTaxDetails.getFinTaxID() == Long.MIN_VALUE) {
			finTaxDetails.setFinTaxID(getNextidviewDAO().getNextId("SeqFinTaxDetails"));
			logger.debug("get NextID:" + finTaxDetails.getFinTaxID());
		}	
		
		insertSql.append(" INSERT INTO FinTaxDetails");
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (FinTaxID, FeeID, PaidCGST, PaidIGST, PaidUGST, PaidSGST, PaidTGST, NETCGST, NETIGST, NETUGST,");
		insertSql.append(" NETSGST, NETTGST, RemFeeCGST, RemFeeIGST, RemFeeUGST, RemFeeSGST, RemFeeTGST, ActualCGST, ActualIGST, ActualUGST, ActualSGST, ActualTGST, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" VALUES (:FinTaxID, :FeeID, :PaidCGST, :PaidIGST, :PaidUGST, :PaidSGST, :PaidTGST,");
		insertSql.append(" :NetCGST, :NetIGST, :NetUGST, :NetSGST, :NetTGST, :RemFeeCGST, :RemFeeIGST, :RemFeeUGST, :RemFeeSGST, :RemFeeTGST, :ActualCGST, :ActualIGST, :ActualUGST, :ActualSGST, :ActualTGST ");
		insertSql.append(" ,:Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
	}

	@Override
	public void deleteByFeeID(long feeId, String tableType) {
		logger.debug("Entering");
		
		FinTaxDetails finTaxDetail = new FinTaxDetails();
		finTaxDetail.setFeeID(feeId);
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" DELETE FROM FinTaxDetails");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE FeeID = :FeeID ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
		
	}

	@Override
	public FinTaxDetails getFinTaxByFeeID(long feeID, String tableType) {
		logger.debug(Literal.ENTERING);

		FinTaxDetails finTaxDetail = new FinTaxDetails();
		finTaxDetail.setFeeID(feeID);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinTaxID, FeeID, PaidCGST, PaidIGST, PaidUGST, PaidSGST, PaidTGST, NETCGST, NETIGST, NETUGST, NETSGST, NETTGST,");
		selectSql.append(" RemFeeCGST, RemFeeIGST, RemFeeUGST, RemFeeSGST, RemFeeTGST, ActualCGST, ActualIGST, ActualUGST, ActualSGST, ActualTGST, ");
		
		if (StringUtils.trimToEmpty(tableType).contains("View")) {
			selectSql.append("  ");
		}
		
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM FinTaxDetails");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE  FeeID = :FeeID ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxDetail);
		RowMapper<FinTaxDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTaxDetails.class);

		try {
			finTaxDetail = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finTaxDetail = null;
		}

		logger.debug(Literal.LEAVING);
		
		return finTaxDetail;
	}

	@Override
	public void update(FinTaxDetails finTaxDetails, String type) {
		logger.debug("Entering");
		
		int recordCount = 0; 
		StringBuilder updateSql = new StringBuilder("Update FinTaxDetails");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set ActualCGST = :ActualCGST, ActualIGST = :ActualIGST, ActualUGST = :ActualUGST, ActualSGST = :ActualSGST, ActualTGST = :ActualTGST,");
		updateSql.append(" PaidCGST = :PaidCGST, PaidIGST = :PaidIGST, PaidUGST = :PaidUGST, PaidSGST = :PaidSGST, PaidTGST = :PaidTGST,");
		updateSql.append(" NetCGST = :NetCGST, NetIGST = :NetIGST, NetUGST = :NetUGST, NetSGST = :NetSGST, NetTGST = :NetTGST,");
		updateSql.append(" RemFeeCGST = :RemFeeCGST, RemFeeIGST = :RemFeeIGST, RemFeeUGST = :RemFeeUGST, RemFeeSGST = :RemFeeSGST, RemFeeTGST = :RemFeeTGST");
		updateSql.append(" ,Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinTaxID =:FinTaxID");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			//throw new ConcurrencyException();
		}
		
		logger.debug("Leaving");
		
	}
	
	@Override
	public void delete(FinTaxDetails finTaxDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinTaxDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinTaxID = :FinTaxID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTaxDetails);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

}
