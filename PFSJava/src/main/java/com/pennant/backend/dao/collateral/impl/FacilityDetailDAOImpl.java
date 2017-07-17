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
 * * FileName : FacilityDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-12-2013 * *
 * Modified Date : 04-12-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 04-12-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.collateral.impl;

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

import com.pennant.backend.dao.collateral.FacilityDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FacilityDetail model</b> class.<br>
 * 
 */

public class FacilityDetailDAOImpl extends BasisCodeDAO<FacilityDetail> implements
        FacilityDetailDAO {

	private static Logger logger = Logger.getLogger(FacilityDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FacilityDetailDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FacilityDetail
	 * 
	 * @return FacilityDetail
	 */

	@Override
	public FacilityDetail getFacilityDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FacilityDetail");
		FacilityDetail facilityDetail = new FacilityDetail();
		if (workFlowDetails != null) {
			facilityDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return facilityDetail;
	}

	/**
	 * This method get the module from method getFacilityDetail() and set the new record flag as true and return
	 * FacilityDetail()
	 * 
	 * @return FacilityDetail
	 */

	@Override
	public FacilityDetail getNewFacilityDetail() {
		logger.debug("Entering");
		FacilityDetail facilityDetail = getFacilityDetail();
		facilityDetail.setNewRecord(true);
		logger.debug("Leaving");
		return facilityDetail;
	}

	/**
	 * Fetch the Record Facility Detail details by key field
	 * 
	 * @param caf
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FacilityDetail
	 */
	@Override
	public FacilityDetail getFacilityDetailById(final String facref, String type) {
		logger.debug("Entering");
		FacilityDetail facilityDetail = getFacilityDetail();

		facilityDetail.setFacilityRef(facref);

		StringBuilder selectSql = new StringBuilder("Select CAFReference, FacilityRef,TermSheetRef," );
		selectSql.append(" FacilityFor, FacilityType, FacilityCCY, Exposure, ExistingLimit, NewLimit, FinanceAmount, " );
		selectSql.append(" Pricing, Repayments, RateType, LCPeriod, UsancePeriod, SecurityClean, SecurityDesc, Utilization, " );
		selectSql.append(" Commission, Purpose, CustID , StartDate, MaturityDate,Guarantee,Covenants,DocumentsRequired,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode," );
		selectSql.append(" TenorYear, TenorMonth, TenorDesc, " );
		selectSql.append(" TransactionType, AgentBank, OtherDetails, TotalFacility, TotalFacilityCcy, " );
		selectSql.append(" UnderWriting, UnderWritingCcy, PropFinalTake, PropFinalTakeCcy, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,Revolving,CCYformat,FacilityTypeDesc ");
		}
		selectSql.append(" From FacilityDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  FacilityRef=:FacilityRef ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityDetail);
		RowMapper<FacilityDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FacilityDetail.class);

		try {
			facilityDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			facilityDetail = null;
		}
		logger.debug("Leaving");
		return facilityDetail;
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
	 * This method Deletes the Record from the FacilityDetails or FacilityDetails_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Facility Detail by key CAFReference
	 * 
	 * @param Facility
	 *            Detail (facilityDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FacilityDetail facilityDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FacilityDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FacilityRef=:FacilityRef");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityDetail);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FacilityDetails or FacilityDetails_Temp.
	 * 
	 * save Facility Detail
	 * 
	 * @param Facility
	 *            Detail (facilityDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FacilityDetail facilityDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FacilityDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CAFReference, FacilityRef, TermSheetRef,FacilityFor, FacilityType, " );
		insertSql.append(" FacilityCCY, Exposure, ExistingLimit, NewLimit, FinanceAmount, Pricing, Repayments," );
		insertSql.append(" RateType, LCPeriod, UsancePeriod, SecurityClean, SecurityDesc, Utilization, Commission, " );
		insertSql.append(" Purpose,CustID,StartDate,MaturityDate,Guarantee,Covenants,DocumentsRequired,");
		insertSql.append(" TenorYear,TenorMonth,TenorDesc," );
		insertSql.append(" TransactionType, AgentBank, OtherDetails, TotalFacility, TotalFacilityCcy, " );
		insertSql.append(" UnderWriting, UnderWritingCcy, PropFinalTake, PropFinalTakeCcy, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CAFReference, :FacilityRef, :TermSheetRef,:FacilityFor, :FacilityType, " );
		insertSql.append(" :FacilityCCY, :Exposure, :ExistingLimit, :NewLimit, :FinanceAmount, :Pricing, :Repayments," );
		insertSql.append(" :RateType, :LCPeriod, :UsancePeriod, :SecurityClean, :SecurityDesc, :Utilization, :Commission," );
		insertSql.append(" :Purpose,:CustID,:StartDate,:MaturityDate,:Guarantee,:Covenants,:DocumentsRequired, ");
		insertSql.append(" :TenorYear,:TenorMonth,:TenorDesc," );
		insertSql.append(" :TransactionType, :AgentBank, :OtherDetails, :TotalFacility, :TotalFacilityCcy, " );
		insertSql.append(" :UnderWriting, :UnderWritingCcy, :PropFinalTake, :PropFinalTakeCcy, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return facilityDetail.getId();
	}

	/**
	 * This method updates the Record FacilityDetails or FacilityDetails_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Facility Detail by key CAFReference and Version
	 * 
	 * @param Facility
	 *            Detail (facilityDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FacilityDetail facilityDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FacilityDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CAFReference = :CAFReference, TermSheetRef=:TermSheetRef,");
		updateSql.append(" FacilityFor = :FacilityFor, FacilityType = :FacilityType, FacilityCCY = :FacilityCCY, Exposure = :Exposure," );
		updateSql.append(" ExistingLimit = :ExistingLimit, NewLimit = :NewLimit, FinanceAmount = :FinanceAmount, Pricing = :Pricing," );
		updateSql.append(" Repayments = :Repayments, RateType = :RateType, LCPeriod = :LCPeriod, UsancePeriod = :UsancePeriod,");
		updateSql.append(" SecurityClean = :SecurityClean, SecurityDesc = :SecurityDesc, Utilization = :Utilization, " );
		updateSql.append(" Commission = :Commission, Purpose = :Purpose ,CustID=:CustID, StartDate=:StartDate, MaturityDate=:MaturityDate,");
		updateSql.append(" Guarantee=:Guarantee, Covenants=:Covenants, DocumentsRequired=:DocumentsRequired,");
		updateSql.append(" TenorYear=:TenorYear,TenorMonth=:TenorMonth,TenorDesc=:TenorDesc," );
		updateSql.append(" TransactionType = :TransactionType, AgentBank = :AgentBank, OtherDetails = :OtherDetails, TotalFacility = :TotalFacility, TotalFacilityCcy = :TotalFacilityCcy, " );
		updateSql.append(" UnderWriting = :UnderWriting, UnderWritingCcy = :UnderWritingCcy, PropFinalTake = :PropFinalTake, PropFinalTakeCcy = :PropFinalTakeCcy, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus," );
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, " );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FacilityRef=:FacilityRef");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<FacilityDetail> getFacilityDetailsByCAF(String cafReference, String type) {
		logger.debug("Entering");
		FacilityDetail facilityDetail = getFacilityDetail();
		facilityDetail.setId(cafReference);
		StringBuilder selectSql = new StringBuilder("Select CAFReference, FacilityRef,TermSheetRef," );
		selectSql.append(" FacilityFor, FacilityType, FacilityCCY, Exposure, ExistingLimit, NewLimit, FinanceAmount, " );
		selectSql.append(" Pricing, Repayments, RateType, LCPeriod, UsancePeriod, SecurityClean, SecurityDesc, Utilization, " );
		selectSql.append(" Commission, Purpose, CustID , StartDate, MaturityDate,Guarantee,Covenants,DocumentsRequired,");
		selectSql.append(" TenorYear,TenorMonth,TenorDesc," );
		selectSql.append(" TransactionType, AgentBank, OtherDetails, TotalFacility, TotalFacilityCcy, " );
		selectSql.append(" UnderWriting, UnderWritingCcy, PropFinalTake, PropFinalTakeCcy, " );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,Revolving,CCYformat,FacilityTypeDesc ");
		}
		selectSql.append(" From FacilityDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CAFReference =:CAFReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityDetail);
		RowMapper<FacilityDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FacilityDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	public void deleteByCAF(String cafReference, String type) {
		logger.debug("Entering");
		FacilityDetail facilityDetail = new FacilityDetail();
		facilityDetail.setCAFReference(cafReference);
		StringBuilder deleteSql = new StringBuilder("Delete From FacilityDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CAFReference =:CAFReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(facilityDetail);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
}