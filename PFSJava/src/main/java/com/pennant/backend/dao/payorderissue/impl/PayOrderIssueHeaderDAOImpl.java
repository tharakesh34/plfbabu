package com.pennant.backend.dao.payorderissue.impl;


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

import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;

public class PayOrderIssueHeaderDAOImpl implements PayOrderIssueHeaderDAO {

	private static Logger logger = Logger.getLogger(PayOrderIssueHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PayOrderIssueHeaderDAOImpl() {
		super();
	}
	
	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method insert new Records into PayOrderIssueHeader or PayOrderIssueHeader_Temp.
	 *
	 * save PayOrderIssueHeader 
	 * 
	 * @param paymentOrderIssue (paymentOrderIssue)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(PayOrderIssueHeader payOrderIssueHeader, String type) {
		logger.debug("Entering");
	
		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" PayOrderIssueHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference,TotalPOAmount,TotalPOCount,IssuedPOAmount,IssuedPOCount,PODueAmount,PODueCount,");
		insertSql.append(" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:FinReference,:TotalPOAmount,:TotalPOCount,:IssuedPOAmount,:IssuedPOCount,:PODueAmount,:PODueCount,");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(payOrderIssueHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}


	/**
	 * This method updates the Record PayOrderIssueHeader or PayOrderIssueHeader_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update PayOrderIssueHeader by key FinReference and Version
	 * 
	 * @param PayOrderIssueHeader
	 *            (PayOrderIssueHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(PayOrderIssueHeader paymentOrderIssue, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update PayOrderIssueHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set TotalPOAmount = :TotalPOAmount,TotalPOCount = :TotalPOCount,");
		updateSql.append(" IssuedPOAmount = :IssuedPOAmount,IssuedPOCount =:IssuedPOCount,PODueAmount = :PODueAmount,PODueCount = :PODueCount,");
		updateSql.append(" Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentOrderIssue);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	@Override
	public PayOrderIssueHeader getPayOrderIssueByHeaderRef(String finReference, String type) {
		logger.debug("Entering");

		PayOrderIssueHeader paymentOrderIssue = new PayOrderIssueHeader();
		paymentOrderIssue.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder(" Select FinReference,TotalPOAmount,TotalPOCount,IssuedPOAmount,IssuedPOCount,PODueAmount,PODueCount,");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" FinType,CustCIF,CustID,CustshrtName, FinTypeDesc, FinCcy, ");
			selectSql.append(" alwMultiPartyDisb, ");
			selectSql.append(" FinIsActive, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From PayOrderIssueHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentOrderIssue);
		RowMapper<PayOrderIssueHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(PayOrderIssueHeader.class);

		try {
			paymentOrderIssue = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			paymentOrderIssue = null;
		}
		logger.debug("Leaving");
		return paymentOrderIssue;
	}

	/**
	 * This method Deletes the Record from the PayOrderIssueHeader or PayOrderIssueHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete PayOrderIssueHeader by key FinReference
	 * 
	 * @param payment OrderIssue (paymentOrderIssue)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(PayOrderIssueHeader paymentOrderIssue, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" PayOrderIssueHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentOrderIssue);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
