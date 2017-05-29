package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.DealerResponseDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.finance.DealerResponse;

public class DealerResponseDAOImpl extends BasisNextidDaoImpl<DealerResponse> implements
        DealerResponseDAO {

	private static Logger logger = Logger.getLogger(DealerResponseDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DealerResponseDAOImpl() {
		super();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * get DealerResponse List based on finance reference
	 * 
	 */
	@Override
	public List<DealerResponse> getDealerResponse(String finReference, String type) {
		logger.debug("Entering");
		DealerResponse dealerResponse = new DealerResponse();
		dealerResponse.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
		        "Select DealerResponseId, DealerId, FinReference, ");
		selectSql.append(" UniqueReference ,AttachmentName, ReqUserRole, ReqUserid,");
		selectSql.append(" Status,RequestDate ,ResponseDate, ResponseRef,Processed ");
		selectSql.append(" From DealerResponse");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealerResponse);
		RowMapper<DealerResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(DealerResponse.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	/**
	 * get DealerResponse List based on finance reference
	 * 
	 */
	@Override
	public List<DealerResponse> getByProcessed(String finReference, boolean processed, String type) {
		logger.debug("Entering");
		DealerResponse dealerResponse = new DealerResponse();
		dealerResponse.setFinReference(finReference);
		dealerResponse.setProcessed(processed);

		StringBuilder selectSql = new StringBuilder(
		        "Select DealerResponseId, DealerId, FinReference, ");
		selectSql.append(" UniqueReference ,AttachmentName, ReqUserRole, ReqUserid,");
		selectSql.append(" Status,RequestDate ,ResponseDate, ResponseRef,Processed ");
		selectSql.append(" From DealerResponse");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and Processed = :Processed");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealerResponse);
		RowMapper<DealerResponse> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(DealerResponse.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}
	/**
	 * get DealerResponse processed count based on finance reference
	 * 
	 */
	@Override
	public int getCountByProcessed(String finReference, boolean processed, String type) {
		logger.debug("Entering");
		DealerResponse dealerResponse = new DealerResponse();
		dealerResponse.setFinReference(finReference);
		dealerResponse.setProcessed(processed);
		
		StringBuilder selectSql = new StringBuilder("Select count(*) From DealerResponse");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and Processed = :Processed");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealerResponse);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Integer.class);
	}

	@Override
	public void updateSatus(DealerResponse dealerResponse, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update DealerResponse");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  Status = :Status, ResponseDate=:ResponseDate, ResponseRef=:ResponseRef where DealerResponseId = :DealerResponseId ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealerResponse);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void updateProcessed(String finReference, boolean processed, String type) {
		logger.debug("Entering");
		DealerResponse dealerResponse = new DealerResponse();
		dealerResponse.setFinReference(finReference);
		dealerResponse.setProcessed(processed);
		StringBuilder updateSql = new StringBuilder("Update DealerResponse");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Processed = :Processed where FinReference = :FinReference ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealerResponse);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * save DealerResponse details
	 */
	@Override
	public long save(DealerResponse dealerResponse, String type) {
		logger.debug("Entering");

		if (dealerResponse.getDealerResponseId() == Long.MIN_VALUE) {
			dealerResponse.setDealerResponseId(getNextidviewDAO().getNextId("SeqDealerResponse"));
		}
		StringBuilder insertSql = new StringBuilder("Insert Into DealerResponse");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( DealerResponseId, DealerId, FinReference, ");
		insertSql.append(" UniqueReference ,AttachmentName, ReqUserRole, ReqUserid,");
		insertSql.append(" Status,RequestDate ,ResponseDate, ResponseRef,Processed )");

		insertSql.append(" Values( :DealerResponseId, :DealerId, :FinReference,");
		insertSql.append(" :UniqueReference , :AttachmentName, :ReqUserRole, :ReqUserid,");
		insertSql.append(" :Status, :RequestDate , :ResponseDate, :ResponseRef, :Processed )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealerResponse);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return dealerResponse.getId();
	}

}
