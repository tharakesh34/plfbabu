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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.finance.ChangeTDSDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 */
public class ChangeTDSDAOImpl extends BasicDao<FinanceMain> implements ChangeTDSDAO {
	private static Logger logger = LogManager.getLogger(ChangeTDSDAOImpl.class);

	public ChangeTDSDAOImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinID, FM.FinReference, FT.FinType, FT.FinTypeDesc");
		sql.append(", FM.TDSApplicable, FM.FinAssetValue, FM.FinBranch");
		sql.append(", FM.CustID, Cust.CustCIF, Cust.CustShrtName");
		sql.append(", FM.FinStartDate, FM.MaturityDate, CURR.CCYCode From FinanceMain FM");
		sql.append(" Inner Join Customers Cust on FM.CustID = Cust.CustID");
		sql.append(" Inner Join RMTFinanceTypes FT ON FT.FinType = FM.FinType");
		sql.append(" Inner Join RMTCurrencies CURR ON CURR.CCYCode = FM.FinCCY");
		sql.append(" Where FM.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setFinType(rs.getString("FinType"));
				fm.setLovDescFinTypeName(rs.getString("FinTypeDesc"));
				fm.setTDSApplicable(rs.getBoolean("TDSApplicable"));
				fm.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setLovDescCustCIF(rs.getString("CustCIF"));
				fm.setLovDescCustShrtName(rs.getString("CustShrtName"));
				fm.setFinStartDate(rs.getDate("FinStartDate"));
				fm.setMaturityDate(rs.getDate("MaturityDate"));
				fm.setFinCcy(rs.getString("CCYCode"));

				return fm;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isTDSCheck(String reference, Date appDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select tdsapplicable from FinScheduleDetails");
		sql.append(" where FINREFERENCE=:Finreference");
		sql.append(" and SCHDATE=");
		sql.append(" (select min(SCHDATE) from FINSCHEDULEDETAILS");
		sql.append(" where FINREFERENCE=:Finreference and SCHDATE> :SchDate)");

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
