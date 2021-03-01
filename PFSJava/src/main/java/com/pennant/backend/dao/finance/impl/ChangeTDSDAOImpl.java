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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.ChangeTDSDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class ChangeTDSDAOImpl extends BasicDao<FinanceMain> implements ChangeTDSDAO {
	private static Logger logger = LogManager.getLogger(ChangeTDSDAOImpl.class);

	public ChangeTDSDAOImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();
		sql.append(
				"  SELECT FM.FinReference, FT.FinType, FT.FINTYPEDESC LovDescFinTypeName, FM.TDSAPPLICABLE, FM.FinAssetValue, FM.FinBranch,");
		sql.append(" FM.CustId, Cust.CUSTCIF LovDescCustCif, Cust.CUSTSHRTNAME LovDescCustShrtName,");
		sql.append(" FM.FinAssetValue,FM.FINSTARTDATE, FM.MATURITYDATE,CURR.CCYCODE finCcy  FROM FINANCEMAIN FM");
		sql.append(" INNER JOIN Customers Cust on FM.CUSTID=Cust.CUSTID");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES CURR ON CURR.CCYCODE = FM.FINCCY");
		sql.append(" Where FM.FinReference = :FinReference");
		logger.trace(Literal.SQL + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		RowMapper<FinanceMain> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isTDSCheck(String reference, Date appDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select tdsapplicable from FinScheduleDetails");
		sql.append(" where FINREFERENCE=:Finreference");
		sql.append(" and SCHDATE=");
		sql.append(" (select max(SCHDATE) from FINSCHEDULEDETAILS");
		sql.append(" where FINREFERENCE=:Finreference and SCHDATE<=:SchDate)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("Finreference", reference);
		detail.addValue("SchDate", appDate);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), detail, Boolean.class);

	}

	public Date getInstallmentDate(String reference, Date appDate) {
		logger.debug("Entering");

		MapSqlParameterSource detail = new MapSqlParameterSource();
		detail.addValue("Finreference", reference);
		detail.addValue("SchDate", appDate);

		StringBuilder selectSql = new StringBuilder(" ");
		selectSql.append(
				"select SCHDATE from FINSCHEDULEDETAILS where FINREFERENCE=:Finreference and SCHDATE>=:SchDate and presentmentid=0 and ROWNUM = 1 order by SCHDATE ");

		logger.debug("SelectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return (Date) this.jdbcTemplate.queryForObject(selectSql.toString(), detail, Date.class);

	}
}
