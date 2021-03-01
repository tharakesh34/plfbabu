package com.pennanttech.pff.logging.dao.impl;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static Logger logger = LogManager.getLogger(IDBInterfaceLoggingDaoImpl.class);

	protected DefaultTransactionDefinition transDef;
	private DataSourceTransactionManager transactionManager;

	@Override
	public int save(IDBInterfaceLogDetail detail) {
		logger.debug(Literal.ENTERING);
		int count = 0;
		StringBuilder insertSql = new StringBuilder("Insert Into  IDB_INTERFACES_LOG");
		insertSql.append("(Interface_Name,Ref_Num,Start_Date,Records_Processed,Status,Status_desc,EodDate)");
		insertSql.append(" Values (:interfaceName,:refNum,:startDate,:recordProcessed,:status,:statusDesc,:EodDate)");
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(transDef);
			this.jdbcTemplate.update(insertSql.toString(), paramSource);
			transactionManager.commit(txStatus);
			txStatus.flush();
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
		} finally {
			if (txStatus != null) {
				txStatus.flush();
			}
		}
		logger.debug(Literal.LEAVING);
		return count;
	}

	@Override
	public void update(IDBInterfaceLogDetail detail) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update IDB_INTERFACES_LOG ");
		sql.append(" set End_Date=:EndDate, RECORDS_PROCESSED =:RecordProcessed , Status =:Status,");
		sql.append(" Status_desc=:statusDesc,Interface_Info=:InterfaceInfo, EodDate =:EodDate");
		sql.append(" where Interface_Name = :InterfaceName and Ref_Num =:RefNum");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		logger.debug("selectSql: " + sql.toString());
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(transDef);
			this.jdbcTemplate.update(sql.toString(), beanParameters);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
		} finally {
			if (txStatus != null) {
				txStatus.flush();
			}
		}
	}

	@Override
	public long getSequence() {
		long nextValue = 0;
		nextValue = getNextValue("SEQ_EXTERANAL_IDBINTERFACE");
		return nextValue;
	}

	@Override
	public long getSequence(String seqName) {
		long nextValue = 0;
		nextValue = getNextValue(seqName);
		return nextValue;
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
