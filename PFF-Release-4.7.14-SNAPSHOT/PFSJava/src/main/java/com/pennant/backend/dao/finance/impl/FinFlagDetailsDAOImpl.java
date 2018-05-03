package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.util.WorkFlowUtil;

public class FinFlagDetailsDAOImpl implements FinFlagDetailsDAO {

	private static Logger logger = Logger.getLogger(FinFlagDetailsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinFlagDetailsDAOImpl() {
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
	 * This method set the Work Flow id based on the module name and return the new FinanceFlag
	 * 
	 * @return FinanceFlag
	 */
	@Override
	public FinFlagsDetail getfinFlagDetails() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("");

		FinFlagsDetail finFlagsDetail = new FinFlagsDetail();
		if (workFlowDetails != null) {
			finFlagsDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finFlagsDetail;

	}
	/**
	 * This method get the module from method getFinanceFlags() and set the new record flag as true and return
	 * FinanceFlag
	 * 
	 * @return FinanceFlag
	 */
	@Override
	public FinFlagsDetail getNewFinFlagsDetail()  {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail = getfinFlagDetails();
		finFlagsDetail.setNewRecord(true);
		logger.debug("Leaving");
		return finFlagsDetail;
	}
	/**
	 * This method insert new Records into FinanceFlags or FlagDetails_Temp.
	 * 
	 * save FinanceFlags
	 * 
	 * @param FinanceFlags
	 *            (finFlagsDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinFlagsDetail finFlagsDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FlagDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Reference,FlagCode,ModuleName,");
		insertSql.append(" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:Reference,:FlagCode,:ModuleName, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		        
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}
	
	@Override
	public List<FinFlagsDetail> getFinFlagsByFinRef(String finReference, String moduleName, String type) {
		logger.debug("Entering");

		FinFlagsDetail finFlagsDetail = new FinFlagsDetail();
		finFlagsDetail.setReference(finReference);
		finFlagsDetail.setModuleName(moduleName);

		StringBuilder selectSql = new StringBuilder(" Select Reference,FlagCode,ModuleName, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		
		selectSql.append(" From FlagDetails");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference AND ModuleName = :ModuleName ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		RowMapper<FinFlagsDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinFlagsDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	/**
	 * Fetch the Record  Finance Flags details by key field
	 * 
	 * @param finRef (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return finFlagsDetail
	 */
	@Override
	public FinFlagsDetail getFinFlagsByRef(final String finRef,String flagCode,String moduleName,String type) {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail = new FinFlagsDetail();
		finFlagsDetail.setReference(finRef);
		finFlagsDetail.setFlagCode(flagCode);
		finFlagsDetail.setModuleName(moduleName);

		StringBuilder selectSql = new StringBuilder(" Select Reference,FlagCode,ModuleName, ");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FlagDetails");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference AND FlagCode =:FlagCode AND ModuleName =:ModuleName");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		RowMapper<FinFlagsDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinFlagsDetail.class);
		
		try{
			finFlagsDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finFlagsDetail = null;
		}
		logger.debug("Leaving");
		return finFlagsDetail;
	}
	/**
	 * This method Deletes the Record from the FinanceFlags or FinanceFlags_Bonds_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Flags by key finRef
	 * 
	 * @param Sukuk Brokers (finRef)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteList(String finRef, String module, String type) {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail=new FinFlagsDetail();
		finFlagsDetail.setReference(finRef);
		finFlagsDetail.setModuleName(module);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FlagDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference =:Reference AND ModuleName = :ModuleName ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the FinanceFlags or FinanceFlags_Bonds_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Flags by key finRef
	 * 
	 * @param Sukuk Brokers (finRef)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(String finRef,String flagCode, String moduleName,String type) {
		logger.debug("Entering");
		FinFlagsDetail finFlagsDetail=new FinFlagsDetail();
		finFlagsDetail.setReference(finRef);
		finFlagsDetail.setFlagCode(flagCode);
		finFlagsDetail.setModuleName(moduleName);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FlagDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference =:Reference AND FlagCode =:FlagCode AND ModuleName =:ModuleName");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("schdChangeReq", schdChangeReq);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ModuleName FROM ScheduleEffectModule WHERE SchdCanModify =:schdChangeReq ");
		
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);
	}
	
	@Override
	public void savefinFlagList(List<FinFlagsDetail> finFlagsDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FlagDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Reference,FlagCode,ModuleName,");
		insertSql.append(" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:Reference,:FlagCode,:ModuleName, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		        
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(finFlagsDetail.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}
	
	@Override
    public void updateList(List<FinFlagsDetail> finFlagsDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FlagDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version = :Version," );
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, " );
		updateSql.append(" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where Reference =:Reference  AND FlagCode =:FlagCode AND ModuleName = :ModuleName ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finFlagsDetail.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	    
    }
	
	/**
	 * Method for Updating Finance Flag Details
	 * @param finFlagsDetail
	 * @param type
	 */
	@Override
	public void update(FinFlagsDetail finFlagsDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FlagDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version = :Version," );
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, " );
		updateSql.append(" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where Reference =:Reference  AND FlagCode =:FlagCode AND ModuleName=:ModuleName ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFlagsDetail);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
	}

	/**
	 * 
	 * @param finReference
	 * @param type
	 * @return Integer
	 */
	@Override
	public int getFinFlagDetailCountByRef(String finReference, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM FlagDetails WHERE Reference = :Reference ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");

		int rcdCount = 0;
		try {
			rcdCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(dae);
			rcdCount = 0;
		}

		logger.debug("Leaving");
		return rcdCount;
	}
	
	
}
