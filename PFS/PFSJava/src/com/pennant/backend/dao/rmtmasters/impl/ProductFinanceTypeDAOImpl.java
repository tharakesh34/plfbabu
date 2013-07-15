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
 * FileName    		:  ProductFinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-08-2011    														*
 *                                                                  						*
 * Modified Date    :  13-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

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
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.rmtmasters.ProductFinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.ProductFinanceType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>ProductFinanceType model</b> class.<br>
 * 
 */
public class ProductFinanceTypeDAOImpl extends
		BasisNextidDaoImpl<ProductFinanceType> implements ProductFinanceTypeDAO {

	private static Logger logger = Logger.getLogger(ProductFinanceTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new ProductFinanceType
	 * 
	 * @return ProductFinanceType
	 */
	@Override
	public ProductFinanceType getProductFinanceType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil
		.getWorkFlowDetails("ProductFinanceType");
		ProductFinanceType productFinanceType = new ProductFinanceType();
		if (workFlowDetails != null) {
			productFinanceType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return productFinanceType;
	}

	/**
	 * This method get the module from method getProductFinanceType() and set
	 * the new record flag as true and return ProductFinanceType()
	 * 
	 * @return ProductFinanceType
	 */
	@Override
	public ProductFinanceType getNewProductFinanceType() {
		logger.debug("Entering");
		ProductFinanceType productFinanceType = getProductFinanceType();
		productFinanceType.setNewRecord(true);
		logger.debug("Leaving getNewProductFinanceType()");
		return productFinanceType;
	}

	/**
	 * Fetch the Record Product Finance details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ProductFinanceType
	 */
	@Override
	public ProductFinanceType getProductFinanceTypeById(final long id,
			String type) {
		logger.debug("Entering");
		ProductFinanceType productFinanceType = getProductFinanceType();
		productFinanceType.setId(id);

		StringBuilder selectSql = new StringBuilder("Select PrdFinId, ProductCode, FinType, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTProductFinanceTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PrdFinId =:PrdFinId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				productFinanceType);
		RowMapper<ProductFinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(ProductFinanceType.class);

		try {
			productFinanceType = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters,typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			productFinanceType = null;
		}

		logger.debug("Leaving");
		return productFinanceType;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param ProductFinanceType
	 *            (productFinanceType)
	 * @return ProductFinanceType
	 */
	@Override
	public void initialize(ProductFinanceType productFinanceType) {
		super.initialize(productFinanceType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param ProductFinanceType
	 *            (productFinanceType)
	 * @return void
	 */
	@Override
	public void refresh(ProductFinanceType productFinanceType) {

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTProductFinanceTypes or
	 * RMTProductFinanceTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Product Finance by key
	 * PrdFinId
	 * 
	 * @param Product
	 *            Finance (productFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ProductFinanceType productFinanceType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTProductFinanceTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PrdFinId =:PrdFinId ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				productFinanceType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
		
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003",
						String.valueOf(productFinanceType.getPrdFinId()),"label_PrdFinId",
						productFinanceType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",
					String.valueOf(productFinanceType.getPrdFinId()),"label_PrdFinId",
					productFinanceType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the RMTProductFinanceTypes or
	 * RMTProductFinanceTypes_Temp. delete Product Finance delete by product Code
	 * 
	 * @param Product
	 *            Finance (productFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void deleteByProductCode(String productCode, String type) {
		logger.debug("Entering");
		ProductFinanceType productFinanceType= new ProductFinanceType();
		productFinanceType.setProductCode(productCode);
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTProductFinanceTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ProductCode =:ProductCode ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productFinanceType);

		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", productCode,"label_ProductCode",
					productFinanceType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTProductFinanceTypes or
	 * RMTProductFinanceTypes_Temp. it fetches the available Sequence form
	 * SeqRMTProductFinanceTypes by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Product Finance
	 * 
	 * @param Product
	 *            Finance (productFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ProductFinanceType productFinanceType, String type) {
		logger.debug("Entering");
		if (productFinanceType.getId() == Long.MIN_VALUE) {
			productFinanceType.setId(getNextidviewDAO().getNextId("SeqRMTProductFinanceTypes"));
			logger.debug("get NextID:" + productFinanceType.getId());
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTProductFinanceTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PrdFinId, ProductCode, FinType,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:PrdFinId, :ProductCode, :FinType,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productFinanceType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return productFinanceType.getId();
	}

	/**
	 * This method updates the Record RMTProductFinanceTypes or
	 * RMTProductFinanceTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Product Finance by key
	 * PrdFinId and Version
	 * 
	 * @param Product
	 *            Finance (productFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(ProductFinanceType productFinanceType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTProductFinanceTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set PrdFinId = :PrdFinId, ProductCode = :ProductCode, FinType = :FinType,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where PrdFinId =:PrdFinId");

		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productFinanceType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails=  getError("41004", 
					String.valueOf(productFinanceType.getPrdFinId()),"label_PrdFinId",
					productFinanceType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	@Override
	public List<ProductFinanceType> getFinanceType(String productCode,boolean selected, String type) {

		logger.debug("Entering");
		ProductFinanceType productFinanceType = getProductFinanceType();
		productFinanceType.setProductCode(productCode);
		
		StringBuilder selectSql = new StringBuilder("");
				
		if (selected) {
			selectSql.append("Select PrdFinId, productCode,FinType," );
			if(type.contains("View")){
				selectSql.append(" lovDescFinType, lovDescProductCode, ");
			}
			selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode," );
			selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
			selectSql.append(" From RMTProductFinanceTypes" );
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" Where productCode =:productCode");
		} else {
			selectSql.append("Select FinType,FinTypeDesc AS lovDescFinType ");
			selectSql.append(" From RMTFinanceTypes" );
			selectSql.append(" Where FinType NOT IN (Select FinType from RMTProductFinanceTypes");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" Where productCode =:productCode)");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productFinanceType);
		RowMapper<ProductFinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				ProductFinanceType.class);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);

	}
	
	public boolean checkFinanceType(String fintype,String type) {
		logger.debug("Entering");
		
		boolean result=false;
		ProductFinanceType productFinanceType = getProductFinanceType();
		productFinanceType.setFinType(fintype);

		StringBuilder selectSql = new StringBuilder("Select PrdFinId, ProductCode, FinType,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTProductFinanceTypes"+ StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(productFinanceType);
		RowMapper<ProductFinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				ProductFinanceType.class);
		
		List<ProductFinanceType>  list =  this.namedParameterJdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);
		if(list!=null && list.size()>0){
			result=true;
		}
		logger.debug("Leaving");
		return result;

	}
	
	private ErrorDetails  getError(String errorId,String prodCode,String label, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = prodCode;
		parms[0][0] = PennantJavaUtil.getLabel(label)+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
				errorId, parms[0],parms[1]), userLanguage);
	}

}