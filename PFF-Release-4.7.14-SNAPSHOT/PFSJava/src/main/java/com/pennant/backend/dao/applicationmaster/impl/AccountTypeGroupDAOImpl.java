package com.pennant.backend.dao.applicationmaster.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.AccountTypeGroupDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class AccountTypeGroupDAOImpl extends BasisNextidDaoImpl<AccountTypeGroup> implements AccountTypeGroupDAO {
	private static Logger logger = Logger.getLogger(AccountTypeGroupDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public AccountTypeGroupDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Account Type Group details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return AccountTypeGroup
	 */
	@Override
	public AccountTypeGroup getAccountTypeGroupById(long id, String type) {
		logger.debug(Literal.ENTERING);
		
		AccountTypeGroup accountTypeGroup = new AccountTypeGroup();
		
		accountTypeGroup.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select GroupId, GroupCode, GroupDescription, AcctTypeLevel,  ParentGroupId, GroupIsActive, " );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(type.contains("View")){
			selectSql.append(",ParentGroup,  ParentGroupDesc, AcctTypeLevel");
		}
		selectSql.append(" From AccountTypeGroup");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupId =:GroupId");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		RowMapper<AccountTypeGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				AccountTypeGroup.class);
		
		try{
			accountTypeGroup = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			accountTypeGroup = null;
		}
		
		logger.debug(Literal.LEAVING);
		return accountTypeGroup;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BMTAggrementDef or BMTAggrementDef_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Account Type Group by key AggCode
	 * 
	 * @param Account Type Group (accountTypeGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(AccountTypeGroup accountTypeGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder("Delete From AccountTypeGroup");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where GroupId =:GroupId");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL +deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * This method insert new Records into AccountTypeGroup or AccountTypeGroup_Temp.
	 *
	 * save Account Type Group  
	 * 
	 * @param Account Type Group (accountTypeGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(AccountTypeGroup accountTypeGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		if (accountTypeGroup.getId() == Long.MIN_VALUE) {
			accountTypeGroup.setId(getNextidviewDAO().getNextId("SeqAccountTypeGroup"));
			logger.debug("get NextID:" + accountTypeGroup.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into AccountTypeGroup");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (GroupId, GroupCode, GroupDescription, AcctTypeLevel, ParentGroupId, GroupIsActive, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:GroupId, :GroupCode, :GroupDescription, :AcctTypeLevel, :ParentGroupId, :GroupIsActive, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug(Literal.LEAVING);
		return String.valueOf(accountTypeGroup.getId());
	}
	
	/**
	 * This method updates the Record BMTAggrementDef or BMTAggrementDef_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Account Type Group by key AggCode and Version
	 * 
	 * @param Account Type Group (accountTypeGroup)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(AccountTypeGroup accountTypeGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);
	
		int recordCount = 0;
		StringBuilder	updateSql =new StringBuilder("Update AccountTypeGroup");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set GroupId = :GroupId, GroupCode = :GroupCode, GroupDescription = :GroupDescription, AcctTypeLevel = :AcctTypeLevel, ParentGroupId = :ParentGroupId, GroupIsActive = :GroupIsActive, " );
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, " );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, " );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where GroupId =:GroupId");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public boolean isDuplicateKey(String groupCode, TableType tableType) {
		
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "GroupCode = :GroupCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AccountTypeGroup", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AccountTypeGroup_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AccountTypeGroup_Temp", "AccountTypeGroup"}, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("GroupCode", groupCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
}