package com.pennanttech.pff.logging.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.IDBInterfaceLoggingDao;
import com.pennanttech.pff.model.IDBInterfaceLogDetail;

public class IDBInterfaceLoggingDaoImpl extends SequenceDao<InterfaceLogDetail> implements IDBInterfaceLoggingDao {
	private static Logger logger = Logger.getLogger(IDBInterfaceLoggingDaoImpl.class);

	protected DefaultTransactionDefinition transDef;
	private DataSourceTransactionManager transactionManager;
	

	@Override
	public int save(IDBInterfaceLogDetail detail) {
		logger.debug(Literal.ENTERING);
		int count = 0;
		StringBuilder insertSql = new StringBuilder("Insert Into  IDB_INTERFACES_LOG");
		insertSql.append("(Interface_Name,Ref_Num,Start_Date,Records_Processed,Status,Status_desc)");
		insertSql.append(" Values (:interfaceName,:refNum,:startDate,:recordProcessed,:status,:statusDesc)");
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		try {
			TransactionStatus txStatus = transactionManager.getTransaction(transDef);
			this.jdbcTemplate.update(insertSql.toString(), paramSource);
			transactionManager.commit(txStatus);
			txStatus.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return count;
	}
	
	@Override
	public void update(IDBInterfaceLogDetail detail) {
		logger.debug(Literal.ENTERING);		
		StringBuilder updateSql = new StringBuilder("Update IDB_INTERFACES_LOG ");
		updateSql.append(" set End_Date=:EndDate, RECORDS_PROCESSED =:RecordProcessed , Status =:Status,Interface_Info=:InterfaceInfo ");
		updateSql.append(" where Interface_Name = :InterfaceName and Ref_Num =:RefNum");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		logger.debug("selectSql: " + updateSql.toString());
		
		try {
			TransactionStatus txStatus = transactionManager.getTransaction(transDef);
			this.jdbcTemplate.update(updateSql.toString(), beanParameters);
			transactionManager.commit(txStatus);
			txStatus.flush();
		} catch (Exception e) {
			logger.error("Exception", e);
			
		}
		
	}
	@Override
	public long getSequence() {
		return getNextValue("SEQ_EXTERANAL_IDBINTERFACE");
	}

	@Override
	public long getSequence(String seqName) {
		return getNextValue(seqName);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.transactionManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(60);
	}
}
