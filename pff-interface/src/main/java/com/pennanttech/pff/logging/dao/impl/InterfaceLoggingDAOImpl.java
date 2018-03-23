package com.pennanttech.pff.logging.dao.impl;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class InterfaceLoggingDAOImpl implements InterfaceLoggingDAO {
	
	private static final Logger			logger		= Logger.getLogger(InterfaceLoggingDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;
	protected DefaultTransactionDefinition	transDef;
	private PlatformTransactionManager	transactionManager;
		
	@Override
	public void save(InterfaceLogDetail interfaceLogDetail) {
		logger.debug(Literal.ENTERING);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// begin transaction
		txStatus = transactionManager.getTransaction(txDef);
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into InterfaceLogDetails ");
		insertSql.append(" (ServiceName, Reference, EndPoint, Request, ");
		insertSql.append(" Response, ReqSentOn, RespReceivedOn, Status, ErrorCode, ErrorDesc)");
		insertSql.append(" Values(:ServiceName, :Reference, :EndPoint, :Request, ");
		insertSql.append(" :Response, :ReqSentOn, :RespReceivedOn, :Status, :ErrorCode, :ErrorDesc)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			//commit
			transactionManager.commit(txStatus);
		} catch (Exception dee) {
			logger.error("Exception", dee);
			transactionManager.rollback(txStatus);
			throw dee;
		}
		logger.debug(Literal.LEAVING);
	}
	
	
	
	/**
	 * Method for fetch the ExtendedFieldDetails based on given fieldaNames
	 * 
	 * @param fieldNames
	 * @return extendedFieldDetailList
	 * @throws Exception
	 */
	public List<ExtendedFieldDetail> getExtendedFieldDetailsByFieldName(Set<String> fieldNames){
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Select ModuleId, FieldName, FieldType, ");
		sql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, ");
		sql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, ");
		sql.append(" FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement,Editable, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ExtendedFieldDetail  WHERE FIELDNAME IN(:fieldNames)");
		paramMap.addValue("fieldNames", fieldNames);
		logger.debug("selectSql: " + sql.toString());
		try {
			RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(ExtendedFieldDetail.class);
			logger.debug(Literal.LEAVING);
			return this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return null;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
