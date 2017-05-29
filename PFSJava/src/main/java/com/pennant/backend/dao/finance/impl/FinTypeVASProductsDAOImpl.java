package com.pennant.backend.dao.finance.impl;

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

import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.util.WorkFlowUtil;

public class FinTypeVASProductsDAOImpl implements FinTypeVASProductsDAO {
	
	private static Logger logger = Logger.getLogger(FinTypeVASProductsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinTypeVASProductsDAOImpl() {
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
	public FinTypeVASProducts getfinTypeVASProducts() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("");

		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		if (workFlowDetails != null) {
			finTypeVASProducts.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finTypeVASProducts;

	}
	/**
	 * This method get the module from method getfinTypeVASProducts() and set the new record flag as true and return
	 * FinTypeVASProducts
	 * 
	 * @return FinTypeVASProducts
	 */
	@Override
	public FinTypeVASProducts getNewfinTypeVASProducts()  {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = getfinTypeVASProducts();
		finTypeVASProducts.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeVASProducts;
	}

	/**
	 * This method insert new Records into FinTypeVASProducts or FinTypeVASProducts_Temp.
	 * 
	 * save FinTypeVASProducts
	 * 
	 * @param FinTypeVASProducts
	 *            (finTypeVASProducts)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinTypeVASProducts finTypeVASProducts, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinTypeVASProducts");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType,VasProduct,Mandatory,");
		insertSql.append(" Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId)");
		insertSql.append(" values (:FinType,:VasProduct,:Mandatory, ");
		insertSql.append(" :Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		        
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}
	
	/**
	 * This method Deletes the Record from the FinTypeVASProducts or FinTypeVASProducts_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Flags by key finRef
	 * 
	 * @param Sukuk Brokers (finType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(String finType, String vasProduct,String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts=new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		finTypeVASProducts.setVasProduct(vasProduct);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinTypeVASProducts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType AND VasProduct =:VasProduct");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Updating Finance FinTypeVASProducts Details
	 * @param finTypeVASProducts
	 * @param type
	 */
	@Override
	public void update(FinTypeVASProducts finTypeVASProducts, String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinTypeVASProducts");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Mandatory = :Mandatory, Version = :Version," );
		updateSql.append(" LastMntBy = :LastMntBy , LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, " );
		updateSql.append(" RoleCode= :RoleCode, NextRoleCode = :NextRoleCode,TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where FinType =:FinType  AND VasProduct =:VasProduct ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		
	}
	
	@Override
	public List<FinTypeVASProducts> getVASProductsByFinType(String finType,  String type) {
		logger.debug("Entering");

		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);

		StringBuilder selectSql = new StringBuilder(" Select T1.FinType, T1.VasProduct, T1.Mandatory, ");
		selectSql.append(" T1.Version , T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode, T1.TaskId,");
		selectSql.append(" T1.NextTaskId, T1.RecordType, T1.WorkflowId, T3.ProductType, T4.ProductCtgDesc, T5.DealerName ManufacturerDesc, T2.RecAgainst, T2.VasFee");
		selectSql.append(" From FinTypeVASProducts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" T1 LEFT OUTER JOIN ");
		selectSql.append(" VasStructure  T2 ON T1.VasProduct = T2.ProductCode LEFT OUTER JOIN ");
		selectSql.append(" VasProductType  T3 ON T3.ProductType = T2.ProductType LEFT OUTER JOIN ");
		selectSql.append(" VasProductCategory  T4 ON T3.ProductCtg = T4.ProductCtg  LEFT OUTER JOIN ");
		selectSql.append(" AMTVehicleDealer T5 ON T2.ManufacturerId = T5.DealerId  ");
		selectSql.append(" Where FinType =:FinType  ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		RowMapper<FinTypeVASProducts> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinTypeVASProducts.class);
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
	public FinTypeVASProducts getFinTypeVASProducts(final String finType,String vasProduct, String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts = new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		finTypeVASProducts.setVasProduct(vasProduct);

		StringBuilder selectSql = new StringBuilder(" Select FinType,VasProduct,Mandatory, ");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinTypeVASProducts");

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND VasProduct =:VasProduct ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		RowMapper<FinTypeVASProducts> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinTypeVASProducts.class);
		
		try{
			finTypeVASProducts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finTypeVASProducts = null;
		}
		logger.debug("Leaving");
		return finTypeVASProducts;
	}
	
	@Override
	public void deleteList(String finType, String type) {
		logger.debug("Entering");
		FinTypeVASProducts finTypeVASProducts=new FinTypeVASProducts();
		finTypeVASProducts.setFinType(finType);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinTypeVASProducts");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeVASProducts);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}	

}

