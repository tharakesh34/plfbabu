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
 * FileName    		:  AddressTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.systemmasters.InterestCertificateDAO;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

/**
 * DAO methods implementation for the <b>AddressType model</b> class.<br>
 */
public class InterestCertificateDAOImpl extends BasicDao<InterestCertificate> implements InterestCertificateDAO {
	private static Logger logger = Logger.getLogger(InterestCertificateDAOImpl.class);
	
	public InterestCertificateDAOImpl() {
		super();
	}
	@Override
	public InterestCertificate getInterestCertificateDetails(String finReference ) throws ParseException{
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
         
		StringBuilder selectSql = new StringBuilder("SELECT distinct FINREFERENCE,CUSTNAME,CUSTADDRHNBR,CUSTADDRSTREET,COUNTRYDESC,CUSTADDRSTATE, ");
		selectSql.append("CUSTADDRCITY,CUSTADDRZIP,CUSTEMAIL,CUSTPHONENUMBER,FINTYPEDESC,COAPPLICANT,FINASSETVALUE,");
		selectSql.append("EFFECTIVERATE,ENTITYCODE,ENTITYDESC,ENTITYPANNUMBER,ENTITYADDRHNBR,");
		selectSql.append("ENTITYFLATNBR,ENTITYADDRSTREET,ENTITYSTATE,ENTITYCITY,FINCCY,FinAmount,fintype,custflatnbr,EntityZip ");
		selectSql.append(" from INTERESTCERTIFICATE_VIEW ");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);

		RowMapper<InterestCertificate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InterestCertificate.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;	
	}
	
	
	@Override
	public InterestCertificate getSumOfPrinicipalAndProfitAmount(String finReference, String finStartDate, String finEndDate) throws ParseException {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
         
		Date startDate=new SimpleDateFormat("dd/MM/yyyy").parse(finStartDate);
		Date endDate=new SimpleDateFormat("dd/MM/yyyy").parse(finEndDate);
		StringBuilder selectSql = new StringBuilder("select sum(FINSCHDPFTPAID) as FINSCHDPFTPAID ,sum(FINSCHDPRIPAID) as FINSCHDPRIPAID");
		selectSql.append(" from INTERESTCERTIFICATE_VIEW ");
		
		if (App.DATABASE == Database.ORACLE) {
			selectSql.append(" Where FinReference =:FinReference and FINPOSTDATE >=:FinstartDate and FINPOSTDATE <=:FinEndDate");
		} else if (App.DATABASE == Database.POSTGRES) {
			selectSql.append(" Where FinReference =:FinReference and to_char(FINPOSTDATE,'dd-MM-yyyy') >=:FinstartDate and to_char(FINPOSTDATE,'dd-MM-yyyy') <=:FinEndDate");
		} else {
			selectSql.append(" Where FinReference =:FinReference and FINPOSTDATE >=:FinstartDate and FINPOSTDATE <=:FinEndDate");
		}

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("FinReference", finReference);
		source.addValue("FinstartDate", new SimpleDateFormat("yyyy-MM-dd").format(startDate));
		source.addValue("FinEndDate", new SimpleDateFormat("yyyy-MM-dd").format(endDate));

		RowMapper<InterestCertificate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(InterestCertificate.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}
	

	public String getCollateralRef(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
         
		StringBuilder selectSql = new StringBuilder("SELECT MIN(COLLATERALREF) ");
		selectSql.append(" FROM COLLATERALASSIGNMENT ");
		selectSql.append(" Where Reference =:Reference and active=1");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("Reference", finReference);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}
	@Override
	public String getCollateralType(String collateralRef) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
         
		StringBuilder selectSql = new StringBuilder("SELECT COLLATERALTYPE FROM COLLATERALSETUP ");
		selectSql.append(" Where COLLATERALREF =:COLLATERALREF ");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("COLLATERALREF", collateralRef);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}
	@Override
	public String getCollateralTypeField(String interfaceType, String table, String field) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
         
		StringBuilder selectSql = new StringBuilder("SELECT MappingColumn FROM INTERFACEMAPPING ");
		selectSql.append(" Where INTERFACENAME =:INTERFACENAME and INTERFACEFIELD =:INTERFACEFIELD and MAPPINGTABLE =:MAPPINGTABLE and active=1 ");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("INTERFACENAME", interfaceType);
		source.addValue("MAPPINGTABLE", table);
		source.addValue("INTERFACEFIELD", field);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;

	}
	@Override
	public String getCollateralTypeValue(String table, String columnField,String reference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder();
		if (StringUtils.containsIgnoreCase(columnField, "city")) {
			selectSql.append("select T1.PCCITYNAME from "+table+" T " );
			selectSql.append("Inner join RMTPROVINCEVSCITY T1 on T1.PCCITY=T."+columnField+" ");
		} else if (StringUtils.containsIgnoreCase(columnField, "state")) {
			selectSql.append("select T1.CPPROVINCENAME from "+table+" T " );
			selectSql.append("Inner join RMTCOUNTRYVSPROVINCE T1 on T1.CPPROVINCE=T."+columnField+" ");
		} else {
			selectSql.append("SELECT " + columnField + " FROM " + table + " ");
		}

		selectSql.append(" Where REFERENCE =:REFERENCE");

		logger.debug("selectSql: " + selectSql.toString());

		source.addValue("REFERENCE", reference);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}
	
}