/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ExtendedFieldDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.dao.solutionfactory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>ExtendedFieldDetail model</b> class.<br>
 */
public class ExtendedFieldDetailDAOImpl extends BasisNextidDaoImpl<ExtendedFieldDetail> implements ExtendedFieldDetailDAO {

	private static Logger logger = Logger.getLogger(ExtendedFieldDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new ExtendedFieldDetail
	 * 
	 * @return ExtendedFieldDetail
	 */
	@Override
	public ExtendedFieldDetail getExtendedFieldDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ExtendedFieldDetail");
		ExtendedFieldDetail extendedFieldDetail= new ExtendedFieldDetail();
		if (workFlowDetails!=null){
			extendedFieldDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return extendedFieldDetail;
	}

	/**
	 * This method get the module from method getExtendedFieldDetail() and set
	 * the new record flag as true and return ExtendedFieldDetail()
	 * 
	 * @return ExtendedFieldDetail
	 */
	@Override
	public ExtendedFieldDetail getNewExtendedFieldDetail() {
		logger.debug("Entering");
		ExtendedFieldDetail extendedFieldDetail = getExtendedFieldDetail();
		extendedFieldDetail.setNewRecord(true);
		logger.debug("Leaving");
		return extendedFieldDetail;
	}

	/**
	 * Fetch the Record  Extended Field Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ExtendedFieldDetail
	 */
	@Override
	public ExtendedFieldDetail getExtendedFieldDetailById(final long id,String name,String type) {
		logger.debug("Entering");

		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setId(id);
		extendedFieldDetail.setFieldName(name);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, " );
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, " );
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, " );
		selectSql.append(" FieldMaxValue, FieldUnique,  ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescModuleName,lovDescSubModuleName");

		}
		selectSql.append(" From ExtendedFieldDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleId =:ModuleId AND FieldName =:FieldName ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldDetail.class);

		try{
			extendedFieldDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			extendedFieldDetail = null;
		}
		logger.debug("Leaving");
		return extendedFieldDetail;
	}

	/**
	 * This method initialise the Record.
	 * @param ExtendedFieldDetail (extendedFieldDetail)
	 * @return ExtendedFieldDetail
	 */
	@Override
	public void initialize(ExtendedFieldDetail extendedFieldDetail) {
		super.initialize(extendedFieldDetail);
	}

	/**
	 * This method refresh the Record.
	 * @param ExtendedFieldDetail (extendedFieldDetail)
	 * @return void
	 */
	@Override
	public void refresh(ExtendedFieldDetail extendedFieldDetail) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the ExtendedFieldDetail or ExtendedFieldDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Extended Field Detail by key ModuleId
	 * 
	 * @param Extended Field Detail (extendedFieldDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ExtendedFieldDetail extendedFieldDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ExtendedFieldDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ModuleId =:ModuleId AND FieldName =:FieldName");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",extendedFieldDetail.getFieldName() ,extendedFieldDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",extendedFieldDetail.getFieldName() ,extendedFieldDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion of Extended Detail List of ExtendedDetail 
	 */
	public void deleteByExtendedFields(final long id,String type) {
		logger.debug("Entering");
		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setId(id);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From ExtendedFieldDetail" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where ModuleId =:ModuleId ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into ExtendedFieldDetail or ExtendedFieldDetail_Temp.
	 * it fetches the available Sequence form SeqExtendedFieldDetail by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Extended Field Detail 
	 * 
	 * @param Extended Field Detail (extendedFieldDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ExtendedFieldDetail extendedFieldDetail,String type) {
		logger.debug("Entering");

		StringBuilder insertSql =new StringBuilder("Insert Into ExtendedFieldDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ModuleId, FieldName, FieldType, FieldLength, FieldPrec, FieldLabel, " );
		insertSql.append(" FieldMandatory, FieldConstraint, FieldSeqOrder, FieldList, " );
		insertSql.append(" FieldDefaultValue, FieldMinValue, FieldMaxValue, FieldUnique,  ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ModuleId, :FieldName, :FieldType, :FieldLength, :FieldPrec, " );
		insertSql.append(" :FieldLabel, :FieldMandatory, :FieldConstraint, :FieldSeqOrder, " );
		insertSql.append(" :FieldList, :FieldDefaultValue, :FieldMinValue, " );
		insertSql.append(" :FieldMaxValue, :FieldUnique, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug("Error"+e.getMessage());
		}

		logger.debug("Leaving");
		return extendedFieldDetail.getId();
	}

	/**
	 * This method updates the Record ExtendedFieldDetail or ExtendedFieldDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Extended Field Detail by key ModuleId and Version
	 * 
	 * @param Extended Field Detail (extendedFieldDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(ExtendedFieldDetail extendedFieldDetail,String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder	updateSql =new StringBuilder("Update ExtendedFieldDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ModuleId = :ModuleId, FieldName = :FieldName, FieldType = :FieldType, " );
		updateSql.append(" FieldLength = :FieldLength, FieldPrec = :FieldPrec, FieldLabel = :FieldLabel, " );
		updateSql.append(" FieldMandatory = :FieldMandatory, FieldConstraint = :FieldConstraint, " );
		updateSql.append(" FieldSeqOrder = :FieldSeqOrder, " );
		updateSql.append(" FieldList = :FieldList, FieldDefaultValue = :FieldDefaultValue, " );
		updateSql.append(" FieldMinValue = :FieldMinValue, FieldMaxValue = :FieldMaxValue, " );
		updateSql.append(" FieldUnique = :FieldUnique, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ModuleId =:ModuleId AND FieldName =:FieldName");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",extendedFieldDetail.getFieldName() ,extendedFieldDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	@Override
	public List<ExtendedFieldDetail> getExtendedFieldDetailById(long id, String type) {
		logger.debug("Entering");
		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();

		extendedFieldDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, " );
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, " );
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, " );
		selectSql.append(" FieldMaxValue, FieldUnique,  ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescModuleName,lovDescSubModuleName ");
		}

		selectSql.append(" From ExtendedFieldDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleId =:ModuleId order by FieldSeqOrder ASC");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				ExtendedFieldDetail.class);

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}

	/**
	 * Method for using getting the list of details for Additional Details by FinanceType
	 */
	@Override
	public List<ExtendedFieldDetail> getExtendedFieldDetailBySubModule(String subModule, String type) {
		logger.debug("Entering");

		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setLovDescSubModuleName(subModule);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, " );
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, " );
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, " );
		selectSql.append(" FieldMaxValue, FieldUnique, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescModuleName,lovDescSubModuleName , ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, " );
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From ExtendedFieldDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where lovDescSubModuleName =:lovDescSubModuleName order by FieldSeqOrder ASC");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldDetail.class);

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}

	@Override
	public List<ExtendedFieldDetail> getExtendedFieldNameById(long id, String type) {
		logger.debug("Entering");

		ExtendedFieldDetail extendedFieldDetail = new ExtendedFieldDetail();
		extendedFieldDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, FieldName, FieldType, " );
		selectSql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, " );
		selectSql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, " );
		selectSql.append(" FieldMaxValue, FieldUnique, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, " );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From ExtendedFieldDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleId =:ModuleId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldDetail);
		RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldDetail.class);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}

	@SuppressWarnings("serial")
	public void alter(ExtendedFieldDetail extendedFieldDetail,String type, boolean dropCol, boolean reCreateCol) {
		logger.debug("Entering");

		if(dropCol){
			StringBuilder alterdelsql = new StringBuilder(" ALTER TABLE " + extendedFieldDetail.getLovDescSubModuleName());
			alterdelsql.append(StringUtils.trimToEmpty(type));
			alterdelsql.append(" DROP COLUMN " + extendedFieldDetail.getFieldName() +" ;" );
			try {
				this.namedParameterJdbcTemplate.getJdbcOperations().update(alterdelsql.toString());
			} catch (Exception e) {
				// if not Exists , do Nothing
			}		
		}

		if(reCreateCol){

			int recordCount = 0;
			StringBuilder altersql = new StringBuilder(" ALTER TABLE " + extendedFieldDetail.getLovDescSubModuleName());
			altersql.append(StringUtils.trimToEmpty(type));

			if(PennantConstants.RECORD_TYPE_UPD.equals(extendedFieldDetail.getRecordType())){

				if ("|TXT|MTXT|DLIST|SLIST|DMLIST|RADIO|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					altersql.append(" ALTER Column " + extendedFieldDetail.getFieldName() + " nvarchar ("
							+ extendedFieldDetail.getFieldLength() + ") ;");
				} else if ("|AMT|RATE|NUMERIC|PRCT|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					if("AMT".equals(extendedFieldDetail.getFieldType())){
						altersql.append(" ALTER Column  " + extendedFieldDetail.getFieldName() + " decimal ("
								+ extendedFieldDetail.getFieldLength() + ", " + 0 + ") ;");
					}else{
						altersql.append(" ALTER Column  " + extendedFieldDetail.getFieldName() + " decimal ("
								+ extendedFieldDetail.getFieldLength() + ", " + extendedFieldDetail.getFieldPrec() + ") ;");
					}
				} else if ("|DATE|DATETIME|TIME|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					altersql.append(" ALTER Column  " + extendedFieldDetail.getFieldName() + " smalldatetime ;");
				} else if ("|CHKB|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					altersql.append(" ALTER Column  " + extendedFieldDetail.getFieldName() + " nchar(1) ;");
				}
				logger.debug("insertSql: " + altersql.toString());
			}else{

				if ("|TXT|MTXT|DLIST|SLIST|DMLIST|RADIO|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					altersql.append(" add " + extendedFieldDetail.getFieldName() + " nvarchar ("
							+ extendedFieldDetail.getFieldLength() + ") ;");
				} else if ("|AMT|RATE|NUMERIC|PRCT|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					if("AMT".equals(extendedFieldDetail.getFieldType())){
						altersql.append(" add  " + extendedFieldDetail.getFieldName() + " decimal ("
								+ extendedFieldDetail.getFieldLength() + ", " + 0 + ") ;");
					}else{
						altersql.append(" add  " + extendedFieldDetail.getFieldName() + " decimal ("
								+ extendedFieldDetail.getFieldLength() + ", " + extendedFieldDetail.getFieldPrec() + ") ;");
					}
				} else if ("|DATE|DATETIME|TIME|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					altersql.append(" add " + extendedFieldDetail.getFieldName() + " smalldatetime ;");
				} else if ("|CHKB|".contains("|" + extendedFieldDetail.getFieldType() + "|")) {
					altersql.append(" add " + extendedFieldDetail.getFieldName() + " nchar(1) ;");
				}

				logger.debug("updateSql: " + altersql.toString());
			}

			recordCount = this.namedParameterJdbcTemplate.getJdbcOperations().update(altersql.toString());

			if (recordCount < 0) {
				logger.debug("Error Update Method Count :"+recordCount);
				ErrorDetails errorDetails= getError("41004",extendedFieldDetail.getFieldName() ,extendedFieldDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}
		logger.debug("Leaving");
	}

	@Override
	public void saveAdditional(final String id, HashMap<String, Object> mappedValues, String type,String tableName) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO "+tableName);
		insertSql.append(StringUtils.trimToEmpty(type));
		
		if(mappedValues.containsKey("FinReference")){
			mappedValues.remove("FinReference");
		}
		mappedValues.put("FinReference", id);
		
		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String columnames = "";
		String columnValues = "";
		for (int i = 0; i < list.size(); i++) {
			if(i < list.size()-1){
				columnames = columnames  + list.get(i)+ " , ";
				columnValues = columnValues  + ":" + list.get(i)+ " , ";
			}else{
				columnames = columnames  + list.get(i);
				columnValues = columnValues  + ":" + list.get(i);
			}
		}
		insertSql.append(" (" + columnames + ") values (" + columnValues  + ")");
		logger.debug("insertSql: " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), mappedValues);
		logger.debug("Leaving");

	}

	/**
	 * Method for Retrive Extended Field Values depend on SubModule
	 */
	@Override
	public Map<String, Object> retrive(String tableName,String id, String type) {
		logger.debug("Entering");
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuilder selectSql = new StringBuilder("Select * from "+tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference ='" + id + "'");

		logger.debug("selectSql: " + selectSql.toString());
		try{
			map = this.namedParameterJdbcTemplate.queryForMap(selectSql.toString(), map);
		}catch (EmptyResultDataAccessException e) {
			if("_Temp".equals(type)){
				selectSql = new StringBuilder("Select * from "+tableName);
				selectSql.append(" where FinReference ='" + id + "'");

				logger.debug("selectSql: " + selectSql.toString());
				try{
					map = this.namedParameterJdbcTemplate.queryForMap(selectSql.toString(), map);
				}catch (EmptyResultDataAccessException ex) {
					map = null;
				}
			}
		}
		logger.debug("Leaving");
		return map;
	}

	/**
	 * Method for Find Extended Field Values are Exist or not
	 */
	@Override
	public boolean isExist(String tableName,String id, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select FinReference from "+tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where FinReference ='" + id + "'");

		logger.debug("selectSql: " + selectSql.toString());
		try{
			this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(selectSql.toString(), String.class);
			return true;
		}catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void updateAdditional(HashMap<String, ?> mappedValues,final String id, String type,String tableName) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" UPDATE "+tableName);
		insertSql.append(StringUtils.trimToEmpty(type));
		List<String> list = new ArrayList<String>(mappedValues.keySet());
		String query = "";

		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				query=" set "+list.get(i)+"=:"+list.get(i);
			} else {
				query = query + "," + list.get(i)+"=:"+list.get(i);
			}
		}
		insertSql.append(query);
		insertSql.append(" where FinReference='" + id + "'");

		logger.debug("insertSql: " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), mappedValues);
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the ExtendedFieldDetail or ExtendedFieldDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Extended Field Detail by key ModuleId
	 * 
	 * @param Extended Field Detail (extendedFieldDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteAdditional(final String id,String tableName,String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From "+tableName);
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" where FinReference ='" + id +"'");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.getJdbcOperations().update(deleteSql.toString());
		logger.debug("Leaving");
	}


	private ErrorDetails  getError(String errorId, String moduleId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = moduleId;
		parms[0][0] = PennantJavaUtil.getLabel("label_FieldName")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}