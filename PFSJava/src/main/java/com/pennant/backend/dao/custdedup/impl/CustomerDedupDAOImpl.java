package com.pennant.backend.dao.custdedup.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class CustomerDedupDAOImpl extends BasicDao<CustomerDedup> implements CustomerDedupDAO {
	private static Logger logger = Logger.getLogger(CustomerDedupDAOImpl.class);
	
	public CustomerDedupDAOImpl() {
		super();
	}
	
	@Override
    public void saveList(List<CustomerDedup> insertList,String type) {
		logger.debug("Entering");
		
    	StringBuilder insertSql = new StringBuilder();
    	insertSql.append("Insert Into CustomerDedupDetail");
    	insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , CustCIF , CustFName , CustLName , ");
		insertSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		insertSql.append(" DedupRule , Override , OverrideUser ,Module )");
		insertSql.append(" Values(:FinReference , :CustCIF , :CustFName , :CustLName , ");
		insertSql.append(" :CustShrtName , :CustDOB , :CustCRCPR ,:CustPassportNo , :MobileNumber , :CustNationality , ");
		insertSql.append(" :DedupRule , :Override , :OverrideUser, :Module )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(insertList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	    
    }

	@Override
    public void updateList(List<CustomerDedup> updateList) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerDedupDetail Set CustFName = :CustFName," );
		updateSql.append(" CustLName = :CustLName , CustShrtName = :CustShrtName, CustDOB = :CustDOB, " );
		updateSql.append(" CustCRCPR= :CustCRCPR, CustPassportNo = :CustPassportNo,MobileNumber = :MobileNumber, CustNationality = :CustNationality," );
		updateSql.append(" DedupRule = :DedupRule, Override = :Override, OverrideUser = :OverrideUser, Module=:Module " );
		updateSql.append(" Where FinReference =:FinReference  AND CustCIF =:CustCIF");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public List<CustomerDedup> fetchOverrideCustDedupData(String finReference, String queryCode,String module) {
		logger.debug("Entering");
		
		CustomerDedup dedup = new CustomerDedup();
		dedup.setFinReference(finReference);
		dedup.setDedupRule(queryCode);
		dedup.setModule(module);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference , CustCIF , CustFName , CustLName , ");
		selectSql.append(" CustShrtName , CustDOB , CustCRCPR ,CustPassportNo , MobileNumber , CustNationality , ");
		selectSql.append(" DedupRule , Override , OverrideUser,Module ");
		selectSql.append(" From CustomerDedupDetail ");
		selectSql.append(" Where FinReference =:FinReference AND DedupRule LIKE('%");
		selectSql.append(queryCode);
		selectSql.append("%') and Module=:Module ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		RowMapper<CustomerDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerDedup.class);

		logger.debug("Leaving");
		
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	
	
	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup,String sqlQuery) {
		List<CustomerDedup> rowTypes = null;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT * FROM CustomersDedup_View ");
		if(!StringUtils.isBlank(sqlQuery)) {
			selectSql.append(StringUtils.trimToEmpty(sqlQuery));
			selectSql.append(" AND");
		} else {
			selectSql.append(" Where");
		}
		selectSql.append(" CustId != :CustId ");

		logger.debug("selectSql: " +  selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		ParameterizedBeanPropertyRowMapper<CustomerDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerDedup.class);

		try{
			rowTypes = this.jdbcTemplate.query(selectSql.toString(),beanParameters,typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			dedup = null;
		}
		logger.debug("Leaving");
		return rowTypes;
	}

	@Override
    public void moveData(String finReference, String suffix) {

		logger.debug(" Entering ");
		try {
	        if (StringUtils.isBlank(suffix)) {
	            return;
	        }
	        
	        MapSqlParameterSource map=new MapSqlParameterSource();
	        map.addValue("FinReference", finReference);
	        
	        StringBuilder selectSql = new StringBuilder();
	        selectSql.append(" SELECT * FROM CustomerDedupDetail");
	        selectSql.append(" WHERE FinReference = :FinReference ");
	        
	        RowMapper<CustomerDedup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerDedup.class);
	        List<CustomerDedup> list = this.jdbcTemplate.query(selectSql.toString(), map,typeRowMapper);
	        
	        if (list!=null && !list.isEmpty()) {
	        	saveList(list,suffix);
            }
	        
        } catch (DataAccessException e) {
	     logger.debug(e);
        }
	    logger.debug(" Leaving ");
	    
    }

}
