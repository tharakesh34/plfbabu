/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : FinanceMainDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified
 * Date : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance.impl;

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

import com.pennant.backend.dao.finance.IndicativeTermDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>IndicativeTermDetail model</b> class.<br>
 * 
 */

public class IndicativeTermDetailDAOImpl extends BasisCodeDAO<IndicativeTermDetail> implements IndicativeTermDetailDAO {

	private static Logger logger = Logger.getLogger(IndicativeTermDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public IndicativeTermDetailDAOImpl() {
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
	 * Method for Fetching Indicative Term Sheet Details By Finance Reference Key
	 */
	@Override
    public IndicativeTermDetail getIndicateTermByRef(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");
		
		IndicativeTermDetail detail = new IndicativeTermDetail();
		detail.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, RpsnName, RpsnDesg, CustId, FacilityType, Pricing, " );
		selectSql.append(" Repayments, LCPeriod, UsancePeriod, SecurityClean, SecurityName, Utilization, Commission, Purpose, Guarantee, Covenants, DocumentsRequired, TenorYear, TenorMonth, TenorDesc, " );
		selectSql.append(" TransactionType, AgentBank, OtherDetails, TotalFacility, TotalFacilityCCY, UnderWriting, UnderWritingCCY, PropFinalTake, PropFinalTakeCCY," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,lovDescCustCIF,lovDescCustShrtName, lovDescRpsnDesgName, lovDescFacilityType ,lovDescFinStartDate,lovDescMaturityDate " );
		}
		if (isWIF) {
			selectSql.append(" FROM WIFIndicativeTermDetail");
		} else {
			selectSql.append(" FROM IndicativeTermDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<IndicativeTermDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IndicativeTermDetail.class);
		
		try {
			detail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			detail = null;
		}
		logger.debug("Leaving");
		return detail;
    }

	@Override
    public void save(IndicativeTermDetail detail, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFIndicativeTermDetail");
		} else {
			insertSql.append(" IndicativeTermDetail");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, RpsnName, RpsnDesg, CustId, FacilityType, Pricing, ");
		insertSql.append(" Repayments, LCPeriod, UsancePeriod, SecurityClean, SecurityName, Utilization, Commission, Purpose, Guarantee, Covenants,DocumentsRequired, TenorYear, TenorMonth, TenorDesc, ");
		insertSql.append(" TransactionType, AgentBank, OtherDetails, TotalFacility, TotalFacilityCCY, UnderWriting, UnderWritingCCY, PropFinalTake, PropFinalTakeCCY," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :RpsnName, :RpsnDesg, :CustId, :FacilityType, :Pricing, ");
		insertSql.append(" :Repayments, :LCPeriod, :UsancePeriod, :SecurityClean, :SecurityName, :Utilization, :Commission, :Purpose, :Guarantee,  :Covenants, :DocumentsRequired,:TenorYear,:TenorMonth,:TenorDesc, ");
		insertSql.append(" :TransactionType, :AgentBank, :OtherDetails, :TotalFacility, :TotalFacilityCCY, :UnderWriting, :UnderWritingCCY, :PropFinalTake, :PropFinalTakeCCY," );
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
    }

	@Override
    public void update(IndicativeTermDetail detail, String type, boolean isWIF) {
		
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");
		if (isWIF) {
			updateSql.append(" WIFIndicativeTermDetail");
		} else {
			updateSql.append(" IndicativeTermDetail");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RpsnName=:RpsnName, RpsnDesg=:RpsnDesg, CustId=:CustId, " );
		updateSql.append(" FacilityType=:FacilityType, Pricing=:Pricing, Repayments=:Repayments, LCPeriod=:LCPeriod, " );
		updateSql.append(" UsancePeriod=:UsancePeriod, SecurityClean=:SecurityClean, SecurityName=:SecurityName, " );
		updateSql.append(" Utilization=:Utilization, Commission=:Commission, Purpose=:Purpose, Guarantee=:Guarantee, " );
		updateSql.append(" Covenants=:Covenants, DocumentsRequired=:DocumentsRequired,TenorYear = :TenorYear," );
		updateSql.append(" TenorMonth = :TenorMonth, TenorDesc = :TenorDesc,TransactionType=:TransactionType,");
		updateSql.append(" AgentBank=:AgentBank, OtherDetails=:OtherDetails, TotalFacility=:TotalFacility, TotalFacilityCCY=:TotalFacilityCCY," );
		updateSql.append(" UnderWriting=:UnderWriting, UnderWritingCCY=:UnderWritingCCY, PropFinalTake=:PropFinalTake, PropFinalTakeCCY=:PropFinalTakeCCY," );
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		if (!type.endsWith("_Temp") && !isWIF) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving"); 
    }

	@Override
    public void delete(IndicativeTermDetail detail, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (isWIF) {
			deleteSql.append(" WIFIndicativeTermDetail");
		} else {
			deleteSql.append(" IndicativeTermDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),  beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
    }

}