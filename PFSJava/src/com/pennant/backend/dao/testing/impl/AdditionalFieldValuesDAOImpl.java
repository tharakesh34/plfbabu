package com.pennant.backend.dao.testing.impl;

import java.util.List;

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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.testing.AdditionalFieldValuesDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.testing.AdditionalFieldValues;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

public class AdditionalFieldValuesDAOImpl extends BasisCodeDAO<AdditionalFieldValues> implements AdditionalFieldValuesDAO{
	
private static Logger logger = Logger.getLogger(AdditionalFieldValuesDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
    public AdditionalFieldValues getAdditionalFieldValues() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("AdditionalFieldValues");
		AdditionalFieldValues additionalFieldValues= new AdditionalFieldValues();
		if (workFlowDetails!=null){
			additionalFieldValues.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return additionalFieldValues;
    }

	@Override
    public AdditionalFieldValues getNewAdditionalFieldValues() {
		logger.debug("Entering");
		AdditionalFieldValues additionalFieldValues = getAdditionalFieldValues();
		additionalFieldValues.setNewRecord(true);
		logger.debug("Leaving");
		return additionalFieldValues;
    }
	@Override
	public List<AdditionalFieldValues> getAddfeldList(String module, String type) {
		logger.debug("Entering");
		AdditionalFieldValues additionalFieldValues=getAdditionalFieldValues();	
		additionalFieldValues.setModuleName(module);
		List<AdditionalFieldValues> additionalFieldValuesList;

		StringBuilder selectSql = new StringBuilder("Select ModuleName, FieldName ,FieldValue");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From AdditionalFieldValues");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleName =:ModuleName");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFieldValues);
		RowMapper<AdditionalFieldValues> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AdditionalFieldValues.class);

		try{
			additionalFieldValuesList= this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			additionalFieldValuesList = null;
		}
		logger.debug("Leaving");
		return additionalFieldValuesList;
	}

	@Override
    public AdditionalFieldValues getAdditionalFieldValuesById(String id, String type) {
		logger.debug("Entering");
		AdditionalFieldValues additionalFieldValues = getAdditionalFieldValues();
		
		additionalFieldValues.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select ModuleName, FieldName ,FieldValue");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			
		}
		selectSql.append(" From AdditionalFieldValues");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleName =:ModuleName and FieldName =:FieldName");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFieldValues);
		RowMapper<AdditionalFieldValues> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AdditionalFieldValues.class);
		
		try{
			additionalFieldValues = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			additionalFieldValues = null;
		}
		logger.debug("Leaving");
		return additionalFieldValues;
    }

	@SuppressWarnings("serial")
    @Override
    public void update(AdditionalFieldValues additionalFieldValues, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update AdditionalFieldValues");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ModuleName = :ModuleName, FieldName = :FieldName, FieldValue = :FieldValue");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ModuleName =:ModuleName AND FieldName =:FieldName");
		
		/*if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}*/
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFieldValues);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",additionalFieldValues.getId() ,additionalFieldValues.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	    
    }

	@SuppressWarnings("serial")
    @Override
    public void delete(AdditionalFieldValues additionalFieldValues, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From AdditionalFieldValues");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ModuleName =:ModuleName and FieldName =:FieldName");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFieldValues);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",additionalFieldValues.getId() ,additionalFieldValues.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",additionalFieldValues.getId() ,additionalFieldValues.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	    
    }

	@Override
    public String save(AdditionalFieldValues additionalFieldValues, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into AdditionalFieldValues");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ModuleName, FieldName, FieldValue");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ModuleName, :FieldName, :FieldValue");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(additionalFieldValues);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return additionalFieldValues.getId();
    }

	@Override
    public void initialize(AdditionalFieldValues additionalFieldValues) {
		super.initialize(additionalFieldValues);
	    
    }

	@Override
    public void refresh(AdditionalFieldValues entity) {
	    // TODO Auto-generated method stub
	    
    }
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	private ErrorDetails  getError(String errorId, String Code, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = Code;
		parms[0][0] = PennantJavaUtil.getLabel("label_Code")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
	
}
