package com.pennant.backend.dao.ext.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.ext.ExtTablesDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.ExtTable;
import com.pennant.backend.model.finance.salary.FinSalariedPayment;

public class ExtTablesDAOImpl extends BasisCodeDAO<ExtTable> implements ExtTablesDAO {

	private static Logger				logger	= Logger.getLogger(ExtTablesDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ExtTablesDAOImpl() {
		super();
	}

	@Override
	public List<ExtTable> getPDDetails() {

		ExtTable autoHunting = new ExtTable();

		StringBuilder selectSql = new StringBuilder("SELECT Id, AccountBalance,Processed ");
		selectSql.append(" From AHB_L_AUTOHUNT_UPD where Processed=0 ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(autoHunting);
		RowMapper<ExtTable> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtTable.class);

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void deleteByid(ExtTable autoHunting) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("DELETE From AHB_L_AUTOHUNT_UPD");
		sql.append(" where Id = :Id");

		logger.debug("Sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(autoHunting);

		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
	}


	@Override
	public void updateByid(ExtTable autoHunting) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Update AHB_L_AUTOHUNT_UPD set Processed=1 ");
		sql.append(" where Id = :Id");

		logger.debug("Sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(autoHunting);

		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateBatch(List<ExtTable> autoHunting) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Update AHB_L_AUTOHUNT_UPD set Processed=1 ");
		sql.append(" where Id = :Id");

		logger.debug("Sql: " + sql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(autoHunting.toArray());

		this.namedParameterJdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		logger.debug("Leaving");
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
	 * Saving ControlTable Data
	 * 
	 * @param extTable
	 */
	@Override
	public void saveCtrlTableData(ExtTable extTable) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into CONTROL_TABLE");
		insertSql.append(" (Sys_Code, Cob_Date, Target_Sys_Code, Status)");
		insertSql.append(" Values(:Sys_Code, :Cob_Date, :Target_Sys_Code, :Status)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extTable);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	/**
	 * update Control Table Data
	 * 
	 * @param extTable
	 */
	@Override
	public void updateCtrlTableStatus(String syscode,Date cobdate) {
		logger.debug("Entering");
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("Sys_Code", syscode);
		source.addValue("Cob_Date", cobdate);
		
		StringBuilder insertSql = new StringBuilder();		
		insertSql.append("Update CONTROL_TABLE set STATUS=1 where SYS_CODE=:Sys_Code and COB_DATE= :Cob_Date");
		
		logger.debug("insertSql: "+ insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Saving Bench Mark Data
	 * 
	 * @param extTable
	 */
	@Override
	public void saveBenchMarkData(ExtTable extTable) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into FIN_BENCHMARK_INFO");
		insertSql.append(" (Key_Code, Key_desc, Cob_Date, Key_Value)");
		insertSql.append(" Values(:Key_Code, :Key_desc, :Cob_Date, :Key_Value)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extTable);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}


	/**
	 * 
	 */
	@Override
	public String insertPushData(final String tabdata, final String ouptut,
			final String messageReturn) {
		final StringBuilder builder = new StringBuilder("{ call SP_InsertPushData(?, ?, ?) }");

		logger.debug("selectSql: " + builder.toString());

		try{		

			this.namedParameterJdbcTemplate.getJdbcOperations().execute(
					new CallableStatementCreator() {
						public CallableStatement createCallableStatement(Connection con) throws SQLException{
							CallableStatement cs = con.prepareCall(builder.toString());
							cs.setString(1, tabdata); 
							cs.setString(2, ouptut); 
							cs.setString(3, messageReturn); 

							return cs;
						}
					},
					new CallableStatementCallback<Object>() {
						public Object doInCallableStatement(CallableStatement cs) throws SQLException{
							cs.execute();
							return "";
						}
					}
					);

		}catch (Exception e) {
			logger.info(e);
		}
		logger.debug("Leaving");
		return "";
	}

	/**
	 * Method for save OD Account details into table for Auto hunting process
	 * 
	 * @param referenceId
	 * @param repayAccNum
	 */
	@Override
	public void saveODAccDetails(int referenceId, String repayAccNum) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", referenceId);
		source.addValue("AccountNumber", repayAccNum);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinODAccounts");
		insertSql.append(" (Id, AccountNumber)");
		insertSql.append(" Values(:Id, :AccountNumber)");

		logger.debug("insertSql: "+ insertSql.toString());

		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * Method for delete all the records in FinODAccounts
	 * 
	 */
	@Override
	public void deleteODAccDetails() {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM FinODAccounts");

		logger.debug("deleteSql: "+ deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), new MapSqlParameterSource());
		logger.debug("Leaving");
	}
	
	/**
	 * Method for adding Salaried Finance Next installment details
	 * 
	 */
	@Override
	public void saveFinSalariedPayment(FinSalariedPayment salariedPayment) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append(" Insert Into FinSalariedPayment");
		insertSql.append(" (FinReference, PriAccount, SecAccount, NextPayDate, NextPayment, ValueDate)");
		insertSql.append(" Values(:FinReference, :PriAccount, :SecAccount, :NextPayDate, :NextPayment, :ValueDate)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salariedPayment);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}


}
