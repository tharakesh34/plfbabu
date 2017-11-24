package com.pennant.backend.dao.servicetasklog.impl;

import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.mchange.util.DuplicateElementException;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.servicetasklog.ServiceTaskDAO;
import com.pennant.backend.model.servicetask.ServiceTaskDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class ServiceTaskDAOImpl extends BasisNextidDaoImpl<ServiceTaskDetail> implements ServiceTaskDAO {
	private static Logger logger = Logger.getLogger(ServiceTaskDAOImpl.class);

	public ServiceTaskDAOImpl() {
		super();
	}

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	protected DefaultTransactionDefinition	transDef;
	private PlatformTransactionManager	transactionManager;
	
	@Override
	public void save(ServiceTaskDetail serviceTaskDetail, String type) {
		logger.debug(Literal.ENTERING);
		
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// begin transaction
		txStatus = transactionManager.getTransaction(txDef);
		
		if (serviceTaskDetail.getId() == Long.MIN_VALUE) {
			serviceTaskDetail.setId(getNextidviewDAO().getNextId("SeqServiceTaskDetails"));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into ServiceTaskDetails ");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (TaskExecutionId, ServiceModule, Reference, ServiceTaskId, ");
		insertSql.append(" ServiceTaskName , UserId, ExecutedTime, Status, Remarks)");
		insertSql.append(" Values( :TaskExecutionId, :ServiceModule, :Reference, :ServiceTaskId,");
		insertSql.append(" :ServiceTaskName , :UserId, :ExecutedTime, :Status, :Remarks)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(serviceTaskDetail);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			//commit
			transactionManager.commit(txStatus);
		} catch (DuplicateElementException dee) {
			logger.error("Exception", dee);
			transactionManager.rollback(txStatus);
			throw dee;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	@Override
	public List<ServiceTaskDetail> getServiceTaskDetails(String module, String reference, String serviceTaskName) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ServiceModule", module);
		source.addValue("Reference", reference);
		source.addValue("ServiceTaskName", serviceTaskName);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT TaskExecutionId, ServiceModule, Reference, ServiceTaskId,");
		selectSql.append(" ServiceTaskName, UserId, ExecutedTime, Status, Remarks From ServiceTaskDetails");
		selectSql.append(" where ServiceModule=:ServiceModule AND Reference=:Reference AND ServiceTaskName=:ServiceTaskName");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug(Literal.LEAVING);
		try {
			RowMapper<ServiceTaskDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ServiceTaskDetail.class);
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(dae);
			return Collections.emptyList();
		}
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
