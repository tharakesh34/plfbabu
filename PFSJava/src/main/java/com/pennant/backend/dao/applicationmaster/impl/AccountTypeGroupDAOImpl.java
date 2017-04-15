package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AccountTypeGroupDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

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
		logger.debug("Entering");
		AccountTypeGroup accountTypeGroup = new AccountTypeGroup();
		
		accountTypeGroup.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select GroupId, GroupCode, GroupDescription, AcctTypeLevel,  ParentGroupId,  " );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(type.contains("View")){
			selectSql.append(",ParentGroup,  ParentGroupDesc, AcctTypeLevel");
		}
		selectSql.append(" From AccountTypeGroup");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupId =:GroupId");
		
		logger.debug("selectSql: " + selectSql.toString());
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
		logger.debug("Leaving");
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
	@SuppressWarnings("serial")
	public void delete(AccountTypeGroup accountTypeGroup, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From AccountTypeGroup");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where GroupId =:GroupId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",accountTypeGroup.getGroupId() ,
						accountTypeGroup.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",accountTypeGroup.getGroupId() ,
					accountTypeGroup.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
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
	public long save(AccountTypeGroup accountTypeGroup,String type) {
		logger.debug("Entering");
		if (accountTypeGroup.getId() == Long.MIN_VALUE) {
			accountTypeGroup.setId(getNextidviewDAO().getNextId("SeqAccountTypeGroup"));
			logger.debug("get NextID:" + accountTypeGroup.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into AccountTypeGroup");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (GroupId, GroupCode, GroupDescription, AcctTypeLevel, ParentGroupId, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:GroupId, :GroupCode, :GroupDescription, :AcctTypeLevel, :ParentGroupId, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return accountTypeGroup.getId();
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
	@SuppressWarnings("serial")
	@Override
	public void update(AccountTypeGroup accountTypeGroup, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update AccountTypeGroup");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set GroupId = :GroupId, GroupCode = :GroupCode, GroupDescription = :GroupDescription, AcctTypeLevel = :AcctTypeLevel, ParentGroupId = :ParentGroupId, " );
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, " );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, " );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where GroupId =:GroupId");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountTypeGroup);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",accountTypeGroup.getGroupId() ,
					accountTypeGroup.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, long groupId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = String.valueOf(groupId);
		parms[0][0] = PennantJavaUtil.getLabel("label_GroupId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

	
}